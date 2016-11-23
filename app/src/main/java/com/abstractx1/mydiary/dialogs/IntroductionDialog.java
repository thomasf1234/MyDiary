package com.abstractx1.mydiary.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.abstractx1.mydiary.AppInfo;
import com.abstractx1.mydiary.EmailClient;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.adapters.ApplicationArrayAdapter;

import java.util.List;

/**
 * Created by tfisher on 27/10/2016.
 */

public class IntroductionDialog {
    public static AlertDialog create(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final EmailClient emailClient = new EmailClient(activity);
        final List<AppInfo> emailApps = emailClient.getInstalledEmailApps();

        View view = inflater.inflate(R.layout.introduction_dialog, null);
        ViewFlipper introductionDialogViewFlipper = (ViewFlipper) view.findViewById(R.id.introductionDialogViewFlipper);

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setIcon(R.drawable.my_diary_launcher_icon);
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
            //introductionDialogViewFlipper.showNext();
            alertDialog.setMessage("Please select your email client preference:");

            alertDialog.setView(view);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Next",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            emailClient.setChosenEmailApp(emailApps.get(adapter.getSelectedIndex()));
                            dialog.dismiss();
                        }
                    });

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    if(true)
                        ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });
        }


        return alertDialog;
    }
}

