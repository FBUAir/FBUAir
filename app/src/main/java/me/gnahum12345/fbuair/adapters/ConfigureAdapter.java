package me.gnahum12345.fbuair.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.interfaces.OnRequestOAuthListener;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;

public class ConfigureAdapter extends BaseAdapter {
    // list of socialMedias and context
    private List<SocialMedia> socialMedias;
    private Context context;
    private User user;



    // pass the socialMedias list in the constructor
    public ConfigureAdapter(Context context) {
        this.context = context;
        this.user = MyUserManager.getInstance().getCurrentUser();
        this.socialMedias = new ArrayList<>(user.getSocialMedias());

        if (!user.getPhoneNumber().isEmpty()) {
            SocialMedia phone = new SocialMedia();
            phone.setName("Phone");
            phone.setProfileUrl(user.getPhoneNumber());
            phone.setUsername("phoneUserName");
            socialMedias.add(phone);
        }
        if (!user.getEmail().isEmpty()) {
            SocialMedia email = new SocialMedia();
            email.setName("Email");
            email.setProfileUrl(user.getEmail());
            email.setUsername("EmailUser");
            socialMedias.add(email);
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

        final ConfigureAdapter.ViewHolder viewHolder = (ConfigureAdapter.ViewHolder)view.getTag(R.id.VIEW_HOLDER_KEY);
        viewHolder.ivImage.setImageDrawable(SocialMediaUtils.getIconDrawable(context, socialMedia));
        viewHolder.tvName.setText(socialMedia.getName());
        boolean added = user.isSendingSocialMedia(socialMedia);
        viewHolder.ivCheck.setVisibility(added ? View.VISIBLE : View.GONE);

        if (socialMedia.getName().contains("Phone")) {
            added = user.isSendingPhone();

            viewHolder.ivCheck.setVisibility(added ? View.VISIBLE : View.GONE);
        }
        if (socialMedia.getName().contains("Email")) {
            added = user.isSendingEmail();
            viewHolder.ivCheck.setVisibility(added ? View.VISIBLE : View.GONE);
        }
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
            SocialMedia socialMedia = socialMedias.get(position);
            boolean changed = false;
            if (socialMedia.getName().contains("Phone")) {
                user.togglePhone();
                changed = true;
            }
            if (socialMedia.getName().contains("Email")) {
                user.toggleEmail();
                changed = true;
            }
            if (changed) {
                MyUserManager.getInstance().commitCurrentUser(user);
                notifyDataSetChanged();
                return;
            }
            if (user.isSendingSocialMedia(socialMedia)) {
                user.removeSendingSocialMedia(socialMedia);
            } else {
                user.addSendingSocialMedia(socialMedia);
            }
            MyUserManager.getInstance().commitCurrentUser(user);
            notifyDataSetChanged();
        }
    }
}