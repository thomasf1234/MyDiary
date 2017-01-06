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
import com.abstractx1.mydiary.MyDiaryApplication;
import com.abstractx1.mydiary.R;


/**
 * Created by tfisher on 27/10/2016.
 */

public class ExpiredDialog {
    public static void show(final MyDiaryActivity activity) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        alertDialogBuilder.setCancelable(false);
        View view = inflater.inflate(R.layout.help_dialog, null);
        TextView messageTextView = (TextView) view.findViewById(R.id.helpMessageTextView);
        String message = "This application has expired. Please uninstall the application";

        messageTextView.setText(message);
        alertDialogBuilder.setTitle("Expired");
        alertDialogBuilder.setView(view);


        alertDialogBuilder.setPositiveButton("Uninstall", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MyDiaryApplication) activity.getApplication()).uninstall(activity);
            }
        });

        alertDialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MyDiaryApplication) activity.getApplication()).uninstall(activity);
            }
        });
    }
}


