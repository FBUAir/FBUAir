package me.gnahum12345.fbuair;

import android.app.Activity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import me.gnahum12345.fbuair.activities.MainActivity;
import retrofit2.Call;

public class TwitterClient {

    // static variable single_instance of type Singleton
    private static TwitterClient single_instance = null;

    private TwitterClient() { }

    public static TwitterClient getInstance()  {
        if (single_instance == null)
            single_instance = new TwitterClient();

        return single_instance;
    }

    TwitterAuthClient client = new TwitterAuthClient();

    public void customLoginTwitter(Activity activity, Callback<TwitterSession> callback) {
        //check if user is already authenticated or not
        if (getTwitterSession() == null) {

            //if user is not authenticated start authenticating
            client.authorize(activity, callback);
        }
    }

    public void fetchUsername(Callback<User> callback) {
        //initialize twitter api client
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        // make request for user credentials
        Call<User> call = twitterApiClient.getAccountService().verifyCredentials(false, false, false);
        call.enqueue(callback);
    }

    private TwitterSession getTwitterSession() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }
}

