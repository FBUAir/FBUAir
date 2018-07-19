package me.gnahum12345.fbuair.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import me.gnahum12345.fbuair.FakeUsers;
import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.DiscoverAdapter;

public class DiscoverActivity extends AppCompatActivity {

    // Instance variables.
    private RecyclerView rvDevicesView;
    private ArrayList<String> deviceLst;
    private DiscoverAdapter rvAdapter;
    public ArrayList<String> listRandos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Bind the views.
        rvDevicesView = findViewById(R.id.rvDevicesView);

        // Set up for RecyclerView.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        deviceLst = new ArrayList<>();
        rvAdapter = new DiscoverAdapter(deviceLst);

        rvDevicesView.setLayoutManager(layoutManager);
        rvDevicesView.setAdapter(rvAdapter);

        loadFakeData(); // TODO: Delete Fake Data .
    }


    // TODO Delete this function... cause its fake.
    private void loadFakeData() {
        for (int i = 0; i < 10000; i++) {
            String count = "Look mom, I can count up to " + Integer.toString(i);
            deviceLst.add(count);
            rvAdapter.notifyItemChanged(deviceLst.size() - 1);
        }
    }

    public void onProfileClick(MenuItem mi) {
        // handle click here
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    public void onHistoryClick(MenuItem mi) {
        // handle click here
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }



    //creating my shared preferences array of fake contacts
    public void onContactAddClick(MenuItem mi) {
        JSONObject rando = createRando();

        listRandos.add(rando.toString());

        SharedPreferences sharedpreferences;
        String MyPREFERENCES = "MyPrefs";

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("history", rando.toString());
        editor.commit();

        Log.d("addContacts", String.valueOf(listRandos));
    }

    public JSONObject createRando(){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
