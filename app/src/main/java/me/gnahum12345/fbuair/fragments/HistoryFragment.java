package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.databinding.FragmentDetailsBinding;
import me.gnahum12345.fbuair.interfaces.UserListener;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.SwipeController;
import me.gnahum12345.fbuair.services.SwipeControllerActions;
import me.gnahum12345.fbuair.utils.ContactUtils;
import me.gnahum12345.fbuair.utils.FakeUsers;


public class HistoryFragment extends Fragment implements UserListener {

    public HistoryAdapter historyAdapter;
    ArrayList<User> history = new ArrayList<>();
    RecyclerView rvHistory;
    UserManager userManager = UserManager.getInstance();
    Activity activity;
    SwipeRefreshLayout swipeContainer;
    LinearLayoutManager linearLayoutManager;
    SwipeController swipeController = null;
    ContactUtils.AddContactResult addContactResult;
    FragmentDetailsBinding bind;
    String contactId;
    String rawContactId;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get reference to main activity
        activity = getActivity();

        // initialize adapter, dataset, and linear manager
        history = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getContext(), history);
        linearLayoutManager = new LinearLayoutManager(activity);
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

        // attach adapter and layout manager
        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setAdapter(historyAdapter);
        rvHistory.setLayoutManager(new LinearLayoutManager(activity));

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                UserManager.getInstance().removeUser(history.get(position));
                history.remove(position);
                historyAdapter.notifyItemRemoved(position);
                historyAdapter.notifyItemRangeChanged(position, historyAdapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position) {
                addContactResult = ContactUtils.findConflict(getContext(), history.get(position));
                if (addContactResult.getResultCode() == ContactUtils.SUCCESS) {
                    addContact(history.get(position));
                }
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(rvHistory);

        rvHistory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

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

    // populates recycler view with history from shared preferences
    public void populateHistory() {
        clearHistoryList();
        List<User> users = userManager.getCurrHistory();
        history.addAll(users);
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

    public void addContact(User user) {
        String ids[] = ContactUtils.addContact(getContext(), user);
        contactId = ids[0];
        rawContactId = ids[1];
        if (ContactUtils.mergeOccurred(getContext(), contactId)) {
            Toast.makeText(getContext(), "Contact was linked with duplicate", Toast.LENGTH_LONG).show();
        }
    }

}
