package me.gnahum12345.fbuair.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.adapters.DiscoverAdapter;
import me.gnahum12345.fbuair.models.GestureDetector;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;


import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class DiscoverActivity extends ConnectionsActivity implements SensorEventListener {

    // Instance variables.
    private RecyclerView rvDevicesView;
    private HashSet<Endpoint> deviceLst;
    private DiscoverAdapter rvAdapter;

    /**
     * The connection strategy we'll use for Nearby Connections. In this case, we've decided on
     * P2P_STAR, which is a combination of Bluetooth Classic and WiFi Hotspots.
     */
    private static final Strategy STRATEGY = Strategy.P2P_CLUSTER;

    /**
     * Acceleration required to detect a shake. In multiples of Earth's gravity.
     */
    private static final float SHAKE_THRESHOLD_GRAVITY = 2;

    /**
     * How long to vibrate the phone when we change states.
     */
    private static final long VIBRATION_STRENGTH = 500;
    /**
     * This service id lets us find other nearby devices that are interested in the same thing. Our
     * sample does exactly one thing, so we hardcode the ID.
     */
    private static final String SERVICE_ID =
            "com.google.location.nearby.apps.walkietalkie.manual.SERVICE_ID";

    /**
     * The state of the app. As the app changes states, the UI will update and advertising/discovery
     * will start/stop.
     */
    private State mState = State.UNKNOWN;

    /**
     * A random UID used as this device's endpoint name.
     */
    private String mName;

    /**
     * The SensorManager gives us access to sensors on the device.
     */
    private SensorManager mSensorManager;

    /**
     * The accelerometer sensor allows us to detect device movement for shake-to-advertise.
     */
    private Sensor mAccelerometer;

    /**
     * Listens to holding/releasing the volume rocker.
     */
    private final GestureDetector mGestureDetector =
            new GestureDetector(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP) {
                @Override
                protected void onHold() {
                    logV("onHold");
                    startRecording();
                }

                @Override
                protected void onRelease() {
                    logV("onRelease");
                    stopRecording();
                }
            };

    /**
     * The phone's original media volume.
     */
    private int mOriginalVolume;

    /**
     * A Handler that allows us to post back on to the UI thread. We use this to resume discovery
     * after an uneventful bout of advertising.
     */
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    /**
     * Starts discovery. Used in a postDelayed manor with {@link #mUiHandler}.
     */
    private final Runnable mDiscoverRunnable =
            new Runnable() {
                @Override
                public void run() {
                    setState(State.DISCOVERING);
                }
            };

    private void stopRecording() {
        logV("stopPlaying()");

    }

    private void startRecording() {
        logV("startRecording()");
        String senderInfo = "This is my info string"; //TODO: Change to be the user's data. ahahahahah
        send(Payload.fromBytes(senderInfo.getBytes()));
    }

    public void sendFromEndPoint(Endpoint endpoint) {
        String payloadInfo = "This is gonnna be one gigantically large json because we are soooooo coolll!!!!!";
        send(Payload.fromBytes(payloadInfo.getBytes()), endpoint);

    }


    @Override
    protected void updateAdapter(Endpoint endpoint) {
        deviceLst.remove(endpoint);
        rvAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Bind the views.
        rvDevicesView = findViewById(R.id.rvDevicesView);

        // Set up for RecyclerView.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        deviceLst = new HashSet<>();
        rvAdapter = new DiscoverAdapter(deviceLst);

        rvDevicesView.setLayoutManager(layoutManager);
        rvDevicesView.setAdapter(rvAdapter);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mName = generateRandomName();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mState == State.CONNECTED && mGestureDetector.onKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        // Set the media volume to max.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mOriginalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        setState(State.DISCOVERING);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);

        // Restore the original volume.
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginalVolume, 0);
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);


        setState(State.UNKNOWN);

        mUiHandler.removeCallbacksAndMessages(null);

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (getState() == State.CONNECTED || getState() == State.ADVERTISING) {
            stopAdvertising();
            stopDiscovering();
            setState(State.DISCOVERING);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onEndpointDiscovered(Endpoint endpoint) {
        // We found an advertiser!
        if (!isConnecting()) {
            connectToEndpoint(endpoint);
        }
    }


    @Override
    protected void onDiscoveryFailed() {
        disconnectFromAllEndpoints();
        deviceLst.clear();
        rvAdapter.notifyDataSetChanged();
        String msg = "StartDiscovery failed. \nIf this is the 3rd time seeing this message, please restart the app";
        Toast.makeText(this, msg, 1).show();

        setState(State.DISCOVERING);

    }


    @Override
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        // A connection to another device has been initiated! We'll accept the connection immediately.
        super.onConnectionInitiated(endpoint, connectionInfo);
        acceptConnection(endpoint);
    }

    @Override
    protected void onEndpointConnected(Endpoint endpoint) {
        Toast.makeText(
                this, getString(R.string.toast_connected, endpoint.getName()), Toast.LENGTH_SHORT)
                .show();
        rvAdapter.add(endpoint);
        deviceLst.add(endpoint);
        rvAdapter.notifyItemChanged(deviceLst.size() - 1);
        vibrate();
        // TODO: Send profile data and display that instead.
        setState(State.CONNECTED);
    }

    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Toast.makeText(
                this, getString(R.string.toast_disconnected, endpoint.getName()), Toast.LENGTH_SHORT)
                .show();

        // If we lost all our endpoints, then we should reset the state of our app and go back
        // to our initial state (discovering).
        deviceLst.remove(endpoint);
        rvAdapter.notifyDataSetChanged();

        if (getConnectedEndpoints().isEmpty()) {
            stopAdvertising();
            stopDiscovering();
            setState(State.DISCOVERING);
        }

    }

    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        // Let's try someone else.

        deviceLst.remove(endpoint);
        rvAdapter.notifyDataSetChanged();

        super.onConnectionFailed(endpoint);


        if (getState() == State.DISCOVERING && !getDiscoveredEndpoints().isEmpty()) {
            connectToEndpoint(pickRandomElem(getDiscoveredEndpoints()));
        }
        stopAdvertising();
        stopDiscovering();
        setState(State.DISCOVERING);
    }

    /**
     * The state has changed. I wonder what we'll be doing now.
     *
     * @param state The new state.
     */
    private void setState(State state) {
        if (mState == state) {
            logW("State set to " + state + " but already in that state");
            return;
        }

        logD("State set to " + state);
        State oldState = mState;
        mState = state;
        onStateChanged(oldState, state);
    }

    /**
     * @return The current state.
     */
    private State getState() {
        return mState;
    }

    /**
     * State has changed.
     *
     * @param oldState The previous state we were in. Clean up anything related to this state.
     * @param newState The new state we're now in. Prepare the UI for this state.
     */
    private void onStateChanged(State oldState, State newState) {
        // Update Nearby Connections to the new state.
        switch (newState) {
            case DISCOVERING:
                // do nothing and fall through to advertising.
                disconnectFromAllEndpoints();
                startDiscovering();
                break;
            case ADVERTISING:
                disconnectFromAllEndpoints();
                startDiscovering();
                startAdvertising();
                logD("I am advertising and discovering at the same time.");
                break;
            case CONNECTED:
                removeCallbacks(mDiscoverRunnable);
                logD("I connected but I'm still discovering and advertising");
                if (!isDiscovering()) {
                    startDiscovering(); // If connected, don't look for more connections... transfer payload... disconnect then continue...
                }
                if (!isAdvertising()) {
                    startAdvertising();
                }
                break;
            case UNKNOWN:
                stopAllEndpoints();
                break;
            default:
                // no-op
                break;
        }

    }



    /**
     * The device has moved. We need to decide if it was intentional or not.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY && getState() == State.DISCOVERING) {
            logD("Device shaken");
            vibrate();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


    /**
     * Vibrates the phone.
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (hasPermissions(this, Manifest.permission.VIBRATE) && vibrator.hasVibrator()) {
            vibrator.vibrate(VIBRATION_STRENGTH);
        }
    }

    /**
     * {@see ConnectionsActivity#onReceive(Endpoint, Payload)}
     */
    // TODO: send back info here.
    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {

        Toast.makeText(this, "I am on the received side", Toast.LENGTH_SHORT).show();

        if (payload.getType() == Payload.Type.BYTES) {
            byte[] b = payload.asBytes();
            String content = new String(b);
            logD(content);
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        }
    }


    public void onClick(MenuItem mi) {
        // handle click here
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * {@see ConnectionsActivity#getRequiredPermissions()}
     */
    @Override
    protected String[] getRequiredPermissions() {
        return super.getRequiredPermissions();
    }


    /**
     * Queries the phone's contacts for their own profile, and returns their name. Used when
     * connecting to another device.
     */
    @Override
    protected String getName() {
        return mName;
    }

    /**
     * {@see ConnectionsActivity#getServiceId()}
     */
    @Override
    public String getServiceId() {
        return SERVICE_ID;
    }

    /**
     * {@see ConnectionsActivity#getStrategy()}
     */
    @Override
    public Strategy getStrategy() {
        return STRATEGY;
    }


    /**
     * {@see Handler#removeCallbacks(Runnable)}
     */
    protected void removeCallbacks(Runnable r) {
        mUiHandler.removeCallbacks(r);
    }


    @Override
    protected void logV(String msg) {
        super.logV(msg);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_verbose)));
    }

    @Override
    protected void logD(String msg) {
        super.logD(msg);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_debug)));
    }

    @Override
    protected void logW(String msg) {
        super.logW(msg);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_warning)));
    }

    @Override
    protected void logW(String msg, Throwable e) {
        super.logW(msg, e);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_warning)));
    }

    @Override
    protected void logE(String msg, Throwable e) {
        super.logE(msg, e);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_error)));
    }

    private void appendToLogs(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private static CharSequence toColor(String msg, int color) {
        SpannableString spannable = new SpannableString(msg);
        spannable.setSpan(new ForegroundColorSpan(color), 0, msg.length(), 0);
        return spannable;
    }

    private static String generateRandomName() {
        String name = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            name += random.nextInt(10);
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    private static <T> T pickRandomElem(Collection<T> collection) {
        return (T) collection.toArray()[new Random().nextInt(collection.size())];
    }

    /**
     * Provides an implementation of Animator.AnimatorListener so that we only have to override the
     * method(s) we're interested in.
     */
    private abstract static class AnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
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
}
