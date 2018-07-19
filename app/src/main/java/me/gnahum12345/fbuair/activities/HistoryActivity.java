package me.gnahum12345.fbuair.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.models.User;

public class HistoryActivity extends AppCompatActivity{
    HistoryAdapter historyAdapter;
    ArrayList<User> contacts;
    RecyclerView rvUser;

    private SwipeRefreshLayout swipeContainer;


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
                // TODO populateTimeline;
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


        //TODO populateTimeline();
    }
}
