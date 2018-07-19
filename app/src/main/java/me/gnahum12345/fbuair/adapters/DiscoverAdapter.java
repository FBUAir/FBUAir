package me.gnahum12345.fbuair.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.ConnectionsActivity;
import me.gnahum12345.fbuair.activities.DiscoverActivity;


public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.ViewHolder>{

    private List<ConnectionsActivity.Endpoint> mDevices;
    private Context mContext;

    public DiscoverAdapter(List<ConnectionsActivity.Endpoint> devices) {
        mDevices = devices;
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

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final ConnectionsActivity.Endpoint device = mDevices.get(i);

        viewHolder.mtvDeviceName.setText(device.getName());
        viewHolder.mtvDeviceName.setTextColor(Color.BLACK);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, viewHolder.mtvDeviceName.getText(), Toast.LENGTH_SHORT).show();
                String name = device.getName();
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

        public ViewHolder(@NonNull View view) {
            super(view);
            mtvDeviceName = view.findViewById(R.id.tvNameOfDevice);
        }

    }
}