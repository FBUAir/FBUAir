package me.gnahum12345.fbuair.services;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.nearby.connection.Payload;

import java.io.FileNotFoundException;

import me.gnahum12345.fbuair.R;


public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadCastReceiverTAG";
    private ConnectionService.Endpoint endpoint;

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        String action = intent.getAction();
        if (action != null && action.equals(context.getString(R.string.reply_label))) {
            replyIntent(context, intent);
        } else if (action != null && action.equals(context.getString(R.string.connection_service))) {
//            context.startService(new Intent(context, ConnectionService.class));
        }
    }

    private void replyIntent(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("user: " + intent.getStringExtra("user") + "\n");
        sb.append("endpoint: " + intent.getStringExtra("endpoint"));
        endpoint = ConnectionService.Endpoint.fromString(intent.getStringExtra("endpoint"));
        Log.d(TAG, String.format("Endpoint created: %s", endpoint.toString()));
        Intent connectionServiceIntent = new Intent(context, ConnectionService.class);
        IBinder binder = peekService(context, connectionServiceIntent);
        if (binder == null) {
            Log.d(TAG, "replyIntent: Binder is null");
            return;
        }
        ConnectionService service = ((ConnectionService.LocalBinder) binder).getService();

        service.sendToEndpoint(endpoint);
        Log.d(TAG, "replyIntent: file sent!");
        Log.d(TAG, "replyIntent: " + sb.toString());
    }
}
