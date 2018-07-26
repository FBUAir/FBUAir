package me.gnahum12345.fbuair.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.interfaces.OnIconClickedListener;
import me.gnahum12345.fbuair.models.Icon;

// adapter for social media icons during sign up
public class IconAdapter extends BaseAdapter {
    // list of icons and context
    private List<Icon> icons;
    private Context context;

    private OnIconClickedListener onIconClickedListener;

    // Clean all elements of the recycler
    public void clear() {
        icons.clear();
        notifyDataSetChanged();
    }

    // pass the icons list in the constructor
    public IconAdapter(Context context, List<Icon> icons) {
        this.context = context;
        this.icons = icons;
        try {
            onIconClickedListener = (OnIconClickedListener) context;
        } catch (ClassCastException e) {
            Log.e("IconAdapter", "Fragment must implement OnIconClickedListener") ;
        }
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Icon icon = icons.get(position);
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_icon, null);
            final ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(R.id.VIEW_HOLDER_KEY, viewHolder);
            view.setTag(R.id.POSITION_KEY, position);
        }

        final ViewHolder viewHolder = (ViewHolder)view.getTag(R.id.VIEW_HOLDER_KEY);
        viewHolder.ivIconImage.setImageDrawable(icon.getDrawable());
        viewHolder.tvIconName.setText(icon.getName());
        viewHolder.ivCheck.setVisibility(icon.isAdded() ? View.VISIBLE : View.GONE);
        return view;
    }

    // create the ViewHolder class
    public class ViewHolder implements View.OnClickListener {
        // declare views
        ImageView ivIconImage;
        TextView tvIconName;
        ImageView ivCheck;

        ViewHolder(View itemView) {
            // perform findViewById lookups
            ivIconImage = itemView.findViewById(R.id.ivIconImage);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            tvIconName = itemView.findViewById(R.id.tvIconName);

            // set this as items' onclicklistener
            itemView.setOnClickListener(this);
        }

        // open dialog to insert url on click
        public void onClick(View view) {
            // get item position
            int position = (int) view.getTag(R.id.POSITION_KEY);
            // get the icon at the position from icons array and toggle its selected
            Icon icon;
            icon = icons.get(position);
            if (icon.isAdded()) {
                onIconClickedListener.removeMedia(icon);
            } else {
                onIconClickedListener.addMedia(icon);
            }
        }
    }

}