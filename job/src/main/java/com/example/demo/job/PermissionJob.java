package com.example.demo.job;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by tfisher on 25/10/2016.
 */

public abstract class PermissionJob extends Job {
    public PermissionJob(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    public boolean hasPermission() {
        for (String permission : getPermissions()) {
            if(!hasPermission(permission))
                return false;
        }
        return true;
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this.appCompatActivity.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ArrayList<String> permissionsNotGranted = new ArrayList<String>();

        for (String permission: getPermissions()) {
            if(!hasPermission(permission))
                permissionsNotGranted.add(permission);
        }

        if(!permissionsNotGranted.isEmpty()) {
            String [] permissionsToRequest = permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]);
            ActivityCompat.requestPermissions(this.appCompatActivity, permissionsToRequest, getPermissionRequestId());
        }
    }

    public abstract int getPermissionRequestId();

    public abstract String[] getPermissions();
}
