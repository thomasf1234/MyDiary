package com.abstractx1.mydiary.jobs;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.abstractx1.mydiary.DataCollector;
import com.example.demo.job.PermissionJob;

import java.io.IOException;

/**
 * Created by tfisher on 15/11/2016.
 */

//requests READ_EXTERNAL_STORAGE permission and then sets the external bitmap on the DataCollector Singleton
// and sets the image on the passed ImageView
public class GetAndSetExternalBitmapJob extends PermissionJob {
    public static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int PERMISSION_REQUEST_ID = 0;

    private ImageView imageView;
    private Uri imageUri;

    public GetAndSetExternalBitmapJob(AppCompatActivity appCompatActivity, Uri imageUri, ImageView imageView) {
        super(appCompatActivity);
        this.imageView = imageView;
        this.imageUri = imageUri;
    }

    @Override
    public void perform() throws IOException {
        DataCollector.getInstance().setImage(getImagePath(imageUri));
        imageView.setImageBitmap(DataCollector.getInstance().getImage());
        imageView.invalidate();
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




