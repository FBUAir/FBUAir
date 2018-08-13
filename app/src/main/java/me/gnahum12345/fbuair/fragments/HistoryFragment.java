package me.gnahum12345.fbuair.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.databinding.adapters.SearchViewBindingAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.interfaces.UserListener;
import me.gnahum12345.fbuair.models.User;


public class HistoryFragment extends Fragment implements UserListener,
        SearchViewBindingAdapter.OnQueryTextSubmit, SearchView.OnQueryTextListener {

    Activity activity;
    SearchView svSearch;
    TextView tvMessage;
    TabLayout tabLayout;
    ViewPager viewPager;

    OnRequestAddContact onAddContactClickedListener;
    OnFragmentChangeListener onFragmentChangeListener;

    // The list of fragments used in the view pager
    private final List<Fragment> fragments = new ArrayList<>();
    // fragments
    HistoryListFragment sentHistoryFragment;
    HistoryListFragment receivedHistoryFragment;
    boolean searchEdited = false;
    // adapter for viewpager
    PagerAdapter pagerAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        //get listeners
        onAddContactClickedListener = (OnRequestAddContact) context;
        onFragmentChangeListener = (OnFragmentChangeListener) context;
        // instantiate fragments and adapter
        receivedHistoryFragment = HistoryListFragment.newInstance(true);
        sentHistoryFragment = HistoryListFragment.newInstance(false);
        fragments.add(receivedHistoryFragment);
        fragments.add(sentHistoryFragment);
        pagerAdapter = new Adapter(getChildFragmentManager(), fragments);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        svSearch = view.findViewById(R.id.svSearch);
        tvMessage = view.findViewById(R.id.tvMessage);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        SearchManager searchManager = (SearchManager) this.activity.getSystemService(Context.SEARCH_SERVICE);
        svSearch.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        svSearch.setOnQueryTextListener(this);

        svSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus || !svSearch.getQuery().toString().isEmpty()) {
                    tvMessage.setText("No results found.");
                }
                else tvMessage.setText("Nothing new yet.");
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    /* implementations for searching through history */
    @Override
    public boolean onQueryTextSubmit(String query) {
        svSearch.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (receivedHistoryFragment.historyAdapter == null || sentHistoryFragment.historyAdapter == null) {
            return false;
        }
        receivedHistoryFragment.historyAdapter.getFilter().filter(query);
        sentHistoryFragment.historyAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public void userAdded(User user) {
        sentHistoryFragment.populateHistory();
    }

    @Override
    public void userRemoved(User user) {
        receivedHistoryFragment.populateHistory();
    }

    class Adapter extends FragmentStatePagerAdapter {

        // Title of the tabs
        private String title[] = {"Received", "Sent"};
        // The list of fragments which we are going to be displaying in the view pager.
        private final List<Fragment> fragments;

        public Adapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
}
