package com.abstractx1.mydiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.dialog_builders.ConfirmationDialogBuilder;
import com.abstractx1.mydiary.dialogs.HelpDialog;
import com.abstractx1.mydiary.dialogs.ResearcherEmailDialog;
import com.abstractx1.mydiary.jobs.DebugPrintFilesJob;
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
    private android.app.AlertDialog saveRecordingDialog;
    private int startCurrentMilliseconds=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        setKeyboardpushActivityUp();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeSaveRecordingDialog();

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
            MyDiaryApplication.log("startCurrentMilliseconds: " + startCurrentMilliseconds);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editResearcherEmailAddressMenuOption:
                AlertDialog editResearcherEmailAddressdialog =  ResearcherEmailDialog.create(this);
                editResearcherEmailAddressdialog.show();
                return true;
            case R.id.contactResearcherMenuOption:
                HelpDialog.create(this).show();
                return true;
            case android.R.id.home:
                if (recordHandler.getState() == RecordHandler.RECORDING) {
                    try {
                        recordHandler.transitionTo(RecordHandler.LOADED);
                    } catch (Exception e) {
                        MyDiaryApplication.log(e, "Error stopping recording.");
                    }
                    saveRecordingDialog.show();
                } else {
                    onBackPressed();
                }
                return true;
            case R.id.debugMenuOption:
                try {
                    triggerJob(new DebugPrintFilesJob(this));
                } catch (Exception e) {
                    alert("Could not print files");
                }
                return true;
            case R.id.clearCacheMenuOption:
                Utilities.clearCache(getApplicationContext());
                alert("Cleared cache");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if (!isInDebugMode())
        {
            menu.findItem(R.id.debugMenuOption).setVisible(false);
            menu.findItem(R.id.clearCacheMenuOption).setVisible(false);
        }

        menu.findItem(R.id.sendButtonMenuOption).setVisible(false);
        menu.findItem(R.id.imageButtonMenuOption).setVisible(false);

        return true;
    }

    private void initializeSaveRecordingDialog() {
        ConfirmationDialogBuilder builder = new ConfirmationDialogBuilder(this, "Do you want to save the current recording?", android.R.drawable.ic_menu_save);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                alert("Recording saved.");
                dialog.dismiss();
                onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button
                try {
                    recordHandler.transitionTo(RecordHandler.EMPTY);
                    recordHandler.transitionTo(RecordHandler.CANCELLED);
                    alert(getString(R.string.recording_not_saved));
                } catch (Exception e) {
                    MyDiaryApplication.log(e, "Error Destroying the Record Handler");
                }
                dialog.dismiss();
                onBackPressed();
            }
        });
        this.saveRecordingDialog = builder.create();
    }
}
