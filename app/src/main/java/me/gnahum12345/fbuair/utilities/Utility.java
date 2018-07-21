package me.gnahum12345.fbuair.utilities;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

public class Utility {
    // shared preferences keys
    public static final String PREFERENCES_FILE_NAME_KEY = "MyPrefs";
    public static final String CURRENT_USER_KEY = "current_user";

    // validity checkers for sign up
    public static boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    public static boolean isValidPhoneNumber(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }
    public static boolean isValidFacebookUrl(String facebookUrlString) {
        return (Patterns.WEB_URL.matcher(facebookUrlString).matches() && facebookUrlString.toLowerCase().contains("facebook"));
    }

    public static boolean isValidInstagramUrl(String instagramUrlString) {
        return (Patterns.WEB_URL.matcher(instagramUrlString).matches() && instagramUrlString.toLowerCase().contains("instagram"));
    }

    public static boolean isValidLinkedInUrl(String linkedInUrlString) {
        return (Patterns.WEB_URL.matcher(linkedInUrlString).matches() && linkedInUrlString.toLowerCase().contains("linkedin"));
    }
}
