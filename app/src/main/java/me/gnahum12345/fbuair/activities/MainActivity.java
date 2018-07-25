package me.gnahum12345.fbuair.activities;

import android.app.SearchManager;
import android.content.Context;
import android.databinding.adapters.SearchViewBindingAdapter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.fragments.DetailsFragment;
import me.gnahum12345.fbuair.fragments.DiscoverFragment;
import me.gnahum12345.fbuair.fragments.HistoryFragment;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.models.User;

public class MainActivity extends AppCompatActivity
        implements SearchViewBindingAdapter.OnQueryTextSubmit, SearchView.OnQueryTextListener, HistoryAdapter.LaunchDetailsListener {

    // views
    BottomNavigationView bottomNavigation;
    android.support.v7.widget.Toolbar toolbar;
    SearchView svSearch;

    // fragments
    DiscoverFragment discoverFragment;
    HistoryFragment historyFragment;
    ProfileFragment profileFragment;
    DetailsFragment detailsFragment;

    // fragment position aliases
    final static int DISCOVER_FRAGMENT = 0;
    final static int HISTORY_FRAGMENT = 1;
    final static int PROFILE_FRAGMENT = 2;
    final static int DETAILS_FRAGMENT = 3;

    // menus
    RelativeLayout historyMenu;

    // The list of fragments used in the view pager
    private final List<Fragment> fragments = new ArrayList<>();

    // A reference to our view pager.
    private ViewPager viewPager;

    // The adapter used to display information for our bottom navigation view.
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set actionbar to be toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        // instantiate fragments
        discoverFragment = new DiscoverFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();
        detailsFragment = new DetailsFragment();

        // Create the fragments to be passed to the ViewPager
        fragments.add(discoverFragment);
        fragments.add(historyFragment);
        fragments.add(profileFragment);
        fragments.add(detailsFragment);

        // Grab a reference to our view pager.
        viewPager = findViewById(R.id.viewPager);

        // Instantiate our Adapter which we will use in our ViewPager
        adapter = new Adapter(getSupportFragmentManager(), fragments);

        // get references to menus
        historyMenu = findViewById(R.id.historyMenu);

        // Attach our adapter to our view pager.
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled
                    (int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                clearMenus();
                switch (position) {
                    case DISCOVER_FRAGMENT:
                        bottomNavigation.setSelectedItemId(R.id.action_discover);
                        break;
                    case HISTORY_FRAGMENT:
                        bottomNavigation.setSelectedItemId(R.id.action_history);
                        historyMenu.setVisibility(View.VISIBLE);
                        break;
                    case PROFILE_FRAGMENT:
                        bottomNavigation.setSelectedItemId(R.id.action_profile);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        // Grab a reference to our bottom navigation view
        bottomNavigation = findViewById(R.id.bottomNavigationView);

        // Handle the click for each item on the bottom navigation view.
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_discover:
                        viewPager.setCurrentItem(DISCOVER_FRAGMENT);
                        return true;
                    case R.id.action_history:
                        viewPager.setCurrentItem(HISTORY_FRAGMENT);
                        return true;
                    case R.id.action_profile:
                        viewPager.setCurrentItem(PROFILE_FRAGMENT);
                        return true;
                    default:
                        return false;
                }
            }
        });

        // associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        svSearch = findViewById(R.id.svSearch);
        svSearch.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        svSearch.setSubmitButtonEnabled(true);
        svSearch.setOnQueryTextListener(this);
    }

    static class Adapter extends FragmentStatePagerAdapter {

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
            return fragments.size();
        }
    }

    // opens details screen for passed in user
    public void launchDetails(User user) {
        fragments.set(DETAILS_FRAGMENT, DetailsFragment.newInstance(user));
        viewPager.setCurrentItem(DETAILS_FRAGMENT, false);
    }

    void clearMenus() {
        historyMenu.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        historyFragment.historyAdapter.getFilter().filter(query);
        return true;
    }
}
