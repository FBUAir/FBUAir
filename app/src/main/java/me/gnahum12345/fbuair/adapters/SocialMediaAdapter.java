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
import me.gnahum12345.fbuair.interfaces.OnRequestOAuthListener;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;

// adapter for social media socialMedias during sign up
public class SocialMediaAdapter extends BaseAdapter {
    // list of socialMedias and context
    private List<SocialMedia> socialMedias;
    private Context context;
    private User user;

    private OnSignUpScreenChangeListener onSignUpScreenChangeListener;
    private OnRequestOAuthListener onRequestOAuthListener;

    // Clean all elements of the recycler
    public void clear() {
        socialMedias.clear();
        notifyDataSetChanged();
    }

    // pass the socialMedias list in the constructor
    public SocialMediaAdapter(Context context, List<SocialMedia> socialMedias, User user) {
        this.context = context;
        this.user = user;
        this.socialMedias = socialMedias;
        try {
            onSignUpScreenChangeListener = (OnSignUpScreenChangeListener) context;
        } catch (ClassCastException e) {
            Log.e("SocialMediaAdapter", "Fragment must implement OnSignUpScreenChangeListener") ;
        }
        try {
            onRequestOAuthListener = (OnRequestOAuthListener) context;
        } catch (ClassCastException e) {
            Log.e("SocialMediaAdapter", "Fragment must implement OnSignUpScreenChangeListener") ;
        }

    }

    @Override
    public int getCount() {
        return socialMedias.size();
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
        final SocialMedia socialMedia = socialMedias.get(position);
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.item_social_media, null);
            final ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(R.id.VIEW_HOLDER_KEY, viewHolder);
            view.setTag(R.id.POSITION_KEY, position);
        }

        final ViewHolder viewHolder = (ViewHolder)view.getTag(R.id.VIEW_HOLDER_KEY);
        viewHolder.ivImage.setImageDrawable(SocialMediaUtils.getDrawable(context, socialMedia));
        viewHolder.tvName.setText(socialMedia.getName());
        boolean added = user.hasSocialMedia(socialMedia);
        viewHolder.ivCheck.setVisibility(added ? View.VISIBLE : View.GONE);
        return view;
    }

    // create the ViewHolder class
    public class ViewHolder implements View.OnClickListener {
        ImageView ivImage;
        ImageView ivCheck;
        TextView tvName;

        ViewHolder(View itemView) {
            // perform findViewById lookups
            ivImage = itemView.findViewById(R.id.ivImage);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            tvName = itemView.findViewById(R.id.tvName);

            // set this as items' onclicklistener
            itemView.setOnClickListener(this);
        }

        // open dialog to insert url on click
        public void onClick(View view) {
            // get item position
            int position = (int) view.getTag(R.id.POSITION_KEY);
            // get the socialMedia at the position from socialMedias array and go to its url fragment to add/edit
            SocialMedia socialMedia;
            socialMedia = socialMedias.get(position);
            switch (socialMedia.getName()) {
                case "Twitter":
                    onRequestOAuthListener.twitterLogin(socialMedia);
                    break;
                case "LinkedIn":
                    onRequestOAuthListener.linkedInLogin(socialMedia);
                    break;
                case "Facebook":
                    onRequestOAuthListener.facebookLogin(socialMedia);
                    break;
                case "Github":
                    onRequestOAuthListener.githubLogin(socialMedia);
                    break;
                default:
                    onSignUpScreenChangeListener.launchUrl(socialMedia);
            }
        }
    }
}