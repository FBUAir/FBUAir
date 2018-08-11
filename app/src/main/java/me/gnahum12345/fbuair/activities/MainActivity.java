package me.gnahum12345.fbuair.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;

import java.util.ArrayList;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.databinding.ActivityMainBinding;
import me.gnahum12345.fbuair.fragments.DiscoverFragment;
import me.gnahum12345.fbuair.fragments.HistoryFragment;
import me.gnahum12345.fbuair.fragments.ProfileFragment;
import me.gnahum12345.fbuair.interfaces.ConnectionListener;
import me.gnahum12345.fbuair.interfaces.OnContactAddedCallback;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.interfaces.OnRequestAddContact;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;
import me.gnahum12345.fbuair.utils.ContactUtils;
import me.gnahum12345.fbuair.utils.Utils;

import static me.gnahum12345.fbuair.models.User.NO_COLOR;
import static me.gnahum12345.fbuair.utils.ImageUtils.getCircularBitmap;


public class MainActivity extends AppCompatActivity implements DiscoverFragment.DiscoverFragmentListener, OnFragmentChangeListener,
        OnRequestAddContact, ProfileFragment.ProfileFragmentListener {

    // request codes for permissions results
    final static int MY_PERMISSIONS_REQUEST_CONTACTS = 4;
    // fragment position aliases
    private final static int DISCOVER_FRAGMENT = 0;
    private final static int HISTORY_FRAGMENT = 1;
    private final static int PROFILE_FRAGMENT = 2;
    private final static int DETAILS_FRAGMENT = 4;

    ImageView profileImage;
    TextView name;

    private static final String TAG = "MainActivityTag";
    // The list of fragments used in the view pager
    private final List<Fragment> fragments = new ArrayList<>();
    public ActivityMainBinding bind;
    //Connection Service.
    public ConnectionService mConnectService;

    // fragments
    DiscoverFragment discoverFragment;
    HistoryFragment historyFragment;
    ProfileFragment profileFragment;
    ProfileFragment detailsFragment;

    MyUserManager mUserManager;
    // menus
    ActionMode mActionMode;
    OnContactAddedCallback mOnContactAddedCallback;
    // whether user granted Contacts permissions
    boolean mContactPermissionGranted;

    // The adapter used to display information for our bottom navigation view.
    private Adapter mPagerAdapter;

    private boolean mBound = false;
    private boolean mListened = false;
    private Menu mMenu;

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Because we have bound to an explicit
            // service that is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mConnectService = binder.getService();
            mBound = true;
            if (discoverFragment != null) {
                mConnectService.addListener(discoverFragment);
                mListened = true;
            }
            startConnectionService();
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "onServiceDisconnected");
            mBound = false;
            mListened = false;
            mConnectService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ActivityCompat.postponeEnterTransition(this);

        // set up ConnectionService
        setUpUserManager();

        // set up ConnectionService
        setUpConnectionService();

        // set actionbar to be toolbar
        setUpActionBar();

        // instantiate fragments
        discoverFragment = new DiscoverFragment();
        historyFragment = new HistoryFragment();
        profileFragment = new ProfileFragment();

        // Create the fragments to be passed to the ViewPager
        fragments.add(discoverFragment);
        fragments.add(historyFragment);
        fragments.add(profileFragment);
        // Instantiate our Adapter which we will use in our ViewPager
        mPagerAdapter = new Adapter(getSupportFragmentManager(), fragments);

        setUpViewPager();
        setUpBottomNavigation();
        addListener();
        // check whether user granted contacts permissions
        mContactPermissionGranted = checkPermissions();
    }

    private void setUpUserManager() {
        mUserManager = MyUserManager.getInstance();
        mUserManager.loadContacts();
        mUserManager.setNotificationAbility(true, this);
    }

    private void setUpActionBar() {
        setSupportActionBar(bind.toolbar);
        getSupportActionBar().setTitle("");
        bind.toolbarTitle.setText("Discover");

        Drawable d;
        if (MyUserManager.getInstance().getCurrentUser().getColor() == NO_COLOR) {
            Bitmap bitmapResized = Bitmap.createScaledBitmap(MyUserManager.getInstance().getCurrentUser().getProfileImage(), 45, 45, false);
            d = new BitmapDrawable(getResources(), getCircularBitmap(bitmapResized));
            bind.toolbarImage.setImageDrawable(d);
        } else {
            Bitmap profileImage = mUserManager.getCurrentUser().getProfileImage();
            if (profileImage != null) {
                d = new BitmapDrawable(getResources(), getCircularBitmap(profileImage));
                bind.toolbarImage.setImageDrawable(d);
            }
        }
    }

    private void setUpConnectionService() {
        Intent intent = new Intent(MainActivity.this, ConnectionService.class);
        if (!Utils.isMyServiceRunning(ConnectionService.class, this)) {
            startService(intent);
        }
        if (mConnectService == null) {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void setUpViewPager() {
        // Attach our adapter to our view pager.
        bind.viewPager.setAdapter(mPagerAdapter);
        bind.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled
                    (int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().show();
                switch (position) {
                    case DISCOVER_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(DISCOVER_FRAGMENT);
                        discoverFragment.populateAdapter();
                        bind.toolbarTitle.setText("Discover");
                        setActionModeVisible(false, null);
                        break;
                    case HISTORY_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(HISTORY_FRAGMENT);
                        bind.toolbarTitle.setText("Recents");
                        break;
                    case PROFILE_FRAGMENT:
                        bind.bottomNavigationView.setCurrentItem(PROFILE_FRAGMENT);
                        bind.toolbar.setVisibility(View.GONE);
                        setActionModeVisible(false, null);
                        getSupportActionBar().hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private void setUpBottomNavigation() {
        int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(bind.bottomNavigationView, tabColors);

        bind.bottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bind.bottomNavigationView.setNotificationBackgroundColor(fetchColor(R.color.notification));
        bind.bottomNavigationView.setColoredModeColors(fetchColor(R.color.gradient_blue), fetchColor(R.color.color_black));
        bind.bottomNavigationView.setTranslucentNavigationEnabled(true);
        bind.bottomNavigationView.setColored(true);
        bind.bottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.light_grey));
        // Handle the click for each item on the bottom navigation view.
        bind.bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                bind.viewPager.setCurrentItem(position, true);
                if (position == 0) {
                    discoverFragment.populateAdapter();
                    if (mMenu != null) {
                        mMenu.findItem(R.id.btnSendAll).setVisible(true);
                    }
                } else {
                    if (mMenu != null) {
                        mMenu.findItem(R.id.btnSendAll).setVisible(false);
                    }
                }
                if (position == 1) {
                    mUserManager.clearNotification();
                }
                return true;
            }
        });
    }
    private void addListener() {
        MyUserManager.getInstance().addListener(historyFragment);
        if (mBound && !mListened) {
            mConnectService.addListener(discoverFragment);
        }
    }
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }



    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    private void startConnectionService() {
        mConnectService.startDiscovering();
        mConnectService.startAdvertising();
        mConnectService.startMedia(this);
    }

    private void stopConnectionService() {
        if (mConnectService == null) {
            return;
        }
        mConnectService.stopAdvertising();
        mConnectService.stopDiscovering();
        mConnectService.stopMedia(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mConnectService != null) {
            mConnectService.startDiscovering();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mConnectService != null) {
            mConnectService.stopDiscovering();
        }
        mUserManager.commit();
        mUserManager.removeListener(historyFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            mConnectService.removeListener(discoverFragment);
            stopConnectionService();
            unbindService(mConnection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBound) {
            if (!mConnectService.isAdvertising()) {
                mConnectService.startAdvertising();
            }
            if (!mConnectService.isDiscovering()) {
                mConnectService.startDiscovering();
            }
        }
    }


    @Override
    public void onBackPressed() {
        // go back to history if currently in details
        if (bind.viewPager.getCurrentItem() == DETAILS_FRAGMENT) {
            bind.viewPager.setCurrentItem(HISTORY_FRAGMENT);
            fragments.remove(2);
            mPagerAdapter.notifyDataSetChanged();
        }
        if (mBound) {
            if (discoverFragment.rvAdapter != null) {
                if (!discoverFragment.rvAdapter.isEmpty()) {
                    mConnectService.onBackPressed();
                }
            }
        }
        super.onBackPressed();
    }



    @Override
    public List<ConnectionService.Endpoint> getCurrEndpoints() {
        List<ConnectionService.Endpoint> currEndpoints = new ArrayList<>();
        if (mConnectService == null) {
            return currEndpoints;
        }
        return mConnectService.getCurrentConnections();
    }

    @Override
    public void addToListener(ConnectionListener listener) {
        if (mBound) {
            if (!mConnectService.contains(listener)) {
                mConnectService.addListener(listener);
            }
        }
    }

    @Override
    public void setActionModeVisible(boolean flag, @Nullable ActionMode.Callback callback) {
        if (flag) {
            mActionMode = startActionMode(callback);
        }
        else if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    /* implementation for switching fragments (OnFragmentChangeListener) */
    @Override
    // opens details screen for passed in user
    public void launchDetails(String uid, View view) {
        // set transition(s)
        detailsFragment = ProfileFragment.newInstance(uid);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        profileImage = view.findViewById(R.id.ivProfileImage);
        name = view.findViewById(R.id.tvName);
        fragmentTransaction.setReorderingAllowed(true);

        fragmentTransaction.setCustomAnimations(R.animator.enter_right, R.animator.exit_left);
        fragmentTransaction.add(R.id.relative_view, detailsFragment, "detailsFragment").addToBackStack(null);
        fragmentTransaction.commit();

        setBottomNavigationVisible(false);
    }


    @Override
    public void launchEditProfile() {
    }


    @Override
    public void onDetailsBackPressed() {
        mUserManager.clearNotification();
        mUserManager.seenAllUsers();
        bind.bottomNavigationView.setCurrentItem(HISTORY_FRAGMENT, false);
        getSupportFragmentManager().popBackStack();
        setBottomNavigationVisible(true);
        historyFragment.populateHistory();
    }

    @Override
    public void launchUrlView(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void deleteAccount() {
        mUserManager.deleteCurrentUser();
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    // check for permissions and conflicts before adding contact
    @Override
    public void requestAddContact(String uid, OnContactAddedCallback onContactAddedCallback) {
        this.mOnContactAddedCallback = onContactAddedCallback;
        User user = MyUserManager.getInstance().getUser(uid);
        if (requestPermissionsIfNeeded()) {
            ContactUtils.AddContactResult addContactResult = ContactUtils.findConflict(this, user);
            if (addContactResult.getResultCode() == ContactUtils.SUCCESS) {
                addContact(user);
            } else showConflictDialog(user, addContactResult);
        }
    }

    /* CONTACT/CONTACT PERMISSIONS STUFF */

    // adds contact to phone and shows snackbar
    void addContact(User user) {
        String contactId = ContactUtils.addContact(this, user)[0];
        mOnContactAddedCallback.onSuccess();
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
        if (!mContactPermissionGranted) {
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
        mContactPermissionGranted = (requestCode == MY_PERMISSIONS_REQUEST_CONTACTS
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED);
        if (!mContactPermissionGranted) {
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
        if (mConnectService == null) {
            return;
        }
        ConnectionService.Endpoint e = MyUserManager.getInstance().avaliableEndpoint(uid);
        if (e != null) {
            mConnectService.sendToEndpoint(e);
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
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_discover, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: item selected" + item.getItemId());

        if (item.getItemId() == R.id.btnSendAll) {
            String msg = String.format("Are you sure you want to send everyone the following configuration? \n( %s )", mUserManager.getCurrentUser().getConfiguration());
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setTitle("Send All Confirmation!")
                                .setMessage(msg)
                                .setIcon(R.drawable.app_launcher)
                                .setPositiveButton("Send!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (mConnectService != null) {
                                            mConnectService.sendToAll();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(MainActivity.this, "Sending Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setMenuVisible(boolean flag) {
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (flag) supportActionBar.show();
            else supportActionBar.hide();
        }
    }

    @Override
    public void setBottomNavigationVisible(boolean flag) {
        if (flag) bind.bottomNavigationView.setVisibility(View.VISIBLE);
        else bind.bottomNavigationView.setVisibility(View.GONE);

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
