package me.gnahum12345.fbuair.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import me.gnahum12345.fbuair.R;

public class AddContactActivity extends AppCompatActivity {

    // placeholder user to add
    JSONObject jsonUser;

    {
        try {
            // populate fields
            jsonUser = new JSONObject()
                    .put("firstName", "Foo")
                    .put("lastName", "Bar")
                    .put("phone", "5478392306")
                    .put("email", "foobar@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
    }

    void AddContact(JSONObject user) throws JSONException {
        // get available user info
        // todo - empty fields "" or null?
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
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME);
    }
}
