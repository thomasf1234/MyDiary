package com.abstractx1.mydiary;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.abstractx1.mydiary.broadcast_receivers.MyDiaryBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by tfisher on 15/12/2016.
 */

public class MyDiaryApplication extends Application {
    // uncaught exception handler variable
    private Thread.UncaughtExceptionHandler defaultUEH;

    // handler listener
    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable e) {
                    log(e);
                    // re-throw critical exception further to the os (important)
                    defaultUEH.uncaughtException(thread, e);
                }
            };

    public MyDiaryApplication() {
        super();
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        // setup handler for uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
    }

    public static void log(Throwable e) {
        Log.e(getLogKey(), "ERROR", e);
        Log.e(getLogKey(), Log.getStackTraceString(e));
    }

    public static void log(Throwable e, String message) {
        Log.e(getLogKey(), message, e);
        Log.e(getLogKey(), Log.getStackTraceString(e));
    }

    public static void log(String message) {
        Log.v(getLogKey(), message);
    }

    public static String getClassName() {
        return MyDiaryApplication.class.getSimpleName();
    }
    public static String getLogKey() {
        return "DEBUG-" + getClassName();
    }

    public static void initializeAlarm(Context context) {
        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, GlobalApplicationValues.getNotificationHour(context));
        alarmStartTime.set(Calendar.MINUTE, GlobalApplicationValues.getNotificationMinute(context));
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, MyDiaryBroadcastReceiver.class);
        intent.setAction(MyDiaryBroadcastReceiver.NOTIFICATION);
        intent.putExtra(MyDiaryBroadcastReceiver.ACTION_ID, MyDiaryBroadcastReceiver.ACTION_REMINDER);
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
