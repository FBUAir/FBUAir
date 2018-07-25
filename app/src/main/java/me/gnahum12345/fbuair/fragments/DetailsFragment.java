package me.gnahum12345.fbuair.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.parceler.Parcels;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.databinding.FragmentDetailsBinding;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.ContactUtils;

public class DetailsFragment extends Fragment {

    public DetailsFragment() {
        // Required empty public constructor
    }

    Activity activity;
    Context context;
    // request codes for permissions results
    final static int MY_PERMISSIONS_REQUEST_CONTACTS = 4;
    // whether user granted Contacts permissions
    boolean permissionGranted;
    // current profile and info
    User user;
    // user contact IDs
    String contactId;
    String rawContactId;
    ContactUtils.AddContactResult addContactResult;

    // data bind
    FragmentDetailsBinding bind;

    public static DetailsFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        bind = DataBindingUtil.inflate
                (inflater, R.layout.fragment_details, container, false);
        View rootView = bind.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get references to activity and context
        activity = getActivity();
        context = getContext();

        // check whether user granted contacts permissions
        permissionGranted = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;

        // get passed in user
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable("user"));
            setInfo();
        }

        // CLICK HANDLERS
        // add contact when add contact button is clicked if they have required permissions
        bind.btAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPermissionsIfNeeded()) {
                    addContactResult = ContactUtils.findConflict(getContext(), user);
                    if (addContactResult.getResultCode() == ContactUtils.SUCCESS) {
                        addContact(user);
                    }
                }
            }
        });

        // undo contact adding when "undo" button is clicked
        bind.tvUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUtils.undo(context, rawContactId);
                showOptions(ContactUtils.SUCCESS, false);
            }
        });

        // go to contacts app to view newly added contact when "view contact" button is clicked
        bind.tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUtils.viewContact(context, contactId);
            }
        });

        // add user despite conflict
        bind.tvIgnoreConflict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions(ContactUtils.PHONE_CONFLICT, false);
                showOptions(ContactUtils.EMAIL_CONFLICT, false);
                addContact(user);
                // display options to undo and view
                showOptions(ContactUtils.SUCCESS, true);
            }
        });

        // view phone/email conflict
        bind.tvViewConflict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUtils.viewContact(context, addContactResult.getContactId());
            }
        });
    }

    // sets views to display user info
    void setInfo() {
        bind.tvName.setText(user.getName());
        bind.tvOrganization.setText(user.getOrganization());
        bind.tvPhone.setText(user.getPhoneNumber());
        bind.tvEmail.setText(user.getEmail());
        bind.ivProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_face));// fake profile image

        // show applicable social media buttons and set to redirect to profile URLs on click
        if (user.getFacebookURL().isEmpty()) {
            bind.btFacebook.setVisibility(View.INVISIBLE);
        } else {
            bind.btFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(user.getFacebookURL()));
                    startActivity(i);
                }
            });
        }
        if (user.getInstagramURL().isEmpty()) {
            bind.btInstagram.setVisibility(View.INVISIBLE);
        } else {
            bind.btInstagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(user.getInstagramURL()));
                    startActivity(i);
                }
            });
        }
        if (user.getLinkedInURL().isEmpty()) {
            bind.btLinkedIn.setVisibility(View.INVISIBLE);
        } else {
            bind.btLinkedIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(user.getLinkedInURL()));
                    startActivity(i);
                }
            });
        }
    }

    // override addContact from utilities to also show appropriate messages and options
    void addContact(User user) {
        String ids[] = ContactUtils.addContact(context, user);
        showOptions(ContactUtils.SUCCESS, true);
        contactId = ids[0];
        rawContactId = ids[1];
        if (ContactUtils.mergeOccurred(context, contactId)) {
            Toast.makeText(context, "Contact was linked with duplicate", Toast.LENGTH_LONG).show();
        }
    }

    // PERMISSIONS STUFF
    // show appropriate options after adding contact based on whether it was successful or there was a conflict
    public void showOptions(int resultCode, boolean show) {
        // show options for no conflict
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
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
