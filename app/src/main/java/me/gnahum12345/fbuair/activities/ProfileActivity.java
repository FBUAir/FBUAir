package me.gnahum12345.fbuair.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import me.gnahum12345.fbuair.Models.User;
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

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Name = etName.getText().toString();
                final String organization = etOrganization.getText().toString();
                final String phoneNumber = etPhoneNumber.getText().toString();
                final String email = etEmail.getText().toString();
                final String address = etAddress.getText().toString();
                final String facebookURL = etFacebookURL.getText().toString();
                try {
                    createProfile(Name, organization, phoneNumber, email, address, facebookURL);
                    addProfile(Name, organization, phoneNumber, email, address, facebookURL);
                    Toast.makeText(ProfileActivity.this, "Profile made!!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void createProfile(String Name, String organization, String phoneNumber, String email, String address, String facebookURL) throws JSONException {
        User user = new User();
        user.setName(Name);
        user.setOrganization(organization);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setAddress(address);
        user.setFacebookURL(facebookURL);
        user.toJson(user);

    }
    private void addProfile(String Name, String organization, String phoneNumber, String email, String address, String facebookURL){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        // insert user's info into intent
        intent.putExtra(ContactsContract.Intents.Insert.NAME, Name)
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
}
