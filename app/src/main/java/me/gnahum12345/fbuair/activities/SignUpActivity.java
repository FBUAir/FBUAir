package me.gnahum12345.fbuair.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import me.gnahum12345.fbuair.clients.GithubClient;
import me.gnahum12345.fbuair.clients.LinkedInClient;
import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.clients.TwitterClient;
import me.gnahum12345.fbuair.databinding.ActivitySignUpBinding;
import me.gnahum12345.fbuair.fragments.SignUpContactFragment;
import me.gnahum12345.fbuair.fragments.SignUpSocialMediaFragment;
import me.gnahum12345.fbuair.fragments.UrlFragment;
import me.gnahum12345.fbuair.fragments.ValidateProfileFragment;
import me.gnahum12345.fbuair.fragments.WelcomeFragment;
import me.gnahum12345.fbuair.interfaces.OnRequestOAuthListener;
import me.gnahum12345.fbuair.interfaces.OnSignUpScreenChangeListener;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.utils.Utils.CURRENT_USER_KEY;
import static me.gnahum12345.fbuair.utils.Utils.PREFERENCES_FILE_NAME_KEY;

public class SignUpActivity extends AppCompatActivity implements OnSignUpScreenChangeListener,
        OnRequestOAuthListener {
    //TODO - HANDLE NOT HAVING SMS PERMISSIONS, FACEBOOK LOGIN NOT WORKING FOR NON-TEST PEOPLE

    // user signing up
    public User user;
    // fragments to be used
    SignUpContactFragment signUpContactFragment;
    SignUpSocialMediaFragment signUpSocialMediaFragment;
    WelcomeFragment welcomeFragment;
    FragmentManager fragmentManager;

    // data binding
    ActivitySignUpBinding bind;
    // api clients
    TwitterClient twitterClient;
    LinkedInClient linkedInClient;
    GithubClient githubClient;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // skip sign up and go to discover page if user already has profile
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE_NAME_KEY,
                Context.MODE_PRIVATE);
        if (sharedPreferences.getString(CURRENT_USER_KEY, null) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        bind = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        // get api clients
        twitterClient = TwitterClient.getInstance(this);
        linkedInClient = LinkedInClient.getInstance();
        githubClient = GithubClient.getInstance(this);

        // initialize user and end all social media sessions
        user = new User();
        endAllSessions();

        // configure toolbar
        configureToolbar();

        // initialize fragments
        welcomeFragment = new WelcomeFragment();
        signUpContactFragment = new SignUpContactFragment();
        signUpSocialMediaFragment = new SignUpSocialMediaFragment();

        // show welcome screen first
        fragmentManager = getSupportFragmentManager();
        startFragment(welcomeFragment, "welcomeFragment");

    }

    // sets toolbar for sign up screens and not welcome screen
    void configureToolbar() {
        setSupportActionBar(bind.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        bind.tvTitle.setText("Create Account");
        bind.ivBack.setOnClickListener(new View.OnClickListener() {
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

    @Override
    // starts fragment to add user contact info
    public void launchSignUpContact() {
        startFragment(signUpContactFragment, "signUpContactFragment");
    }

    @Override
    // starts fragment to add social media profiles. passes in info from previous page
    public void launchSignUpSocialMedia() {
        startFragment(signUpSocialMediaFragment, "signUpSocialMediaFragment");
    }

    @Override
    public void finishUrl() {
        fragmentManager.popBackStack();
        signUpSocialMediaFragment.socialMediaAdapter.notifyDataSetChanged();
    }

    @Override
    // saves user profile and starts main activity when sign up is finished
    public void launchMainActivity() {
        UserManager userManager = UserManager.getInstance();
        userManager.commitCurrentUser(user);
        userManager.addUser(user);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // prompts user to enter social media profile url and returns true if user does so successfully
    @Override
    public void launchUrl(SocialMedia socialMedia) {
        // go to url fragment
        startFragment(UrlFragment.newInstance(socialMedia), "urlFragment");
    }

    // starts fragment to view profile on webview and confirm
    @Override
    public void launchValidateProfile(SocialMedia socialMedia) {
        startFragment(ValidateProfileFragment.newInstance(socialMedia),
                "validateProfileFragment");
    }

    // goes to appropriate screen after validating profile based on whether user confirmed
    @Override
    public void finishValidateProfile(boolean success) {
        fragmentManager.popBackStack();
        if (success) {
            fragmentManager.popBackStack();
        }
        signUpSocialMediaFragment.socialMediaAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        githubClient.onActivityResult(requestCode, resultCode, data);
        twitterClient.onActivityResult(requestCode, resultCode, data);
        linkedInClient.getSessionManager(this)
                .onActivityResult(this, requestCode, resultCode, data);
        if (mCallbackManager != null) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // authenticate twitter and add new social media on success
    @Override
    public void twitterLogin(SocialMedia socialMedia) {
        twitterClient.login(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                socialMedia.setUsername(result.data.getUserName());
                user.addSocialMedia(socialMedia);
                signUpSocialMediaFragment.socialMediaAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
            }
        });
    }

    // authenticate linked in and add new social media on success
    @Override
    public void facebookLogin(SocialMedia socialMedia) {
        if (mCallbackManager!=null){
            LoginManager.getInstance().unregisterCallback(mCallbackManager);
        }

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        user.addSocialMedia(socialMedia);
                        signUpSocialMediaFragment.socialMediaAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancel() {
                        Log.d("cancel", "cancel error");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("error", exception.getLocalizedMessage());
                        exception.printStackTrace();
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

    }

    @Override
    public void linkedInLogin(SocialMedia socialMedia) {
        // try to authenticate the user
        linkedInClient.login(this, new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // on auth success, make api request to get user's linkedIn name and profile url
                linkedInClient.getDisplayName(getBaseContext(), new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse) {
                        try {
                            JSONObject jsonResponse = apiResponse.getResponseDataAsJson();
                            socialMedia.setUsername(jsonResponse
                                    .getString("formattedName"));
                            socialMedia.setProfileUrl(jsonResponse
                                    .getString("publicProfileUrl"));
                            user.addSocialMedia(socialMedia);
                            signUpSocialMediaFragment.socialMediaAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("getDisplayName", e.getLocalizedMessage());
                        }
                    }
                    @Override
                    public void onApiError(LIApiError LIApiError) {
                        Log.e("getDisplayName", LIApiError.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.e("linkedInLogin", error.toString());
            }
        });
    }

    @Override
    public void githubLogin(SocialMedia socialMedia) {
        githubClient.authorizeAndGetUsername(SignUpActivity.this, this,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resp = new JSONObject(response);
                    socialMedia.setUsername(resp.getString("login"));
                    user.addSocialMedia(socialMedia);
                    signUpSocialMediaFragment.socialMediaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // shows dialog asking if user wants to remove added social media and removes if confirmed
    @Override
    public void showRemoveSocialMediaDialog(SocialMedia socialMedia) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.remove_social_media_confirmation)
                + " " + socialMedia.getName() + "?")
                .setTitle("Remove " + socialMedia.getName())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        // if user selects yes, remove scoial media from users profile
                        // & revoke api authorization
                        user.removeSocialMedia(socialMedia);
                        switch (socialMedia.getName()) {
                            case "Twitter":
                                twitterClient.logout();
                                break;
                            case "LinkedIn":
                                linkedInClient.logout(getBaseContext());
                                break;
                            case "Facebook":
                                // todo - facebook logout
                                break;
                            case "Github":
                                githubClient.logoutGithub();
                                break;
                        }
                        signUpSocialMediaFragment.socialMediaAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
        builder.show();
    }

    public void endAllSessions() {
        twitterClient.logout();
        linkedInClient.logout(this);
        githubClient.logoutGithub();
    }

}
