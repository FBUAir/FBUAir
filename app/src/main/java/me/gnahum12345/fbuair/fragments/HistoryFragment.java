package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.interfaces.UserListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.ContactUtils;
import me.gnahum12345.fbuair.utils.FakeUsers;

import static me.gnahum12345.fbuair.utils.ImageUtils.getCircularBitmap;


public class HistoryFragment extends Fragment implements UserListener {

    public HistoryAdapter historyAdapter;
    ArrayList<User> history = new ArrayList<>();
    MyUserManager userManager = MyUserManager.getInstance();
    RecyclerView rvHistory;
    Activity activity;
    SwipeRefreshLayout swipeContainer;
    LinearLayoutManager linearLayoutManager;
    //    SwipeController swipeController = null;
    ContactUtils.AddContactResult addContactResult;

    OnRequestAddContact onAddContactClickedListener;


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        // initialize adapter, dataset, and linear manager
        history = new ArrayList<>();
        historyAdapter = new HistoryAdapter(activity, history);
        linearLayoutManager = new LinearLayoutManager(activity);
        onAddContactClickedListener = (OnRequestAddContact) context;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // configure swipe container
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                history.clear();
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

        // attach adapter and layout manager
        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setAdapter(historyAdapter);
        rvHistory.setLayoutManager(new LinearLayoutManager(activity));


//        clearHistory();
        // add fake users to history
        FakeUsers fakeUsers = new FakeUsers();
        JSONObject[] fakeHistory;
        fakeHistory = new JSONObject[]{
                fakeUsers.jsonUser1, fakeUsers.jsonUser2, fakeUsers.jsonUser3,
                fakeUsers.jsonUser4, fakeUsers.jsonUser5, fakeUsers.jsonUser6,
                fakeUsers.jsonUser7, fakeUsers.jsonUser8};
        User user = userManager.getCurrentUser();
        user.setNumConnections(fakeHistory.length);
        userManager.commitCurrentUser(user);
        for (JSONObject jsonUser : fakeHistory) {
            userManager.addUser(User.fromJson(jsonUser));
        }

        // populate recycler view with history from shared preferences
        populateHistory();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable d = new BitmapDrawable(getResources(), getCircularBitmap(MyUserManager.getInstance().getCurrentUser().getProfileImage()));
        ((MainActivity) getActivity()).getSupportActionBar().setLogo(d);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);

        return inflater.inflate(R.layout.fragment_history, container, false);
    }


    // gets history from shared preferences. return empty json array if no history has been added
    List<User> getHistory() {
        return MyUserManager.getInstance().getCurrHistory();
    }


    // populates recycler view with history from shared preferences
    public void populateHistory() {
        clearHistoryList();
        List<User> users = getHistory();
        history.addAll(users);
        if (historyAdapter != null) {
            historyAdapter.notifyDataSetChanged();
        }
    }

    // clears history
    void clearHistory() {
        MyUserManager.getInstance().clearHistory();
        historyAdapter.notifyDataSetChanged();
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
