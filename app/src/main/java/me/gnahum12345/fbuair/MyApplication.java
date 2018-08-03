package me.gnahum12345.fbuair;

import android.app.Application;
import android.content.BroadcastReceiver;
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
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.services.ConnectionService;
import me.gnahum12345.fbuair.services.MyBroadcastReceiver;


public class MyApplication extends Application {

    BroadcastReceiver br;

    @Override
    public void onCreate() {
        super.onCreate();
        AirNotificationManager.getInstance().setContext(getApplicationContext());
        MyUserManager.getInstance().setContext(getApplicationContext());
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        br = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);

        // Connection...
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(br);
    }
}
