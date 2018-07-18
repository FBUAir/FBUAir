package me.gnahum12345.fbuair.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.gnahum12345.fbuair.R;


public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.ViewHolder>{

    private List<String> mDevices;
    private Context mContext;

    public DiscoverAdapter(List<String> devices) {
        mDevices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // creat eht view using the item_movie layout.
        View deviceView = inflater.inflate(R.layout.discover_item, viewGroup, false);
        // return a new viewHolder,
        return new ViewHolder(deviceView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        String device = mDevices.get(i);

        viewHolder.mtvDeviceName.setText(device);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* TODO Delete this toast and replace it with snackbar
                 *  that will send data from one device to another, unless clicked on undo.
                 */
                Toast.makeText(mContext, viewHolder.mtvDeviceName.getText(), Toast.LENGTH_SHORT).show();
                /* Snackbar... TODO: need to add it to gradle? probably.
                 Snackbar.make(mContext, "Contact Added", Snackbar.LENGTH_LONG)
                        .setActionTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .show(); // Don’t forget to show!
                        */
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