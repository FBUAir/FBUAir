package me.gnahum12345.fbuair.activities;

import android.app.SearchManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.adapters.SearchViewBindingAdapter;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SearchView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.databinding.ActivityMainBinding;
import me.gnahum12345.fbuair.fragments.DetailsFragment;
import me.gnahum12345.fbuair.fragments.DiscoverFragment;
import me.gnahum12345.fbuair.fragments.HistoryFragment;
import me.gnahum12345.fbuair.fragments.ProfileFragmentTwo;
import me.gnahum12345.fbuair.interfaces.ConnectionListener;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.models.GestureDetector;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;
import me.gnahum12345.fbuair.utils.Utils;


public class MainActivity extends AppCompatActivity implements DiscoverFragment.DiscoverFragmentListener,
        SearchViewBindingAdapter.OnQueryTextSubmit, SearchView.OnQueryTextListener, HistoryAdapter.LaunchDetailsListener {

    public ActivityMainBinding bind;
    // fragment position aliases
    final static int DISCOVER_FRAGMENT = 0;
    final static int HISTORY_FRAGMENT = 1;
    final static int PROFILE_FRAGMENT = 2;
    final static int DETAILS_FRAGMENT = 3;
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private static final String TAG = "MainActivityTag";
    // The list of fragments used in the view pager
    private final List<Fragment> fragments = new ArrayList<>();
    //Connection Service.
    public ConnectionService connectService;
    /**
     * Listens to holding/releasing the volume rocker.
     */
    public final GestureDetector mGestureDetector =
            new GestureDetector(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP) {
                @Override
                protected void onHold() {
                    connectService.sendToAll();
                }
            };
    // fragments
    DiscoverFragment discoverFragment;
    HistoryFragment historyFragment;
    ProfileFragmentTwo profileFragment;
    DetailsFragment detailsFragment;
    UserManager userManager;
    boolean debug = true;

    // The adapter used to display information for our bottom navigation view.
    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        userManager = UserManager.getInstance();
        userManager.loadContacts();
        userManager.setNotificationAbility(true, this);
        // set up ConnectionService
        connectService = new ConnectionService(this); //TODO: add the parameters that are missing.

        // set actionbar to be toolbar
        setSupportActionBar(bind.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        // instantiate fragments
        discoverFragment = new DiscoverFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragmentTwo();
        detailsFragment = new DetailsFragment();

        // Create the fragments to be passed to the ViewPager
        fragments.add(discoverFragment);
        fragments.add(historyFragment);
        fragments.add(profileFragment);
        fragments.add(detailsFragment);

        // Instantiate our Adapter which we will use in our ViewPager
        adapter = new Adapter(getSupportFragmentManager(), fragments);

        // Attach our adapter to our view pager.
        bind.viewPager.setAdapter(adapter);
        bind.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled
                    (int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().show();
                clearMenus();
                switch (position) {
                    case DISCOVER_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(0);
                        break;
                    case HISTORY_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(1);
                        bind.historyMenu.setVisibility(View.VISIBLE);
                        break;
                    case PROFILE_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(2);
                        getSupportActionBar().hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(bind.bottomNavigationView, tabColors);

        bind.bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bind.bottomNavigationView.setNotificationBackgroundColor(fetchColor(R.color.notification));
        bind.bottomNavigationView.setColoredModeColors(fetchColor(R.color.color_blue_orchid), fetchColor(R.color.color_black));
        bind.bottomNavigationView.setTranslucentNavigationEnabled(true);
        bind.bottomNavigationView.setColored(true);
        // Handle the click for each item on the bottom navigation view.
        bind.bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                bind.viewPager.setCurrentItem(position, true);
                if (position == 1) {
                    UserManager.getInstance().clearNotification();
                }

                //TODO: Delete this.. this is a proof of concept that if a user is added, it will be added to the HistoryAdapter.
                if (position == 0) {
                    User u = new User();
                    u.setName("this is a fake user...");
                    u.setTimeAddedToHistory(Utils.getRelativeTimeAgo(Calendar.getInstance().getTime()));
                    UserManager.getInstance().addUser(u);
                }
                return true;
            }
        });

        UserManager.getInstance().addListener(historyFragment);
        connectService.addListener(discoverFragment);

        // associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        bind.svSearch.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        bind.svSearch.setSubmitButtonEnabled(true);
        bind.svSearch.setOnQueryTextListener(this);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    private void startConnectionService() {
        connectService.startDiscovering();
        connectService.startAdvertising();
        connectService.startMedia();
    }

    private void stopConnectionService() {
        connectService.stopAdvertising();
        connectService.stopDiscovering();
        connectService.stopMedia();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectService.startMedia();
        //TODO: start service.
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopConnectionService();

        //TODO: put notification or widget for advertising... and stop discovering..
        //TODO: stop discovering, but possibly keep advertising.
        userManager.commit();
        userManager.removeListener(historyFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectService.removeListener(discoverFragment);
        stopConnectionService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startConnectionService();
    }



    @Override
    public void onBackPressed() {

        if (debug) {
            connectService.debug();
        }

        if (!discoverFragment.rvAdapter.isEmpty()) {
            connectService.onBackPressed();
            return;
        }

        if (debug) {
            return;
        }

        super.onBackPressed();
    }

    // Feature to send eveything at once.
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!discoverFragment.rvAdapter.isEmpty() &&
                mGestureDetector.onKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public List<ConnectionService.Endpoint> getCurrEndpoints() {
        List<ConnectionService.Endpoint> currEndpoints = new ArrayList<>();
        if (connectService == null) {
            return currEndpoints;
        }
        return connectService.getCurrentConnections();
    }

    @Override
    public void addToListener(ConnectionListener listener) {
        if (!connectService.contains(listener)) {
            connectService.addListener(listener);
        }
    }

    // opens details screen for passed in user
    public void launchDetails(String uid) {
        fragments.set(DETAILS_FRAGMENT, ProfileFragmentTwo.newInstance(uid));
        bind.viewPager.setCurrentItem(DETAILS_FRAGMENT, false);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    void clearMenus() {
        bind.historyMenu.setVisibility(View.GONE);
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


}
