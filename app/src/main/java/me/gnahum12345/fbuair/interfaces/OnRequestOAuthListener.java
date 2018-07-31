package me.gnahum12345.fbuair.interfaces;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;

import me.gnahum12345.fbuair.models.SocialMedia;


public interface OnRequestOAuthListener {
    void twitterLogin(SocialMedia socialMedia);
    void linkedInLogin(SocialMedia socialMedia);
    void githubLogin(SocialMedia socialMedia);
}
