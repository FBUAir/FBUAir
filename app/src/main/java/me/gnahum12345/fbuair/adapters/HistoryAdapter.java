package me.gnahum12345.fbuair.adapters;


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
import android.widget.ListView;
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
import me.gnahum12345.fbuair.models.Contact;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.ContactUtils;

import static me.gnahum12345.fbuair.utils.ImageUtils.getCircularBitmap;
import static me.gnahum12345.fbuair.utils.Utils.getHistoryDate;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
        implements Filterable {

    private List<User> history;
    private List<User> filteredHistory;
    private HistoryFilter historyFilter;
    private Context context;
    private OnFragmentChangeListener onFragmentChangeListener;
    private boolean multiSelect = false;
    private ArrayList<User> selectedUsers = new ArrayList<>();
    private ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            multiSelect = true;
            actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
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
                    //TODO: add it all selected to contacts.
                    Toast.makeText(context, String.format("Adding %d users to contacts", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                    addContacts(selectedUsers);
                    actionMode.finish();
                    return true;
                case R.id.delete_menu_action:
                    //TODO: delete all selected
                    Toast.makeText(context, String.format("Deleting %d users", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                    deleteContacts(selectedUsers);
                    actionMode.finish();
                    return true;
                case R.id.star_menu_action:
                    // Do nothing here.. maybe add a favorites tab??
                    Toast.makeText(context, String.format("Loving %d users", selectedUsers.size()), Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            selectedUsers.clear();
            notifyDataSetChanged();
        }
    };


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
        View contactView = inflater.inflate(R.layout.history_item_two, holder, false);
        // return a new viewHolder,
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final User user = filteredHistory.get(position);
        viewHolder.tvName.setText(user.getName());
        if (user.getOrganization().isEmpty()) {
            viewHolder.tvOrganization.setVisibility(View.GONE);
        } else {
            viewHolder.tvOrganization.setText(user.getOrganization());
        }
        if (selectedUsers.contains(user)) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            Drawable drawable = context.getDrawable(R.drawable.item_checked);
            viewHolder.ivProfileImage.setImageDrawable(drawable);
        } else {
            setImage(user, viewHolder);

            if (!user.isSeen()) {
                viewHolder.itemView.setBackgroundColor(context.getColor(R.color.gradient_extremely_light_blue));
                viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
            } else {
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
            }
        }


        viewHolder.tvTime.setText(getHistoryDate(user.getTimeAddedToHistory()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multiSelect) {
                    selectItem(user, viewHolder);
                } else {
                    onFragmentChangeListener.launchDetails(user.getId());
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                return false;
                onFragmentChangeListener.startAction(actionModeCallBack);
                selectItem(user, viewHolder);
                return true;
            }
        });
    }

    private void selectItem(User user, ViewHolder viewHolder) {
        if (multiSelect) {
            if (selectedUsers.contains(user)) {
                selectedUsers.remove(user);
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                setImage(user, viewHolder);
            } else {
                selectedUsers.add(user);
                setImage(context.getDrawable(R.drawable.item_checked), viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
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
        public TextView tvOrganization;

        ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            ivProfileImage = view.findViewById(R.id.ivProfileImage);
            tvOrganization = view.findViewById(R.id.tvOrganization);
        }

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