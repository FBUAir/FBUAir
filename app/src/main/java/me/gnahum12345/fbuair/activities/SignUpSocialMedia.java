package me.gnahum12345.fbuair.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;

public class SignUpSocialMedia extends AppCompatActivity {
    // views
    EditText etFacebookUrl;
    EditText etInstagramUrl;
    EditText etLinkedInUrl;
    TextView tvFacebookError;
    TextView tvInstagramError;
    TextView tvLinkedInError;
    Button btSubmit;

    // user info
    String name;
    String email;
    String organization;
    String phone;
    Bitmap profileImage;
    String facebookUrl;
    String linkedInUrl;
    String instagramUrl;

    // preferences filename
    String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_social_media);

        // get references to views
        etFacebookUrl = findViewById(R.id.etFacebookUrl);
        etLinkedInUrl = findViewById(R.id.etLinkedInUrl);
        etInstagramUrl = findViewById(R.id.etInstagramUrl);
        tvFacebookError = findViewById(R.id.tvFacebookError);
        tvInstagramError = findViewById(R.id.tvInstagramError);
        tvLinkedInError = findViewById(R.id.tvLinkedInError);
        btSubmit = findViewById(R.id.btSubmit);

        // hide error messages
        clearErrors();

        // get info from last screen
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        organization = intent.getStringExtra("organization");
        phone = intent.getStringExtra("phone");
        email = intent.getStringExtra("email");
        profileImage = SignUpContact.profileImage;

        // create and save profile if valid
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set profile URLs to be what user submitted
                facebookUrl = etFacebookUrl.getText().toString();
                linkedInUrl = etLinkedInUrl.getText().toString();
                instagramUrl = etInstagramUrl.getText().toString();
                try {
                    // create profile and go to discover page if valid. if not, shows appropriate error messages
                    if (isValidSocialMedia()) {
                        createProfile();
                        Intent intent = new Intent(getBaseContext(), DiscoverActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // checks that all profile URLs are valid
    boolean isValidSocialMedia() {
        // clear previous errors
        clearErrors();
        // check fields and set appropriate error messages
        boolean valid = true;
        if (!facebookUrl.isEmpty() && !isValidFacebookUrl(facebookUrl)) {
            tvFacebookError.setText(getResources().getString(R.string.bad_fb_url_error));
            valid = false;
        }
        if (!linkedInUrl.isEmpty() && !isValidLinkedInUrl(linkedInUrl)) {
            tvLinkedInError.setText(getResources().getString(R.string.bad_linked_in_url_error));
            valid = false;
        }
        if (!instagramUrl.isEmpty() && !isValidInstagramUrl(instagramUrl)) {
            tvInstagramError.setText(getResources().getString(R.string.bad_instagram_url_error));
            valid = false;
        }
        return valid;
    }

    // checks for valid profile URLs
    public static boolean isValidFacebookUrl(String facebookUrlString) {
        return (Patterns.WEB_URL.matcher(facebookUrlString).matches() && facebookUrlString.toLowerCase().contains("facebook"));
    }

    public static boolean isValidInstagramUrl(String instagramUrlString) {
        return (Patterns.WEB_URL.matcher(instagramUrlString).matches() && instagramUrlString.toLowerCase().contains("instagram"));
    }

    public static boolean isValidLinkedInUrl(String linkedInUrlString) {
        return (Patterns.WEB_URL.matcher(linkedInUrlString).matches() && linkedInUrlString.toLowerCase().contains("linkedin"));
    }

    // clears error textviews
    void clearErrors() {
        tvFacebookError.setText("");
        tvInstagramError.setText("");
        tvLinkedInError.setText("");
    }

    // creates java object user from class vars and saves json of user to sharedpreferences
    private void createProfile() throws JSONException {
        User user = new User();
        user.setName(name);
        user.setOrganization(organization);
        user.setPhoneNumber(phone);
        user.setEmail(email);
        user.setProfileImage(profileImage);
        user.setFacebookURL(facebookUrl);
        user.setInstagramURL(instagramUrl);
        user.setLinkedInURL(linkedInUrl);

        // add user json string to shared preferences for persistence
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("current_user", User.toJson(user).toString());
        editor.commit();
    }

}
