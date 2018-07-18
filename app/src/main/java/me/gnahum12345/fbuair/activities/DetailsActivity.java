package me.gnahum12345.fbuair.activities;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.gnahum12345.fbuair.FakeUsers;
import me.gnahum12345.fbuair.R;

public class DetailsActivity extends AppCompatActivity {

    // views
    // user info views
    ImageView ivImage;
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

    // current profile whose details are being views
    JSONObject user;

    // user contact IDs
    String contactId;
    String[] rawContactId;

    // codes for options after adding contact
    final static int SUCCESS = 1;
    final static int PHONE_CONFLICT = 2;
    final static int EMAIL_CONFLICT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);

        // set user (would be from intent or something, but placeholder for now)
        FakeUsers fakeUsers = new FakeUsers();
        user = fakeUsers.jsonUser8;

        // get references to views
        ivImage = findViewById(R.id.ivImage);
        tvName = findViewById(R.id.tvName);
        tvOrganization = findViewById(R.id.tvOrganization);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        btFacebook = findViewById(R.id.btFacebook);
        btAddContact = findViewById(R.id.btAddContact);
        rlContactOptions = findViewById(R.id.rlContactOptions);
        tvUndo = findViewById(R.id.tvUndo);
        tvView = findViewById(R.id.tvView);
        rlConflict = findViewById(R.id.rlConflict);
        tvViewConflict = findViewById(R.id.tvViewConflict);
        tvIgnoreConflict = findViewById(R.id.tvIgnoreConflict);
        tvConflictMessage = findViewById(R.id.tvConflictMessage);

        // get user info and display in views
        try {
            setInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // CLICK HANDLERS
        // add contact when add contact button is clicked
        btAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (noConflict(user)) {
                        addContact(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
    void setInfo() throws JSONException {
        // set views
        tvName.setText(user.getString("name"));
        tvOrganization.setText(user.getString("organization"));
        tvPhone.setText(user.getString("phone"));
        tvEmail.setText(user.getString("email"));

        // set  'add on fb button' to redirect to fb url on click
        final String facebookUrl = user.getString("facebookURL");
        btFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(facebookUrl));
                startActivity(i);
            }
        });
    }

    // adds given json user to contacts
    void addContact(JSONObject user) throws JSONException {
        // get user's info
        String name = user.getString("name");
        String phone = user.getString("phone");
        String email = user.getString("email");
        String organization = user.getString("organization");

        // start adding contact
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = ops.size();
        // set user contact fields
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
            final String[] projection = new String[] { ContactsContract.RawContacts.CONTACT_ID };
            final Cursor cursor = getContentResolver().query(results[0].uri, projection, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                contactId = String.valueOf(cursor.getLong(0));
                cursor.close();
            }
            // get created raw contact ID (for undoing)
            rawContactId = new String[] {String.valueOf(ContentUris.parseId(results[0].uri))};
            // check if merge occured and notify user
            if (merged (contactId)) {
                Toast.makeText(this, "Contact was linked with duplicate", Toast.LENGTH_LONG).show();
            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    // checks for email and phone conflicts and shows conflict options if true
    boolean noConflict(final JSONObject user) throws JSONException {
        // get user info
        final String phone = user.getString("phone");
        final String email = user.getString("email");

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
                        addContact(user);
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
                        addContact(user);
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
        String[] phoneNumberProjection = { ContactsContract.PhoneLookup._ID };
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
        String[] emailProjection = { CommonDataKinds.Email.CONTACT_ID };
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
        String[] projection = { ContactsContract.RawContacts.CONTACT_ID , ContactsContract.RawContacts._ID};
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
            }
            else {
                rlContactOptions.setVisibility(View.GONE);
                btAddContact.setVisibility(View.VISIBLE);
            }
        }
        // show options for conflict
        else {
            if (show) {
                if (optionsType == PHONE_CONFLICT) {
                    tvConflictMessage.setText(getResources().getString(R.string.phone_conflict_message));
                }
                else if (optionsType == EMAIL_CONFLICT) {
                    tvConflictMessage.setText(getResources().getString(R.string.email_conflict_message));
                }
                rlConflict.setVisibility(View.VISIBLE);
                btAddContact.setVisibility(View.GONE);
            }
            else {
                rlConflict.setVisibility(View.GONE);
                btAddContact.setVisibility(View.VISIBLE);
            }
        }
    }
}
