package com.abstractx1.mydiary;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;


import com.abstractx1.mydiary.adapters.QuestionsExpandableListAdapter;
import com.abstractx1.mydiary.dialogs.IntroductionDialog;
import com.abstractx1.mydiary.record.RecordHandler;

import java.io.File;
import java.io.FileOutputStream;


public class TitleActivity extends MyDiaryActivity {
    public ExpandableListView questionsExpandableListView;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isInDebugMode() && !GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) { showIntroductionDialog();}

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_title);
        questionsExpandableListView = (ExpandableListView) findViewById(R.id.questionsExpandableListView);
        sendButton = (ImageButton) findViewById(R.id.sendButton);

        if (savedInstanceState != null) {
            if (!Researcher.getInstance().hasData()) {
                setupResearcher();
                for (DataCollection dataCollection : Researcher.getInstance().getDataCollections()) {
                    dataCollection.setAnswer(savedInstanceState.getString("answer" + dataCollection.getQuestionNumber()));
                    if (savedInstanceState.containsKey("recordingPath" + dataCollection.getQuestionNumber())) {
                        debugAlert("recording loaded");
                        dataCollection.setRecording(new File(savedInstanceState.getString("recordingPath" + dataCollection.getQuestionNumber())));
                    }
                }
            }
        } else {
            setupResearcher();
        }

        final ExpandableListAdapter questionsExpandableListViewAdapter = new QuestionsExpandableListAdapter(this);

        questionsExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                int groupPosition = i;

                if (expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.collapseGroup(groupPosition);
                } else {
                    closeKeyboard();
                    for (int j = 0; j < questionsExpandableListViewAdapter.getGroupCount(); j++) {
                        if (j != groupPosition) {
                            questionsExpandableListView.collapseGroup(j);
                        }
                    }

                    clearFocus();
                    expandableListView.expandGroup(groupPosition);
                }

                return true;
            }
        });

        questionsExpandableListView.setAdapter(questionsExpandableListViewAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    new Compress(new String[]{Researcher.getInstance().getDataCollections().get(0).getRecording().getAbsolutePath()}, Environment.getExternalStorageDirectory() + File.separator + "myZip.zip").zip();
//                    EmailClient emailClient = new EmailClient(TitleActivity.this);
//                    emailClient.open("abstractunderstandings@gmail.com", new String[] {Environment.getExternalStorageDirectory() + File.separator + "myZip.zip"});
//                } catch (Exception e) {
//                    alert("Error compressing: " + e.getMessage());
//                }
            }
        });
    }

    private void setupResearcher() {
        Resources res = getResources();
        String[] questions = res.getStringArray(R.array.questions);

        for (int i=0; i<questions.length; ++i) {
            String question = questions[i];
            int questionNumber = i + 1;
            Researcher.getInstance().getDataCollections().add(new DataCollection(questionNumber, question));
        }
    }

    private void showIntroductionDialog() {
        AlertDialog alertDialog = IntroductionDialog.create(this);
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        for (DataCollection dataCollection : Researcher.getInstance().getDataCollections()) {
            savedInstanceState.putString("answer" + dataCollection.getQuestionNumber(), dataCollection.getAnswer());
            if(dataCollection.hasRecording()) {
                if (isInDebugMode()) { alert("recording saved"); }
                savedInstanceState.putString("recordingPath" + dataCollection.getQuestionNumber(), dataCollection.getRecording().getAbsolutePath());
            }
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void clearFocus() {
        RelativeLayout focuslayout = (RelativeLayout) findViewById(R.id.RequestFocusLayout);
        focuslayout.requestFocus();
    }
}
