package com.abstractx1.mydiary.jobs;

import android.Manifest;
import android.app.AlertDialog;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.abstractx1.mydiary.Researcher;
import com.abstractx1.mydiary.TitleActivity;
import com.abstractx1.mydiary.dialogs.ScreenShotDialog;
import com.example.demo.job.PermissionJob;

import java.io.IOException;

/**
 * Created by tfisher on 15/11/2016.
 */

//requests READ_EXTERNAL_STORAGE permission and then sets the external bitmap on the DataCollection Singleton
// and sets the image on the passed ImageView
public class GetAndSetExternalBitmapJob extends PermissionJob {
    public static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int PERMISSION_REQUEST_ID = 0;

    private Uri imageUri;
    private TitleActivity titleActivity;

    public GetAndSetExternalBitmapJob(TitleActivity titleActivity, Uri imageUri) {
        super(titleActivity);
        this.imageUri = imageUri;
        this.titleActivity = titleActivity;
    }

    @Override
    public void perform() throws IOException {
        Researcher.getInstance().setImagePath(getImagePath(imageUri));
        AlertDialog alertDialog = ScreenShotDialog.create(titleActivity);
        alertDialog.show();
    }

    @Override
    public int getPermissionRequestId() {
        return PERMISSION_REQUEST_ID;
    }

    @Override
    public String[] getPermissions() {
        return PERMISSIONS;
    }

    public String getImagePath(Uri uri){
        Cursor cursor = appCompatActivity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = appCompatActivity.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}




