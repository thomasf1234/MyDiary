package com.abstractx1.mydiary.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.abstractx1.mydiary.Device;
import com.abstractx1.mydiary.Utilities;

import java.io.File;
import java.io.IOException;

/**
 * Created by tfisher on 14/11/2016.
 */

/*

 */

public class CameraHandler {
    //http://stackoverflow.com/questions/7720383/camera-intent-not-saving-photo
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    // your authority, must be the same as in your manifest file
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.abstractx1.fileprovider";
    private final Activity activity;
    public String imagePath;

    public CameraHandler(Activity activity, String imagePath) {
        this.activity = activity;
        this.imagePath = imagePath;
    }

    public void dispatchTakePictureIntent() throws IOException {
        if (Device.hasCameraApp(activity)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = Utilities.createFile(this.imagePath);
            Uri photoURI = FileProvider.getUriForFile(activity, CAPTURE_IMAGE_FILE_PROVIDER, photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            throw new RuntimeException("No camera apps installed");
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public static String getImagePath(Activity activity, Uri uri){
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = activity.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
