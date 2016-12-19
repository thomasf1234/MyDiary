package com.abstractx1.mydiary.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.abstractx1.mydiary.GlobalApplicationValues;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.MyDiaryApplication;
import com.abstractx1.mydiary.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tfisher on 27/10/2016.
 */

public class ResearcherEmailDialog {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isValid(String emailAddress) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.find();
    }

    public static AlertDialog create(final MyDiaryActivity activity, String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        alertDialogBuilder.setTitle("Settings");
        alertDialogBuilder.setMessage("Researcher email address:");

        final EditText input = new EditText(activity);
        //disable autocomplete
        input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        float currentInputTextSize = input.getTextSize();
        MyDiaryApplication.log("ResearcherEmailDialog inputEditText current getTextSize: " + currentInputTextSize );

        float newInputTextSize = currentInputTextSize * 0.8f;
        MyDiaryApplication.log("ResearcherEmailDialog inputEditText setTextSize: " + newInputTextSize );
        input.setTextSize(TypedValue.COMPLEX_UNIT_PX, newInputTextSize);
        MyDiaryApplication.log("ResearcherEmailDialog inputEditText new getTextSize: " + input.getTextSize() );

        String currentEmailAddress = GlobalApplicationValues.getResearcherEmailAddress(activity);
        if (currentEmailAddress != null) {
            input.setText(currentEmailAddress);
        }
        input.setGravity(Gravity.CENTER);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailAddress = input.getText().toString();
                if (isValid(emailAddress)) {
                    GlobalApplicationValues.editResearcherEmailAddress(activity, input.getText().toString());
                } else {
                    activity.alert("Failed to update email address due to Unrecognized format. Please use another.");
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                if (true) {
                    final Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    input.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence newEmailAddress, int arg1, int arg2, int arg3) {
                            if (isValid(newEmailAddress.toString())) {
                                positiveButton.setEnabled(true);
                            } else {
                                positiveButton.setEnabled(false);
                            }
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                        }

                    });
                }
            }
        });

        return alertDialog;
    }
}

