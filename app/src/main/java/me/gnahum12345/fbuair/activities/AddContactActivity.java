package me.gnahum12345.fbuair.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import me.gnahum12345.fbuair.R;

public class AddContactActivity extends AppCompatActivity {

    // placeholder users to add
    JSONObject jsonUser1;
    {
        try {
            // populate fields
            jsonUser1 = new JSONObject()
                    .put("firstName", "Foo")
                    .put("lastName", "Bar")
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
                    .put("firstName", "Foo")
                    .put("lastName", "Bar")
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
                    .put("firstName", "Mary")
                    .put("lastName", "Smith")
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
                    .put("firstName", "James")
                    .put("lastName", "Smith")
                    .put("phone", "4958203748")
                    .put("email", "foobar@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // views
    Button btAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // get references to views
        btAddContact = findViewById(R.id.btAddContact);

        // add new contact when button is clicked
        btAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AddContact(jsonUser4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public final static int ADD_CONTACT_REQUEST = 1;

    void AddContact(JSONObject user) throws JSONException {
        // get available user info
        String firstName = user.getString("firstName");
        String lastName = user.getString("lastName");
        String phone = user.getString("phone");
        String email = user.getString("email");

        // create intent to create new contact
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        // insert user's info into intent
        intent.putExtra(ContactsContract.Intents.Insert.NAME, firstName + " " + lastName)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                // insert email and phone types
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                // go back to app after saving the contact
                // todo - should user even have to edit/press save?
                .putExtra("finishActivityOnSaveCompleted", true);

        // send the intent
        startActivityForResult(intent, ADD_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
        }
    }
}
