package me.gnahum12345.fbuair.adapters;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.ImageUtils.getCircularBitmap;
import static me.gnahum12345.fbuair.utils.SocialMediaUtils.getSummary;
import static me.gnahum12345.fbuair.utils.Utils.getHistoryDate;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
        implements Filterable {

    private List<User> history;
    private List<User> filteredHistory;
    private HistoryFilter historyFilter;
    private boolean isReceivedHistory;
    private Context context;
    private OnFragmentChangeListener onFragmentChangeListener;
    public boolean multiSelectMode = false;
    private ArrayList<User> selectedUsers = new ArrayList<>();
    private final static int SUMMARY_LIMIT = 3;
    private int firstSelectedPosition = -1;

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;

    private void deleteFromHistory(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            MyUserManager.getInstance().removeUser(users.get(i));
            filteredHistory.remove(users.get(i));
        }
    }

    public HistoryAdapter(Context context, List<User> history, boolean isReceivedHistory) {
        this.history = history;
        this.filteredHistory = history;
        this.isReceivedHistory = isReceivedHistory;
        getFilter();
        // load animations
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.in_animation);
        // try to get listener
        try {
            onFragmentChangeListener = ((OnFragmentChangeListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("MainActivity must implement OnFragmentChangeListener.");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup holder, int i) {
        context = holder.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_history, holder, false);
        // return a new viewHolder,
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        // get user at current position
        final User user = filteredHistory.get(position);

        // set user info
        viewHolder.tvName.setText(user.getName());
        viewHolder.tvSummary.setText(getSummary(user, isReceivedHistory, SUMMARY_LIMIT));
        viewHolder.tvTime.setText(getHistoryDate(user.getTimeAddedToHistory()));

        // set profile image and bring it to front
        viewHolder.ivCheck.setVisibility(View.INVISIBLE);
        viewHolder.ivProfileImage.setImageBitmap(getCircularBitmap(user.getProfileImage()));
        viewHolder.ivProfileImage.bringToFront();

        resetAfterAnimation(viewHolder.ivProfileImage);
        resetAfterAnimation(viewHolder.ivCheck);

        // set seen/unseen tint. make white if in multiselect mode
        if ((multiSelectMode || user.isSeen() || !isReceivedHistory)) {
            viewHolder.itemView.setBackgroundColor(context.getColor(R.color.color_white));
            viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        } else {
            viewHolder.itemView.setBackgroundColor(context.getColor(R.color.gradient_extremely_light_blue));
            viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
        }

        // select first long-clicked item
        if (multiSelectMode && position == firstSelectedPosition) {
            selectItem(user, viewHolder);
        }
    }

    private void resetAfterAnimation(View view) {
        view.setAlpha(1.0f);
        view.setRotationY(0.0f);
    }

    // shows img change animation and adds selected user to selected list
    private void selectItem(User user, ViewHolder viewHolder) {
        viewHolder.ivCheck.setVisibility(View.VISIBLE);
        if (selectedUsers.contains(user)) {
            animateSelection(false, viewHolder.ivProfileImage, viewHolder.ivCheck);
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            selectedUsers.remove(user);
        } else {
            animateSelection(true, viewHolder.ivProfileImage, viewHolder.ivCheck);
            viewHolder.itemView.setBackgroundColor(context.getColor(R.color.light_grey));
            selectedUsers.add(user);
        }
    }

    // performs flip animations
    private void animateSelection(boolean isSelecting, ImageView ivProfileImage, ImageView ivCheck) {
        // cancel previous animations if they're still running
        cancelRunningAnimations();
        // start flip animation
        View leaving, entering;
        leaving = isSelecting ? ivProfileImage : ivCheck;
        entering = isSelecting ? ivCheck : ivProfileImage;
        mSetRightOut.setTarget(leaving);
        mSetLeftIn.setTarget(entering);
        mSetRightOut.start();
        mSetLeftIn.start();
    }

    // cancels animations (used for when animation gets paused mid-anim)
    private void cancelRunningAnimations() {
        mSetRightOut.end();
        mSetLeftIn.end();
    }

    public void clear() {
        filteredHistory.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredHistory.size();
    }

    // get filter
    @Override
    public Filter getFilter() {
        if (historyFilter == null) {
            historyFilter = new HistoryFilter();
        }
        return historyFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvName;
        public TextView tvTime;
        public ImageView ivProfileImage;
        public TextView tvSummary;
        public ImageView ivCheck;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        // CLICK HANDLERS
        @Override
        public void onClick(View view) {
            User user = filteredHistory.get(getAdapterPosition());
            if (multiSelectMode) {
                selectItem(user, this);
            } else if (isReceivedHistory) {
                onFragmentChangeListener.launchDetails(user.getId(),view);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            multiSelectMode = true;
            firstSelectedPosition = getAdapterPosition();
            notifyDataSetChanged();
            onFragmentChangeListener.setActionModeVisible(true,
                    getActionModeCallBack(onFragmentChangeListener));
            return true;
        }
    }

    // gets action mode callback
    public ActionMode.Callback getActionModeCallBack(OnFragmentChangeListener onFragmentChangeListener) {
        ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                onFragmentChangeListener.setMenuVisible(false);
                actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.add_menu_action:
                        Toast.makeText(context, String.format("Added %d users to phone contacts", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                        actionMode.finish();
                        return true;
                    case R.id.delete_menu_action:
                        Toast.makeText(context, String.format("Deleted %d users", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                        deleteFromHistory(selectedUsers);
                        actionMode.finish();
                        return true;
                    case R.id.star_menu_action:
                        Toast.makeText(context, String.format("Starred %d users", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                cancelRunningAnimations();
                multiSelectMode = false;
                selectedUsers.clear();
                firstSelectedPosition = -1;
                notifyDataSetChanged();
                onFragmentChangeListener.setMenuVisible(true);
            }
        };
        return actionModeCallBack;
    }

    // filter history for searching
    private class HistoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<User> filteredList = new ArrayList<>();
                // search content in history
                for (User user : history) {
                    if (user.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(user);
                    }
                }
                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
            } else {
                filterResults.count = history.size();
                filterResults.values = history;
            }
            return filterResults;
        }

        // notify ui of filtering
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredHistory = (ArrayList<User>) results.values;
            notifyDataSetChanged();
        }
    }
}