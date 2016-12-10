package com.abstractx1.mydiary;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;


import com.abstractx1.mydiary.adapters.QuestionsExpandableListAdapter;
import com.abstractx1.mydiary.dialogs.IntroductionDialog;
import com.abstractx1.mydiary.record.RecordHandler;

import java.io.File;


public class TitleActivity extends MyDiaryActivity {
    public ExpandableListView questionsExpandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isInDebugMode() && !GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) { showIntroductionDialog();}

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_title);
        alert("Being created! " + Researcher.getInstance().getDataCollections().size() );
        questionsExpandableListView = (ExpandableListView) findViewById(R.id.questionsExpandableListView);

        if (savedInstanceState != null) {
            if (!Researcher.getInstance().hasData()) {
                setupResearcher();
                for(int i=0; i<Researcher.getInstance().getDataCollections().size(); ++i) {
                    DataCollection dataCollection = Researcher.getInstance().getDataCollections().get(i);
                    dataCollection.setAnswer(savedInstanceState.getString("answer" + i));
                    if (savedInstanceState.containsKey("recordingPath" + i)) {
                        alert("recording loaded");
                        dataCollection.setRecording(new File(savedInstanceState.getString("recordingPath" + i)));
                    }
                }
            }
        } else {
            setupResearcher();
        }

        final ExpandableListAdapter questionsExpandableListViewAdapter = new QuestionsExpandableListAdapter(this);
        questionsExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                QuestionsExpandableListAdapter customExpandAdapter = (QuestionsExpandableListAdapter) questionsExpandableListView.getExpandableListAdapter();
                if (customExpandAdapter == null) {return;}
                for (int i = 0; i < customExpandAdapter.getGroupCount(); i++) {
                    if (i != groupPosition) {
                        customExpandAdapter.haltGroup(i);
                        questionsExpandableListView.collapseGroup(i);
                    }
                }

                questionsExpandableListView.setFocusableInTouchMode(true);
                questionsExpandableListView.requestFocus();
            }
        });

        questionsExpandableListView.setAdapter(questionsExpandableListViewAdapter);
    }

    private void setupResearcher() {
        Resources res = getResources();
        String[] questions = res.getStringArray(R.array.questions);

        for (String question : questions) {
            Researcher.getInstance().getDataCollections().add(new DataCollection(question));
        }
    }

    private void showIntroductionDialog() {
        AlertDialog alertDialog = IntroductionDialog.create(this);
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        for(int i=0; i<Researcher.getInstance().getDataCollections().size(); ++i) {
            DataCollection dataCollection = Researcher.getInstance().getDataCollections().get(i);
            savedInstanceState.putString("answer" + i, dataCollection.getAnswer());
            if(dataCollection.hasRecording()) {
                alert("recording saved");
                savedInstanceState.putString("recordingPath" + i, dataCollection.getRecording().getAbsolutePath());
            }
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
