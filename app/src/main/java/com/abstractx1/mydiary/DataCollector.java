package com.abstractx1.mydiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by tfisher on 15/11/2016.
 */
//singleton class
public class DataCollector {

    private static DataCollector instance = null;
    private DataCollector() {
        // Exists only to defeat instantiation.
    }

    public static DataCollector getInstance() {
        if(instance == null) {
            instance = new DataCollector();
        }
        return instance;
    }

    private Bitmap image;

    public void setImage(String path) throws IOException {
        Bitmap bmp = BitmapFactory.decodeFile(path);
        ExifInterface exif = new ExifInterface(path);
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

        this.image = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

    }

    public void setImage(Bitmap bmp) throws IOException {
        this.image = bmp;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean hasImage() {
        return image != null;
    }
}