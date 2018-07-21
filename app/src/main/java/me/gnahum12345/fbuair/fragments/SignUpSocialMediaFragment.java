package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utilities.Utility.PREFERENCES_FILE_NAME_KEY;

public class SignUpSocialMediaFragment extends Fragment {
    // views
    EditText etFacebookUrl;
    EditText etInstagramUrl;
    EditText etLinkedInUrl;
    TextView tvFacebookError;
    TextView tvInstagramError;
    TextView tvLinkedInError;
    Button btSubmit;
    TextView tvSkip;

    // user's profile URLs
    String facebookUrl;
    String linkedInUrl;
    String instagramUrl;

    SignUpActivity activity;

    public SignUpSocialMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_social_media, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get reference to activity
        activity = (SignUpActivity) getActivity();

        // get references to views
        etFacebookUrl = view.findViewById(R.id.etFacebookUrl);
        etLinkedInUrl = view.findViewById(R.id.etLinkedInUrl);
        etInstagramUrl = view.findViewById(R.id.etInstagramUrl);
        tvFacebookError = view.findViewById(R.id.tvFacebookError);
        tvInstagramError = view.findViewById(R.id.tvInstagramError);
        tvLinkedInError = view.findViewById(R.id.tvLinkedInError);
        tvSkip = view.findViewById(R.id.tvSkip);
        btSubmit = view.findViewById(R.id.btSubmit);

        // hide error messages
        clearErrors();

        // create and save profile if valid when user presses submit
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        // create profile without social media if user presses skip
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    facebookUrl = "";
                    linkedInUrl = "";
                    instagramUrl = "";
                    createProfile();
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

    // creates java object user from class vars and saves user json object to sharedpreferences
    private void createProfile() throws JSONException {
        // get info from last screen
        Bundle userInfoBundle = getArguments();
        String name = userInfoBundle.getString("name");
        String organization = userInfoBundle.getString("organization");
        String phone = userInfoBundle.getString("phone");
        String email = userInfoBundle.getString("email");
        Bitmap profileImage = activity.profileImage;

        // create user object out of info
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
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("current_user", User.toJson(user).toString());
        editor.commit();

        // launch Main Activity
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        activity.finish();
    }
}
