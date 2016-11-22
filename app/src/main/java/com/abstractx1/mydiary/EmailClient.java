package com.abstractx1.mydiary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfisher on 12/11/2016.
 */

public class EmailClient {
    private Activity activity;

    public EmailClient(Activity activity) {
        this.activity = activity;
    }

    public void open(String toAddress) {
        String[] TO = {toAddress};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        try {
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
        emailIntent.setType("message/rfc822");
        return activity.getPackageManager().queryIntentActivities( emailIntent, 0);
    }

//    public void setChosenEmailApp() {
//
//    }

    //Title Screen = main screen
    //One Dialog
    //disable continue button
    //ask user to select an email app to use
    //display "No email applications installed." if none
    //upon selection, enable continue
    /*
|
|  Please select an email client
|  icon1 w/name  [x]
|  icon2  "      []
|  icon3  "      []
|             [continue] (enabled after click)
|_____________
continue buttton sets new layout.
http://stackoverflow.com/questions/10434473/how-can-i-get-the-icons-of-the-applications-in-a-list
http://stackoverflow.com/questions/32316002/get-the-name-icon-and-package-name-of-all-the-installed-applications-in-android
https://www.tutorialspoint.com/android/android_list_view.htm
Write into Shared preferences the name

or
|
|  No email clients installed.
|  This app requires an email
|  client such that the researcher
|  can be contacted etc.
|             [continue] (disabled)
|_____________

Next
|
|  Please read our terms and conditions
|  | .....            |^|
|  |                  | |
|  |__________________|v|
|
|   I agree and accept .. [x]
|             [continue] (disabled until checkbox)
|_____________



     */
}
