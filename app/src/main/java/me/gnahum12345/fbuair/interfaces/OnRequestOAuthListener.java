package me.gnahum12345.fbuair.interfaces;

import me.gnahum12345.fbuair.models.SocialMedia;


public interface OnRequestOAuthListener {
    void twitterLogin(SocialMedia socialMedia);
    void linkedInLogin(SocialMedia socialMedia);
    void githubLogin(SocialMedia socialMedia);
    void facebookLogin(SocialMedia socialMedia);
}
