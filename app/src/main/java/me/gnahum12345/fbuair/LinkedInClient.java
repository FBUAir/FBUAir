package me.gnahum12345.fbuair;

import android.app.Activity;
import android.content.Context;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

public class LinkedInClient {

    // static variable single_instance of type Singleton
    private static LinkedInClient single_instance = null;
    private Context applicationContext;

    private LinkedInClient() { }

    public static LinkedInClient getInstance(Context applicationContext)  {
        if (single_instance == null) {
            single_instance = new LinkedInClient();
            single_instance.applicationContext = applicationContext;
        }
        return single_instance;
    }

    public LISessionManager getSessionManager() {
        return LISessionManager.getInstance(applicationContext);
    }

    public void login(Activity activity, AuthListener callback) {
        getSessionManager().init(activity, Scope.build(Scope.R_BASICPROFILE),
                callback, true);
    }

    public void loginWithToken() {
        getSessionManager().init(getSessionManager().getSession().getAccessToken());
    }

    public void logout() {
        getSessionManager().clearSession();
    }

    public void getDisplayName(Context context, ApiListener callback) {
        loginWithToken();
        String url = "https://api.linkedin.com/v2/people/~?format=json";
        if (getSessionManager().getSession() != null) {
            APIHelper apiHelper = APIHelper.getInstance(applicationContext);
            apiHelper.getRequest(context, url, callback);
        }
    }
    public void getProfileUrl(Context context, ApiListener callback) {
        String url = "https://api.linkedin.com/v2/people/~:(public-profile-url)";
        if (getSessionManager().getSession() != null) {
            APIHelper apiHelper = APIHelper.getInstance(applicationContext);
            apiHelper.getRequest(context, url, callback);
        }
    }

}
