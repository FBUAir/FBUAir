package me.gnahum12345.fbuair;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import me.gnahum12345.fbuair.activities.MainActivity;
import retrofit2.Call;

public class TwitterClient extends TwitterAuthClient {

    // static variable single_instance of type Singleton
    private static TwitterClient single_instance = null;

    private TwitterClient() { }

    public static TwitterClient getInstance()  {
        if (single_instance == null) {
            single_instance = new TwitterClient();
        }
        return single_instance;
    }

    public void loginTwitter(Activity activity, Callback<TwitterSession> callback) {
        // if user's not authenticated already, send them to authentication
        if (getTwitterSession() == null) {
            authorize(activity, callback);
        }
    }

    public void logoutTwitter() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    private TwitterSession getTwitterSession() {
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        return session;
    }

    public void fetchTwitterUser(Callback<User> callback) {
        //initialize twitter api client
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        // make request for user credentials
        Call<User> call = twitterApiClient.getAccountService().verifyCredentials(false, true, false);
        call.enqueue(callback);
    }
}

