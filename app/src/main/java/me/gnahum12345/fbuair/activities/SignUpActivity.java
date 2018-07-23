package me.gnahum12345.fbuair.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.fragments.SignUpContactFragment;
import me.gnahum12345.fbuair.fragments.SignUpSocialMediaFragment;
import me.gnahum12345.fbuair.fragments.SignUpUrlFragment;
import me.gnahum12345.fbuair.fragments.WelcomeFragment;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utilities.Utility.CURRENT_USER_KEY;
import static me.gnahum12345.fbuair.utilities.Utility.PREFERENCES_FILE_NAME_KEY;

public class SignUpActivity extends AppCompatActivity {
    // fragments to be used
    SignUpContactFragment signUpContactFragment;
    SignUpSocialMediaFragment signUpSocialMediaFragment;
    SignUpUrlFragment signUpUrlFragment;
    WelcomeFragment welcomeFragment;

    // menu items
    TextView tvTitle;
    ImageView ivBack;
    Toolbar toolbar;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // skip sign up and go to discover page if user already has profile
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE_NAME_KEY,
                Context.MODE_PRIVATE);
        if (sharedPreferences.getString(CURRENT_USER_KEY, null) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // configure toolbar
        configureToolbar();

        // initialize fragments
        welcomeFragment = new WelcomeFragment();
        signUpContactFragment = new SignUpContactFragment();
        signUpSocialMediaFragment = new SignUpSocialMediaFragment();
        signUpUrlFragment = new SignUpUrlFragment();

        // show welcome screen first
        fragmentManager = getSupportFragmentManager();
        startFragment(welcomeFragment, "welcomeFragment");
    }

    // sets toolbar for sign up screens and not welcome screen
    void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        ivBack = findViewById(R.id.ivBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText("Create Account");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.popBackStack();
            }
        });
    }

    // starts a given fragment
    void startFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag).addToBackStack(tag);
        fragmentTransaction.commit();
    }

    // starts fragment to add user contact info
    public void launchSignUpContact() {
        startFragment(signUpContactFragment, "signUpContactFragment");
    }

    // starts fragment to add social media profiles. passes in info from previous page
    public void launchSignUpSocialMedia(User user) {
        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user", Parcels.wrap(user));
        signUpSocialMediaFragment.setArguments(userBundle);
        startFragment(signUpSocialMediaFragment, "signUpSocialMediaFragment");
    }

    // starts fragment to add urls for selected platforms. passes in info from previous page
    public void launchSignUpUrl(User user, ArrayList<String> platforms) {
        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user", Parcels.wrap(user));
        userBundle.putStringArrayList("platforms", platforms);
        signUpUrlFragment.setArguments(userBundle);
        startFragment(signUpUrlFragment, "signUpUrlFragment");
    }

    // saves user profile and starts main activity when sign up is finished
    public void launchMainActivity(User user) {
        // add user json string to shared preferences for persistence
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("current_user", User.toJson(user).toString());
            editor.commit();
            // launch Main Activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
