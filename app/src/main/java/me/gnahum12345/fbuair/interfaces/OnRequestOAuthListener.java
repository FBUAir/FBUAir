package me.gnahum12345.fbuair.interfaces;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;


public interface OnRequestOAuthListener {
    void twitterLogin(Callback<TwitterSession> callback);
}
