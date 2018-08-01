package me.gnahum12345.fbuair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;


public class GithubClient {

    private static GithubClient single_instance = null;

    private GithubClient() { }

    public static GithubClient getInstance()  {
        if (single_instance == null) {
            single_instance = new GithubClient();
        }
        return single_instance;
    }

    private ClientAuthentication clientAuth;
    private AuthorizationService authService;
    private AuthorizationService.TokenResponseCallback callback;
    private String clientId;
    private String clientSecret;
    private Context context;
    private Response.Listener<String> successCallback;
    final String BASE_API_URL = "https://api.github.com";
    final String ACCESS_TOKEN_PARAM = "?access_token=";
    final String AUTHORIZATION_URL = "https://github.com/login/oauth/authorize";
    final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    public final int AUTH_REQUEST_CODE = 30;
    //GitHubClient client = new GitHubClient();
//client.setOAuth2Token("SlAV32hkKG");

    AuthorizationServiceConfiguration serviceConfig =
            new AuthorizationServiceConfiguration(
                    Uri.parse(AUTHORIZATION_URL), // authorization endpoint
                    Uri.parse(TOKEN_URL)); // token endpoint

    private AuthState authState = new AuthState(serviceConfig);

    public void authorizeAndGetUsername(Activity activity, Context context, Response.Listener<String> successCallback) {
        this.context = context;
        this.successCallback = successCallback;
        doAuthorization(activity);
    }

    AuthorizationRequest getAuthRequest(Context context) {
        clientId = context.getResources().getString(R.string.github_client_id);
        clientSecret = context.getResources().getString(R.string.github_client_secret);
        final Uri REDIRECT_URI = Uri.parse("githublogin://");
        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        clientId, // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        REDIRECT_URI); // the redirect URI to which the auth response is s
        return authRequestBuilder.build();
    }

    public void doAuthorization(Activity activity) {
        this.context = context;
        authService = new AuthorizationService(context);
        Intent authIntent = authService.getAuthorizationRequestIntent(getAuthRequest(context));
        activity.startActivityForResult(authIntent, AUTH_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTH_REQUEST_CODE) {
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);
            authState.update(resp, ex);
            if (resp != null) {
                clientAuth = new ClientSecretBasic(clientSecret);
                authService.performTokenRequest(
                        resp.createTokenExchangeRequest(),
                        clientAuth,
                        new AuthorizationService.TokenResponseCallback() {
                            @Override
                            public void onTokenRequestCompleted(
                                    TokenResponse resp, AuthorizationException ex) {
                                if (resp != null) {
                                    authState.update(resp, ex);
                                    getUsername(successCallback);
                                    Log.e("GITHUBCLIENT3", "tokenRequest success. token is: " + authState.getAccessToken());
                                } else {
                                    Log.e("GITHUBCLIENT3", "tokenRequest failure: " + ex.getLocalizedMessage());
                                }
                            }
                        });
            } else {
                Log.e("GITHUBCLIENT3", "onActivityResult failure: " + ex.getLocalizedMessage());
            }
        }
    }

    public void getUsername(Response.Listener<String> successCallback) {
        authState.performActionWithFreshTokens(authService, clientAuth,
                new AuthState.AuthStateAction() {
                    @Override
                    public void execute(
                            String accessToken,
                            String idToken,
                            AuthorizationException ex) {
                        if (ex != null) {
                            Log.e("GITHUBCLIENT3", "performActionWithFreshToken failure: " + ex.getLocalizedMessage());
                            return;
                        }
                        Log.e("GITHUBCLIENT3", "performActionWithFreshToken success. token is: " + authState.getAccessToken());
                        String getUsernameUrl = BASE_API_URL + "/user";
                        addVolleyRequest(getUsernameUrl, accessToken, successCallback);
                    }
                });
    }

    void addVolleyRequest(String url, String accessToken, Response.Listener<String> successCallback) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String finalUrl = url + ACCESS_TOKEN_PARAM + accessToken;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, finalUrl,
                successCallback, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GITHUBCLIENT3", "addVolleyRequest failed: " + error.getLocalizedMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void logoutGithub(Response.Listener<String> successCallback) {
        String url = BASE_API_URL + "/applications";
        addVolleyRequest(url, readAuthState(context).getAccessToken(), successCallback);
    }

    @NonNull
    public AuthState readAuthState(Context context) {
        SharedPreferences authPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String stateJson = authPrefs.getString("stateJson", null);
        if (stateJson != null) {
            try {
                return AuthState.jsonDeserialize(stateJson);
            } catch (JSONException e) {
                Log.e("GITHUBCLIENT3", "readAuthState failure: " + e.getLocalizedMessage());
                return new AuthState();
            }
        } else {
            return new AuthState();
        }
    }

    public void writeAuthState(Context context, @NonNull AuthState state) {
        SharedPreferences authPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        authPrefs.edit()
                .putString("stateJson", state.jsonSerializeString())
                .apply();
    }
}