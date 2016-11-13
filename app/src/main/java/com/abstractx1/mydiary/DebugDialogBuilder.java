package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.File;

/**
 * Created by tfisher on 27/10/2016.
 */

public class DebugDialogBuilder extends AlertDialog.Builder {
    public DebugDialogBuilder(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        setTitle(R.string.debug_dialog_title);

        setMessage(buildMessage());

        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Cancel button
                dialog.dismiss();
            }
        });
    }

    private String buildMessage() {
        String message = "";

        File[] cacheFiles = Utilities.getCacheFiles(getContext());
        message += "File count: " + Integer.toString(cacheFiles.length) + "\r\n";
        for (File file : cacheFiles) {
            int fileSize = Integer.parseInt(String.valueOf(file.length()/1024));
            message += "  Path: " + file.getPath() + ", size: " + Integer.toString(fileSize) + "KB\r\n";
        }
        return message;
    }
}
