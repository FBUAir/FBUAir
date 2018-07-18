package me.gnahum12345.fbuair.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.R;

public class ProfileActivity extends AppCompatActivity{

    ImageView ivProfileImage;
    EditText etName;
    EditText etOrganization;
    EditText etPhoneNumber;
    EditText etEmail;
    EditText etAddress;
    EditText etFacebookURL;
    Context context;
    Button btnCheck;

    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        etName = (EditText) findViewById(R.id.etName);
        etOrganization = (EditText) findViewById(R.id.etOrganization);
        etPhoneNumber = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etFacebookURL = (EditText) findViewById(R.id.etFacebookURL);
        btnCheck = (Button) findViewById(R.id.btnCheck);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = etName.getText().toString();
                final String organization = etOrganization.getText().toString();
                final String phoneNumber = etPhoneNumber.getText().toString();
                final String email = etEmail.getText().toString();
                final String address = etAddress.getText().toString();
                final String facebookURL = etFacebookURL.getText().toString();
                try {
                    createProfile(name, organization, phoneNumber, email, address, facebookURL);
                    addContact(name, organization, phoneNumber, email, address, facebookURL);
                    Toast.makeText(ProfileActivity.this, "Profile made!!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        String current_user = sharedpreferences.getString("current_user", null);

        if(current_user != null) {
            Toast.makeText(ProfileActivity.this, "Profile already made!", Toast.LENGTH_LONG).show();
            Log.d("MadeUser", current_user);
            Intent intent = new Intent(ProfileActivity.this, me.gnahum12345.fbuair.Activities.DiscoverActivity.class);
            startActivity(intent);
        }

    }

    private void createProfile(String name, String organization, String phoneNumber, String email, String address, String facebookURL) throws JSONException {
        User user = new User();
        user.setName(name);
        user.setOrganization(organization);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setAddress(address);
        user.setFacebookURL(facebookURL);
        saveUserTwo(user);
    }
    
    private void addContact(String name, String organization, String phoneNumber, String email, String address, String facebookURL){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        // insert user's info into intent
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                .putExtra(ContactsContract.Intents.Insert.COMPANY, organization)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                .putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
                .putExtra(ContactsContract.Intents.Insert.POSTAL, address)
                .putExtra(ContactsContract.Intents.Insert.NOTES, facebookURL)

                // insert email and phone types
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME);

        // send the intent
        startActivity(intent);
    }


    private void saveUserTwo(User user) throws JSONException {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("current_user", user.toJson(user).toString());
        editor.commit();
    }
}
