package me.gnahum12345.fbuair.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.ProfileAdapter;
import me.gnahum12345.fbuair.databinding.ContactAddedDialogBinding;
import me.gnahum12345.fbuair.databinding.FragmentProfileTwoBinding;
import me.gnahum12345.fbuair.interfaces.OnAddContactClickedListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.Contact;
import me.gnahum12345.fbuair.models.Header;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.ContactUtils;

public class ProfileFragmentTwo extends Fragment implements OnAddContactClickedListener {

    FragmentProfileTwoBinding bind;
    final static String ARG_UID = "uid";

    Activity activity;
    Context context;
    ArrayList<SocialMedia> socialMedias;
    ProfileAdapter profileAdapter;

    ViewGroup container;

    // request codes for permissions results
    final static int MY_PERMISSIONS_REQUEST_CONTACTS = 4;
    // whether user granted Contacts permissions
    boolean permissionGranted;
    // user contact IDs
    String contactId;
    String rawContactId;
    ContactUtils.AddContactResult addContactResult;
    User user;

    public ProfileFragmentTwo() {
        // Required empty public constructor
    }

    public static ProfileFragmentTwo newInstance(String uid) {
        ProfileFragmentTwo fragment = new ProfileFragmentTwo();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        String uid;
        boolean isCurrentUserProfile;
        // if no arguments were passed in, assume current user profile
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            user = MyUserManager.getInstance().getUser(getArguments().getString(ARG_UID));
            isCurrentUserProfile = (user.equals(MyUserManager.getInstance().getCurrentUser()));
        }
        else {
            user = MyUserManager.getInstance().getCurrentUser();
            uid = user.getId();
            isCurrentUserProfile = true;
        }
        socialMedias = user.getSocialMedias();
        Contact contact = new Contact(uid);
        Header header = new Header(uid);
        profileAdapter = new ProfileAdapter(getContext(), contact, header,
                socialMedias, isCurrentUserProfile, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bind = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_two, container, false);
        this.container = container;
        return bind.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // attach adapter and layout manager
        bind.rvRecyclerView.setAdapter(profileAdapter);
        bind.rvRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // get context
        context = getContext();

        // check whether user granted contacts permissions
        permissionGranted = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    // check for permissions before adding contact
    @Override
    public void requestAddContact(String uid) {
        User user = MyUserManager.getInstance().getUser(uid);
        if (requestPermissionsIfNeeded() || permissionGranted) {
            addContactResult = ContactUtils.findConflict(context, user);
            if (addContactResult.getResultCode() == ContactUtils.SUCCESS) {
                addContact(user);
                showContactAddedDialog();
            } else showConflictDialog();
        }
    }

    // override addContact from utilities to also show appropriate messages and options
    void addContact(User user) {
        String ids[] = ContactUtils.addContact(context, user);
        showOptions(ContactUtils.SUCCESS, true);
        contactId = ids[0];
        rawContactId = ids[1];
        if (ContactUtils.mergeOccurred(context, contactId)) {
            //Toast.makeText(context, "Contact was automatically linked with duplicate", Toast.LENGTH_LONG).show();
        }
    }

    // shows options to undo and/or view as fake snackbar at bottom
    void showContactAddedDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        LayoutInflater inflater = LayoutInflater.from(context);
        ContactAddedDialogBinding dialogBind = DataBindingUtil.inflate(inflater, R.layout.contact_added_dialog, container,
                false);
        View sheetView = dialogBind.getRoot();
        bottomSheetDialog.setContentView(sheetView);

        dialogBind.btUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUtils.undo(context, rawContactId);
                bottomSheetDialog.cancel();
            }
        });
        dialogBind.btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUtils.viewContact(context, contactId);
            }
        });
        bottomSheetDialog.show();
    }

    // shows dialog about contact duplicate w/ action options
    void showConflictDialog() {
        int messageId = addContactResult.getResultCode() == ContactUtils.EMAIL_CONFLICT ?
                R.string.email_conflict_message : R.string.phone_conflict_message;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(messageId)
                .setTitle("Contact duplicate")
                .setPositiveButton("Add anyway", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addContact(user);
                    }
                })
                .setNegativeButton("View existing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContactUtils.viewContact(context, addContactResult.getContactId());
                    }
                });
        builder.show();
    }

    // PERMISSIONS STUFF
    // show appropriate options after adding contact based on whether it was successful or there was a conflict
    public void showOptions(int resultCode, boolean show) {
        /*// show options for no conflict
        if (resultCode == ContactUtils.SUCCESS) {
            if (show) {
                bind.rlContactOptions.setVisibility(View.VISIBLE);
                bind.btAddContact.setVisibility(View.GONE);
            } else {
                bind.rlContactOptions.setVisibility(View.GONE);
                bind.btAddContact.setVisibility(View.VISIBLE);
            }
        }
        // show options for conflict
        else {
            if (show) {
                if (resultCode == ContactUtils.PHONE_CONFLICT) {
                    bind.tvConflictMessage.setText(getResources().getString(R.string.phone_conflict_message));
                } else if (resultCode == ContactUtils.EMAIL_CONFLICT) {
                    bind.tvConflictMessage.setText(getResources().getString(R.string.email_conflict_message));
                }
                bind.rlConflict.setVisibility(View.VISIBLE);
                bind.btAddContact.setVisibility(View.GONE);
            } else {
                bind.rlConflict.setVisibility(View.GONE);
                bind.btAddContact.setVisibility(View.VISIBLE);
            }
        }*/
    }

    // requests permissions if needed and returns true if permission is granted
    boolean requestPermissionsIfNeeded() {
        if (!permissionGranted) {
            requestPermissions(
                    new String[]
                            {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_CONTACTS);
            return false;
        }
        return true;
    }

    // show rationale for needing contact permissions and offer to request permissions again
    void showPermissionsRationale() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(R.string.contact_permissions_rationale)
                .setTitle("Permission Denied")
                .setPositiveButton("I'm Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .setNegativeButton("Re-Try", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.WRITE_CONTACTS},
                                MY_PERMISSIONS_REQUEST_CONTACTS);
                    }
                })
                .setCancelable(false);
        builder.show();
    }

    // result after user accepts/denies permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // set permissionsGranted variable to true if user granted all requested permissions. false otherwise.
        permissionGranted = (requestCode == MY_PERMISSIONS_REQUEST_CONTACTS
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED);
        if (!permissionGranted) {
            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
            // user checked "never ask again", show them message to go to Settings to change
            if (!showRationale) {
                showPermissionDeniedForeverDialog();
            }
            // user denied but didn't press press "never ask again". show rationale and request permission again
            else {
                showPermissionsRationale();
            }
        }
    }

    // allow user to go to settings to manually grant permissions if denied and pressed "Never show again"
    void showPermissionDeniedForeverDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Go to App Permissions in Settings to change this.")
                .setTitle("Missing Contact Permissions")
                // go to settings if user wants to
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
        builder.show();
    }
}
