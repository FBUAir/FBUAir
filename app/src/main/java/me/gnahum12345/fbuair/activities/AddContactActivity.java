package me.gnahum12345.fbuair.Activities;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;

public class AddContactActivity extends AppCompatActivity {

    // placeholder users to add
    JSONObject jsonUser1;
    {
        try {
            // populate fields
            jsonUser1 = new JSONObject()
                    .put("name", "Foo Bar")
                    .put("phone", "5478392306")
                    .put("email", "foobar@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate name
    JSONObject jsonUser2;
    {
        try {
            // populate fields
            jsonUser2 = new JSONObject()
                    .put("name", "Foo Bar")
                    .put("phone", "7482034937")
                    .put("email", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate number
    JSONObject jsonUser3;
    {
        try {
            // populate fields
            jsonUser3 = new JSONObject()
                    .put("name", "Mary Smith")
                    .put("phone", "5478392306")
                    .put("email", "mary@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // duplicate email
    JSONObject jsonUser4;
    {
        try {
            // populate fields
            jsonUser4 = new JSONObject()
                    .put("name", "James Smith")
                    .put("phone", "4958203748")
                    .put("email", "foobar@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // views
    Button btAddContact;
    TextView tvUndo;
    TextView tvView;
    RelativeLayout rlContactOptions;

    // User contact ID
    long contactId;
    String[] rawContactIds;

    Uri contactUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // get references to views
        btAddContact = findViewById(R.id.btAddContact);
        tvUndo = findViewById(R.id.tvUndo);
        tvView = findViewById(R.id.tvView);
        rlContactOptions = findViewById(R.id.rlContactOptions);

        // CLICK HANDLERS
        // add new contact when button is clicked
        btAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addContact(jsonUser4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // undo contact adding when "undo" button is clicked
        tvUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoContact();
            }
        });

        // go to contacts app to view newly added contact when "view contact" button is clicked
        tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewContact();
            }
        });
    }

    void addContact(JSONObject user) throws JSONException {
        // get user's info
        String name = user.getString("name");
        String phone = user.getString("phone");
        String email = user.getString("email");

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
            showOptions(true);
            // create the new contact
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            // get contact ID (for viewing)
            final String[] projection = new String[] { ContactsContract.RawContacts.CONTACT_ID };
            final Cursor cursor = getContentResolver().query(results[0].uri, projection, null, null, null);
            cursor.moveToNext();
            contactId = cursor.getLong(0);
            cursor.close();
            // get raw contact IDs for each operation (for undoing)
            rawContactIds = new String[results.length];
            for (int i = 0; i < results.length; i++) {
                rawContactIds[i] = String.valueOf(ContentUris.parseId(results[i].uri));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    void undoContact() {
        // hide undo and view options
        showOptions(false);
        // delete raw contact
        ArrayList ops = new ArrayList();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts._ID + "=?", rawContactIds)
                .build());
    }

    void viewContact() {
        // create intent to view newly added contact
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(contactUri);
        startActivity(intent);
    }

/*        // create intent to create new contact
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        // insert user's info into intent
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                // insert email and phone types
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                // go back to app after saving the contact
                .putExtra("finishActivityOnSaveCompleted", true);

        // send the intent
        startActivityForResult(intent, ADD_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
        }
    }*/

    // show options after adding contact; hide after undoing contact
    public void showOptions(boolean flag) {
        if (flag) {
            rlContactOptions.setVisibility(View.VISIBLE);
            btAddContact.setVisibility(View.GONE);
        }
        else {
            rlContactOptions.setVisibility(View.GONE);
            btAddContact.setVisibility(View.VISIBLE);
        }
    }
}
