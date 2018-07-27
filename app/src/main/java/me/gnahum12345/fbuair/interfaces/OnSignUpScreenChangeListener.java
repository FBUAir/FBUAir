package me.gnahum12345.fbuair.interfaces;

import android.graphics.drawable.Icon;

import me.gnahum12345.fbuair.models.SocialMedia;

public interface OnSignUpScreenChangeListener {
    /** goes to sign up contact fragment */
    void launchSignUpContact();

    /** goes to sign up social media fragment */
    void launchSignUpSocialMedia();

    /** launches fragment to add/edit/remove social media*/
    void launchUrl(SocialMedia socialMedia);

    /** closes url fragment and goes back to social media fragment. */
    void finishUrl();

    /** starts intent to go to main activity */
    void launchMainActivity();
}
