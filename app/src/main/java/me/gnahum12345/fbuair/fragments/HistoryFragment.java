package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utilities.FakeUsers;

import static me.gnahum12345.fbuair.utilities.Utility.HISTORY_KEY;
import static me.gnahum12345.fbuair.utilities.Utility.PREFERENCES_FILE_NAME_KEY;
import static me.gnahum12345.fbuair.utilities.Utility.dateFormatter;


public class HistoryFragment extends Fragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    public HistoryAdapter historyAdapter;
    ArrayList<User> history;
    RecyclerView rvUser;
    SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeContainer;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get reference to main activity
        activity = getActivity();

        // configure swipe container
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                historyAdapter.clear();
                populateHistory();
                swipeContainer.setRefreshing(false);
                Log.d("refresh", "refreshingggg");
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvUser = view.findViewById(R.id.rvContact);
        history = new ArrayList<>();
        historyAdapter = new HistoryAdapter(history);

        rvUser.setLayoutManager(new LinearLayoutManager(activity));
        rvUser.setAdapter(historyAdapter);

        // clear old history and add fake users to history
        clearHistory();
        FakeUsers fakeUsers = new FakeUsers();
        JSONObject[] fakeHistory;
        try {
            fakeHistory = new JSONObject[] {
                    fakeUsers.jsonUser1, fakeUsers.jsonUser2, fakeUsers.jsonUser3,
                            fakeUsers.jsonUser4, fakeUsers.jsonUser5, fakeUsers.jsonUser6,
                            fakeUsers.jsonUser7, fakeUsers.jsonUser8};
            for (JSONObject jsonUser : fakeHistory) {
                addToHistory(User.fromJson(jsonUser));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // populate recycler view with history from shared preferences
        populateHistory();
    }

    // adds a given user to history, noting the time (to be called right after sharing data)
    void addToHistory(User user) {
        sharedpreferences =
                activity.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        JSONArray historyJSONArray = getHistory();
        user.setTimeAddedToHistory(dateFormatter.format(Calendar.getInstance().getTime()));
        try {
            historyJSONArray.put(User.toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(HISTORY_KEY, historyJSONArray.toString());
        editor.commit();
    }

    // gets history from shared preferences. return empty json array if no history has been added
    JSONArray getHistory() {
        sharedpreferences =
                activity.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        String historyArrayString = sharedpreferences.getString(HISTORY_KEY, null);
        if (historyArrayString == null) {
            return new JSONArray();
        }
        else {
            try {
                return new JSONArray(historyArrayString);
            } catch (JSONException e) {
                return new JSONArray();
            }
        }
    }


    // populates recycler view with history from shared preferences
    public void populateHistory() {
        JSONArray historyJSONArray = getHistory();
        for (int i = 0; i < historyJSONArray.length(); i++) {
            User user = null;
            try {
                user = User.fromJson(historyJSONArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            history.add(user);
            historyAdapter.notifyItemInserted(history.size() - 1);
        }
    }

    // clears history
    void clearHistory() {
        sharedpreferences =
                activity.getSharedPreferences(PREFERENCES_FILE_NAME_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(HISTORY_KEY, null);
        editor.commit();
    }
}
