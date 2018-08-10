package me.gnahum12345.fbuair.interfaces;

import android.graphics.drawable.Icon;

import me.gnahum12345.fbuair.models.SocialMedia;

public interface OnSignUpScreenChangeListener {
    /** hides menu */
    void setMenuVisible(boolean flag);

    /** goes to sign up contact fragment */
    void launchSignUpContact();

    /** goes to sign up contact two fragment */
    void launchSignUpContactTwo();

    /** goes to sign up social media fragment */
    void launchSignUpSocialMedia();

    /** launches fragment to add/edit/remove social media*/
    void launchUrl(SocialMedia socialMedia);

    /** closes url fragment and goes back to social media fragment. */
    void finishUrl();

    void launchValidateProfile(SocialMedia socialMedia);

    void finishValidateProfile(boolean success);

    /** starts intent to go to main activity */
    void createAccount();
}
