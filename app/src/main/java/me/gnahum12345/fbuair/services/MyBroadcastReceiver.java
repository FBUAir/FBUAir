package me.gnahum12345.fbuair.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class RestartService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ConnectionService.class));
    }
}
