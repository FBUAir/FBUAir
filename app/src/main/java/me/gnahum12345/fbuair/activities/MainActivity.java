package me.gnahum12345.fbuair.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.adapters.SearchViewBindingAdapter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.databinding.ActivityMainBinding;
import me.gnahum12345.fbuair.fragments.ConfigureFragment;
import me.gnahum12345.fbuair.fragments.DiscoverFragment;
import me.gnahum12345.fbuair.fragments.HistoryFragment;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.interfaces.ConnectionListener;
import me.gnahum12345.fbuair.interfaces.OnContactAddedCallback;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.GestureDetector;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;
import me.gnahum12345.fbuair.utils.ContactUtils;
import me.gnahum12345.fbuair.utils.Utils;


public class MainActivity extends AppCompatActivity implements DiscoverFragment.DiscoverFragmentListener,
        SearchViewBindingAdapter.OnQueryTextSubmit, SearchView.OnQueryTextListener, OnFragmentChangeListener,
        OnRequestAddContact, ProfileFragment.ProfileFragmentListener {

    public ActivityMainBinding bind;
    // fragment position aliases
    private final static int DISCOVER_FRAGMENT = 0;
    private final static int HISTORY_FRAGMENT = 1;
    private final static int PROFILE_FRAGMENT = 2;
    private final static int CONFIGURE_FRAGMENT = 3;
    private final static int DETAILS_FRAGMENT = 4;
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
    ProfileFragment profileFragment;
    ProfileFragment detailsFragment;
    ConfigureFragment configureFragment;

    MyUserManager userManager;
    // menus
    RelativeLayout historyMenu;
    boolean debug;

    OnContactAddedCallback onContactAddedCallback;

    // request codes for permissions results
    final static int MY_PERMISSIONS_REQUEST_CONTACTS = 4;
    // whether user granted Contacts permissions
    boolean contactPermissionGranted;

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
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        userManager = MyUserManager.getInstance();
        userManager.loadContacts();
        userManager.setNotificationAbility(true, this);
        // set up ConnectionService

        Intent intent = new Intent(MainActivity.this, ConnectionService.class);

        if (!Utils.isMyServiceRunning(ConnectionService.class, this)) {
            startService(intent);
        }

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // set actionbar to be toolbar
        setSupportActionBar(bind.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Discover");

        // instantiate fragments
        discoverFragment = new DiscoverFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();
        detailsFragment = new ProfileFragment();
        configureFragment = new ConfigureFragment();

        // Create the fragments to be passed to the ViewPager
        fragments.add(discoverFragment);
        fragments.add(historyFragment);
        fragments.add(profileFragment);
        fragments.add(configureFragment);
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
                        bind.historyMenu.setVisibility(View.INVISIBLE);
                        getSupportActionBar().setTitle("Discover");
                        break;
                    case HISTORY_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(1);
                        bind.historyMenu.setVisibility(View.VISIBLE);
                        getSupportActionBar().setTitle("Recent");
                        break;
                    case PROFILE_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(2);
                        getSupportActionBar().hide();
                        break;
                    case CONFIGURE_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(3);
                        getSupportActionBar().setTitle("Configure");
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
        bind.bottomNavigationView.setColoredModeColors(fetchColor(R.color.gradient_blue), fetchColor(R.color.color_black));
        bind.bottomNavigationView.setTranslucentNavigationEnabled(true);
        bind.bottomNavigationView.setColored(true);
        // Handle the click for each item on the bottom navigation view.
        bind.bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                bind.viewPager.setCurrentItem(position, true);
                if (position == 1) {
                    MyUserManager.getInstance().clearNotification();
                }
                return true;
            }
        });

        MyUserManager.getInstance().addListener(historyFragment);
        if (mBound && !listened) {
            connectService.addListener(discoverFragment);
        }

        // associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        bind.svSearch.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        bind.svSearch.setSubmitButtonEnabled(true);
        bind.svSearch.setOnQueryTextListener(this);

        // check whether user granted contacts permissions
        contactPermissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
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
        if (connectService != null) {
            connectService.startDiscovering();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopConnectionService();
        if (connectService != null) {
            connectService.stopDiscovering();
        }
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

    @Override
    public void startAction(ActionMode.Callback callback) {
        startActionMode(callback);
    }
    void clearMenus() {
        bind.historyMenu.setVisibility(View.GONE);
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

    /* implementation for switching fragments (OnFragmentChangeListener) */
    @Override
    // opens details screen for passed in user
    public void launchDetails(String uid) {
        fragments.set(DETAILS_FRAGMENT, ProfileFragment.newInstance(uid));
        bind.bottomNavigationView.setCurrentItem(-1);
        bind.viewPager.setCurrentItem(DETAILS_FRAGMENT, false);

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    public void launchEditProfile() {
    }

    @Override
    public void launchUrlView(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void deleteAccount() {
        userManager.deleteCurrentUser();
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }
    

    /* implementations for searching through history */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (historyFragment.historyAdapter == null) {
            return false;
        }
        historyFragment.historyAdapter.getFilter().filter(query);
        return true;
    }

    /* CONTACT/CONTACT PERMISSIONS STUFF */

    // check for permissions and conflicts before adding contact
    @Override
    public void requestAddContact(String uid, OnContactAddedCallback onContactAddedCallback) {
        this.onContactAddedCallback = onContactAddedCallback;
        User user = MyUserManager.getInstance().getUser(uid);
        if (requestPermissionsIfNeeded()) {
            ContactUtils.AddContactResult addContactResult = ContactUtils.findConflict(this, user);
            if (addContactResult.getResultCode() == ContactUtils.SUCCESS) {
                addContact(user);
            } else showConflictDialog(user, addContactResult);
        }
    }

    // adds contact to phone and shows snackbar
    void addContact(User user) {
        String contactId = ContactUtils.addContact(this, user)[0];
        onContactAddedCallback.onSuccess();
        showContactAddedDialog(contactId);
    }

    // shows options to undo and/or view as fake snackbar at bottom
    void showContactAddedDialog(String contactId) {
        Snackbar snackbar = Snackbar.make(bind.getRoot(),
                R.string.contact_added_message, Snackbar.LENGTH_LONG);
        snackbar.setAction("View", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUtils.viewContact(getBaseContext(), contactId);
            }
        });
        snackbar.show();
    }

    // shows dialog about contact duplicate w/ action options
    void showConflictDialog(User user, ContactUtils.AddContactResult addContactResult) {
        int messageId = addContactResult.getResultCode() == ContactUtils.EMAIL_CONFLICT ?
                R.string.email_conflict_message : R.string.phone_conflict_message;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(messageId)
                .setTitle("Contact duplicate")
                .setPositiveButton("Add anyway", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addContact(user);
                    }
                })
                .setNegativeButton("View existing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContactUtils.viewContact(getBaseContext(), addContactResult.getContactId());
                    }
                });
        builder.show();
    }

    // requests permissions if needed and returns true if permission is granted
    boolean requestPermissionsIfNeeded() {
        if (!contactPermissionGranted) {
            requestPermissions(
                    new String[]
                            {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_CONTACTS);
            return false;
        }
        return true;
    }

    // show rationale for needing contact permissions and offer to request permissions again
    void showPermissionsRationale() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getBaseContext());
        builder.setMessage(R.string.contact_permissions_rationale)
                .setTitle("Permission Denied")
                .setPositiveButton("I'm Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .setNegativeButton("Re-Try", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.WRITE_CONTACTS},
                                MY_PERMISSIONS_REQUEST_CONTACTS);
                    }
                })
                .setCancelable(false);
        builder.show();
    }

    // result after user accepts/denies permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // set permissionsGranted variable to true if user granted all requested permissions. false otherwise.
        contactPermissionGranted = (requestCode == MY_PERMISSIONS_REQUEST_CONTACTS
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED);
        if (!contactPermissionGranted) {
            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
            // user checked "never ask again", show them message to go to Settings to change
            if (!showRationale) {
                showPermissionDeniedForeverDialog();
            }
            // user denied but didn't press press "never ask again". show rationale and request permission again
            else {
                showPermissionsRationale();
            }
        }
    }

    @Override
    public void sendBack(String uid) {
        if (connectService == null) {
            return;
        }
        ConnectionService.Endpoint e = MyUserManager.getInstance().avaliableEndpoint(uid);
        if (e != null) {
            connectService.sendToEndpoint(e);
        }
    }


    // allow user to go to settings to manually grant permissions if denied and pressed "Never show again"
    void showPermissionDeniedForeverDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Go to App Permissions in Settings to change this.")
                .setTitle("Missing Contact Permissions")
                // go to settings if user wants to
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_discover, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: item selected" + item.getItemId());

        if (item.getItemId() == R.id.miCompose) {
            bind.bottomNavigationView.setCurrentItem(-1);
            bind.viewPager.setCurrentItem(CONFIGURE_FRAGMENT, false);
        }
        return super.onOptionsItemSelected(item);
    }
}
