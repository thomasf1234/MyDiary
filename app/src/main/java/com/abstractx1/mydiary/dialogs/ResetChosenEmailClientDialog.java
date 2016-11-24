package com.abstractx1.mydiary.dialogs;

/**
 * Created by tfisher on 23/11/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.abstractx1.mydiary.AppInfo;
import com.abstractx1.mydiary.EmailClient;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.adapters.ApplicationArrayAdapter;

import java.util.List;

/**
 * Created by tfisher on 27/10/2016.
 */

public class ResetChosenEmailClientDialog {
    public static AlertDialog create(final MyDiaryActivity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final EmailClient emailClient = new EmailClient(activity);
        final List<AppInfo> emailApps = emailClient.getInstalledEmailApps();

        View view = inflater.inflate(R.layout.email_client_selection_layout, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        //alertDialog.setIcon(R.drawable.my_diary_launcher_icon);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Email Settings");
        if(emailApps.isEmpty()) {
            alertDialog.setMessage("No email clients installed. Please install an email client.");
        } else {
            final ApplicationArrayAdapter adapter = new ApplicationArrayAdapter(activity,
                    R.layout.email_client_listview_item, emailApps, getDefaultSelectedIndex(emailApps,emailClient.getChosenEmailAppPackage()));
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
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            ;
                        }
                    });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
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
                                emailClient.setChosenEmailApp(emailApps.get(adapter.getSelectedIndex()));
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
        }


        return alertDialog;
    }

    private static int getDefaultSelectedIndex(List<AppInfo> emailApps, String currentEmailAppPackageName) {
        int currentEmailId = 0;

        for (AppInfo emailApp : emailApps) {
            if (emailApp.getPackageName().equals(currentEmailAppPackageName)) {
                break;
            } else {
                currentEmailId++;
            }
        }

        return currentEmailId;
    }
}

