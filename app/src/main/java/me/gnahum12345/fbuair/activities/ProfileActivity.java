package me.gnahum12345.fbuair.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

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

        ivProfileImage = findViewById(R.id.ivProfileImage);
        etName = findViewById(R.id.etName);
        etOrganization = findViewById(R.id.etOrganization);
        etPhoneNumber = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etFacebookURL = findViewById(R.id.etFacebookURL);
        btnCheck = findViewById(R.id.btnCheck);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // add formatter to phone number field
        etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = etName.getText().toString();
                final String organization = etOrganization.getText().toString();
                final String phoneNumber = etPhoneNumber.getText().toString();
                final String email = etEmail.getText().toString();
                final String facebookURL = etFacebookURL.getText().toString();
                try {
                    // check for valid profile before submitting
                    if (isValidProfile(name, phoneNumber, email, facebookURL)) {
                        createProfile(name, organization, phoneNumber, email, facebookURL);
                        Toast.makeText(ProfileActivity.this, "Profile made!!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        String current_user = sharedpreferences.getString("current_user", null);

        if(current_user != null) {
            Toast.makeText(ProfileActivity.this, "Profile already made!", Toast.LENGTH_LONG).show();
            Log.d("MadeUser", current_user);
            Intent intent = new Intent(ProfileActivity.this, DiscoverActivity.class);
            startActivity(intent);
        }

    }

    // checks if profile is valid before submitting. if not, sets invalid fields red
    public boolean isValidProfile(String name, String phone, String email, String facebookUrl){
        boolean valid = true;
        if (name.isEmpty()) {
            etName.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            valid = false;
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
            etEmail.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            valid = false;
        }
        if (!isValidPhoneNumber(phone)) {
            etPhoneNumber.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            valid = false;
        }
        if (!facebookUrl.isEmpty() && !isValidFacebookUrl(facebookUrl)) {
            etFacebookURL.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            valid = false;
        }
        return valid;
    }

    // validity checkers
    public static boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isValidPhoneNumber(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static boolean isValidFacebookUrl(String facebookUrlString) {
        URL facebookUrl;
        try {
            facebookUrl = new URL(facebookUrlString);
            if (Patterns.WEB_URL.matcher(facebookUrlString).matches() && facebookUrl.getHost().contains("facebook")) {
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createProfile(String name, String organization, String phoneNumber, String email, String facebookURL) throws JSONException {
        User user = new User();
        user.setName(name);
        user.setOrganization(organization);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setFacebookURL(facebookURL);
        saveUserTwo(user);
    }

    private void saveUserTwo(User user) throws JSONException {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("current_user", user.toJson(user).toString());
        editor.commit();
    }
}
