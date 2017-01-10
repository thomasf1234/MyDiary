package com.abstractx1.mydiary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;

/**
 * Created by tfisher on 10/01/2017.
 */

public class Device {
    public static boolean hasCameraApp(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return (hasSystemFeatureCamera(activity) && takePictureIntent.resolveActivity(activity.getPackageManager()) != null);
    }

    public static boolean hasSystemFeatureCamera(Activity activity) {
        boolean hasCamera = false;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            hasCamera = hasSystemFeature(activity, PackageManager.FEATURE_CAMERA_ANY);
        else if (hasSystemFeature(activity, PackageManager.FEATURE_CAMERA))
            hasCamera = true;
        else if (hasSystemFeature(activity, PackageManager.FEATURE_CAMERA_FRONT))
            hasCamera = true;

        return hasCamera;
    }

    public static boolean hasSystemFeatureMicrophone(Activity activity) {
        return hasSystemFeature(activity, PackageManager.FEATURE_MICROPHONE);
    }

    public static boolean hasSystemFeature(Activity activity, String feature) {
        return activity.getApplicationContext().getPackageManager().hasSystemFeature(feature);
    }
}
