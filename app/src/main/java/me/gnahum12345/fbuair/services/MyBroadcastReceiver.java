package me.gnahum12345.fbuair.services;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import me.gnahum12345.fbuair.R;


public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        String action = intent.getAction();
        if (action != null && action.equals(context.getString(R.string.reply_label))) {
            replyIntent(context, intent);
        } else if (action != null && action.equals(context.getString(R.string.connection_service))) {
            context.startService(new Intent(context, ConnectionService.class));
        }
    }

    private void replyIntent(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        StringBuilder sb = new StringBuilder();
        sb.append("Action: "+ intent.getAction() + "\n");
        sb.append("userID: " + intent.getStringExtra("user"));
        Log.d("BroadCastReceiverTAG", "replyIntent: " + sb.toString());
    }
}


/**
 *
 String action = intent.getAction();

 if (action == null) {
 // do nothing
 } else if (action.equals(context.getString(R.string.reply_label))) {
 replyIntent(context, intent);
 } else if (action.equals(context.getString(R.string.connection_service))) {
 context.startService(new Intent(context, ConnectionService.class));
 }


 File file = new File("D:/test.txt");
 FileInputStream fis1 = new FileInputStream(file);
 FileDescriptor fd = fis1.getFD();
 //passing FileDescriptor to another  FileInputStream
 FileInputStream fis2 = new FileInputStream(fd);
 int i=0;
 while((i=fis2.read())!=-1){
 System.out.print((char)i);
 }
 fis1.close();
 fis2.close();
 */