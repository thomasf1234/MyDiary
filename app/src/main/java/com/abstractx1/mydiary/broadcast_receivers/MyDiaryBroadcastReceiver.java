package com.abstractx1.mydiary.broadcast_receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.abstractx1.mydiary.MyDiaryApplication;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.TitleActivity;

public class MyDiaryBroadcastReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION = "com.abstractx1.mydiary.NOTIFICATION";
    public static final String ACTION_ID = "com.abstractx1.mydiary.ACTION_ID";
    public static final String ACTION_REMINDER = "com.abstractx1.mydiary.ACTION_REMINDER";

    private static final int NOTIFICATION_ID = 0;

    public MyDiaryBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        MyDiaryApplication.log("NotificationService onHandleIntent received action: " + action.toString());

        if (action.equals(NOTIFICATION)) {
            Bundle extras = intent.getExtras();

            if (extras.containsKey(ACTION_ID)) {
                String actionType = extras.getString(ACTION_ID);
                MyDiaryApplication.log("NotificationService onHandleIntent received actionType " + actionType);

                if (actionType.equals(ACTION_REMINDER)) {
                    sendReminderNotification(context);
                    MyDiaryApplication.setAlarm(context, true);
                } else {
                    MyDiaryApplication.log("NotificationService onHandleIntent received unknown actionType " + actionType);
                }
            }
        } else if (action.equals(android.content.Intent.ACTION_BOOT_COMPLETED)) {
            MyDiaryApplication.log("NotificationService onHandleIntent received ACTION_BOOT_COMPLETED. Resetting alarm.");
            MyDiaryApplication.setAlarm(context, false);

        } else {
            MyDiaryApplication.log("NotificationService onHandleIntent received unknown intent action" + action);
        }
    }

    private void sendReminderNotification(Context context) {
        Resources resources = context.getResources();
        Intent notificationIntent = new Intent(context, TitleActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.my_diary_launcher_icon))
                        .setContentTitle("MyDiary")
                        .setContentText("Reminder to input entry.")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}