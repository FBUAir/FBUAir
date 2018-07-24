package me.gnahum12345.fbuair.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.DetailsActivity;
import me.gnahum12345.fbuair.models.User;
import static me.gnahum12345.fbuair.utilities.Utility.dateFormatter;
import static me.gnahum12345.fbuair.utilities.Utility.getRelativeTimeAgo;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
        implements Filterable {

    private List<User> history;
    private List<User> filteredHistory;
    private HistoryFilter historyFilter;
    private Context context;

    public HistoryAdapter(List<User> history) {
        this.history = history;
        this.filteredHistory = history;
        getFilter();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup holder, int i) {
        context = holder.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.history_item, holder, false);
        // return a new viewHolder,
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        User user = filteredHistory.get(position);
        viewHolder.tvName.setText(user.getName());
        viewHolder.ivProfileImage.setImageBitmap(user.getProfileImage());
        String relativeTimeString;
        try {
            relativeTimeString =
                    getRelativeTimeAgo(dateFormatter.parse(user.getTimeAddedToHistory()));
        } catch (ParseException e) {
            e.printStackTrace();
            relativeTimeString  = "";
        }
        viewHolder.tvTime.setText(relativeTimeString);
    }

    public void clear() {
        filteredHistory.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvTime;
        public ImageView ivProfileImage;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            ivProfileImage = view.findViewById(R.id.ivProfileImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("go to details", "went to details");
                    int position = getAdapterPosition();
                    User user = history.get(position);
                    Intent i = new Intent(context, DetailsActivity.class);
                    i.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    context.startActivity(i);
                }
            });
        }

    }

    // get filter
    @Override
    public Filter getFilter() {
        if (historyFilter == null) {
            historyFilter = new HistoryFilter();
        }
        return historyFilter;
    }

    // filter history for searching
    private class HistoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
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