package me.gnahum12345.fbuair.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    public DiscoverAdapter rvAdapter;
    private boolean hasInit = false;
    private DiscoverFragmentListener mListener;
    private TextView tvRVEmpty;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            permissionsNotGranted();

        }
        hasInit = true;

        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        tvRVEmpty = view.findViewById(R.id.tvRVEmptyView);
        rvDevicesView = view.findViewById(R.id.rvDevicesView);

        tvRVEmpty.setVisibility(View.VISIBLE);
        rvDevicesView.setVisibility(View.GONE);
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
        if (context instanceof DiscoverFragmentListener) {
            mListener =  (DiscoverFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        rvAdapter = new DiscoverAdapter(mContext);
    }




    @Override
    public void updateEndpoint(ConnectionService.Endpoint endpoint, Object userData, boolean isProfile) {
        if (isProfile) {
            rvAdapter.put(endpoint, ((ProfileUser) userData));
            rvAdapter.notifyDataSetChanged();
        } else {
            if (userData instanceof User) {
                User user = (User) userData;
                saveUser(user);
            }
        }
    }

    private void saveUser(User user) {
        //TODO: save user.
        me.gnahum12345.fbuair.managers.UserManager manager = me.gnahum12345.fbuair.managers.UserManager.getInstance();
        manager.addUser(user);
    }


    @Override
    public void addEndpoint(ConnectionService.Endpoint endpoint) {
        rvAdapter.add(endpoint);
        tvRVEmpty.setVisibility(View.GONE);
        rvDevicesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeEndpoint(ConnectionService.Endpoint endpoint) {
        rvAdapter.remove(endpoint);
        if (rvAdapter.isEmpty()) {
            rvDevicesView.setVisibility(View.GONE);
            tvRVEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void notifyAdapter() {

    }

    public interface DiscoverFragmentListener {
        public void onPermissionsNotGranted();
    }

    private void permissionsNotGranted() {
        String[] permissions = ((MainActivity) mContext).connectService.getRequiredPermissions();
        //TODO: put dialog here.
        requestPermissions(permissions, REQUEST_CODE_REQUIRED_PERMISSIONS);
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
}



