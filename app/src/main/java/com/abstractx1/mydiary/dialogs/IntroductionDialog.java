package com.abstractx1.mydiary.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.abstractx1.mydiary.AppInfo;
import com.abstractx1.mydiary.EmailClient;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfisher on 27/10/2016.
 */

//http://www.mkyong.com/android/android-custom-dialog-example/
public class IntroductionDialog {
    public static AlertDialog create(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        EmailClient emailClient = new EmailClient(activity);
        List<AppInfo> emailApps = emailClient.getInstalledEmailApps();

        List<String> names = new ArrayList<String>();

        for(AppInfo emailApp : emailApps) {
            names.add(emailApp.getName());
        }

        View view = inflater.inflate(R.layout.introduction_dialog, null);
        ViewFlipper introductionDialogViewFlipper = (ViewFlipper) view.findViewById(R.id.introductionDialogViewFlipper);

        try {
            ArrayAdapter adapter = new ArrayAdapter<String>(activity,
                    R.layout.email_client_listview_item, names);

            ListView listView = (ListView) view.findViewById(R.id.emailClientListView);
            listView.setAdapter(adapter);
        } catch(Exception e) {
            Utilities.showToolTip(activity, e.getMessage());
        }

        //introductionDialogViewFlipper.showNext();

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Setup");
        if(names.isEmpty()) {
            alertDialog.setMessage("No email clients installed. Please install an email client.");
        } else {
            alertDialog.setMessage("Please select your email client preference:");
        }
        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Next",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
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

        return alertDialog;
    }
}

