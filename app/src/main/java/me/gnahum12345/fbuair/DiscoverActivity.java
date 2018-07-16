package me.gnahum12345.fbuair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import me.gnahum12345.fbuair.Adapaters.DiscoverAdapter;

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

}
