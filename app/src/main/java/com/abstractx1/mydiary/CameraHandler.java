package com.abstractx1.mydiary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

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
        if (hasCameraApp()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = Utilities.createFile(this.imagePath);
            Uri photoURI = FileProvider.getUriForFile(activity, CAPTURE_IMAGE_FILE_PROVIDER, photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            throw new RuntimeException("No camera apps installed");
        }
    }

    private boolean hasCameraApp() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return takePictureIntent.resolveActivity(activity.getPackageManager()) != null;
    }

    public String getImagePath() {
        return imagePath;
    }
}
