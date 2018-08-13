package me.gnahum12345.fbuair.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    // shared preferences keys
    public static final String PREFERENCES_FILE_NAME_KEY = "MyPrefs";
    public static final String CURRENT_USER_KEY = "current_user";
    public static final String HISTORY_KEY = "history";
    public static final String SENT_HISTORY_KEY = "historyIncoming";

    // validity checkers for sign up
    public static boolean isValidEmail(CharSequence email) {
        return (TextUtils.isEmpty(email) || Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    public static boolean isValidPhoneNumber(String number) {
        return (TextUtils.isEmpty(number) || android.util.Patterns.PHONE.matcher(number).matches());
    }

    // hides keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && inputMethodManager != null) {
            try {
                inputMethodManager.hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(), 0);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    // JSON date string formatter
    public static DateFormat dateFormatter = new SimpleDateFormat
            ("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    // gets relative time from json format date
    public static String getRelativeTimeAgo(Date date) {
        String relativeDate;
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }

    // returns time if in the same day, return day of week if in same week, return month and day otherwise
    public static String getHistoryDate(String dateString) {
        String formattedDate = "";
        try {
            Date date = dateFormatter.parse(dateString);
            Date beginningOfDay;
            Date sixDaysAgo;

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            beginningOfDay = cal.getTime();

            cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 6);
            sixDaysAgo = cal.getTime();

            if (date.after(beginningOfDay)) {
                DateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
                formattedDate = format.format(date);
            } else if (date.after(sixDaysAgo)) {
                DateFormat format = new SimpleDateFormat("EEE", Locale.US);
                formattedDate = format.format(date);
            } else {
                DateFormat format = new SimpleDateFormat("MMM d", Locale.US);
                formattedDate = format.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
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
