package com.abstractx1.mydiary.dialog_builders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by tfisher on 19/11/2016.
 */

public class ConfirmationDialogBuilder extends AlertDialog.Builder {
    public ConfirmationDialogBuilder(Context context, String message) {
        super(context);
        setCancelable(false);
        setTitle("Confirmation");
        setMessage(message);
    }

    public ConfirmationDialogBuilder(Context context, String message, int icon) {
        super(context);
        setCancelable(false);
        setTitle("Confirmation");
        setIcon(icon);
        setMessage(message);
    }
}
