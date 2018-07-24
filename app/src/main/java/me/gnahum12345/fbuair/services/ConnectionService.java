package me.gnahum12345.fbuair.services;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.activities.DiscoverActivity;
import me.gnahum12345.fbuair.activities.MainActivity;
import me.gnahum12345.fbuair.models.GestureDetector;
import me.gnahum12345.fbuair.models.ProfileUser;
import me.gnahum12345.fbuair.models.User;

import static me.gnahum12345.fbuair.models.ProfileUser.MyPREFERENCES;


public class ConnectionService {

    /**
     * These permissions are required before connecting to Nearby Connections. Only {@link
     * Manifest.permission#ACCESS_COARSE_LOCATION} is considered dangerous. The others should be granted.
     */
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    private static final String TAG = "ConnectionService";
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    private static final String SERVICE_ID =
            "com.fbuair.apps.air.discovery.automatic.SERVICE_ID";
    /*Devices we've discovered near us..*/
    private final Map<String, Endpoint> mDiscoveredEndpoints = new HashMap<>();
    /**
     * The devices we have pending connections to. They will stay pending until we call {@link
     * #acceptConnection(Endpoint)} or {@link #rejectConnection(Endpoint)}.
     */
    private final Map<String, Endpoint> mPendingConnections = new HashMap<>();
    /**
     * The devices we are currently connected to. For advertisers, this may be large. For discoverers,
     * there will only be one entry in this map.
     */
    private final Map<String, Endpoint> mEstablishedConnections = new HashMap<>();
    private Context mContext; // TODO: see if needed.
    /**
     * Callbacks for payloads sent form another device to us.
     */
    private final PayloadCallback mPayloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    logD(String.format("onPayloadReceived(endpointId=%s, payload=%s)", endpointId, payload));
                    onReceive(mEstablishedConnections.get(endpointId), payload);
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    logD(
                            String.format(
                                    "onPayloadTransferUpdate(endpointId=%s, update=%s)", endpointId, update));
                }
            };
    /**
     * The state of the app. As the app changes states, the UI will update and advertising/discovery
     * will start/stop.
     */
    private State mState = State.UNKNOWN;
    private String mName;
    /**
     * My endpoint is to optimistically connect devices and
     * make them stable.
     */
    Endpoint mEndpoint = new Endpoint("4DS1", getName());   // TODO change to be a resonable id. (Perferably the actual id)
    /**
     * The phone's original media volume.
     */
    private int mOriginalVolume;
    /**
     * Our handler to Nearby Connections.
     */
    private ConnectionsClient mConnectionsClient;
    /**
     * Listens to holding/releasing the volume rocker.
     */
    private final GestureDetector mGestureDetector =
            new GestureDetector(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP) {
                @Override
                protected void onHold() {
                    logV("onHold");
//                    startRecording();
                    sendToAll();
                }

            };
    /**
     * True if we are asking a discovered device to connect to us. While we ask, we cannot ask another
     * device.
     */
    private boolean mIsConnecting = false;
    /**
     * True if we are discovering.
     */
    private boolean mIsDiscovering = false;
    /**
     * True if we are advertising.
     */
    private boolean mIsAdvertising = false;
    /**
     * Callbacks for connections to other devices.
     */
    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    logD(
                            String.format(
                                    "onConnectionInitiated(endpointId=%s, endpointName=%s)",
                                    endpointId, connectionInfo.getEndpointName()));
                    Endpoint endpoint = new Endpoint(endpointId, connectionInfo.getEndpointName());
                    mPendingConnections.put(endpointId, endpoint);
                    ((MainActivity) mContext).connectService.onConnectionInitiated(endpoint, connectionInfo);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    logD(String.format("onConnectionResponse(endpointId=%s, result=%s)", endpointId, result));

                    // We're no longer connecting
                    mIsConnecting = false;

                    if (!result.getStatus().isSuccess()) {
                        logW(
                                String.format(
                                        "Connection failed. Received status %s.",
                                        ConnectionService.toString(result.getStatus())));
                        onConnectionFailed(mPendingConnections.remove(endpointId));
                        return;
                    }
                    connectedToEndpoint(mPendingConnections.remove(endpointId));
                }

                @Override
                public void onDisconnected(String endpointId) {
                    if (!mEstablishedConnections.containsKey(endpointId)) {
                        logW("Unexpected disconnection from endpoint " + endpointId);
                        return;
                    }
                    disconnectedFromEndpoint(mEstablishedConnections.get(endpointId));
                }
            };

    ProfileUser mProfileUser;
    // TODO: give parameters to the constructor so everything can flow smoothly.
    public ConnectionService(Context context) {
        mContext = context;
        mConnectionsClient = Nearby.getConnectionsClient(mContext);
        // Set the media volume to max.
        ((MainActivity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mOriginalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        mProfileUser = new ProfileUser(context);
        mName = mProfileUser.getName() + context.getString(R.string.divider) + generateRandomName();
    }



    private static CharSequence toColor(String msg, int color) {
        SpannableString spannable = new SpannableString(msg);
        spannable.setSpan(new ForegroundColorSpan(color), 0, msg.length(), 0);
        return spannable;
    }

    private static String generateRandomName() {
        String name = "";
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            name += random.nextInt(10);
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    private static <T> T pickRandomElem(Collection<T> collection) {
        return (T) collection.toArray()[new Random().nextInt(collection.size())];
    }

    /**
     * Transforms a {@link Status} into a English-readable message for logging.
     *
     * @param status The current status
     * @return A readable String. eg. [404]File not found.
     */
    private static String toString(Status status) {
        return String.format(
                Locale.US,
                "[%d]%s",
                status.getStatusCode(),
                status.getStatusMessage() != null
                        ? status.getStatusMessage()
                        : ConnectionsStatusCodes.getStatusCodeString(status.getStatusCode()));
    }

    private void sendToAll() {
        logV("startRecording()");
        String senderInfo = "This is my info string"; //TODO: Change to be the user's data. ahahahahah
        send(Payload.fromBytes(senderInfo.getBytes()));
    }

    public void sendToEndpoint(Endpoint endpoint) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String current_user = sharedpreferences.getString("current_user", null);

        send(Payload.fromBytes(current_user.getBytes()), endpoint);
    }

    public void stopMedia() {
        // Restore the original volume.
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginalVolume, 0);
        ((MainActivity) mContext).setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    //TODO: call in main activity.
    public void onBackPressed() {
        if (getState() == State.CONNECTED) {
            stopAdvertising();
            stopDiscovering();
            setState(State.DISCOVERING);
            return;
        }
    }

    private State getState() {
        return mState;
    }

    private void setState(State newState) {
        mState = newState;
    }



    /**
     * Called when a pending connection with a remote endpoint is created. Use {@link ConnectionInfo}
     * for metadata about the connection (like incoming vs outgoing, or the authentication token). If
     * we want to continue with the connection, call {@link #acceptConnection(Endpoint)}. Otherwise,
     * call {@link #rejectConnection(Endpoint)}.
     */
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        logD("onConnectionInitiated: " + endpoint.getName() + "intiated. ");
        acceptConnection(endpoint);
    }

    /**
     * Sets the device to advertising mode. It will broadcast to other devices in discovery mode.
     * Either {@link #onAdvertisingStarted()} or {@link #onAdvertisingFailed()} will be called once
     * we've found out if we successfully entered this mode.
     */
    public void startAdvertising() {
        if (!mIsAdvertising) {
            mIsAdvertising = true;
            final String localEndpointName = getName();
            mConnectionsClient
                    .startAdvertising(
                            localEndpointName,
                            getServiceId(),
                            mConnectionLifecycleCallback,
                            new AdvertisingOptions(getStrategy()))
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unusedResult) {
                                    logV("Now advertising endpoint " + localEndpointName);
                                    onAdvertisingStarted();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mIsAdvertising = false;
                                    logW("startAdvertising() failed.", e);
                                    onAdvertisingFailed();
                                }
                            });
        }
    }

    /**
     * Stops advertising.
     */
    public void stopAdvertising() {
        mIsAdvertising = false;
        mConnectionsClient.stopAdvertising();
    }

    /**
     * Returns {@code true} if currently advertising.
     */
    protected boolean isAdvertising() {
        return mIsAdvertising;
    }

    /**
     * Called when advertising successfully starts. Override this method to act on the event.
     */
    protected void onAdvertisingStarted() {
    }

    /**
     * Called when advertising fails to start. Override this method to act on the event.
     */
    protected void onAdvertisingFailed() {
    }


    /**
     * Accepts a connection request.
     */
    protected void acceptConnection(final Endpoint endpoint) {
        mConnectionsClient
                .acceptConnection(endpoint.getId(), mPayloadCallback)
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                logW("acceptConnection() failed.", e);
                            }
                        });
    }

    /**
     * Rejects a connection request.
     */
    protected void rejectConnection(Endpoint endpoint) {
        mConnectionsClient
                .rejectConnection(endpoint.getId())
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                logW("rejectConnection() failed.", e);
                            }
                        });
    }

    /**
     * Sets the device to discovery mode. It will now listen for devices in advertising mode. Either
     * {@link #onDiscoveryStarted()} or {@link #onDiscoveryFailed()} will be called once we've found
     * out if we successfully entered this mode.
     */
    protected void startDiscovering() {
        if (!isDiscovering()) {
            mIsDiscovering = true;
            mDiscoveredEndpoints.clear();

            mConnectionsClient
                    .startDiscovery(
                            getServiceId(),
                            new EndpointDiscoveryCallback() {
                                @Override
                                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                                    logD(
                                            String.format(
                                                    "onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",
                                                    endpointId, info.getServiceId(), info.getEndpointName()));

                                    Toast.makeText(mContext, "This is a toast when i found a user", Toast.LENGTH_SHORT).show();

                                    if (getServiceId().equals(info.getServiceId())) {
                                        Endpoint endpoint = new Endpoint(endpointId, info.getEndpointName());
                                        mDiscoveredEndpoints.put(endpointId, endpoint);
                                        onEndpointDiscovered(endpoint);
                                    }
                                }

                                @Override
                                public void onEndpointLost(String endpointId) {
                                    logD(String.format("onEndpointLost(endpointId=%s)", endpointId));
                                }
                            },
                            new DiscoveryOptions(getStrategy()))
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unusedResult) {
                                    onDiscoveryStarted();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mIsDiscovering = false;
                                    logW("startDiscovering() failed.", e);
                                    onDiscoveryFailed();
                                }
                            });
        }
        mEndpoint = new Endpoint(Integer.toString(mConnectionsClient.getInstanceId()), getName());

    }

    /**
     * Stops discovery.
     */
    protected void stopDiscovering() {
        mIsDiscovering = false;
        mConnectionsClient.stopDiscovery();
    }

    /**
     * Returns {@code true} if currently discovering.
     */
    protected boolean isDiscovering() {
        return mIsDiscovering;
    }

    /**
     * Called when discovery successfully starts. Override this method to act on the event.
     */
    protected void onDiscoveryStarted() {

    }

    /**
     * Called when discovery fails to start. Override this method to act on the event.
     */
    protected void onDiscoveryFailed() {
        disconnectFromAllEndpoints();
        logW(String.format("StartDiscovery has failed. \nisDiscovering: %b", isDiscovering()));
    }

    /**
     * Called when a remote endpoint is discovered. To connect to the device, call {@link
     * #connectToEndpoint(Endpoint)}.
     */
    protected void onEndpointDiscovered(Endpoint endpoint) {
        // We found an advertiser!
        logV("I discovered a new endpoint\n" +
                String.format("Endpoint(id={%s}, name={%s}", endpoint.getId(), endpoint.getName()));

        int result = endpoint.compareTo(mEndpoint);
        logV(String.format("Comparing the 2 endpoints: %d", result));

        if (result > 0) {
            connectToEndpoint(endpoint);
            logV("I am connecting to a new endpoint\n" +
                    String.format("Endpoint(id={%s}, name={%s}", endpoint.getId(), endpoint.getName()));
        }
    }

    /**
     * Disconnects from the given endpoint.
     */
    protected void disconnect(Endpoint endpoint) {
        mConnectionsClient.disconnectFromEndpoint(endpoint.getId());
        mEstablishedConnections.remove(endpoint.getId());
    }

    /**
     * Disconnects from all currently connected endpoints.
     */
    protected void disconnectFromAllEndpoints() {
        for (Endpoint endpoint : mEstablishedConnections.values()) {
            mConnectionsClient.disconnectFromEndpoint(endpoint.getId());
        }
        mEstablishedConnections.clear();
    }

    /**
     * Resets and clears all state in Nearby Connections.
     */
    protected void stopAllEndpoints() {
        mConnectionsClient.stopAllEndpoints();
        mIsAdvertising = false;
        mIsDiscovering = false;
        mIsConnecting = false;
        mDiscoveredEndpoints.clear();
        mPendingConnections.clear();
        mEstablishedConnections.clear();
    }

    /**
     * Sends a connection request to the endpoint. Either {@link #onConnectionInitiated(Endpoint,
     * ConnectionInfo)} or {@link #onConnectionFailed(Endpoint)} will be called once we've found out
     * if we successfully reached the device.
     */
    protected void connectToEndpoint(final Endpoint endpoint) {
        logV("Sending a connection request to endpoint " + endpoint);
        // Mark ourselves as connecting so we don't connect multiple times
        mIsConnecting = true;

        // Ask to connect
        mConnectionsClient
                .requestConnection(getName(), endpoint.getId(), mConnectionLifecycleCallback)
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                logW("requestConnection() failed.", e);
                                mIsConnecting = false;
                                onConnectionFailed(endpoint);
                            }
                        });
    }

    /**
     * Returns {@code true} if we're currently attempting to connect to another device.
     */
    protected final boolean isConnecting() {
        return mIsConnecting;
    }

    private void connectedToEndpoint(Endpoint endpoint) {
        logD(String.format("connectedToEndpoint(endpoint=%s)", endpoint));
        mEstablishedConnections.put(endpoint.getId(), endpoint);
        onEndpointConnected(endpoint);
    }

    private void disconnectedFromEndpoint(Endpoint endpoint) {
        logD(String.format("disconnectedFromEndpoint(endpoint=%s)", endpoint));
        mEstablishedConnections.remove(endpoint.getId());
        onEndpointDisconnected(endpoint);
    }

    /**
     * Called when a connection with this endpoint has failed. Override this method to act on the
     * event.
     */
    protected void onConnectionFailed(Endpoint endpoint) {
        mDiscoveredEndpoints.remove(endpoint);
        disconnect(endpoint);
        connectedToEndpoint(endpoint);

    }

    /**
     * Called when someone has connected to us. Override this method to act on the event.
     */
    protected void onEndpointConnected(Endpoint endpoint) {
        Toast.makeText(mContext, mContext.getString(R.string.toast_connected, endpoint.getName()), Toast.LENGTH_SHORT).show();
        sendProfileUser(endpoint);
        //TODO update listeners.
    }

    protected void sendProfileUser(Endpoint endpoint) {
        ProfileUser profileUser = new ProfileUser(mContext);
        Payload payload = Payload.fromBytes(profileUser.toString().getBytes());
        send(payload, endpoint);
    }

    /**
     * Called when someone has disconnected. Override this method to act on the event.
     */
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Toast.makeText(mContext, mContext.getString(R.string.toast_disconnected, endpoint.getName()), Toast.LENGTH_SHORT).show();
        //TODO update listeners.

        if (getConnectedEndpoints().isEmpty()) {
            stopDiscovering();
            stopAdvertising();
            startAdvertising();
            startDiscovering();
        }
    }

    /**
     * Returns a list of currently connected endpoints.
     */
    protected Set<Endpoint> getDiscoveredEndpoints() {
        Set<Endpoint> endpoints = new HashSet<>();
        endpoints.addAll(mDiscoveredEndpoints.values());
        return endpoints;
    }

    /**
     * Returns a list of currently connected endpoints.
     */
    protected Set<Endpoint> getConnectedEndpoints() {
        Set<Endpoint> endpoints = new HashSet<>();
        endpoints.addAll(mEstablishedConnections.values());
        return endpoints;
    }

    /**
     * Sends a {@link Payload} to all currently connected endpoints.
     *
     * @param payload The data you want to send.
     */
    protected void send(Payload payload) {
        send(payload, mEstablishedConnections.keySet());
    }

    //TODO: this is how we send data backwards.
    private void send(Payload payload, Set<String> endpoints) {
        mConnectionsClient
                .sendPayload(new ArrayList<>(endpoints), payload)
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                logW("sendPayload() failed.", e);
                            }
                        });

    }

    public void send(Payload payload, final Endpoint endpoint) {

        //TODO: CHECK THAT THE GIVEN STRING IS THE ID AND NOT THE NAME.
        mConnectionsClient.sendPayload(endpoint.getId(), payload)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logW("sendPayload() Failed given an endpoint", e);
                        updateAdapter(endpoint);
                    }
                });
    }

    /**
     * If the payload failed to send, then make sure to update the adapter to no longer show that user.
     *
     * @param endpoint The sender.
     */
    protected void updateAdapter(Endpoint endpoint) {

    }

    /**
     * Someone connected to us has sent us data. Override this method to act on the event.
     *
     * @param endpoint The sender.
     * @param payload  The data.
     */
    protected void onReceive(Endpoint endpoint, Payload payload) {
        Toast.makeText(mContext, "I am on the received side", Toast.LENGTH_SHORT).show();

        if (payload.getType() == Payload.Type.BYTES) {
            byte[] b = payload.asBytes();
            String content = new String(b);
            logD(content);
            Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();

            User user;
            ProfileUser profileUser;
            boolean userMade = false;

            try {
                user = User.fromString(content);
                userMade = true;
                logV("userMade = true");
                //TODO: update listeners to deal with the user.
            } catch (JSONException e) {
                e.printStackTrace();
                logE("User cannot be created", e);
                userMade = false;
            }


            if (!userMade) {
                try {
                    profileUser = ProfileUser.fromJSONString(content);
                    // TODO: UPdate listeners to deal with the profile
                } catch (JSONException e) {
                    e.printStackTrace();
                    logE("The file wasn't a profile either", e);
                }
            }
        }
    }

    /**
     * An optional hook to pool any permissions the app needs with the permissions ConnectionService
     * will request.
     *
     * @return All permissions required for the app to properly function.
     */
    protected String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }


    private Strategy getStrategy() {
        return STRATEGY;
    }

    private String getServiceId() {
        return ""; //TODO: Fix it to return service id.
    }

    private String getName() {
        return mName;
    }


    /**
     * TODO: put in activity.
     * Returns {@code true} if the app was granted all the permissions. Otherwise, returns {@code
     * false}.
     */
