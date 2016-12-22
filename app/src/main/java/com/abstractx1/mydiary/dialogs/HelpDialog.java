package com.abstractx1.mydiary.dialogs;

/**
 * Created by tfisher on 22/12/2016.
 */

import com.abstractx1.mydiary.MyDiaryActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.abstractx1.mydiary.GlobalApplicationValues;
import com.abstractx1.mydiary.R;


/**
 * Created by tfisher on 27/10/2016.
 */

public class HelpDialog {
    public static AlertDialog create(final MyDiaryActivity activity) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.help_dialog, null);
        TextView messageTextView = (TextView) view.findViewById(R.id.helpMessageTextView);
        String message = "If you have any questions, need clarification, face any issues, or need any support/assistance regarding this study, then please email the researcher at the following email address: '" + GlobalApplicationValues.getResearcherEmailAddress(activity) + "'.";

        messageTextView.setText(message);
        alertDialogBuilder.setTitle("Help");
        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialog;
    }
}


