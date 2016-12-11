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
                for(int i=0; i<Researcher.getInstance().getDataCollections().size(); ++i) {
                    DataCollection dataCollection = Researcher.getInstance().getDataCollections().get(i);
                    dataCollection.setAnswer(savedInstanceState.getString("answer" + i));
                    if (savedInstanceState.containsKey("recordingPath" + i)) {
                        if (isInDebugMode()) { alert("recording loaded"); }
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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new Compress(new String[]{Researcher.getInstance().getDataCollections().get(0).getRecording().getAbsolutePath()}, Environment.getExternalStorageDirectory() + File.separator + "myZip.zip").zip();
                    EmailClient emailClient = new EmailClient(TitleActivity.this);
                    emailClient.open("abstractunderstandings@gmail.com", new String[] {Environment.getExternalStorageDirectory() + File.separator + "myZip.zip"});
                } catch (Exception e) {
                    alert("Error compressing: " + e.getMessage());
                }
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
        for(int i=0; i<Researcher.getInstance().getDataCollections().size(); ++i) {
            DataCollection dataCollection = Researcher.getInstance().getDataCollections().get(i);
            savedInstanceState.putString("answer" + i, dataCollection.getAnswer());
            if(dataCollection.hasRecording()) {
                if (isInDebugMode()) { alert("recording saved"); }
                savedInstanceState.putString("recordingPath" + i, dataCollection.getRecording().getAbsolutePath());
            }
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
