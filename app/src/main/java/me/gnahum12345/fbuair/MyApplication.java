package me.gnahum12345.fbuair;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import me.gnahum12345.fbuair.callbacks.MyLifecycleHandler;
import me.gnahum12345.fbuair.managers.AirNotificationManager;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.services.MyBroadcastReceiver;


public class MyApplication extends Application {

    BroadcastReceiver br;

    @Override
    public void onCreate() {
        super.onCreate();
        AirNotificationManager.getInstance().setContext(getApplicationContext());
        UserManager.getInstance().setContext(getApplicationContext());
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);
        // Connection...

        // set up twitter.
        String CONSUMER_KEY = getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY);
        String CONSUMER_SECRET = getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(br);
    }


    public static void endAllSessions(Context context) {
        TwitterClient.getInstance().logout();
        LinkedInClient.getInstance(context).logout();
    }
}
