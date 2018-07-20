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
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.DetailsActivity;
import me.gnahum12345.fbuair.models.User;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<User> mContacts;
    private Context mContext;

    public HistoryAdapter(List<User> contacts) {
        mContacts = contacts;
    }

    //TODO ADD TIMESTAMPS
    public static String getRelativeTimeAgo(Date date) {
        String relativeDate;
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup holder, int i) {
        mContext = holder.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);


        View contactView = inflater.inflate(R.layout.history_item, holder, false);
        // return a new viewHolder,
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        User contact = mContacts.get(position);
        viewHolder.tvName.setText(contact.name);
        viewHolder.ivProfileImage.setImageBitmap(contact.profileImage);
    }

    public void clear() {
        mContacts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvTime;
        public ImageView ivProfileImage;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("go to details", "went to details");
                    int position = getAdapterPosition();
                    User user = mContacts.get(position);
                    Intent i = new Intent(mContext, DetailsActivity.class);
                    i.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    mContext.startActivity(i);

                }
            });
        }

    }

}