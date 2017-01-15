package com.abstractx1.mydiary.jobs;

/**
 * Created by tfisher on 16/11/2016.
 */

import android.Manifest;
import android.support.v7.app.AppCompatActivity;

import com.abstractx1.mydiary.Utilities;
import com.abstractx1.mydiary.record.RecordHandler;
import com.example.demo.job.PermissionJob;

/**
 * Created by tfisher on 25/10/2016.
 */

public class StartRecordingJob extends PermissionJob {
    public static final String[] PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO};
    public static final int PERMISSION_REQUEST_ID = 1;

    private RecordHandler recordHandler;

    public StartRecordingJob(AppCompatActivity appCompatActivity, RecordHandler recordHandler) {
        super(appCompatActivity);
        this.recordHandler = recordHandler;
    }

    @Override
    public void perform() throws Exception {
        try {
            recordHandler.transitionTo(RecordHandler.RECORDING);
        } catch (Exception e) {
            recordHandler.transitionTo(RecordHandler.CANCELLED);
            Utilities.alert(appCompatActivity, "We apologize, but the recording feature is not supported on this phone.");
        }
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






