package me.gnahum12345.fbuair;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String CONSUMER_KEY = getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY);
        String CONSUMER_SECRET = getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
    public static TwitterClient getTwitterClient() {
        return TwitterClient.getInstance();
    }
}
