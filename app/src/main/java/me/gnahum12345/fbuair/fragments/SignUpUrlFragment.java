package me.gnahum12345.fbuair.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.parceler.Parcels;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.SignUpActivity;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.Utils.isValidFacebookUrl;
import static me.gnahum12345.fbuair.utils.Utils.isValidInstagramUrl;
import static me.gnahum12345.fbuair.utils.Utils.isValidLinkedInUrl;

public class SignUpUrlFragment extends Fragment {
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

    public SignUpUrlFragment() {
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
        return inflater.inflate(R.layout.fragment_sign_up_url, container, false);
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
                // create profile and go to discover page if valid. if not, shows appropriate error messages
                if (isValidSocialMedia()) {
                    createProfile();
                }
            }
        });
        // create profile without social media if user presses skip
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookUrl = "";
                linkedInUrl = "";
                instagramUrl = "";
                createProfile();
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

    // clears error textviews
    void clearErrors() {
        tvFacebookError.setText("");
        tvInstagramError.setText("");
        tvLinkedInError.setText("");
    }

    // creates java object user from class vars and saves user json object to sharedpreferences
    private void createProfile() {
        // get user info from last screen
        User user;
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable("user"));
            // add social media fields to user
            user.setFacebookURL(facebookUrl);
            user.setInstagramURL(instagramUrl);
            user.setLinkedInURL(linkedInUrl);
            // save profile and launch main activity
            activity.launchMainActivity(user);
        }
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }
}
