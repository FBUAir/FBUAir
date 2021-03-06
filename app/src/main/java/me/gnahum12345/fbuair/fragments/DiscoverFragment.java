package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.util.HashSet;
import java.util.List;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.adapters.DiscoverAdapter;
import me.gnahum12345.fbuair.interfaces.ConnectionListener;
import me.gnahum12345.fbuair.interfaces.OnFragmentChangeListener;
import me.gnahum12345.fbuair.interfaces.OnProfileSentListener;
import me.gnahum12345.fbuair.managers.MyUserManager;
import me.gnahum12345.fbuair.models.ProfileUser;
import me.gnahum12345.fbuair.models.SocialMedia;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;

public class DiscoverFragment extends Fragment implements ConnectionListener, OnProfileSentListener {

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    public DiscoverAdapter rvAdapter;
    private Context mContext;
    private RecyclerView rvDevicesView;
    private HashSet<ConnectionService.Endpoint> deviceLst;
    //TODO: delete the listener.
    private DiscoverFragmentListener mListener;
    private TextView tvRVEmpty;
    private TextView tvRVEmptyAdd;
    private RippleBackground rippleBackground;
    private ImageView configureFAB;

    OnFragmentChangeListener onFragmentChangeListener;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * TODO: put in activity.
     * Returns {@code true} if the app was granted all the permissions. Otherwise, returns {@code
     * false}.
     */

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (!hasPermissions(mContext, ConnectionService.getRequiredPermissions())) {
            //TODO: show reasoning for why we need permissions and request them;
            permissionsNotGranted();

        }
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        tvRVEmpty = view.findViewById(R.id.tvRVEmptyView);
        tvRVEmptyAdd = view.findViewById(R.id.tvRVEmptyAddView);
        rvDevicesView = view.findViewById(R.id.rvDevicesView);
        rippleBackground = view.findViewById(R.id.rippleBackground);
        configureFAB = view.findViewById(R.id.configureFAB);

        tvRVEmpty.setVisibility(View.VISIBLE);
        tvRVEmptyAdd.setVisibility(View.VISIBLE);
        rippleBackground.setVisibility(View.VISIBLE);
        rippleBackground.startRippleAnimation();

        rvDevicesView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        rvDevicesView.setLayoutManager(layoutManager);
        rvDevicesView.setAdapter(rvAdapter);

        configureFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigureFragment configureFragment = new ConfigureFragment();
                configureFragment.show(getFragmentManager(), "");
                populateAdapter();
            }
        });
        mListener.addToListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof DiscoverFragmentListener) {
            mListener = (DiscoverFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        rvAdapter = new DiscoverAdapter(mContext, this);
        populateAdapter();
        onFragmentChangeListener = (OnFragmentChangeListener) context;
    }

    public void populateAdapter() {
        if (rvAdapter == null) {
            return;
        }
        List<ConnectionService.Endpoint> currEndpoints = mListener.getCurrEndpoints();
        for (ConnectionService.Endpoint endpoint : currEndpoints) {
            if (!rvAdapter.contains(endpoint)) {
                rvAdapter.add(endpoint);
            }
        }
        rvAdapter.notifyDataSetChanged();


        if (!rvAdapter.isEmpty()) {
            tvRVEmpty.setVisibility(View.GONE);
            tvRVEmptyAdd.setVisibility(View.GONE);
            rippleBackground.stopRippleAnimation();
            rippleBackground.setVisibility(View.GONE);
            rvDevicesView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateEndpoint(ConnectionService.Endpoint endpoint, Object userData, boolean isProfile) {
        if (isProfile) {
            if (rvAdapter == null) {
                return;
            }
            rvAdapter.put(endpoint, ((ProfileUser) userData));
            rvAdapter.notifyDataSetChanged();
        } else {
            if (userData instanceof User) {
                User user = (User) userData;
                saveUser(user, endpoint);
            }
        }
    }

    private void saveUser(User user, ConnectionService.Endpoint endpoint) {
        //TODO: save user.
        MyUserManager manager = MyUserManager.getInstance();
        manager.addUser(user, endpoint);
    }

    @Override
    public void addEndpoint(ConnectionService.Endpoint endpoint) {
        if (rvAdapter == null) {
            return;
        }
        rvAdapter.add(endpoint);
        tvRVEmpty.setVisibility(View.GONE);
        tvRVEmptyAdd.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        rippleBackground.setVisibility(View.GONE);
        rvDevicesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeEndpoint(ConnectionService.Endpoint endpoint) {
        if (rvAdapter == null) {
            return;
        }
        rvAdapter.remove(endpoint);
        if (rvAdapter.isEmpty()) {
            rvDevicesView.setVisibility(View.GONE);
            tvRVEmpty.setVisibility(View.VISIBLE);
            tvRVEmptyAdd.setVisibility(View.VISIBLE);
            rippleBackground.setVisibility(View.VISIBLE);
            rippleBackground.startRippleAnimation();
        }
    }

    private void permissionsNotGranted() {
        final String[] permissions = ConnectionService.getRequiredPermissions();

        if (((MainActivity) mContext).mConnectService == null) {return; }

        if (!((MainActivity) mContext).mConnectService.isDiscovering()) {
            //TODO: put dialog to agree to permissions in order to discover.
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle(R.string.permissions_explanation_title)
                    .setMessage(R.string.permissions_explanation_body)
                    .setPositiveButton("Let me see the permission!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(permissions, REQUEST_CODE_REQUIRED_PERMISSIONS);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            permissionsNotGranted();
                        }
                    });
            builder.create().show();
        }
    }

    //TODO possibly place inside the other fragment.
    @Override //TODO: put in Activity.
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(mContext, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();

                    return;
                }
            }
            ((MainActivity) mContext).recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public interface DiscoverFragmentListener {
        List<ConnectionService.Endpoint> getCurrEndpoints();
        void addToListener(ConnectionListener listener);
    }

    @Override
    public void onProfileSent(ProfileUser profileUser) {
        User currentUser = MyUserManager.getInstance().tryToGetCurrentUser();
        if (currentUser != null) {
            User sentToUser = new User();
            sentToUser.setName(profileUser.getName());
            sentToUser.setProfileImage(profileUser.getIvProfileImage());
            if (currentUser.isSendingEmail() || currentUser.isSendingPhone()) {
                sentToUser.setPhoneNumber("1");
            }
            for (SocialMedia socialMedia : currentUser.getSendingSocialMedias()) {
                sentToUser.addSocialMedia(socialMedia);
            }
            MyUserManager.getInstance().addSentToUser(sentToUser);
        }
        onFragmentChangeListener.onProfileSent();
    }

    // todo - attribute vector authors: <div>Icons made by <a href="https://www.flaticon.com/authors/smashicons" title="Smashicons">Smashicons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
}



