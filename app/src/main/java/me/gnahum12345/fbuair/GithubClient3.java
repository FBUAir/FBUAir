package me.gnahum12345.fbuair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import org.apache.http.auth.AUTH;
import org.json.JSONException;

import java.net.URI;

import me.gnahum12345.fbuair.activities.SignUpActivity;

public class GithubClient3 {
    private AuthorizationService authService;
    private AuthorizationService.TokenResponseCallback callback;
    public final int AUTH_REQUEST_CODE = 30;
    final String AUTHORIZATION_URL = "https://github.com/login/oauth/authorize";
    final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    AuthorizationServiceConfiguration serviceConfig =
            new AuthorizationServiceConfiguration(
                    Uri.parse(AUTHORIZATION_URL), // authorization endpoint
                    Uri.parse(TOKEN_URL)); // token endpoint
    private AuthState authState = new AuthState(serviceConfig);

    AuthorizationRequest getAuthRequest(Context context) {
        final String CLIENT_ID = context.getResources().getString(R.string.github_client_id);
        final String CLIENT_SECRET = context.getResources().getString(R.string.github_client_secret);
        final Uri REDIRECT_URI = Uri.parse("gtihublogin://");
        AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                        serviceConfig, // the authorization service configuration
                        CLIENT_ID, // the client ID, typically pre-registered and static
                        ResponseTypeValues.CODE, // the response_type value: we want a code
                        REDIRECT_URI); // the redirect URI to which the auth response is s
        return authRequestBuilder.build();
    }

    @NonNull
    public AuthState readAuthState(Context context) {
        SharedPreferences authPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String stateJson = authPrefs.getString("stateJson", null);
        AuthState state;
        if (stateJson != null) {
            try {
                return AuthState.jsonDeserialize(stateJson);
            } catch (JSONException e) {
                Log.e("GITHUBCLIENT3", "readAuthState failure. bad json");
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

    public void doAuthorization(Activity activity, Context context) {
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
                authService.performTokenRequest(
                        resp.createTokenExchangeRequest(),
                        new AuthorizationService.TokenResponseCallback() {
                            @Override
                            public void onTokenRequestCompleted(
                                    TokenResponse resp, AuthorizationException ex) {
                                if (resp != null) {
                                    authState.update(resp, ex);
                                    Log.e("GITHUBCLIENT3", "tokenRequest success. token is: " + authState.getAccessToken());

                                } else {
                                    Log.e("GITHUBCLIENT3", "tokenRequest failure.");
                                }
                            }
                        });
            } else {
                Log.e("GITHUBCLIENT3", "onActivityResult failure.");
            }
        }
    }

    public void getUsername() {
        authState.performActionWithFreshTokens(authService, new AuthState.AuthStateAction() {
            @Override
            public void execute(
                    String accessToken,
                    String idToken,
                    AuthorizationException ex) {
                if (ex != null) {
                    Log.e("GITHUBCLIENT3", "performActionWithFreshToken failure." + authState.getAccessToken());
                    return;
                }

                Log.e("GITHUBCLIENT3", "performActionWithFreshToken success. token is: " + authState.getAccessToken());
            }
        });
    }
}