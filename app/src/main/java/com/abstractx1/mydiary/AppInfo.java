package com.abstractx1.mydiary;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by tfisher on 22/11/2016.
 */

public class AppInfo {
    String appName;
    String packageName;
    Drawable icon;

    public AppInfo(PackageManager packageManager, ResolveInfo resolveInfo) {
        this.appName = resolveInfo.loadLabel(packageManager).toString();
        this.packageName = resolveInfo.activityInfo.packageName;
        this.icon = resolveInfo.loadIcon(packageManager);
    }

    public String getName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
