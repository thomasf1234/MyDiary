package com.abstractx1.mydiary.jobs;

/**
 * Created by tfisher on 16/11/2016.
 */

import android.Manifest;
import android.support.v7.app.AppCompatActivity;

import com.abstractx1.mydiary.record.RecordHandler;
import com.example.demo.job.PermissionJob;

/**
 * Created by tfisher on 25/10/2016.
 */

public class RecordJob extends PermissionJob {
    public static final String[] PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO};
    public static final int PERMISSION_REQUEST_ID = 0;

    private RecordHandler recordButtonHandler;

    public RecordJob(AppCompatActivity appCompatActivity, RecordHandler recordButtonHandler) {
        super(appCompatActivity);
        this.recordButtonHandler = recordButtonHandler;
    }

    @Override
    public void perform() throws Exception {
        //recordButtonHandler.initializeAndRecord();
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






