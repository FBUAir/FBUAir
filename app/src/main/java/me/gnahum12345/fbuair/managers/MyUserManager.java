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

import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.callbacks.MyLifecycleHandler;
import me.gnahum12345.fbuair.interfaces.UserListener;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;


import static me.gnahum12345.fbuair.utils.Utils.CURRENT_USER_KEY;
import static me.gnahum12345.fbuair.utils.Utils.HISTORY_KEY;
import static me.gnahum12345.fbuair.utils.Utils.PREFERENCES_FILE_NAME_KEY;
import static me.gnahum12345.fbuair.utils.Utils.dateFormatter;

public class MyUserManager {

    private static final MyUserManager ourInstance = new MyUserManager();
    private static final String TAG = "UserManagerTAG";
    Map<String, User> currentUsers;
    ArrayList<UserListener> userListeners;
    int count = 0;
    private Context mContext;
    private Handler handler = new Handler();
    private boolean notificationsEnabled = false;
    private Activity activity;
    private MyUserManager() {
        currentUsers = new TreeMap<>();
        userListeners = new ArrayList<UserListener>();
    }

    public static MyUserManager getInstance() {
        return ourInstance;
    }

    public void setContext(Context c) {
        mContext = c;
    }

    private boolean isInBackground() {
        return !MyLifecycleHandler.isApplicationInForeground() || !MyLifecycleHandler.isApplicationVisible();
    }

    public User getUser(String id) {
        //TODO: get user given the id.
        User currUser = getCurrentUser();
        return currUser.getId().equals(id) ? currUser : currentUsers.get(id);
    }

    public void addListener(UserListener listener) {
        userListeners.add(listener);
    }

    public void removeListener(UserListener listener) {
        userListeners.remove(listener);
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
        currentUsers.put(user.getId(), user);
        if (commit()) {
            notifyListeners(user, true);
            return true;
        } else {
            return false;
        }
    }

    public boolean addUser(User user, ConnectionService.Endpoint endpoint) {
        user.setTimeAddedToHistory(dateFormatter.format(Calendar.getInstance().getTime()));
        String title;
        if (!currentUsers.containsKey(user.getId())) {
            runBadgeNotification();
            title = "New User has been added!";
        } else {
            title = "User has been updated!";
        }
        if (isInBackground()) {
            AirNotificationManager.getInstance().createNotification(title, String.format("%s has sent you their information!\nWould you want to send them your information?", user.getName()), user, endpoint);
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
                    ((MainActivity) activity).bind.bottomNavigationView.setNotification(Integer.toString(count), 1);
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
                    ((MainActivity) activity).bind.bottomNavigationView.setNotification("", 1);
                    seenAllUsers();
                }
            }
        }, 1000);
    }

    private void seenAllUsers() {
        for (User u : currentUsers.values()) {
            u.isSeen(true);
        }
        commit();
    }

    public void notifyListeners(User user, boolean added) {
        if (added) {
            for (UserListener listener : userListeners) {
                listener.userAdded(user);
            }
        } else {
            for (UserListener listener : userListeners) {
                listener.userRemoved(user);
            }
        }
    }

    public void setNotificationAbility(boolean enabled, Activity activity) {
        notificationsEnabled = enabled;
        this.activity = activity;
    }

    public void removeUser(User user) {
        for (String key : currentUsers.keySet()) {
            User u = currentUsers.get(key);
            if (u.equals(user)) {
                currentUsers.remove(key);
                commit();
                return;
            }
        }
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

    public void commitCurrentUser(User user) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_USER_KEY, user.toString());
        editor.commit();
    }

    public void deleteCurrentUser() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_USER_KEY, null);
        editor.commit();
    }

    private JSONArray getJSONArray() {
        JSONArray jArr = new JSONArray();

        for (User u : currentUsers.values()) {
            try {
                jArr.put(User.toJson(u));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("getJSONArray: User {%s} failed to convert to JSON", u.toString()), e);
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

    public User getCurrentUser() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        String currentUser = sharedPreferences.getString(CURRENT_USER_KEY, null);
        User user = new User();
        if (currentUser != null) {
            try {
                JSONObject userJson = new JSONObject(currentUser);
                user = User.fromJson(userJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public ConnectionService.Endpoint avaliableEndpoint(String uid) {
        User user = getUser(uid);
        //TODO get add endpoint to the user???
        List<ConnectionService.Endpoint> currEndpoints = ((MainActivity) activity).getCurrEndpoints();
        if (currEndpoints == null) {
            return null;
        }
        for (ConnectionService.Endpoint e :  currEndpoints) {
            if (e.getName().contains(user.getName())) {
                return e;
            }
        }
        return null;
    }

}
