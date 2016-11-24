package com.abstractx1.mydiary.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.abstractx1.mydiary.AppInfo;
import com.abstractx1.mydiary.EmailClient;
import com.abstractx1.mydiary.GlobalApplicationValues;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.adapters.ApplicationArrayAdapter;

import java.util.List;

/**
 * Created by tfisher on 27/10/2016.
 */

public class IntroductionDialog {
    private static int FIRST_PAGE = 0;
    private static int LAST_PAGE = 1;

    public static AlertDialog create(final Activity activity) {
        final int[] currentPage = {FIRST_PAGE};
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final EmailClient emailClient = new EmailClient(activity);
        final List<AppInfo> emailApps = emailClient.getInstalledEmailApps();

        View view = inflater.inflate(R.layout.introduction_dialog, null);
        final ViewFlipper introductionDialogViewFlipper = (ViewFlipper) view.findViewById(R.id.introductionDialogViewFlipper);

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        //alertDialog.setIcon(R.drawable.my_diary_launcher_icon);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Setup");
        if(emailApps.isEmpty()) {
            alertDialog.setMessage("No email clients installed. Please install an email client.");
        } else {
            final ApplicationArrayAdapter adapter = new ApplicationArrayAdapter(activity,
                    R.layout.email_client_listview_item, emailApps);
            final ListView listView = (ListView) view.findViewById(R.id.emailClientListView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setSelectedIndex(position, listView);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            });
            listView.setAdapter(adapter);

            alertDialog.setMessage("Please select your email preference:");

            alertDialog.setView(view);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Next",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            ;
                        }
                    });

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(final DialogInterface dialog) {
                    if(true) {
                        final Button positiveButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setEnabled(false);
                        positiveButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if(currentPage[0] == LAST_PAGE) {
                                    GlobalApplicationValues.acceptTermsAndConditions(activity);
                                    dialog.dismiss();
                                } else {
                                    emailClient.setChosenEmailApp(emailApps.get(adapter.getSelectedIndex()));
                                    introductionDialogViewFlipper.showNext();
                                    positiveButton.setEnabled(false);
                                    positiveButton.setText("Finish");
                                    currentPage[0] += 1;
                                    alertDialog.setMessage("You must accept and agree to the terms and conditions below to begin using this application.");
                                    CheckBox checkBox = (CheckBox) introductionDialogViewFlipper.getCurrentView().findViewById(R.id.acceptTermsAndConditionsCheckBox);
                                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                                    {
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                        {
                                            positiveButton.setEnabled(isChecked);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }


        return alertDialog;
    }
}

