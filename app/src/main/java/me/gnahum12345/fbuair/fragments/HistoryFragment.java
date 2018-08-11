package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.databinding.adapters.SearchViewBindingAdapter;
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
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.interfaces.UserListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.utils.ContactUtils;
import me.gnahum12345.fbuair.utils.FakeUsers;

import static me.gnahum12345.fbuair.utils.Utils.hideSoftKeyboard;


public class HistoryFragment extends Fragment implements UserListener,
        SearchViewBindingAdapter.OnQueryTextSubmit, SearchView.OnQueryTextListener {

    public HistoryAdapter historyAdapter;
    ArrayList<User> history = new ArrayList<>();
    RecyclerView rvHistory;
    Activity activity;
    SwipeRefreshLayout swipeContainer;
    SearchView svSearch;
    TextView tvMessage;
    LinearLayoutManager linearLayoutManager;

    OnRequestAddContact onAddContactClickedListener;
    OnFragmentChangeListener onFragmentChangeListener;


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
        onFragmentChangeListener = (OnFragmentChangeListener) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        svSearch = view.findViewById(R.id.svSearch);
        tvMessage = view.findViewById(R.id.tvMessage);

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

        SearchManager searchManager = (SearchManager) this.activity.getSystemService(Context.SEARCH_SERVICE);
        svSearch.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        svSearch.setOnQueryTextListener(this);

        svSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && historyAdapter.getItemCount() != 0) {
                    tvMessage.setText("No results found.");
                }
                else tvMessage.setText("Nothing new yet.");
            }
        });

        // populate recycler view with history from shared preferences
        populateHistory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    // populates recycler view with history from shared preferences
    public void populateHistory() {
        clearHistoryList();
        List<User> users = MyUserManager.getInstance().getCurrHistory();
        history.addAll(users);
        if (historyAdapter != null) {
            historyAdapter.notifyDataSetChanged();
        }
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

    /* implementations for searching through history */
    @Override
    public boolean onQueryTextSubmit(String query) {
        hideSoftKeyboard(activity);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (historyAdapter == null) {
            return false;
        }
        historyAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

}
