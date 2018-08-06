package me.gnahum12345.fbuair.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import me.gnahum12345.fbuair.utils.FakeUsers;
import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.models.User;

public class HistoryActivity extends AppCompatActivity {
    static JSONArray listRandos = new JSONArray();
    HistoryAdapter historyAdapter;
    ArrayList<User> contacts;
    RecyclerView rvUser;
    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "MyPrefs";
    private SwipeRefreshLayout swipeContainer;

    public static JSONObject createRando() {
        FakeUsers fakeUsers = new FakeUsers();

        JSONArray listUsers = new JSONArray();
        listUsers.put(fakeUsers.jsonUser1);
        listUsers.put(fakeUsers.jsonUser2);
        listUsers.put(fakeUsers.jsonUser3);
        listUsers.put(fakeUsers.jsonUser4);
        listUsers.put(fakeUsers.jsonUser5);
        listUsers.put(fakeUsers.jsonUser6);
        listUsers.put(fakeUsers.jsonUser7);
        listUsers.put(fakeUsers.jsonUser8);

        Random random = new Random();
        JSONObject rando = null;
        try {
            rando = (JSONObject) listUsers.get(random.nextInt(listUsers.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rando;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                historyAdapter.clear();
                getHistory();
                swipeContainer.setRefreshing(false);
                Log.d("refresh", "refreshingggg");
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvUser = (RecyclerView) findViewById(R.id.rvContact);
        contacts = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, contacts);

        rvUser.setLayoutManager(new LinearLayoutManager(this));
        rvUser.setAdapter(historyAdapter);

        addHistory();
        getHistory();
    }

    public static void addSharedPreferences(SharedPreferences.Editor editor, String list) {
        editor.putString("history", list);
        editor.commit();
    }

    public void addHistory() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        //adding to the shared preferences
        addSharedPreferences(editor, listRandos.toString());

    }

    public void getHistory() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String json_array = sharedpreferences.getString("history", null);
        try {
            JSONArray history = new JSONArray(json_array);
            populateHistory(history);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void populateHistory(JSONArray history) {
        for (int i = 0; i < history.length(); i++) {
            try {
                JSONObject rando = (JSONObject) history.get(i);
                User user = User.fromJson(rando);
                contacts.add(user);
                historyAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
