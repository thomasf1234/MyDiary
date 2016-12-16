package com.abstractx1.mydiary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.record.RecordHandler;

public class InputActivity extends MyDiaryActivity {
    public final static String KEY_EXTRA_MESSAGE =
            "com.abstractx1.mydiary.QUESTION_NUMBER";

    private DataCollection dataCollection;
    private TextView questionTextView;
    private RecordHandler recordHandler;
    private ImageButton recordButton, playButton, clearRecordingButton;
    private SeekBar recordingSeekBar;
    private TextView recordingDurationTextView;
    private EditText answerEditText;
    private int startCurrentMilliseconds=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        setKeyboardpushActivityUp();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int questionNumber = intent.getIntExtra(KEY_EXTRA_MESSAGE, 0);
        dataCollection = Researcher.getInstance().getDataCollection(questionNumber);

        questionTextView = (TextView) findViewById(R.id.questionTextView);
        recordButton = (ImageButton) findViewById(R.id.recordButton);
        playButton = (ImageButton) findViewById(R.id.playButton);
        clearRecordingButton = (ImageButton) findViewById(R.id.clearRecordingButton);
        recordingSeekBar = (SeekBar) findViewById(R.id.recordingSeekBar);
        recordingDurationTextView = (TextView) findViewById(R.id.recordingDurationTextView);
        answerEditText = (EditText) findViewById(R.id.answerEditText);

        questionTextView.setText(dataCollection.getFullQuestion());
        answerEditText.setText(dataCollection.getAnswer());
        answerEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                dataCollection.setAnswer(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("currentMilliseconds" + dataCollection.getQuestionNumber())) {
                startCurrentMilliseconds = savedInstanceState.getInt("currentMilliseconds" + dataCollection.getQuestionNumber());
                MyDiaryApplication.log("currentMillseconds loaded");
            }
        }

        try {
            this.recordHandler = new RecordHandler(this,
                    recordButton,
                    playButton,
                    clearRecordingButton,
                    recordingSeekBar,
                    recordingDurationTextView,
                    dataCollection,
                    startCurrentMilliseconds);
        } catch (Exception e) {
            MyDiaryApplication.log(e, "Exception raised setting RecordHandler");
        }
    }

    public void halt() {
        if (recordHandler.recordingInProgress()) {
            try {
                recordHandler.cancelRecording();
                alert("Recording Cancelled");
            } catch (Exception e) {
                MyDiaryApplication.log(e, "Error cancelling recording");
            }
        } else if (recordHandler.playingInProgress()) {
            try {
                recordHandler.transitionTo(RecordHandler.PAUSED);
            } catch (Exception e) {
                MyDiaryApplication.log(e, "Error pausing the recording playback");
            }
        }
    }

    static protected Intent newStartIntent(Context context, int questionNumber) {
        Intent newIntent = new Intent(context, InputActivity.class);
        newIntent.putExtra(KEY_EXTRA_MESSAGE, questionNumber);
        return newIntent;
    }

    @Override
    public void onBackPressed() {
        try {
            recordHandler.transitionTo(RecordHandler.CANCELLED);
        } catch (Exception e) {
            MyDiaryApplication.log(e, "Error Destroying the Record Handler");
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        halt();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (recordHandler.getState() != RecordHandler.DISABLED) {
                recordHandler.transitionTo(RecordHandler.CANCELLED);
            }
        } catch (Exception e) {
            MyDiaryApplication.log(e, "Error Destroying the Record Handler");
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (dataCollection.hasRecording()) {
            savedInstanceState.putInt("currentMilliseconds" + dataCollection.getQuestionNumber(), recordingSeekBar.getProgress());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
