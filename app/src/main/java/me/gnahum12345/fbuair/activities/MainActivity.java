package me.gnahum12345.fbuair.activities;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.adapters.SearchViewBindingAdapter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
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
import me.gnahum12345.fbuair.fragments.DetailsFragment;
import me.gnahum12345.fbuair.fragments.DiscoverFragment;
import me.gnahum12345.fbuair.fragments.HistoryFragment;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.interfaces.ConnectionListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.GestureDetector;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;
import me.gnahum12345.fbuair.utils.Utils;


public class MainActivity extends AppCompatActivity implements DiscoverFragment.DiscoverFragmentListener,
        SearchViewBindingAdapter.OnQueryTextSubmit, SearchView.OnQueryTextListener, HistoryAdapter.LaunchDetailsListener {

    // fragment position aliases
    final static int DISCOVER_FRAGMENT = 0;
    // references to bottom navigation bar and toolbar
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
    android.support.v7.widget.Toolbar toolbar;
    SearchView svSearch;
    // fragments
    DiscoverFragment discoverFragment;
    HistoryFragment historyFragment;
    ProfileFragment profileFragment;
    DetailsFragment detailsFragment;
    MyUserManager userManager;
    // menus
    RelativeLayout historyMenu;
    boolean debug;
    // A reference to our view pager.
    private AHBottomNavigationViewPager viewPager;
    // BottomNavigationView bottomNavigation;
    public AHBottomNavigation bottomNavigation;

    // The adapter used to display information for our bottom navigation view.
    private Adapter adapter;

    private boolean mBound = false;
    private boolean listened = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Because we have bound to an explicit
            // service that is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            connectService = binder.getService();
            mBound = true;
            if (discoverFragment != null) {
                connectService.addListener(discoverFragment);
                listened = true;
            }
            startConnectionService();
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "onServiceDisconnected");
            mBound = false;
            listened = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debug = true;

        userManager = MyUserManager.getInstance();
        userManager.loadContacts();
        userManager.setNotificationAbility(true, this);
        // set up ConnectionService

        Intent intent = new Intent(MainActivity.this, ConnectionService.class);
        if (!isMyServiceRunning(ConnectionService.class)) {
            startService(intent);
        }

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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
                    (int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                clearMenus();
                switch (position) {
                    case DISCOVER_FRAGMENT:
                        bottomNavigation.setCurrentItem(0);
                        break;
                    case HISTORY_FRAGMENT:
                        bottomNavigation.setCurrentItem(1);
                        historyMenu.setVisibility(View.VISIBLE);
                        break;
                    case PROFILE_FRAGMENT:
                        bottomNavigation.setCurrentItem(2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        // Grab a reference to our bottom navigation view
        bottomNavigation = findViewById(R.id.bottomNavigationView);

        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setNotificationBackgroundColor(fetchColor(R.color.notification));
        bottomNavigation.setColoredModeColors(fetchColor(R.color.color_blue_orchid), fetchColor(R.color.color_black));
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setColored(true);
        // Handle the click for each item on the bottom navigation view.
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                viewPager.setCurrentItem(position, true);
                if (position == 1) {
                    MyUserManager.getInstance().clearNotification();
                }

                //TODO: Delete this.. this is a proof of concept that if a user is added, it will be added to the HistoryAdapter.
                if (position == 0) {
                    User u = new User();
                    u.setName("this is a fake user...");
                    u.setTimeAddedToHistory(Utils.getRelativeTimeAgo(Calendar.getInstance().getTime()));
                    MyUserManager.getInstance().addUser(u);
                }
                return true;
            }
        });

        MyUserManager.getInstance().addListener(historyFragment);
        if (mBound && !listened) {
            connectService.addListener(discoverFragment);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    private void startConnectionService() {
        connectService.startDiscovering();
        connectService.startAdvertising();
        connectService.startMedia(this);
    }

    private void stopConnectionService() {
        if (connectService == null) { return; }
        connectService.stopAdvertising();
        connectService.stopDiscovering();
        connectService.stopMedia(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBound) {
            connectService.startMedia(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopConnectionService();

        //TODO: put notification or widget for advertising... and stop discovering..
        //TODO: stop discovering, but possibly keep advertising.
        userManager.commit();
        userManager.removeListener(historyFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            connectService.removeListener(discoverFragment);
            stopConnectionService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBound) {
            startConnectionService();
        } else {
            // don't do anything...
        }
    }



    @Override
    public void onBackPressed() {
        if (mBound) {
            if (debug) {
                connectService.debug();
            }
            if (discoverFragment.rvAdapter == null) {return;}
            if (!discoverFragment.rvAdapter.isEmpty()) {
                connectService.onBackPressed();
                return;
            }

            if (debug) {
                return;
            }
        }

        super.onBackPressed();

        // associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        svSearch = findViewById(R.id.svSearch);
        svSearch.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        svSearch.setSubmitButtonEnabled(true);
        svSearch.setOnQueryTextListener(this);
    }

    // Feature to send eveything at once.
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (discoverFragment != null && discoverFragment.rvAdapter != null) {
            if (!discoverFragment.rvAdapter.isEmpty() &&
                    mGestureDetector.onKeyEvent(event)) {
                return true;
            }
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
        if (mBound) {
            if (!connectService.contains(listener)) {
                connectService.addListener(listener);
            }
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
