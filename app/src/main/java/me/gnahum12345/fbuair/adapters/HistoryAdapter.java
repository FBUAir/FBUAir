package me.gnahum12345.fbuair.adapters;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
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
import me.gnahum12345.fbuair.activities.MainActivity;
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
    private Context context;
    private OnFragmentChangeListener onFragmentChangeListener;
    private boolean multiSelectMode = false;
    private ArrayList<User> selectedUsers = new ArrayList<>();
    final static int SUMMARY_LIMIT = 3;

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;

    private void addContacts(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
//            ContactUtils.addContact(context, users.);
        }
    }

    private void deleteContacts(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            MyUserManager.getInstance().removeUser(users.get(i));
            history.remove(users.get(i));
        }
        notifyDataSetChanged();
    }


    public HistoryAdapter(Context context, List<User> history) {
        this.history = history;
        this.filteredHistory = history;
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
        final User user = filteredHistory.get(position);
        setImage(user, viewHolder);
        if (selectedUsers.contains(user)) {
            viewHolder.itemView.setBackgroundColor(context.getColor(R.color.light_grey));
            Drawable drawable = context.getDrawable(R.drawable.ic_checked_button);
            viewHolder.ivProfileImage.setImageDrawable(drawable);
        } else {
            if (multiSelectMode) {
                viewHolder.itemView.setBackgroundColor(context.getColor(R.color.color_white));
                viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
            } else {
                if (!user.isSeen()) {
                    viewHolder.itemView.setBackgroundColor(context.getColor(R.color.gradient_extremely_light_blue));
                    viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
                } else {
                    viewHolder.itemView.setBackgroundColor(Color.WHITE);
                    viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
                }
            }
        }

        viewHolder.tvName.setText(user.getName());
        viewHolder.tvSummary.setText(getSummary(user, SUMMARY_LIMIT));
        viewHolder.tvTime.setText(getHistoryDate(user.getTimeAddedToHistory()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multiSelectMode) {
                    selectItem(user, viewHolder);
                } else {
                    Pair<View, String> p1 = Pair.create(viewHolder.ivProfileImage, "profileImage");
                    Pair<View, String> p2 = Pair.create(viewHolder.tvName, "name");
                    onFragmentChangeListener.launchDetails(user.getId(), p1, p2);
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                multiSelectMode = true;
                notifyDataSetChanged();
                ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        onFragmentChangeListener.setMenuVisible(false);
                        actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
                        selectItem(user, viewHolder);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.add_menu_action:
                                Toast.makeText(context, String.format("Added %d users to phone contacts", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                                addContacts(selectedUsers);
                                actionMode.finish();
                                return true;
                            case R.id.delete_menu_action:
                                Toast.makeText(context, String.format("Deleted %d users", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                                deleteContacts(selectedUsers);
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
                        multiSelectMode = false;
                        selectedUsers.clear();
                        notifyDataSetChanged();
                        onFragmentChangeListener.setMenuVisible(true);
                    }
                };

                onFragmentChangeListener.launchActionMode(actionModeCallBack);
                return true;
            }
        });
        // change camera distance for profile image so full animation shows
        changeCameraDistance(context, viewHolder.ivProfileImage);
    }

    private void selectItem(User user, ViewHolder viewHolder) {
        if (selectedUsers.contains(user)) {
            // start animations
            mSetRightOut.setTarget(viewHolder.ivCheck);
            mSetLeftIn.setTarget(viewHolder.ivProfileImage);
            mSetRightOut.start();
            mSetLeftIn.start();
            // change BG color to grey
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            // remove user from selected
            selectedUsers.remove(user);
            //setImage(user, viewHolder);
        } else {
            // start animations
            mSetRightOut.setTarget(viewHolder.ivProfileImage);
            mSetLeftIn.setTarget(viewHolder.ivCheck);
            mSetRightOut.start();
            mSetLeftIn.start();
            // add user to selected
            selectedUsers.add(user);
            // change BG color to light grey
            viewHolder.itemView.setBackgroundColor(context.getColor(R.color.light_grey));
            //setImage(context.getDrawable(R.drawable.ic_checked_button), viewHolder);
        }
    }

    private void setImage(Object img, ViewHolder viewHolder) {
        if (img instanceof User) {
            User user = (User) img;
            Bitmap bitmap = user.getProfileImage();
            // generate fake profile images (real users should never have null)
            if (bitmap == null) {
                ColorGenerator generator = ColorGenerator.MATERIAL;
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(Character.toString(user.getName().toCharArray()[0]).toUpperCase(),
                                generator.getRandomColor());
                viewHolder.ivProfileImage.setImageDrawable(drawable);
            } else {
                viewHolder.ivProfileImage.setImageBitmap(getCircularBitmap(bitmap));
            }
        } else {
            if (img instanceof Drawable) {
                Drawable drawable = (Drawable) img;
                viewHolder.ivProfileImage.setImageDrawable(drawable);
            }
        }

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvTime;
        public ImageView ivProfileImage;
        public TextView tvSummary;
        public ImageView ivCheck;

        ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            ivProfileImage = view.findViewById(R.id.ivProfileImage);
            tvSummary = view.findViewById(R.id.tvSummary);
            ivCheck = view.findViewById(R.id.ivCheck);
        }
    }

    private void changeCameraDistance(Context context, View view) {
        int distance = 8000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        view.setCameraDistance(scale);
        view.setCameraDistance(scale);
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