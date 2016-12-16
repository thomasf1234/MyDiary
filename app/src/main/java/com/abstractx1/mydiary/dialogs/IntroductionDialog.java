package com.abstractx1.mydiary.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.abstractx1.mydiary.GlobalApplicationValues;
import com.abstractx1.mydiary.R;

import java.util.List;

/**
 * Created by tfisher on 27/10/2016.
 */

public class IntroductionDialog {
    public static AlertDialog create(final Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.introduction_dialog, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Introduction");

        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Finish",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        ;
                    }
                });

        alertDialog.setMessage("You must accept and agree to the terms and conditions below to begin using this application.");

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                if (true) {
                    final Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setEnabled(false);

                    positiveButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            GlobalApplicationValues.acceptTermsAndConditions(activity);
                            dialog.dismiss();
                        }
                    });
                    CheckBox checkBox = (CheckBox) alertDialog.findViewById(R.id.acceptTermsAndConditionsCheckBox);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            positiveButton.setEnabled(isChecked);
                        }
                    });
                }
            }
        });


        return alertDialog;
    }
}

