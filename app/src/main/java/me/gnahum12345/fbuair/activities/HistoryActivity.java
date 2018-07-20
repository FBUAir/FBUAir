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
import android.view.MenuItem;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.gnahum12345.fbuair.FakeUsers;
import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.models.User;

public class HistoryActivity extends AppCompatActivity {
    public static ArrayList<String> listRandos = new ArrayList<>();
    HistoryAdapter historyAdapter;
    ArrayList<User> contacts;
    RecyclerView rvUser;
    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "MyPrefs";
    private SwipeRefreshLayout swipeContainer;

    //creating my shared preferences array of fake contacts
    public static void onContactAddClick(MenuItem mi) {
        JSONObject rando = createRando();
        listRandos.add(rando.toString());
        Log.d("addContacts", String.valueOf(listRandos));
    }

    public static JSONObject createRando() {
        FakeUsers fakeUsers = new FakeUsers();

        ArrayList<JSONObject> listUsers = new ArrayList<JSONObject>();
        listUsers.add(fakeUsers.jsonUser1);
        listUsers.add(fakeUsers.jsonUser2);
        listUsers.add(fakeUsers.jsonUser3);
        listUsers.add(fakeUsers.jsonUser4);
        listUsers.add(fakeUsers.jsonUser5);
        listUsers.add(fakeUsers.jsonUser6);
        listUsers.add(fakeUsers.jsonUser7);
        listUsers.add(fakeUsers.jsonUser8);

        Random random = new Random();
        JSONObject rando = listUsers.get(random.nextInt(listUsers.size()));
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
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvUser = (RecyclerView) findViewById(R.id.rvContact);
        contacts = new ArrayList<>();
        historyAdapter = new HistoryAdapter(contacts);

        rvUser.setLayoutManager(new LinearLayoutManager(this));
        rvUser.setAdapter(historyAdapter);

        getHistory();
    }

    public void addSharedPreferences(SharedPreferences.Editor editor, List list) throws IOException {
        editor.putString("history", ObjectSerializer.serialize((Serializable) list));
        editor.commit();
    }

    public void getHistory() {

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        //adding to the shared preferences
        try {
            addSharedPreferences(editor, listRandos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //reading from the shared preferences
        try {
            listRandos = (ArrayList<String>) ObjectSerializer.deserialize(sharedpreferences.getString("history", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.d("history", String.valueOf(listRandos));
            populateHistory(listRandos);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //TODO
    public void populateHistory(List listRandos){
       // for (int i = 0; i<listRandos.size(); i++){
            //try {

                //TODO NEED TO MAKE LISTRANODS BE MADE OF JSONOBJECTS
                //JSONObject rando = (JSONObject) listRandos.get(i);
               // User user = User.fromJson(rando);
                //contacts.add(user);
                //HistoryAdapter.notifyItemInserted(contacts.size() - 1);

            //}catch (JSONException e){
             //   e.printStackTrace();
            //}

       // }

    }


}
