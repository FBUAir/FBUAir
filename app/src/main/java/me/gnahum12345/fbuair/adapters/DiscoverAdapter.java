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

import org.json.JSONException;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.models.ProfileUser;
import me.gnahum12345.fbuair.services.ConnectionService.Endpoint;


public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.ViewHolder> {

    private TreeMap<Endpoint, String> mDevices;
    private Context mContext;


    public DiscoverAdapter() {
        // NEED THIS COMPARATOR SO ORDER IS BY NAME NOT ID (I.E RANDOM).
        mDevices = new TreeMap<>(new Comparator<Endpoint>() {
            @Override
            public int compare(Endpoint endpoint, Endpoint t1) {
                return parseName(endpoint.getName()).compareTo(parseName(t1.getName()));
            }
        });
    }


    public DiscoverAdapter(Set<Endpoint> devices) {
        // NEED THIS COMPARATOR SO ORDER IS BY NAME NOT ID (I.E RANDOM).
        mDevices = new TreeMap<>(new Comparator<Endpoint>() {
            @Override
            public int compare(Endpoint endpoint, Endpoint t1) {
                return parseName(endpoint.getName()).compareTo(parseName(t1.getName()));
            }
        });

        for (Endpoint e: devices) {
            mDevices.put(e, "UPDATE ME");
        }
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

    public void add(Endpoint e) {
        mDevices.put(e, "UPDATE ME");
        notifyDataSetChanged();
    }

    public void remove(Endpoint e) {
        mDevices.remove(e);
        notifyDataSetChanged();
//        return true; //TODO: possibly uncomment if this makes something crash.
    }

    public void put(Endpoint e, ProfileUser profileUser) {
        if (profileUser.getName() == null || profileUser.getName().isEmpty()) {
            return;
        }
        mDevices.put(e, profileUser.toString());
        notifyDataSetChanged();
    }

    public void clear() {
        mDevices.clear();
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Endpoint device = (Endpoint) mDevices.keySet().toArray()[i];

        ProfileUser profileUser = null;
        try {
            profileUser = ProfileUser.fromJSONString(mDevices.get(device));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (profileUser != null) {
            viewHolder.mivProfilePic.setVisibility(View.VISIBLE);
            viewHolder.mivProfilePic.setImageBitmap(profileUser.getIvProfileImage());
            viewHolder.mtvDeviceName.setText(profileUser.getName());
        } else {
            viewHolder.mtvDeviceName.setText(parseName(device.getName()));
        }

        viewHolder.mtvDeviceName.setTextColor(Color.BLACK);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, viewHolder.mtvDeviceName.getText(), Toast.LENGTH_SHORT).show();
//                ((DiscoverActivity) mContext).sendToEndpoint(device);  //TODO figure out what mContext really is.
                ((MainActivity) mContext).connectService.sendToEndpoint(device);
            }
        });
    }

    private String parseName(String name) {
//        String divider = ((MainActivity) mContext).getString(R.string.divider);
        // TODO: figure out why this is a null pointer.
        String divider = mContext.getResources().getString(R.string.divider);
        if (divider == null || divider.isEmpty()) {
            return name;
        } else {
            return name.split(divider)[0];
        }
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