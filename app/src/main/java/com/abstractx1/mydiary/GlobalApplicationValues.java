package com.abstractx1.mydiary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tfisher on 23/11/2016.
 */

public class GlobalApplicationValues {
    public static final String USER_SETTINGS_KEY = "user_settings";
    public static final String ACCEPTED_TERMS_AND_CONDITIONS_KEY = "accepted_terms_and_conditions";
    public static final String RESEARCHER_EMAIL_ADDRESS_KEY = "researcher_email_address";
    public static final String NOTIFICATION_HOUR_KEY = "notification_hour";
    public static final int DEFAULT_NOTIFICATION_HOUR = 21;
    public static final String NOTIFICATION_MINUTE_KEY = "notification_minute";

    public static boolean hasAcceptedTermsAndConditions(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(ACCEPTED_TERMS_AND_CONDITIONS_KEY)) {
            return sharedpreferences.getBoolean(ACCEPTED_TERMS_AND_CONDITIONS_KEY, false);
        } else
            return false;
    }

    public static void acceptTermsAndConditions(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(ACCEPTED_TERMS_AND_CONDITIONS_KEY, true);
        editor.commit();
    }

    public static String getResearcherEmailAddress(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(RESEARCHER_EMAIL_ADDRESS_KEY)) {
            return sharedpreferences.getString(RESEARCHER_EMAIL_ADDRESS_KEY, null);
        } else
            return null;
    }

    public static void editResearcherEmailAddress(Context context, String value) {
        MyDiaryApplication.log(String.format("Editing shared preferences key '%s' to value '%s'", RESEARCHER_EMAIL_ADDRESS_KEY, value));
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(RESEARCHER_EMAIL_ADDRESS_KEY, value);
        editor.commit();
    }

    public static int getNotificationHour(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(NOTIFICATION_HOUR_KEY)) {
            return sharedpreferences.getInt(NOTIFICATION_HOUR_KEY, DEFAULT_NOTIFICATION_HOUR);
        } else
            return DEFAULT_NOTIFICATION_HOUR;
    }

    public static void editNotificationHour(Context context, int value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(NOTIFICATION_HOUR_KEY, value);
        editor.commit();
    }

    public static int getNotificationMinute(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(NOTIFICATION_MINUTE_KEY)) {
            return sharedpreferences.getInt(NOTIFICATION_MINUTE_KEY, 0);
        } else
            return 0;
    }

    public static void editNotificationMinute(Context context, int value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(NOTIFICATION_MINUTE_KEY, value);
        editor.commit();
    }
}
