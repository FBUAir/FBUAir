package me.gnahum12345.fbuair.adapters;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.databinding.ItemProfileHeaderBinding;
import me.gnahum12345.fbuair.databinding.ItemSocialMediaCardBinding;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.interfaces.OnContactAddedCallback;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.Header;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.utils.SocialMediaUtils;

import static android.content.Context.CLIPBOARD_SERVICE;
import static me.gnahum12345.fbuair.models.User.NO_COLOR;
import static me.gnahum12345.fbuair.utils.ImageUtils.getCircularBitmap;
import static me.gnahum12345.fbuair.utils.ImageUtils.getDarkenedBitmap;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_SOCIAL_MEDIA = 1;

    private Context context;
    private ProfileFragment.ProfileFragmentListener mListener;
    private Header header;
    private ArrayList<SocialMedia> socialMedias;
    private boolean isCurrentUserProfile;
    private Bitmap coverPhotoBitmap;

    private OnFragmentChangeListener onFragmentChangeListener;
    private OnRequestAddContact onAddContactClickedListener;

    public ProfileAdapter(Context context, Header header,
                          ArrayList<SocialMedia> socialMedias, boolean isCurrentUserProfile) {
        this.header = header;
        this.socialMedias = socialMedias;
        this.context = context;
        this.isCurrentUserProfile = isCurrentUserProfile;

        onFragmentChangeListener = (OnFragmentChangeListener) context;
        this.onAddContactClickedListener = (OnRequestAddContact) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            ItemProfileHeaderBinding headerBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_profile_header, parent,
                            false);
            return new VHHeader(headerBinding);
        } else if (viewType == TYPE_SOCIAL_MEDIA) {
            ItemSocialMediaCardBinding socialMediaBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.item_social_media_card, parent,
                            false);
            return new VHSocialMedia(socialMediaBinding);
        }
        throw new RuntimeException("No type matching " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // populate views in header
        if (holder instanceof VHHeader) {
            // set name, org, connections, and profile image
            VHHeader vhHeader = (VHHeader) holder;
            vhHeader.bind.ivProfileImage.setImageBitmap(header.getProfileImage());
            vhHeader.bind.tvName.setText(header.getName());
            if (header.getOrganization().isEmpty()) {
                vhHeader.bind.tvOrganization.setVisibility(View.GONE);
            } else {
                vhHeader.bind.tvOrganization.setText(header.getOrganization());
            }
            vhHeader.bind.tvConnections.setText(String.valueOf(header.getConnections()) + " connections");

            // set profile image for fake users (real ones should never be null)
            Bitmap profileImage = header.getProfileImage();
            if (profileImage == null) {
                ColorGenerator generator = ColorGenerator.MATERIAL;
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(Character.toString(header.getName().toCharArray()[0]).toUpperCase(),
                                generator.getRandomColor());
                vhHeader.bind.ivProfileImage.setImageDrawable(drawable);
            } else {
                // set profile image
                vhHeader.bind.ivProfileImage.setImageBitmap(getCircularBitmap(profileImage));
                // set cover photo/background for non-default profile pics
                if (header.getColor() == NO_COLOR) {
                    coverPhotoBitmap = profileImage.copy(Bitmap.Config.ARGB_8888, true);
                    coverPhotoBitmap = getDarkenedBitmap(coverPhotoBitmap);
                    vhHeader.bind.ivBackground.setImageBitmap(coverPhotoBitmap);
                }
            }

            // hide phone number card if there's no phone number, otehrwise set it to the phone #
            if (header.getPhone().isEmpty()) {
                vhHeader.bind.llPhone.setVisibility(View.GONE);
                vhHeader.bind.horizontalLine.setVisibility(View.GONE);
            } else {
                vhHeader.bind.llPhone.setVisibility(View.VISIBLE);
                vhHeader.bind.horizontalLine.setVisibility(View.VISIBLE);
                vhHeader.bind.tvPhone.setText(header.getPhone());
                // go to phone when phone number is clicked
                vhHeader.bind.tvPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + header.getPhone()));

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Toast.makeText(context, "calling unsuccessful, will copy instead.", Toast.LENGTH_SHORT).show();
                            copy(context, header.getPhone());
                            return;
                        }
                        Toast.makeText(context, "Calling...", Toast.LENGTH_SHORT).show();

                        context.startActivity(intent);
                    }
                });
                vhHeader.bind.tvPhone.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return copy(context, header.getPhone());
                    }
                });
                String formattedNumber = PhoneNumberUtils.formatNumber(header.getPhone(), "US");
                vhHeader.bind.tvPhone.setText(formattedNumber);
            }

            // hide email card if there's no email, otehrwise set it to the email
            if (header.getEmail().isEmpty()) {
                vhHeader.bind.llEmail.setVisibility(View.GONE);
                vhHeader.bind.horizontalLine.setVisibility(View.GONE);
            } else {
                vhHeader.bind.llEmail.setVisibility(View.VISIBLE);
                vhHeader.bind.horizontalLine.setVisibility(View.VISIBLE);
                vhHeader.bind.tvEmail.setText(header.getEmail());
                // go to mail when email is clicked
                vhHeader.bind.tvEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse("mailto:" + header.getEmail())
                                .buildUpon()
                                .appendQueryParameter("subject", "AIR Subject: ")
                                .build();

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                        context.startActivity(Intent.createChooser(emailIntent, "Email!"));

                    }
                });

                vhHeader.bind.tvEmail.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return copy(context, header.getEmail());
                    }
                });
            }

            // set buttons visibility and click listeners
            if (isCurrentUserProfile) {
                vhHeader.bind.llDetailsOptions.setVisibility(View.GONE);
                vhHeader.bind.ivBack.setVisibility(View.GONE);
            } else {
                vhHeader.bind.btEditProfile.setVisibility(View.GONE);
                if (header.isAdded()) {
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

        }
        // populate social media cards
        else if (holder instanceof VHSocialMedia) {
            SocialMedia socialMedia = socialMedias.get(position - 1);
            VHSocialMedia vhSocialMedia = (VHSocialMedia) holder;
            String username = socialMedia.getUsername();
            if (socialMedia.getName().equals("Twitter") || socialMedia.getName().equals("Instagram")) {
                username = "@" + username;
            }
            vhSocialMedia.bind.tvUsername.setText(username);
            vhSocialMedia.bind.ivIcon.setImageDrawable(SocialMediaUtils.getIconDrawable(context, socialMedia));
            // hide horizontal line for last element
            if (position == socialMedias.size()) {
                vhSocialMedia.bind.horizontalLine.setVisibility(View.INVISIBLE);
            }
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
            default:
                return TYPE_SOCIAL_MEDIA;
        }
    }

    // increasing itemCount by difference to account for header and contact
    @Override
    public int getItemCount() {
        return socialMedias.size() + 1;
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
            bind.ivBack.setOnClickListener(this);
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
                            header.setAdded(true);
                            notifyItemChanged(0);
                        }
                    });
                    break;
                case R.id.btEditProfile:
                    onFragmentChangeListener.launchEditProfile();
                    break;
                case R.id.btDeleteProfile:
                    onFragmentChangeListener.deleteAccount();
                    break;
                case R.id.ivBack:
                    onFragmentChangeListener.onDetailsBackPressed();
                    break;
            }
        }
    }

    private boolean copy(Context context, String msg) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(msg);
        Toast.makeText(context, "Copied: " + clipboard.getPrimaryClip().toString(), Toast.LENGTH_SHORT).show();
        return true;
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
            int position = getAdapterPosition() - 1;
            String url = socialMedias.get(position).getProfileUrl();
            Log.d("PROFILEADAPTER", "clicked link: " + url);
            onFragmentChangeListener.launchUrlView(url);
        }
    }
}
