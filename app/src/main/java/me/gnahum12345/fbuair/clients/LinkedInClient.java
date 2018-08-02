package me.gnahum12345.fbuair.clients;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

public class LinkedInClient {

    // static variable single_instance of type Singleton
    private static LinkedInClient single_instance = null;

    private LinkedInClient() { }

    public static LinkedInClient getInstance()  {
        if (single_instance == null) {
            single_instance = new LinkedInClient();
        }
        return single_instance;
    }

    public LISessionManager getSessionManager(Context context) {
        return LISessionManager.getInstance(context.getApplicationContext());
    }

    public void login(Activity activity, AuthListener callback) {
        getSessionManager(activity.getApplicationContext()).init(activity, buildScope(),
                callback, true);
    }

    public void logout(Context context) {
        getSessionManager(context).clearSession();
    }

    public void getDisplayName(Context context, ApiListener callback) {
        if (getSessionManager(context).getSession().isValid()) {
            String url = "https://api.linkedin.com/v1/people/~:" +
                    "(formatted-name,public-profile-url)?format=json";
            APIHelper apiHelper = APIHelper.getInstance(context.getApplicationContext());
            apiHelper.getRequest(context, url, callback);
        }
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
    }

}
