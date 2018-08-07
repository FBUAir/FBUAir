package me.gnahum12345.fbuair.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.ImageUtils.getCircularBitmap;
import static me.gnahum12345.fbuair.utils.Utils.dateFormatter;
import static me.gnahum12345.fbuair.utils.Utils.getHistoryDate;
import static me.gnahum12345.fbuair.utils.Utils.getRelativeTimeAgo;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
        implements Filterable {

    private List<User> history;
    private List<User> filteredHistory;
    private HistoryFilter historyFilter;
    private Context context;
    private OnFragmentChangeListener onFragmentChangeListener;

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

        if (!user.isSeen()) {
//            viewHolder.itemView.setBackgroundTintMode();
//            viewHolder.itemView.setBackgroundColor();
            viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
        } else {
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.itemView.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        }

        viewHolder.tvTime.setText(getHistoryDate(user.getTimeAddedToHistory()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChangeListener.launchDetails(user.getId());
            }
        });
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