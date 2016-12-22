package com.abstractx1.mydiary.dialogs;

/**
 * Created by tfisher on 22/12/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.abstractx1.mydiary.GlobalApplicationValues;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.jobs.SendDataJob;


/**
 * Created by tfisher on 27/10/2016.
 */

public class SendDialog {
    public static AlertDialog create(final MyDiaryActivity activity) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.help_dialog, null);
        TextView messageTextView = (TextView) view.findViewById(R.id.helpMessageTextView);
        String message = "Do you wish to save, finish, and submit your answers to the researcher?";

        messageTextView.setText(message);
        alertDialogBuilder.setTitle("Submit Answers");
        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    activity.triggerJob(new SendDataJob(activity));
                } catch (Exception e) {
                    activity.alert("Please contact the researcher, for some reason we could not submit data.");
                }
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialog;
    }
}


