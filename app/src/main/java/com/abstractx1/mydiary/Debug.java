package com.abstractx1.mydiary;

import android.content.Context;

import java.io.File;

/**
 * Created by tfisher on 16/12/2016.
 */

public class Debug {
    public static void logFiles(Context context) {
        File[] cacheFiles = Utilities.getCacheFiles(context);
        File[] internalFiles = Utilities.getFiles(context);
        File[] externalFiles = Utilities.getExternalFiles();

        MyDiaryApplication.log("Dumping file Paths:");
        MyDiaryApplication.log("Cache File count: " + Integer.toString(cacheFiles.length));
        for (File file : cacheFiles ) {
            int fileSize = Integer.parseInt(String.valueOf(file.length()/1024));
            MyDiaryApplication.log("Path: " + file.getAbsolutePath() + " " + Integer.toString(fileSize) + "KB");
        }

        MyDiaryApplication.log("Internal File count: " + Integer.toString(internalFiles.length));
        for (File file : internalFiles ) {
            int fileSize = Integer.parseInt(String.valueOf(file.length()/1024));
            MyDiaryApplication.log("Path: " + file.getAbsolutePath() + " " + Integer.toString(fileSize) + "KB");
        }

        MyDiaryApplication.log("External File count: " + Integer.toString(externalFiles.length));
        for (File file : externalFiles ) {
            int fileSize = Integer.parseInt(String.valueOf(file.length()/1024));
            MyDiaryApplication.log("Path: " + file.getAbsolutePath() + " " + Integer.toString(fileSize) + "KB");
        }
    }
}