//    public static boolean hasPermissions(Context context, String... permissions) {
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(context, permission)
//                    != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }

//    @Override TODO: put in Activity.
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
//            for (int grantResult : grantResults) {
//                if (grantResult == PackageManager.PERMISSION_DENIED) {
//                    Toast.makeText(mContext, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
//
//                    return;
//                }
//            }
//            recreate();
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
    private void logV(String msg) {
        Log.v(TAG, msg);
        appendToLogs(toColor(msg, mContext.getResources().getColor(R.color.log_verbose)));
    }

    private void logD(String msg) {
        Log.d(TAG, msg);
        appendToLogs(toColor(msg, mContext.getResources().getColor(R.color.log_debug)));
    }

    private void logW(String msg) {
        Log.w(TAG, msg);
        appendToLogs(toColor(msg, mContext.getResources().getColor(R.color.log_warning)));
    }

    private void logW(String msg, Throwable e) {
        Log.w(TAG, msg, e);
        appendToLogs(toColor(msg, mContext.getResources().getColor(R.color.log_warning)));
    }

    private void logE(String msg, Throwable e) {
        Log.e(TAG, msg, e);
        appendToLogs(toColor(msg, mContext.getResources().getColor(R.color.log_error)));
    }

    private void appendToLogs(CharSequence msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * States that the UI goes through.
     */
    public enum State {
        UNKNOWN,
        DISCOVERING,
        ADVERTISING,
        CONNECTED
    }

    /**
     * Represents a device we can talk to.
     */
    public static class Endpoint implements Comparable {
        @NonNull
        private final String id;
        @NonNull
        private final String name;


        private Endpoint(@NonNull String id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof Endpoint) {
                Endpoint other = (Endpoint) obj;
                return id.equals(other.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Endpoint{id=%s, name=%s}", id, name);
        }

        @Override
        public int compareTo(@NonNull Object o) {
            if (o instanceof Endpoint) {
                Endpoint e = (Endpoint) o;
                int result = getName().compareTo(e.getName());
                return result == 0 ? 1 : result;
            } else {
                return -1;
            }
        }
    }

}