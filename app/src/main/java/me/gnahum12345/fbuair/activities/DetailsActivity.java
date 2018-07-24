package me.gnahum12345.fbuair.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;

public class DetailsActivity extends AppCompatActivity {

    // codes for options after adding contact
    final static int SUCCESS = 1;
    final static int PHONE_CONFLICT = 2;
    final static int EMAIL_CONFLICT = 3;
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
    String name;
    String email;
    String organization;
    String phone;
    Bitmap profileImage;
    String facebookUrl;
    String linkedInUrl;
    String instagramUrl;
    // user contact IDs
    String contactId;
    String[] rawContactId;
    // whether user granted Contacts permissions
    boolean permissionGranted;

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

        // display selected user's info
        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        setInfo();


        // CLICK HANDLERS
        // add contact when add contact button is clicked if they have required permissions
        btAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPermissionsIfNeeded()) {
                    try {
                        if (noConflict()) {
                            addContact();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // undo contact adding when "undo" button is clicked
        tvUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undo();
            }
        });

        // go to contacts app to view newly added contact when "view contact" button is clicked
        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewContact(contactId);
            }
        });
    }

    // sets views to display user info
    void setInfo() {
        // get user's info
        name = user.getName();
        email = user.getEmail();
        organization = user.getOrganization();
        profileImage = user.getProfileImage();
        facebookUrl = user.getFacebookURL();
        instagramUrl = user.getInstagramURL();
        linkedInUrl = user.getLinkedInURL();
        // set views to display info
        tvName.setText(name);
        tvOrganization.setText(organization);
        tvPhone.setText(phone);
        tvEmail.setText(email);
        ivProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_face));// fake profile image

        // show applicable social media buttons and set to redirect to profile URLs on click
        if (facebookUrl.isEmpty()) {
            btFacebook.setVisibility(View.INVISIBLE);
        } else {
            btFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(facebookUrl));
                    startActivity(i);
                }
            });
        }
        if (instagramUrl.isEmpty()) {
            btInstagram.setVisibility(View.INVISIBLE);
        } else {
            btInstagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(instagramUrl));
                    startActivity(i);
                }
            });
        }
        if (linkedInUrl.isEmpty()) {
            btLinkedIn.setVisibility(View.INVISIBLE);
        } else {
            btLinkedIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkedInUrl));
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

    // adds given json user to contacts
    void addContact() throws JSONException {
        // fake image
        Bitmap profileImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.happy_face);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profileImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] profileImageBytes = stream.toByteArray();
        profileImageBitmap.recycle();

        // start adding contact
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = ops.size();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        // add name
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        // add photo
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Photo.PHOTO, profileImageBytes)
                .build());
        // add organization
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Organization.TITLE, organization)
                .build());
        // add number
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Phone.NUMBER, phone)
                .withValue(CommonDataKinds.Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        // add email
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Email.ADDRESS, email)
                .withValue(CommonDataKinds.Email.TYPE, CommonDataKinds.Email.TYPE_HOME)
                .build());
        try {
            // display options to undo and view
            showOptions(SUCCESS, true);
            // create the new contact
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            // get created contact ID (for viewing)
            final String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID};
            final Cursor cursor = getContentResolver().query(results[0].uri, projection, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                contactId = String.valueOf(cursor.getLong(0));
                cursor.close();
            }
            // get created raw contact ID (for undoing)
            rawContactId = new String[]{String.valueOf(ContentUris.parseId(results[0].uri))};
            // check if merge occured and notify user
            if (merged(contactId)) {
                Toast.makeText(this, "Contact was linked with duplicate", Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    // checks for email and phone conflicts and shows conflict options if true
    boolean noConflict() throws JSONException {
        // find phone number conflicts
        final String phoneConflictId = getPhoneConflictId(phone);
        if (phoneConflictId != null) {
            // set conflict options click listeners (view conflict; add anyway) and show them
            showOptions(PHONE_CONFLICT, true);
            // set click listeners
            tvViewConflict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewContact(phoneConflictId);
                }
            });
            tvIgnoreConflict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        // check for more conflicts
                        showOptions(PHONE_CONFLICT, false);
                        addContact();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return false;
        }

        // find email conflicts
        final String emailConflictId = getEmailConflictId(email);
        if (emailConflictId != null) {
            // set conflict options click listeners (view conflict; add anyway) and show them
            showOptions(EMAIL_CONFLICT, true);
            tvViewConflict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewContact(emailConflictId);
                }
            });
            tvIgnoreConflict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        showOptions(EMAIL_CONFLICT, false);
                        addContact();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return false;
        }

        // return false if no conflicts
        return true;
    }

    // undoes last raw contact operations
    void undo() {
        // hide undo and view options
        showOptions(SUCCESS, false);
        // delete raw contact
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts._ID + " = ?", rawContactId)
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    // creates intent to view given contact
    void viewContact(String contactId) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(contactUri);
        startActivity(intent);
    }

    // returns ID of contact matching phone number. if doesn't exist, returns null
    public String getPhoneConflictId(String phone) {
        // get URI path for given phone number
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        // set columns to retrieve
        String[] phoneNumberProjection = {ContactsContract.PhoneLookup._ID};
        // lookup phone number
        Cursor cursor = getContentResolver().query(lookupUri, phoneNumberProjection, null, null, null);
        // if there is a result, return ID of matching contact
        if (cursor != null && cursor.moveToFirst()) {
            String id = String.valueOf(cursor.getLong(0));
            cursor.close();
            return id;
        }
        // return null if no matching contacts
        return null;
    }

    // returns ID of contact matching email address. returns null if no conflict/match
    public String getEmailConflictId(String email) {
        // get URI path for given email
        Uri lookupUri = Uri.withAppendedPath(CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(email));
        // set columns to retrieve
        String[] emailProjection = {CommonDataKinds.Email.CONTACT_ID};
        // lookup email
        Cursor cursor = getContentResolver().query(lookupUri, emailProjection, null, null, null);
        // if there is a result, return ID of matching contact
        if (cursor != null && cursor.moveToFirst()) {
            String id = String.valueOf(cursor.getLong(0));
            cursor.close();
            return id;
        }
        // return null if no matching contacts
        return null;
    }

    // checks if there are multiple raw contacts for one contact ID (for notifying user of merge)
    public boolean merged(String contactId) {
        // get URI path for given ID
        Uri lookupUri = ContactsContract.RawContacts.CONTENT_URI;
        // no columns needed
        String[] projection = {ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts._ID};
        // find where contact ID = user's contact id
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=" + contactId;
        // lookup contact ID
        Cursor cursor = getContentResolver().query(lookupUri, projection, selection, null, null);
        // if there are multiple results, ID existed before adding contact and merge occured
        if (cursor != null && cursor.getCount() > 1) {
            cursor.close();
            return true;
        }
        return false;
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
