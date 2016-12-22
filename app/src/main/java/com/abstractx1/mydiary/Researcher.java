package com.abstractx1.mydiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfisher on 07/12/2016.
 */
public class Researcher {
    private static Researcher ourInstance = new Researcher();

    public static Researcher getInstance() {
        return ourInstance;
    }

    private Researcher() {

        this.dataCollections = new ArrayList<>();
        this.caption = "";
    }

    private List<DataCollection> dataCollections;
    private String imagePath;
    private Bitmap image;
    private String caption;

    public List<DataCollection> getDataCollections() {
        return dataCollections;
    }

    public DataCollection getDataCollection(int questionNumber) {
        for (DataCollection dataCollection : dataCollections) {
            if (dataCollection.getQuestionNumber() == questionNumber) {
                return dataCollection;
            }
        }

        return null;
    }

    public boolean hasData() {
        return dataCollections.size() > 0;
    }

    public boolean hasImagePath() {
        return imagePath != null;
    }

    public Bitmap getImage() {
        return image;
    }

    public Boolean hasImage() {
        return image != null;
    }

    public void loadImage() throws IOException {
        Bitmap bmp = BitmapFactory.decodeFile(getImagePath());
        ExifInterface exif = new ExifInterface(getImagePath());
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) throws IOException {
        this.imagePath = imagePath;
        loadImage();
    }

    public String getCaption() {
        return caption;
    }

    public boolean hasCaption() {
        return !Utilities.isNullOrBlank(getCaption());
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
