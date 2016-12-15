package com.abstractx1.mydiary;

import android.app.Application;
import android.util.Log;

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
}
