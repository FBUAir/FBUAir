package me.gnahum12345.fbuair.managers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import me.gnahum12345.fbuair.R;
import me.gnahum12345.fbuair.models.User;
import me.gnahum12345.fbuair.services.ConnectionService;
import me.gnahum12345.fbuair.services.MyBroadcastReceiver;

public class AirNotificationManager {
    private static final AirNotificationManager ourInstance = new AirNotificationManager();

    public static AirNotificationManager getInstance() {
        return ourInstance;
    }

    private AirNotificationManager() {
    }

    private NotificationManager notificationManager;
    private Context mContext;
    private static final String CHANNEL_ID = "AirChannelID";
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private Map<User, Integer> userMap = new TreeMap<>();

    public void setContext(Context context) {
        mContext = context;
        createNotificationChannel(context);
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void createNotification(String title, String body, User user, ConnectionService.Endpoint endpoint) {
        int id;
        if (userMap.containsKey(user)) {
            id = userMap.get(user);
        } else {
            try {
                id = (int) Calendar.getInstance().getTimeInMillis();
            } catch (ClassCastException e) {
                id = user.hashCode();
            }
            userMap.put(user, id);
        }
        NotificationCompat.Builder mBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        } else {
            mBuilder = new NotificationCompat.Builder(mContext);
        }

        Intent intent = new Intent(mContext, MyBroadcastReceiver.class);
        intent.setAction(mContext.getString(R.string.reply_label));
        intent.putExtra(mContext.getString(R.string.extra_notification_label), 0);
        intent.putExtra("endpoint", endpoint.toString());
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);

        mBuilder.setSmallIcon(R.drawable.ic_discover)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(replyPendingIntent)
                .addAction(R.drawable.home_icon, mContext.getString(R.string.reply_label), replyPendingIntent);

        //TODO: Change the id.

        notificationManager.notify(id, mBuilder.build());
    }
}