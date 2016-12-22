package com.abstractx1.mydiary.jobs;

import android.Manifest;
import android.content.Intent;
import com.abstractx1.mydiary.TitleActivity;
import com.example.demo.job.PermissionJob;

import java.io.IOException;

/**
 * Created by tfisher on 15/11/2016.
 */

public class GetAndSetExternalBitmapJob extends PermissionJob {
    public static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int PERMISSION_REQUEST_ID = 0;

    public GetAndSetExternalBitmapJob(TitleActivity titleActivity) {
        super(titleActivity);
    }

    @Override
    public void perform() throws IOException {
        appCompatActivity.startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), TitleActivity.REQUEST_GET_FROM_GALLERY);
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




