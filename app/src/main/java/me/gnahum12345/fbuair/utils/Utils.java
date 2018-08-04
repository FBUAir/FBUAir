package me.gnahum12345.fbuair.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    // shared preferences keys
    public static final String PREFERENCES_FILE_NAME_KEY = "MyPrefs";
    public static final String CURRENT_USER_KEY = "current_user";
    public static final String HISTORY_KEY = "history";

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

    // hides keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    // JSON date string formatter
    public static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // gets relative time from json format date
    public static String getRelativeTimeAgo(Date date) {
        String relativeDate;
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getClass().equals(service.service.getClass())) {
                return true;
            }
        }
        return false;
    }
}
