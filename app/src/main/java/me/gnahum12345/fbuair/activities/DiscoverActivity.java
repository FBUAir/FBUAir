package me.gnahum12345.fbuair.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.DiscoverAdapter;

public class DiscoverActivity extends AppCompatActivity {

    // Instance variables.
    private RecyclerView rvDevicesView;
    private ArrayList<String> deviceLst;
    private DiscoverAdapter rvAdapter;


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

    public void onClick(MenuItem mi) {
        // handle click here
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
