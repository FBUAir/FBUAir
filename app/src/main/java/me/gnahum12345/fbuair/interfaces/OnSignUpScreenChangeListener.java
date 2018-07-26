package me.gnahum12345.fbuair.interfaces;

public interface OnSignUpScreenChangeListener {
    /** goes to sign up contact fragment */
    void launchSignUpContact();
    /** goes to sign up social media fragment */
    void launchSignUpSocialMedia();
    /** closes url fragment and goes back to social media fragment.
     *  takes in boolean whether social media was successfully added or not */
    void finishUrl(boolean added);
    /** starts intent to go to main activity */
    void launchMainActivity();
}
