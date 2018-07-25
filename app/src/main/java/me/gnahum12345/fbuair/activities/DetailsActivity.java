package me.gnahum12345.fbuair.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.ContactUtils;

import static me.gnahum12345.fbuair.utils.ContactUtils.*;

public class DetailsActivity extends AppCompatActivity {

    // request codes for permissions results
    final static int MY_PERMISSIONS_REQUEST_CONTACTS = 4;
    // user info views
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvOrganization;
    TextView tvPhone;
    TextView tvEmail;
    // add contact views
    Button btAddContact;
    RelativeLayout rlContactOptions;
    TextView tvUndo;
    TextView tvView;
    RelativeLayout rlConflict;
    TextView tvViewConflict;
    TextView tvIgnoreConflict;
    TextView tvConflictMessage;
    // add on social media views
    Button btFacebook;
    Button btInstagram;
    Button btLinkedIn;
    // current profile and info
    User user;
    // user contact IDs
    String contactId;
    String rawContactId;
    // whether user granted Contacts permissions
    boolean permissionGranted;
    AddContactResult addContactResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get references to views
        ivProfileImage = findViewById(R.id.ivImage);
        tvName = findViewById(R.id.tvName);
        tvOrganization = findViewById(R.id.tvOrganization);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        btFacebook = findViewById(R.id.btFacebook);
        btInstagram = findViewById(R.id.btInstagram);
        btLinkedIn = findViewById(R.id.btLinkedIn);
        btAddContact = findViewById(R.id.btAddContact);
        rlContactOptions = findViewById(R.id.rlContactOptions);
        tvUndo = findViewById(R.id.tvUndo);
        tvView = findViewById(R.id.tvView);
        rlConflict = findViewById(R.id.rlConflict);
        tvViewConflict = findViewById(R.id.tvViewConflict);
        tvIgnoreConflict = findViewById(R.id.tvIgnoreConflict);
        tvConflictMessage = findViewById(R.id.tvConflictMessage);

        // check whether user granted contacts permissions
        permissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;

        // get passed in user
        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        setInfo();

        // CLICK HANDLERS
        // add contact when add contact button is clicked if they have required permissions
        btAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPermissionsIfNeeded()) {
                    addContactResult = findConflict(getBaseContext(), user);
                    if (addContactResult.getResultCode() == SUCCESS) {
                        addContact(user);
                    }
                }
            }
        });

        // undo contact adding when "undo" button is clicked
        tvUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undo(getBaseContext(), rawContactId);
                showOptions(SUCCESS, false);
            }
        });

        // go to contacts app to view newly added contact when "view contact" button is clicked
        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewContact(getBaseContext(), contactId);
            }
        });

        // add user despite conflict
        tvIgnoreConflict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions(PHONE_CONFLICT, false);
                showOptions(EMAIL_CONFLICT, false);
                addContact(user);
                // display options to undo and view
                showOptions(SUCCESS, true);
            }
        });

        // view phone/email conflict
        tvViewConflict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewContact(getBaseContext(), addContactResult.getContactId());
            }
        });
    }

    // sets views to display user info
    void setInfo() {
        tvName.setText(user.getName());
        tvOrganization.setText(user.getOrganization());
        tvPhone.setText(user.getPhoneNumber());
        tvEmail.setText(user.getEmail());
        ivProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_face));// fake profile image

        // show applicable social media buttons and set to redirect to profile URLs on click
        if (user.getFacebookURL().isEmpty()) {
            btFacebook.setVisibility(View.INVISIBLE);
        } else {
            btFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(user.getFacebookURL()));
                    startActivity(i);
                }
            });
        }
        if (user.getInstagramURL().isEmpty()) {
            btInstagram.setVisibility(View.INVISIBLE);
        } else {
            btInstagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(user.getInstagramURL()));
                    startActivity(i);
                }
            });
        }
        if (user.getLinkedInURL().isEmpty()) {
            btLinkedIn.setVisibility(View.INVISIBLE);
        } else {
            btLinkedIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(user.getLinkedInURL()));
                    startActivity(i);
                }
            });
        }
    }

    // requests permissions if needed and returns true if permission is granted
    boolean requestPermissionsIfNeeded() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_CONTACTS);
            return false;
        }
        return true;
    }

    // show rationale for needing contact permissions and offer to request permissions again
    void showPermissionsRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        ActivityCompat.requestPermissions(DetailsActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    // override addContact from utilities to also show appropriate messages and options
    void addContact(User user) {
        String ids[] = ContactUtils.addContact(this, user);
        showOptions(SUCCESS, true);
        contactId = ids[0];
        rawContactId = ids[1];
        if (mergeOccurred(this, contactId)) {
            Toast.makeText(this, "ContactUtils was linked with duplicate", Toast.LENGTH_LONG).show();
        }
    }

    // show appropriate options after adding contact based on whether it was successful or there was a conflict
    public void showOptions(int optionsType, boolean show) {
        // show options for no conflict
        if (optionsType == SUCCESS) {
            if (show) {
                rlContactOptions.setVisibility(View.VISIBLE);
                btAddContact.setVisibility(View.GONE);
            } else {
                rlContactOptions.setVisibility(View.GONE);
                btAddContact.setVisibility(View.VISIBLE);
            }
        }
        // show options for conflict
        else {
            if (show) {
                if (optionsType == PHONE_CONFLICT) {
                    tvConflictMessage.setText(getResources().getString(R.string.phone_conflict_message));
                } else if (optionsType == EMAIL_CONFLICT) {
                    tvConflictMessage.setText(getResources().getString(R.string.email_conflict_message));
                }
                rlConflict.setVisibility(View.VISIBLE);
                btAddContact.setVisibility(View.GONE);
            } else {
                rlConflict.setVisibility(View.GONE);
                btAddContact.setVisibility(View.VISIBLE);
            }
        }
    }
}
