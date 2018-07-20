package me.gnahum12345.fbuair.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.ConnectionsActivity;
import me.gnahum12345.fbuair.activities.DiscoverActivity;
import me.gnahum12345.fbuair.models.ProfileUser;


public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.ViewHolder>{

    // TODO 1: find a way s.t. we don't need a SET, MAP and LIST.
    private Set<ConnectionsActivity.Endpoint> mDevices;
    private List<ConnectionsActivity.Endpoint> mDeviceList;
    private Map<ConnectionsActivity.Endpoint, ProfileUser> profileUserMap;

    private Context mContext;



    public DiscoverAdapter(Set<ConnectionsActivity.Endpoint> devices) {
        mDevices = devices;
        mDeviceList = new ArrayList<>();
        profileUserMap = new HashMap<>();
        mDeviceList.addAll(mDevices);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // create the view using the item_movie layout.
        View deviceView = inflater.inflate(R.layout.discover_item, viewGroup, false);
        // return a new viewHolder,
        return new ViewHolder(deviceView);
    }

    public void add(ConnectionsActivity.Endpoint e) {
        if (mDevices.add(e)) {
            mDeviceList.add(e);
        }
    }

    public boolean remove(ConnectionsActivity.Endpoint e) {
        if (mDevices.remove(e)) {
            mDeviceList.remove(e);
            return true;
        }
        return false;
    }

    // TODO 2: find a way s.t. after a call to put the onBindViewholder is called and updates the image.
    public void put(ConnectionsActivity.Endpoint e, ProfileUser profileUser) {
        profileUserMap.put(e, profileUser);
        notifyDataSetChanged();
    }

    public void clear() {
        profileUserMap.clear();
        mDeviceList.clear();
        mDevices.clear();
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final ConnectionsActivity.Endpoint device = mDeviceList.get(i);

        ProfileUser profileUser = profileUserMap.get(device);
        if (profileUser != null) {
            viewHolder.mivProfilePic.setVisibility(View.VISIBLE);
            viewHolder.mivProfilePic.setImageBitmap(profileUser.getIvProfileImage());
            viewHolder.mtvDeviceName.setText(profileUser.getName());
        } else {
            viewHolder.mtvDeviceName.setText(device.getName());
        }

        viewHolder.mtvDeviceName.setTextColor(Color.BLACK);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, viewHolder.mtvDeviceName.getText(), Toast.LENGTH_SHORT).show();
                ((DiscoverActivity) mContext).sendFromEndPoint(device);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mtvDeviceName;
        private ImageView mivProfilePic;

        public ViewHolder(@NonNull View view) {
            super(view);
            mtvDeviceName = view.findViewById(R.id.tvNameofDevice);
            mivProfilePic = view.findViewById(R.id.ivProfilePic);
        }
    }
}