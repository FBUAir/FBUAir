package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.interfaces.UserListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.FakeUsers;


public class HistoryFragment extends Fragment implements UserListener {

    public HistoryAdapter historyAdapter;
    ArrayList<User> history = new ArrayList<>();
    RecyclerView rvUser;
    SharedPreferences sharedpreferences;
    MyUserManager userManager = MyUserManager.getInstance();
    Activity activity;
    private SwipeRefreshLayout swipeContainer;
    public HistoryFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvUser = view.findViewById(R.id.rvContact);
        history = new ArrayList<>();

        historyAdapter = new HistoryAdapter(getContext(), history);

        rvUser.setLayoutManager(new LinearLayoutManager(activity));
        rvUser.setAdapter(historyAdapter);

        // clear old history and add fake users to history
//        clearHistory();
        FakeUsers fakeUsers = new FakeUsers();
        JSONObject[] fakeHistory;
        fakeHistory = new JSONObject[]{
                fakeUsers.jsonUser1, fakeUsers.jsonUser2, fakeUsers.jsonUser3,
                fakeUsers.jsonUser4, fakeUsers.jsonUser5, fakeUsers.jsonUser6,
                fakeUsers.jsonUser7, fakeUsers.jsonUser8};
        User user = userManager.getCurrentUser();
        user.setNumConnections(fakeHistory.length);
        userManager.commitCurrentUser(user);
//            user = userManager.getCurrentUser(); //TODO: delete this. this is for debugging purposes to see that the current user has been updated.
        for (JSONObject jsonUser : fakeHistory) {
            addToHistory(User.fromJson(jsonUser));
        }

        // populate recycler view with history from shared preferences
        populateHistory();

    }

    // adds a given user to history, noting the time (to be called right after sharing data)
    void addToHistory(User user) {
        MyUserManager.getInstance().addUser(user);
    }

    // gets history from shared preferences. return empty json array if no history has been added
    List<User> getHistory() {
        return MyUserManager.getInstance().getCurrHistory();
    }


    // populates recycler view with history from shared preferences
    public void populateHistory() {
        clearHistoryList();
        List<User> users = getHistory();
        //TODO: make sure that history adapter is not null..
        history.addAll(users);
        if (historyAdapter != null) {
            historyAdapter.notifyDataSetChanged();
        }
    }

    // clears history
    void clearHistory() {
        MyUserManager.getInstance().clearHistory();
    }

    private void clearHistoryList() {
        history.clear();
    }

    @Override
    public void userAdded(User user) {
        populateHistory();
    }

    @Override
    public void userRemoved(User user) {
        populateHistory();
    }
}
