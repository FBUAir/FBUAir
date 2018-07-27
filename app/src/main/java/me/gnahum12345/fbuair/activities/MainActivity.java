package me.gnahum12345.fbuair.activities;

import android.app.SearchManager;
import android.content.Context;
import android.databinding.adapters.SearchViewBindingAdapter;
import android.graphics.Color;
import android.os.Bundle;
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
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.HistoryAdapter;
import me.gnahum12345.fbuair.fragments.DetailsFragment;
import me.gnahum12345.fbuair.fragments.DiscoverFragment;
import me.gnahum12345.fbuair.fragments.HistoryFragment;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.managers.UserManager;
import me.gnahum12345.fbuair.models.GestureDetector;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;


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
    //    BottomNavigationView bottomNavigation;
    AHBottomNavigation bottomNavigation;
    android.support.v7.widget.Toolbar toolbar;
    SearchView svSearch;
    // fragments
    DiscoverFragment discoverFragment;
    HistoryFragment historyFragment;
    ProfileFragment profileFragment;
    DetailsFragment detailsFragment;
    UserManager userManager;
    // menus
    RelativeLayout historyMenu;
    boolean debug;
    // A reference to our view pager.
    private AHBottomNavigationViewPager viewPager;
    // The adapter used to display information for our bottom navigation view.
    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debug = true;

        userManager = UserManager.getInstance();
        userManager.loadContacts(this);
        // set up ConnectionService
        connectService = new ConnectionService(this); //TODO: add the parameters that are missing.
        //TODO: delete this.
        //connectService.inputData();

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
                resetItems();
                switch (position) {
                    case DISCOVER_FRAGMENT:
//                        bottomNavigation.setSelectedItemId(R.id.action_discover);
                        bottomNavigation.setCurrentItem(0);
                        bottomNavigation.getItem(0).setColor(fetchColor(R.color.colorAccent));
                        break;
                    case HISTORY_FRAGMENT:
//                        bottomNavigation.setSelectedItemId(R.id.action_history);
                        bottomNavigation.setCurrentItem(1);
                        bottomNavigation.getItem(1).setColor(fetchColor(R.color.colorAccent));
                        historyMenu.setVisibility(View.VISIBLE);
                        break;
                    case PROFILE_FRAGMENT:
                        bottomNavigation.setCurrentItem(2);
                        bottomNavigation.getItem(2).setColor(fetchColor(R.color.colorAccent));
//                      bottomNavigation.setSelectedItemId(R.id.action_profile);
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
        bottomNavigation.setColoredModeColors(fetchColor(R.color.colorAccent), fetchColor(R.color.color_black));
        bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                viewPager.setCurrentItem(position, true);
                if (position == 1) {
                    bottomNavigation.setNotification("", 1);
                }
//                bottomNavigation.setColored(true);
                return true;
            }
        });


        // Handle the click for each item on the bottom navigation view.
//        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_discover:
//                        discoverFragment.notifyAdapter();
//                        viewPager.setCurrentItem(DISCOVER_FRAGMENT);
//                        return true;
//                    case R.id.action_history:
//                        viewPager.setCurrentItem(HISTORY_FRAGMENT);
//                        return true;
//                    case R.id.action_profile:
//                        viewPager.setCurrentItem(PROFILE_FRAGMENT);
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });

        connectService.addListener(discoverFragment);
    }

    private void resetItems() {
        for (int i = 0; i < bottomNavigation.getItemsCount(); i++) {
            bottomNavigation.getItem(i).setColor(fetchColor(R.color.color_black));
        }
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopConnectionService();
        userManager.commit();
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
        if (!discoverFragment.rvAdapter.isEmpty()) {
            connectService.onBackPressed();
            return;
        }

        if (debug) {
            connectService.debug();
            return;
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
        if (!discoverFragment.rvAdapter.isEmpty() &&
                mGestureDetector.onKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPermissionsNotGranted() {
        //change fragments to ask for permissions.
        requestPermissions(connectService.getRequiredPermissions(), REQUEST_CODE_REQUIRED_PERMISSIONS);
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
