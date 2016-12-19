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
    private static final String MESSAGE_TYPE = "message/rfc822";
    private Activity activity;

    public EmailClient(Activity activity) {
        this.activity = activity;
    }

    public void open(String toAddress, String subject) {
        open(toAddress, subject, new String[]{});
    }

    public void open(String toAddress, String subject, String[] attachments) {
        String[] TO = {toAddress};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType(MESSAGE_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        for (String file_path : attachments) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(file_path)));
        }

        try {
            emailIntent.setPackage(getGmailAppPackage());
            Intent gmailIntent = Intent.createChooser(emailIntent, "Send mail...");
            activity.startActivity(gmailIntent);
            activity.finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getGmailAppPackage() {
        for (AppInfo appInfo : getInstalledEmailApps()) {
            if(appInfo.getPackageName().endsWith(".gm") || appInfo.getName().toLowerCase().contains("gmail")) {
                return appInfo.getPackageName();
            }
        }
        return null;
    }

    private List<AppInfo> getInstalledEmailApps() {
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
}
