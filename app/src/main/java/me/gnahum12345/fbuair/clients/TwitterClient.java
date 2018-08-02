package me.gnahum12345.fbuair.clients;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import me.gnahum12345.fbuair.R;

public class TwitterClient extends TwitterAuthClient {

    // static variable single_instance of type Singleton
    private static TwitterClient single_instance = null;

    private TwitterClient() { }

    public static TwitterClient getInstance(Context context)  {
        if (single_instance == null) {
            initializeTwitter(context);
            single_instance = new TwitterClient();
        }
        return single_instance;
    }

    private static void initializeTwitter(Context context) {
        String CONSUMER_KEY = context.getResources().getString
                (R.string.com_twitter_sdk_android_CONSUMER_KEY);
        String CONSUMER_SECRET = context.getResources().getString
                (R.string.com_twitter_sdk_android_CONSUMER_SECRET);
        TwitterConfig config = new TwitterConfig.Builder(context.getApplicationContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    public void login(Activity activity, Callback<TwitterSession> callback) {
        // if user's not authenticated already, send them to authentication
        if (getTwitterSession() == null) {
            authorize(activity, callback);
        }
    }

    public void logout() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    private TwitterSession getTwitterSession() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    public String getDisplayName() {
        return getTwitterSession().getUserName();
/*        //initialize twitter api client
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        // make request for user credentials
        Call<User> call = twitterApiClient.getAccountService().verifyCredentials(false, true, false);
        call.enqueue(callback);*/
    }
}

