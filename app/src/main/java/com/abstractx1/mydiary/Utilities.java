package com.abstractx1.mydiary;

/**
 * Created by tfisher on 13/11/2016.
 */

import android.content.Context;
import android.widget.Toast;

import java.io.File;

public class Utilities {
    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Toast.makeText(context, "ERROR Could not clear cache", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static File[] getCacheFiles(Context context) {
        File dir = context.getCacheDir();
        File[] subFiles = new File[0];
        if (dir != null && dir.isDirectory()) {
            subFiles = dir.listFiles();
        }

        return subFiles;
    }

    public static String formatSeconds(int pTime) {
        return String.format("%01d:%02d", pTime / 60, pTime % 60);
    }
}
