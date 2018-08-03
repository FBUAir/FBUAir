package me.gnahum12345.fbuair.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.databinding.ItemContactCardBinding;
import me.gnahum12345.fbuair.databinding.ItemProfileHeaderBinding;
import me.gnahum12345.fbuair.databinding.ItemSocialMediaCardBinding;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.models.Contact;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTACT = 1;
    private static final int TYPE_SOCIAL_MEDIA = 2;
    private static final int TYPE_EMPTY_CONTACT = -1;

    Context context;
    Header header;
    Contact contact;
    ArrayList<SocialMedia> socialMedias;
    boolean isCurrentUserProfile;

    public ProfileAdapter(Context context, String uid, boolean isCurrentUserProfile)
    {
        this.context = context;
        this.header = new Header(uid);
        this.contact = new Contact(uid);
        this.socialMedias = UserManager.getInstance().getUser(uid).getSocialMedias();
        this.isCurrentUserProfile = isCurrentUserProfile;
    }

/*    public ProfileAdapter(Context context, User user)
    {
        this.context = context;
        this.header = new Header(user);
        this.contact = new Contact(user);
        this.socialMedias = user.getSocialMedias();
        this.isCurrentUserProfile = (user.equals(UserManager.getInstance().getCurrentUser()));
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_HEADER)
        {
            ItemProfileHeaderBinding headerBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_profile_header, parent,
                            false);
            return new VHHeader(headerBinding);
        }
        else if(viewType == TYPE_CONTACT)
        {
            ItemContactCardBinding contactBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_contact_card, parent,
                            false);
            return new VHContact(contactBinding);
        }
        else if(viewType == TYPE_SOCIAL_MEDIA)
        {
            ItemSocialMediaCardBinding socialMediaBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_social_media_card, parent,
                            false);
            return new VHSocialMedia(socialMediaBinding);
        }
        throw new RuntimeException("No type matching " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // populate views in each item
        if(holder instanceof VHHeader)
        {
            VHHeader vhHeader = (VHHeader)holder;
            vhHeader.bind.ivProfileImage.setImageBitmap(header.getProfileImage());
            vhHeader.bind.tvName.setText(header.getName());
            if (header.organization.isEmpty()) {
                vhHeader.bind.tvOrganization.setVisibility(View.GONE);
            }
            else vhHeader.bind.tvOrganization.setText(header.getOrganization());
            vhHeader.bind.tvConnections.setText(String.valueOf(header.getConnections()) + " connections");
            if (isCurrentUserProfile) {
                vhHeader.bind.llDetailsOptions.setVisibility(View.GONE);
                vhHeader.bind.llProfileOptions.setVisibility(View.VISIBLE);
            }
            else {
                vhHeader.bind.llDetailsOptions.setVisibility(View.VISIBLE);
                vhHeader.bind.llProfileOptions.setVisibility(View.GONE);
            }

        }
        else if(holder instanceof VHContact)
        {
            VHContact vhContact = (VHContact)holder;
            vhContact.bind.tvPhone.setText(contact.getPhone());
            vhContact.bind.tvEmail.setText(contact.getEmail());
        }
        else if(holder instanceof VHSocialMedia)
        {
            SocialMedia socialMedia = socialMedias.get(position - 2);
            VHSocialMedia vhSocialMedia = (VHSocialMedia) holder;
            vhSocialMedia.bind.tvUsername.setText(socialMedia.getUsername());
            vhSocialMedia.bind.ivIcon.setImageDrawable(SocialMediaUtils.getIconDrawable(context, socialMedia));
        }
    }

    // overriding this method for different types
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            case 1:
                return TYPE_CONTACT;
            default:
                return TYPE_SOCIAL_MEDIA;
        }
    }

    // increasing itemCount by 2 to account for contact and header
    @Override
    public int getItemCount() {
        return socialMedias.size() + 2;
    }

    // view holders for different items
    class VHHeader extends RecyclerView.ViewHolder{
        ItemProfileHeaderBinding bind;
        VHHeader(ItemProfileHeaderBinding bind) {
            super(bind.getRoot());
            this.bind = bind;
        }
    }

    class VHContact extends RecyclerView.ViewHolder{
        ItemContactCardBinding bind;
        VHContact(ItemContactCardBinding bind) {
            super(bind.getRoot());
            this.bind = bind;
        }
    }

    class VHSocialMedia extends RecyclerView.ViewHolder{
        ItemSocialMediaCardBinding bind;
        VHSocialMedia(ItemSocialMediaCardBinding bind) {
            super(bind.getRoot());
            this.bind = bind;
        }
    }

    // header class containing main user info
    class Header {
        Bitmap profileImage;
        String name;
        String organization;
        int connections;

        public Header(String uid) {
            User user = UserManager.getInstance().getUser(uid);
            this.profileImage = user.getProfileImage();
            this.name = user.getName();
            this.organization = user.getOrganization();
            this.connections = user.getNumConnections();
        }

/*        public Header(User user) {
            this.profileImage = user.getProfileImage();
            this.name = user.getName();
            this.organization = user.getOrganization();
            this.connections = user.getNumConnections();
        }*/

        public Bitmap getProfileImage() {
            return profileImage;
        }

        public String getName() {
            return name;
        }


        public String getOrganization() {
            return organization;
        }


        public int getConnections() {
            return connections;
        }
    }

    // contact class containing user contact info
    class Contact {
        String phone;
        String email;

        public Contact(String uid) {
            User user = UserManager.getInstance().getUser(uid);
            this.phone = user.getPhoneNumber();
            this.email = user.getEmail();
        }

/*        public Contact(User user) {
            this.phone = user.getPhoneNumber();
            this.email = user.getEmail();
        }*/

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public boolean isEmpty() {
            return phone.isEmpty() && email.isEmpty();
        }
    }
}
