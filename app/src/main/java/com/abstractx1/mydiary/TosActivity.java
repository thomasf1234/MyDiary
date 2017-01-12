package com.abstractx1.mydiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.abstractx1.mydiary.adapters.TosExpandableListAdapter;
import com.abstractx1.mydiary.dialogs.HelpDialog;
import com.abstractx1.mydiary.dialogs.ViewTermsAndConditionsDialog;

import java.util.ArrayList;
import java.util.List;

public class TosActivity extends MyDiaryActivity {
    public ExpandableListView tosExpandableListView;
    private CheckBox acceptCheckBox;
    private Button acceptButton, declineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isInDebugMode() && !GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) {
            GlobalApplicationValues.editResearcherEmailAddress(this, getString(R.string.default_researcher_email_address));
        } else {
            startActivity(new Intent(this, TitleActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tos);
        setTitle("MyDiary Privacy & Terms");

        tosExpandableListView = (ExpandableListView) findViewById(R.id.tosListView);
        acceptCheckBox = (CheckBox) findViewById(R.id.acceptTosCheckBox);
        acceptButton = (Button) findViewById(R.id.acceptButton);
        declineButton = (Button) findViewById(R.id.declineButton);


        List<String> sectionTitles = new ArrayList<>();
        sectionTitles.add(getResources().getString(R.string.introduction_title));
        sectionTitles.add(getResources().getString(R.string.accepting_the_eula_title));
        sectionTitles.add(getResources().getString(R.string.intellectual_property_rights_title));
        sectionTitles.add(getResources().getString(R.string.use_of_mydiary_by_you_title));
        sectionTitles.add(getResources().getString(R.string.disclaimer_of_warranties_title));
        sectionTitles.add(getResources().getString(R.string.limitation_of_liability_title));
        sectionTitles.add(getResources().getString(R.string.idemnification_title));
        sectionTitles.add(getResources().getString(R.string.termination_of_service_title));
        sectionTitles.add(getResources().getString(R.string.privacy_policy_title));

        List<String> sectionDetails = new ArrayList<>();

        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "introduction.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "accepting_the_eula.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "intellectual_property_rights.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "use_of_mydiary_by_you.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "disclaimer_of_warranties.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "limitation_of_liability.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "idemnification.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "termination_of_service.txt"));
        sectionDetails.add(ViewTermsAndConditionsDialog.getTxtFileString(this, "privacy_policy.txt"));


        ExpandableListAdapter tosExpandableListViewAdapter = new TosExpandableListAdapter(this, sectionTitles, sectionDetails);
        tosExpandableListView.setAdapter(tosExpandableListViewAdapter);

        acceptCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ButtonHelper.toggleAvailable(acceptButton, isChecked);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TosActivity.this.alert("Exiting");
                TosActivity.this.finish();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                GlobalApplicationValues.acceptTermsAndConditions(TosActivity.this);
                startActivity(new Intent(TosActivity.this, TitleActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tos_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uninstallMenuOption:
                ((MyDiaryApplication) getApplication()).uninstall(this);
                return true;
            case R.id.contactResearcherMenuOption:
                HelpDialog.create(this).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
