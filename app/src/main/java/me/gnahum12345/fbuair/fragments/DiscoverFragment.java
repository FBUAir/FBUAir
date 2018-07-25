package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.nio.charset.MalformedInputException;
import java.util.HashSet;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.adapters.DiscoverAdapter;
import me.gnahum12345.fbuair.models.ProfileUser;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionListener;
import me.gnahum12345.fbuair.services.ConnectionService;

public class DiscoverFragment extends Fragment implements ConnectionListener {

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private Context mContext;
    private RecyclerView rvDevicesView;
    private HashSet<ConnectionService.Endpoint> deviceLst;
    private DiscoverAdapter rvAdapter;

    private DiscoverFragmentListener mListener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (!hasPermissions(mContext, ConnectionService.getRequiredPermissions())) {
            //TODO: show reasoning for why we need permissions and request them;
            mListener.onPermissionsNotGranted();
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        rvDevicesView = view.findViewById(R.id.rvDevicesView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        rvDevicesView.setLayoutManager(layoutManager);
        rvDevicesView.setAdapter(rvAdapter);
        ((MainActivity) mContext).connectService.addListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mListener = (MainActivity) context;
        rvAdapter = new DiscoverAdapter();

    }




    @Override
    public void updateEndpoint(ConnectionService.Endpoint endpoint, Object userData, boolean isProfile) {
        if (isProfile) {
            rvAdapter.put(endpoint, ((ProfileUser) userData));
        } else {
            if (userData instanceof User) {
                User user = (User) userData;
                saveUser(user);
            }
        }
    }

    private void saveUser(User user) {
        //TODO: save user.
    }


    @Override
    public void addEndpoint(ConnectionService.Endpoint endpoint) {
        rvAdapter.add(endpoint);
    }

    @Override
    public void removeEndpoint(ConnectionService.Endpoint endpoint) {
        rvAdapter.remove(endpoint);
    }

    public interface DiscoverFragmentListener {
        public void onPermissionsNotGranted();
    }
}



