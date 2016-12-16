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
        SharedPreferences sharedpreferences = context.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(RESEARCHER_EMAIL_ADDRESS_KEY, value);
        editor.commit();
    }
}
