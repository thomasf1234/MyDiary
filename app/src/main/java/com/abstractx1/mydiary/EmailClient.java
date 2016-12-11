package com.abstractx1.mydiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfisher on 12/11/2016.
 */

public class EmailClient {
    private static final String EMAIL_CLIENT_PACKAGE_NAME_KEY = "email_client_package_name";
    private static final String MESSAGE_TYPE = "message/rfc822";
    private Activity activity;

    public EmailClient(Activity activity) {
        this.activity = activity;
    }

    public void open(String toAddress) {
        String[] TO = {toAddress};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(MESSAGE_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        try {
            emailIntent.setPackage(getChosenEmailAppPackage());
            Intent gmailIntent = Intent.createChooser(emailIntent, "Send mail...");
            activity.startActivity(gmailIntent);
            activity.finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void open(String toAddress, String[] attachments) {
        String[] TO = {toAddress};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(MESSAGE_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        String subjectTitle = "Test";
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectTitle);
        for (String file_path : attachments) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(file_path)));
        }

        try {
            emailIntent.setPackage(getChosenEmailAppPackage());
            Intent gmailIntent = Intent.createChooser(emailIntent, "Send mail...");
            activity.startActivity(gmailIntent);
            activity.finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public List<AppInfo> getInstalledEmailApps() {
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        PackageManager packageManager = activity.getPackageManager();

        for(ResolveInfo resolveInfo : getInstalledEmailAppsResolveInfo()) {
            appInfos.add(new AppInfo(packageManager, resolveInfo));
        }

        return appInfos;
    }

    private List<ResolveInfo> getInstalledEmailAppsResolveInfo() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(MESSAGE_TYPE);
        return activity.getPackageManager().queryIntentActivities( emailIntent, 0);
    }

    public void setChosenEmailApp(AppInfo appInfo) {
        SharedPreferences sharedpreferences = activity.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(EMAIL_CLIENT_PACKAGE_NAME_KEY, appInfo.getPackageName());
        editor.commit();
    }

    public String getChosenEmailAppPackage() {
        SharedPreferences sharedpreferences = activity.getSharedPreferences(GlobalApplicationValues.USER_SETTINGS_KEY, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(EMAIL_CLIENT_PACKAGE_NAME_KEY)) {
            return sharedpreferences.getString(EMAIL_CLIENT_PACKAGE_NAME_KEY, "");
        } else
            return null;
    }

    public boolean hasChosenEmailAppPackage() {
        return getChosenEmailAppPackage() != null;
    }
}
