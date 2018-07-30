package me.gnahum12345.fbuair.managers;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import me.gnahum12345.fbuair.callbacks.MyLifecycleHandler;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.interfaces.UserListener;
import me.gnahum12345.fbuair.models.User;
import static me.gnahum12345.fbuair.utils.Utils.HISTORY_KEY;
import static me.gnahum12345.fbuair.utils.Utils.PREFERENCES_FILE_NAME_KEY;
import static me.gnahum12345.fbuair.utils.Utils.dateFormatter;

public class UserManager {

    private static final UserManager ourInstance = new UserManager();
    private static final String TAG = "UserManagerTAG";
    private Context mContext;
    public static UserManager getInstance() {
        return ourInstance;
    }
    private Handler handler = new Handler();
    private boolean notificationsEnabled = false;
    private Activity activity;
    Map<String, User> currentUsers;
    ArrayList<UserListener> listeners;
    int count = 0;

    public void setContext(Context c) {
        mContext = c;
    }

    private boolean isInBackground() {
        return !MyLifecycleHandler.isApplicationInForeground() || !MyLifecycleHandler.isApplicationVisible();
    }

    private UserManager() {
        currentUsers = new TreeMap<>();
        listeners = new ArrayList<UserListener>();
    }

    public User getUser(String id) {
        //TODO: get user given the id.
        return currentUsers.get(id);
    }

    public void addListener(UserListener listener) {
        listeners.add(listener);
    }

    public void removeListener(UserListener listener) {
        listeners.remove(listener);
    }

    public boolean addUser(User user) {
        user.setTimeAddedToHistory(dateFormatter.format(Calendar.getInstance().getTime()));
        String title;
        if (!currentUsers.containsKey(user.getId())) {
            runBadgeNotification();
            title = "New User has been added!";
        } else {
            title = "User has been updated!";
        }
        if (isInBackground()) {
            AirNotificationManager.getInstance().createNotification(title, String.format("%s has sent you their information!\nWould you want to send them your information?", user.getName()), user);
        }
        currentUsers.put(user.getId(), user);
        if (commit()) {
            notifyListeners(user, true);
            return true;
        } else {
            return false;
        }
    }


    private void runBadgeNotification() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (notificationsEnabled) {
                    count++;
                    ((MainActivity) activity).bottomNavigation.setNotification(Integer.toString(count), 1);
                }
            }
        }, 1000);
    }
    public void clearNotification() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (notificationsEnabled) {
                    count = 0;
                    ((MainActivity) activity).bottomNavigation.setNotification("", 1);
                    seenAllUsers();
                }
            }
        }, 1000);
    }

    private void seenAllUsers() {
        for (User u : currentUsers.values()) {
            u.hasSeen(true);
        }
    }

    public void notifyListeners(User user, boolean added) {
        if (added) {
            for (UserListener listener : listeners) {
                listener.userAdded(user);
            }
        } else {
            for (UserListener listener : listeners) {
                listener.userRemoved(user);
            }
        }
    }

    public void setNotificationAbility(boolean enabled, Activity activity) {
        notificationsEnabled = enabled;
        this.activity = activity;
    }

    public void removeUser(User user) {
        currentUsers.remove(user);
        commit();
    }
    public void clearHistory() {
        currentUsers.clear();
        commit();
    }

    public boolean commit() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray newHistoryArray = getJSONArray();
        editor.putString(HISTORY_KEY, newHistoryArray.toString());
        return editor.commit();
    }

    private JSONArray getJSONArray() {
        JSONArray jArr = new JSONArray();

        for (User u : currentUsers.values()) {
            try {
                jArr.put(User.toJson(u));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("getJSONArray: User {%s} failed to convert to JSON", u.toString()), e );
            }
        }

        return jArr;
    }

    public void loadContacts() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        String historyArrayString = sharedPreferences.getString(HISTORY_KEY, null);
        if (historyArrayString == null) {
            return;
        } else {
            JSONArray jsonArr = new JSONArray();
            try {
                jsonArr = new JSONArray(historyArrayString);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            for (int i = 0; i < jsonArr.length(); i++) {
                try {
                    JSONObject poUser = jsonArr.getJSONObject(i);
                    User user = User.fromJson(poUser);
                    currentUsers.put(user.getId(), user);
                } catch (JSONException e) {
                    Log.e(TAG, String.format("loadContacts: USER={%d} cannot be created.", i), e);
                }
            }
        }
    }

    public List<User> getCurrHistory() {
        return new ArrayList(currentUsers.values());
    }
}
