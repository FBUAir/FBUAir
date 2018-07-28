package me.gnahum12345.fbuair;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

public class LinkedInClient {

    // static variable single_instance of type Singleton
    private static LinkedInClient single_instance = null;
    private static Context applicationContext;

    private LinkedInClient() { }

    public static LinkedInClient getInstance(Context applicationContext1)  {
        if (single_instance == null) {
            single_instance = new LinkedInClient();
            applicationContext = applicationContext1;
        }
        return single_instance;
    }

    public LISessionManager getSessionManager() {
        return LISessionManager.getInstance(applicationContext);
    }

    public void login(Activity activity, AuthListener authListener) {
        getSessionManager().init(activity,Scope.build(Scope.R_BASICPROFILE),
                authListener, true);
    }

    public void logout() {
        getSessionManager().clearSession();
    }

    public void getDisplayName(Context context, ApiListener apiListener) {
        String url = "https://api.linkedin.com/v2/people/~:(id,formatted-name)";
        APIHelper apiHelper = APIHelper.getInstance(applicationContext);
        apiHelper.getRequest(context, url, apiListener);
    }
    public void getProfileUrl(Context context, ApiListener apiListener) {
        String url = "https://api.linkedin.com/v2/people/~:(public-profile-url)";
        APIHelper apiHelper = APIHelper.getInstance(applicationContext);
        apiHelper.getRequest(applicationContext, url, apiListener);
    }

}
