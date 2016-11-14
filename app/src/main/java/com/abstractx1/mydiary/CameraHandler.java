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
    public String filePath;

    public CameraHandler(Activity activity, String filePath) {
        this.activity = activity;
        this.filePath = filePath;
    }

    public void dispatchTakePictureIntent() throws IOException {
        if (hasCameraApp()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = Utilities.createFile(this.filePath);
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

    public Bitmap getBitmap() throws IOException {
        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        ExifInterface exif = new ExifInterface(filePath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                break;
        }

        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
    }

    public boolean hasImage() {
        File file = new File(filePath);
        if(file.exists()) {
            int fileSize = Integer.parseInt(String.valueOf(file.length()));
            return fileSize > 0;
        } else {
            return false;
        }
    }
}
