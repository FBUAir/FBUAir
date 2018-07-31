package me.gnahum12345.fbuair;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.AuthorizationUIController;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;

public class GithubClient {

    private static GithubClient single_instance = null;
    private OAuthManager oauthManager;
    private final String CALLBACK_URL =  "https://fbuair.redirect";

    private GithubClient() { }

    public static GithubClient getInstance()  {
        if (single_instance == null) {
            single_instance = new GithubClient();
        }
        return single_instance;
    }

    public void authorize(Context context, FragmentManager fragmentManager, OAuthManager.OAuthCallback<Credential> callback) {
        oauthManager = new OAuthManager(getFlow(context), getController(fragmentManager));
        try {
            oauthManager.authorizeImplicitly("userId", callback, null);
        } catch (Exception e) {
            Log.e("authorize", e.getLocalizedMessage());
        }
    }

    OAuthManager.OAuthCallback<Credential> getCallback() {
        return future -> {
            try {
                Credential credential = future.getResult();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // make API queries with credential.getAccessToken()
        };
    }


    AuthorizationFlow getFlow(Context context) {
        final String CLIENT_ID = context.getResources().getString(R.string.github_client_id);
        final String CLIENT_SECRET = context.getResources().getString(R.string.github_client_secret);
        final String AUTHORIZATON_URL = "https://github.com/login/oauth/authorize";
        final String ENCODED_URL = "https://github.com/login/oauth/access_token";
        SharedPreferencesCredentialStore credentialStore =
                new SharedPreferencesCredentialStore(context,
                        "github", new JacksonFactory());
        AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                AndroidHttp.newCompatibleTransport(),
                new JacksonFactory(),
                new GenericUrl(ENCODED_URL),
                new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                CLIENT_ID,
                AUTHORIZATON_URL);
        builder.setCredentialStore(credentialStore);
        return builder.build();
    }


    AuthorizationUIController getController(FragmentManager fragmentManager) {
        return new DialogFragmentController(fragmentManager) {

            @Override
            public String getRedirectUri() throws IOException {
                return CALLBACK_URL;
            }

            @Override
            public boolean isJavascriptEnabledForWebView() {
                return true;
            }

            @Override
            public boolean disableWebViewCache() {
                return false;
            }

            @Override
            public boolean removePreviousCookie() {
                return false;
            }
        };
    }
}
