package me.gnahum12345.fbuair.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.fragments.DiscoverFragment;
import me.gnahum12345.fbuair.fragments.HistoryFragment;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.services.ConnectionService;

public class MainActivity extends AppCompatActivity implements DiscoverFragment.DiscoverFragmentListener{

    //TODO: CHECK IF THIS IS RIGHT.
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    // references to bottom navigation bar and toolbar
    BottomNavigationView bottomNavigation;
    android.support.v7.widget.Toolbar toolbar;

    // fragments
    DiscoverFragment discoverFragment;
    HistoryFragment historyFragment;
    ProfileFragment profileFragment;

    //Connection Service.
    public ConnectionService connectService;

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

        // set up ConnectionService
        connectService = new ConnectionService(this); //TODO: add the parameters that are missing.

        // set actionbar to be toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // instantiate fragments
        discoverFragment = new DiscoverFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();

        // Create the fragments to be passed to the ViewPager
        fragments.add(discoverFragment);
        fragments.add(historyFragment);
        fragments.add(profileFragment);

        // Grab a reference to our view pager.
        viewPager = findViewById(R.id.viewPager);

        // Instantiate our Adapter which we will use in our ViewPager
        adapter = new Adapter(getSupportFragmentManager(), fragments);

        // Attach our adapter to our view pager.
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled
                    (int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.action_discover);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.action_history);
                        break;
                    case 2:
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
                        // Set the item to the first item in our list (discover)
                        startConnectionService();
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.action_history:
                        // Set the item to the first item in our list (history)
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.action_profile:
                        // Set the current item to the third item in our list (profile)
                        viewPager.setCurrentItem(2);
                        return true;
                    default:
                        return false;
                }
            }
        });
        connectService.addListener(discoverFragment);
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
    protected void onStop() {
        super.onStop();
        stopConnectionService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startConnectionService();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        connectService.onBackPressed();
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


    // Feature to send eveything at once.
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (connectService.getState() == ConnectionService.State.CONNECTED &&
                connectService.mGestureDetector.onKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPermissionsNotGranted() {
        //change fragments to ask for permissions.
    }

    //TODO possibly place inside the other fragment.
    @Override //TODO: put in Activity.
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();

                    return;
                }
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
