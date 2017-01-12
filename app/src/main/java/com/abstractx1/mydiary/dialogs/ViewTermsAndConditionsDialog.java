package com.abstractx1.mydiary.dialogs;

/**
 * Created by tfisher on 22/12/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.MyDiaryApplication;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.adapters.TosExpandableListAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by tfisher on 27/10/2016.
 */

public class ViewTermsAndConditionsDialog {
    public static AlertDialog create(final MyDiaryActivity activity) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.help_dialog, null);

        alertDialogBuilder.setTitle("Privacy & Terms");

        List<String> sectionTitles = new ArrayList<>();
        sectionTitles.add(activity.getResources().getString(R.string.introduction_title));
        sectionTitles.add(activity.getResources().getString(R.string.accepting_the_eula_title));
        sectionTitles.add(activity.getResources().getString(R.string.intellectual_property_rights_title));
        sectionTitles.add(activity.getResources().getString(R.string.use_of_mydiary_by_you_title));
        sectionTitles.add(activity.getResources().getString(R.string.disclaimer_of_warranties_title));
        sectionTitles.add(activity.getResources().getString(R.string.limitation_of_liability_title));
        sectionTitles.add(activity.getResources().getString(R.string.idemnification_title));
        sectionTitles.add(activity.getResources().getString(R.string.termination_of_service_title));
        sectionTitles.add(activity.getResources().getString(R.string.privacy_policy_title));

        List<String> sectionDetails = new ArrayList<>();

        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "introduction.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "accepting_the_eula.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "intellectual_property_rights.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "use_of_mydiary_by_you.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "disclaimer_of_warranties.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "limitation_of_liability.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "idemnification.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "termination_of_service.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(activity, "privacy_policy.txt"));

        ExpandableListView myList = new ExpandableListView(activity);
        ExpandableListAdapter tosExpandableListViewAdapter = new TosExpandableListAdapter(activity, sectionTitles, sectionDetails);
        myList.setAdapter(tosExpandableListViewAdapter);

        alertDialogBuilder.setView(myList);

        alertDialogBuilder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialog;
    }

    public static String getTermsOfUse(MyDiaryActivity activity) {
        return getTxtFileString(activity, "terms_of_use.txt");
    }

    public static String getPrivacyPolicy(MyDiaryActivity activity) {
        return getTxtFileString(activity, "privacy_policy.txt");
    }

    public static String getTxtFileString(MyDiaryActivity activity, String fileName) {
        BufferedReader reader = null;
        StringBuilder text = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(activity.getAssets().open(fileName)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            MyDiaryApplication.log(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return text.toString();
    }
}


