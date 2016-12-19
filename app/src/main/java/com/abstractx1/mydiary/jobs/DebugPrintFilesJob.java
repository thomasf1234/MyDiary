package com.abstractx1.mydiary.jobs;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import com.abstractx1.mydiary.Debug;
import com.example.demo.job.PermissionJob;

/**
 * Created by tfisher on 16/12/2016.
 */
public class DebugPrintFilesJob  extends PermissionJob {
    public static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int PERMISSION_REQUEST_ID = 3;

    public DebugPrintFilesJob(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    @Override
    public void perform() throws Exception {
        Debug.logFiles(appCompatActivity);
    }

    @Override
    public int getPermissionRequestId() {
        return PERMISSION_REQUEST_ID;
    }

    @Override
    public String[] getPermissions() {
        return PERMISSIONS;
    }
}
