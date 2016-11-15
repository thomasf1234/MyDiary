package com.abstractx1.mydiary;

/**
 * Created by tfisher on 13/11/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utilities {
    public static void clearCache(Context context) {
        try {
            purgeDirectory(context.getCacheDir());
        } catch (Exception e) {
            Toast.makeText(context, "ERROR Could not clear cache", Toast.LENGTH_LONG).show();
        }
    }

    public static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) {
                purgeDirectory(file);
            }
            file.delete();
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

    public static File createFile(String name, String extension, File directory) throws IOException {
        File file = new File(directory, name + extension);
        file.createNewFile();

        return file;
    }

    public static File createFile(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();

        return file;
    }

    private static String getDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(new Date());
    }

    public static void moveFile(String src, String dst) throws IOException
    {
        File from = new File(src);
        File to = new File(dst);
        from.renameTo(to);
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
