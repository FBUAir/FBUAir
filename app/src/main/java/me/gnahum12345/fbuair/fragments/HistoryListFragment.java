package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.User;


public class HistoryListFragment extends Fragment {

    public HistoryAdapter historyAdapter;
    ArrayList<User> history = new ArrayList<>();
    RecyclerView rvHistory;
    Activity activity;

    SwipeRefreshLayout swipeContainer;
    LinearLayoutManager linearLayoutManager;

    OnRequestAddContact onAddContactClickedListener;
    OnFragmentChangeListener onFragmentChangeListener;

    final static String ARG_IS_RECEIVED_HISTORY = "isReceivedHistory";
    boolean isReceivedHistory;

    public HistoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();

        if (getArguments() != null) {
            isReceivedHistory = getArguments().getBoolean(ARG_IS_RECEIVED_HISTORY);
        }

        // initialize adapter, dataset, and linear manager
        history = new ArrayList<>();
        historyAdapter = new HistoryAdapter(activity, history, isReceivedHistory);
        linearLayoutManager = new LinearLayoutManager(activity);
        onAddContactClickedListener = (OnRequestAddContact) context;
        onFragmentChangeListener = (OnFragmentChangeListener) context;
    }

    public static HistoryListFragment newInstance(boolean isReceivedHistory) {
        Bundle args = new Bundle();
        HistoryListFragment fragment = new HistoryListFragment();
        args.putBoolean(ARG_IS_RECEIVED_HISTORY, isReceivedHistory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // configure swipe container
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (historyAdapter.multiSelectMode) {
                    swipeContainer.setRefreshing(false);
                    return;
                }
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

        // populate recycler view with history from shared preferences
        populateHistory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    // populates recycler view with history from shared preferences
    public void populateHistory() {
        clearHistoryList();
        List<User> users = isReceivedHistory ? MyUserManager.getInstance().getCurrHistory() :
                MyUserManager.getInstance().getSentToHistory();
        history.addAll(users);
        if (historyAdapter != null) {
            historyAdapter.notifyDataSetChanged();
        }
    }

    private void clearHistoryList() {
        history.clear();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }
}
