package me.gnahum12345.fbuair.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.databinding.ItemContactCardBinding;
import me.gnahum12345.fbuair.databinding.ItemProfileHeaderBinding;
import me.gnahum12345.fbuair.databinding.ItemSocialMediaCardBinding;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.interfaces.OnContactAddedCallback;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.Contact;
import me.gnahum12345.fbuair.models.Header;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;

import static me.gnahum12345.fbuair.utils.Utils.getCircularBitmap;
import static me.gnahum12345.fbuair.utils.Utils.getDarkenedBitmap;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTACT = 1;
    private static final int TYPE_SOCIAL_MEDIA = 2;

    Context context;
    ProfileFragment.ProfileFragmentListener mListener;
    Header header;
    Contact contact;
    ArrayList<SocialMedia> socialMedias;
    boolean isCurrentUserProfile;
    int difference;

    OnFragmentChangeListener onFragmentChangeListener;
    OnRequestAddContact onAddContactClickedListener;

    public ProfileAdapter(Context context, Contact contact, Header header,
                          ArrayList<SocialMedia> socialMedias, boolean isCurrentUserProfile)
    {
        this.header = header;
        this.contact = contact;
        this.socialMedias = socialMedias;
        this.context = context;
        this.isCurrentUserProfile = isCurrentUserProfile;
        difference = contact.isEmpty() ? 1 : 2;

        onFragmentChangeListener = (OnFragmentChangeListener)context;
        this.onAddContactClickedListener = (OnRequestAddContact)context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            ItemProfileHeaderBinding headerBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_profile_header, parent,
                            false);
            return new VHHeader(headerBinding);
        } else if (viewType == TYPE_CONTACT) {
            ItemContactCardBinding contactBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_contact_card, parent,
                            false);
            return new VHContact(contactBinding);
        } else if (viewType == TYPE_SOCIAL_MEDIA) {
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
        if (holder instanceof VHHeader) {
            VHHeader vhHeader = (VHHeader) holder;
            vhHeader.bind.ivProfileImage.setImageBitmap(header.getProfileImage());
            vhHeader.bind.tvName.setText(header.getName());
            Bitmap profileImage = header.getProfileImage();
            if (profileImage == null) {
                vhHeader.bind.ivProfileImage.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.default_profile, null));
            } else {
                // set profile
                vhHeader.bind.ivProfileImage.setImageBitmap(getCircularBitmap(profileImage));
                // set cover photo/background
                Bitmap coverPhotoBitmap = profileImage.copy(Bitmap.Config.ARGB_8888, true);
                coverPhotoBitmap = getDarkenedBitmap(coverPhotoBitmap);
                vhHeader.bind.ivBackground.setImageBitmap(coverPhotoBitmap);
            }
            if (header.getOrganization().isEmpty()) {
                vhHeader.bind.tvOrganization.setVisibility(View.GONE);
            } else vhHeader.bind.tvOrganization.setText(header.getOrganization());
            vhHeader.bind.tvConnections.setText
                    (String.valueOf(header.getConnections()) + " connections");

            if (isCurrentUserProfile) {
                vhHeader.bind.llDetailsOptions.setVisibility(View.GONE);
            } else {
                vhHeader.bind.btEditProfile.setVisibility(View.GONE);
                if (contact.isAdded()) {
                    vhHeader.bind.btAddContact.setEnabled(false);
                    vhHeader.bind.btAddContact.setImageDrawable
                            (context.getResources().getDrawable(R.drawable.ic_add_button_disabled,
                                    null));
                }
            }
            if (!isAvaliable(header.getUid()) || !isListener()) {
                vhHeader.bind.btSendBack.setEnabled(false);
                vhHeader.bind.btSendBack.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_share_button_disabled, null));
                return;
            }
            vhHeader.bind.btSendBack.setVisibility(View.VISIBLE);
            vhHeader.bind.btSendBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.sendBack(header.getUid());
                }
            });

        } else if (holder instanceof VHContact) {
            VHContact vhContact = (VHContact) holder;
            if (contact.getPhone().isEmpty()) {
                vhContact.bind.llPhone.setVisibility(View.GONE);
                vhContact.bind.horizontalLine.setVisibility(View.GONE);
            } else {
                vhContact.bind.llPhone.setVisibility(View.VISIBLE);
                vhContact.bind.horizontalLine.setVisibility(View.VISIBLE);
                String formattedNumber = PhoneNumberUtils.formatNumber(contact.getPhone(), "US");
                vhContact.bind.tvPhone.setText(formattedNumber);
            }
            if (contact.getEmail().isEmpty()) {
                vhContact.bind.llEmail.setVisibility(View.GONE);
                vhContact.bind.horizontalLine.setVisibility(View.GONE);
            } else {
                vhContact.bind.llEmail.setVisibility(View.VISIBLE);
                vhContact.bind.horizontalLine.setVisibility(View.VISIBLE);
                vhContact.bind.tvEmail.setText(contact.getEmail());
            }

        } else if (holder instanceof VHSocialMedia) {
            SocialMedia socialMedia = socialMedias.get(position - difference);
            VHSocialMedia vhSocialMedia = (VHSocialMedia) holder;
            vhSocialMedia.bind.tvUsername.setText(socialMedia.getUsername());
            vhSocialMedia.bind.ivIcon.setImageDrawable(SocialMediaUtils.getIconDrawable(context, socialMedia));
        }
    }

    private boolean isAvaliable(String uid) {
        return MyUserManager.getInstance().avaliableEndpoint(uid) != null;
    }

    private boolean isListener() {
        return mListener != null;
    }

    // overriding this method for different types
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            case 1:
                if (contact.isEmpty()) return TYPE_SOCIAL_MEDIA;
                else return TYPE_CONTACT;
            default:
                return TYPE_SOCIAL_MEDIA;
        }
    }

    // increasing itemCount by difference to account for header and contact
    @Override
    public int getItemCount() {
        return socialMedias.size() + difference;
    }

    public void setListener(ProfileFragment.ProfileFragmentListener listener) {
        mListener = listener;
    }

    // view holders for different items
    class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemProfileHeaderBinding bind;

        VHHeader(ItemProfileHeaderBinding bind) {
            super(bind.getRoot());
            this.bind = bind;
            bind.btEditProfile.setOnClickListener(this);
            bind.btDeleteProfile.setOnClickListener(this);
            bind.btAddContact.setOnClickListener(this);
            bind.btSendBack.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btAddContact:
                    onAddContactClickedListener.requestAddContact(header.getUid(), new OnContactAddedCallback() {
                        @Override
                        public void onSuccess() {
                            contact.setAdded(true);
                            notifyItemChanged(0);
                        }
                    });
                    break;
                case R.id.btSendBack:
                    // todo - send back functionality
                    break;
                case R.id.btEditProfile:
                    onFragmentChangeListener.launchEditProfile();
                    break;
                case R.id.btDeleteProfile:
                    onFragmentChangeListener.deleteAccount();
                    break;
            }
        }
    }

    class VHContact extends RecyclerView.ViewHolder {
        ItemContactCardBinding bind;

        VHContact(ItemContactCardBinding bind) {
            super(bind.getRoot());
            this.bind = bind;
        }
    }

    class VHSocialMedia extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemSocialMediaCardBinding bind;

        VHSocialMedia(ItemSocialMediaCardBinding bind) {
            super(bind.getRoot());
            bind.getRoot().setOnClickListener(this);
            this.bind = bind;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition() - difference;

//            String url = socialMedias.get(position).getProfileUrl();
//            Log.d("PROFILEADAPTER", "clicked link: " + url);
//            onFragmentChangeListener.launchUrlView(url);
        }
    }
}
