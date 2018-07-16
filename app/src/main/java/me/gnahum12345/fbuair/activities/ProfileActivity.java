package me.gnahum12345.fbuair;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import me.gnahum12345.fbuair.Models.User;

public class ProfileActivity extends AppCompatActivity{

    ImageView ivProfileImage;
    EditText etFirstName;
    EditText etLastName;
    EditText etOrganization;
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
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etOrganization = (EditText) findViewById(R.id.etOrganization);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etFacebookURL = (EditText) findViewById(R.id.etFacebookURL);
        btnCheck = (Button) findViewById(R.id.btnCheck);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String firstName = etFirstName.getText().toString();
                final String lastName = etLastName.getText().toString();
                final String organization = etOrganization.getText().toString();
                final String email = etEmail.getText().toString();
                final String address = etAddress.getText().toString();
                final String facebookURL = etFacebookURL.getText().toString();
                try {
                    createProfile(firstName, lastName, organization, email, address, facebookURL);
                    Toast.makeText(ProfileActivity.this, "Profile made!!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });

    }

    private void createProfile(String firstName, String lastName, String organization, String email, String address, String facebookURL) throws JSONException {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setOrganization(organization);
        user.setEmail(email);
        user.setAddress(address);
        user.setFacebookURL(facebookURL);
        user.toJson(user);

    }
}
