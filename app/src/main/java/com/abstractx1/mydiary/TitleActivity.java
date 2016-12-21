package com.abstractx1.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.abstractx1.mydiary.adapters.QuestionsArrayAdapter;
import com.abstractx1.mydiary.dialogs.IntroductionDialog;


public class TitleActivity extends MyDiaryActivity {
    public ListView questionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isInDebugMode() && !GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) {
            GlobalApplicationValues.editResearcherEmailAddress(this, getString(R.string.default_researcher_email_address));
            showIntroductionDialog();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        questionsListView = (ListView) findViewById(R.id.questionsListView);

        ArrayAdapter<DataCollection> questionsArrayAdapter = new QuestionsArrayAdapter(this,
                R.layout.questions_listview_item, Researcher.getInstance().getDataCollections());


        questionsListView.setAdapter(questionsArrayAdapter);

        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                int questionNumber = position + 1;
                Intent intent = InputActivity.newStartIntent(TitleActivity.this, questionNumber);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        MyDiaryApplication.log("Resuming TitleActivity");

        if (questionsListView != null) {
            ((QuestionsArrayAdapter) questionsListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void showIntroductionDialog() {
        AlertDialog alertDialog = IntroductionDialog.create(this);
        alertDialog.show();
    }

}
