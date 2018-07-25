package me.gnahum12345.fbuair.managers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import me.gnahum12345.fbuair.models.User;
import static me.gnahum12345.fbuair.utils.Utils.HISTORY_KEY;
import static me.gnahum12345.fbuair.utils.Utils.PREFERENCES_FILE_NAME_KEY;

class UserManager {

    private static final UserManager ourInstance = new UserManager();
    private static final String TAG = "UserManagerTAG";
    private Context mContext;
    static UserManager getInstance() {
        return ourInstance;
    }

    Map<String, User> currUsers;

    private UserManager() {
        currUsers = new TreeMap<>();
    }

    public User getUser(String id) {
        //TODO: get user given the id.
        return currUsers.get(id);
    }

    public void addUser(User user) {
        currUsers.put(user.getId(), user);
    }
    public void removeUser(User user) {
        currUsers.remove(user);
    }
    public void clearHistory() {
        currUsers.clear();
    }

    public void commiting() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray newHistoryArray = getJSONArray();
        editor.putString(HISTORY_KEY, newHistoryArray.toString());
        editor.commit();
    }

    private JSONArray getJSONArray() {
        JSONArray jArr = new JSONArray();

        for (User u : currUsers.values()) {
            try {
                jArr.put(User.toJson(u));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("getJSONArray: User {%s} failed to convert to JSON", u.toString()), e );
            }
        }

        return jArr;
    }

    public void loadContacts(Context context) {
        mContext = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
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
                    currUsers.put(user.getId(), user);
                } catch (JSONException e) {
                    Log.e(TAG, String.format("loadContacts: USER={%d} cannot be created.", i), e);
                }
            }
        }
    }

    public List<User> getCurrHistory() {
        return new ArrayList(currUsers.values());
    }
}
