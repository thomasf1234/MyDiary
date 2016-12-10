package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.dialog_builders.ConfirmationDialogBuilder;
import com.abstractx1.mydiary.jobs.StartRecordingJob;
import com.abstractx1.mydiary.record.RecordHandler;

public class RecordActivity extends MyDiaryActivity implements View.OnClickListener, View.OnTouchListener {
    public Animation scaleAnimation;
    public ImageButton recordButton, playButton, clearRecordingButton;
    public SeekBar recordingSeekBar;
    public TextView recordingDurationTextView;
    public Button saveInputButton;

    private RecordHandler recordHandler;
    private AlertDialog clearRecordingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initializeWidgets();
    }

    public void initializeWidgets() {
        this.scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        this.recordButton = (ImageButton) findViewById(R.id.recordButton);
        this.playButton = (ImageButton) findViewById(R.id.playButton);
        this.clearRecordingButton = (ImageButton) findViewById(R.id.clearRecordingButton);
        this.recordingSeekBar = (SeekBar) findViewById(R.id.recordingSeekBar);
        this.recordingDurationTextView = (TextView) findViewById(R.id.recordingDurationTextView);
        this.saveInputButton = (Button) findViewById(R.id.saveButton);

        ButtonHelper.customizeAndroidStyle(this, recordButton, android.R.drawable.ic_btn_speak_now, "Record Audio");
        ButtonHelper.customizeAndroidStyle(this, playButton, android.R.drawable.ic_media_play, "Play Recorded Audio");
        ButtonHelper.customizeAndroidStyle(this, clearRecordingButton, android.R.drawable.ic_menu_delete, "Clear Recorded Audio");

        recordButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        clearRecordingButton.setOnClickListener(this);
        recordingSeekBar.setOnTouchListener(this);

        try {
            recordHandler = new RecordHandler(this,
                    recordButton,
                    playButton,
                    clearRecordingButton,
                    recordingSeekBar,
                    recordingDurationTextView, new DataCollection("test"));
        } catch (Exception e) {
            alert("Could not instantiate RecordHandler: " + e.getMessage());
        }
        initializeClearRecordingDialog();
    }



    @Override
    protected void onPause() {
        // Another activity is taking focus (this activity is about to be "paused").
        if (recordHandler.recordingInProgress()) {
            try {
                recordHandler.cancelRecording();
                alert("Recording Cancelled");
            } catch (Exception e) {
                alert("Error cancelling recording: " + e.getMessage());
            }
        }
        else if (recordHandler.playingInProgress()) {
            try {
                recordHandler.transitionTo(RecordHandler.PAUSED);
                alert("Recording playback paused");
            } catch (Exception e) {
                alert("Error pausing the recording playback: " + e.getMessage());
            }
        }
        super.onPause();
    }

    //http://stackoverflow.com/questions/19793194/is-onstop-always-preceded-by-onpause
    @Override
    protected void onDestroy() {
        try {
            recordHandler.onDestroy();
        } catch (Exception e) {
            alert("Error Destroying the Record Handler: " + e.getMessage());
        }
        super.onDestroy();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recordButton:
                if (recordHandler.recordingInProgress()) {
                    try {
                        view.playSoundEffect(SoundEffectConstants.CLICK);
                        recordHandler.transitionTo(RecordHandler.READY);
                    } catch (Exception e) {
                        alert("Error stopping recording: " + e.getMessage());
                    }
                } else {
                    try {
                        triggerJob(new StartRecordingJob(this, recordHandler));
                    } catch (Exception e) {
                        alert("Error starting recording: " + e.getMessage());
                    }
                }
                break;
            case R.id.playButton:
                if (recordHandler.playingInProgress()) {
                    try {
                        recordHandler.transitionTo(RecordHandler.PAUSED);
                    } catch (Exception e) {
                        alert("Error pausing recording: " + e.getMessage());
                    }
                } else {
                    try {
                        recordHandler.transitionTo(RecordHandler.PLAYING);
                    } catch (Exception e) {
                        alert("Error playing recording: " + e.getMessage());
                    }
                }
                break;
            case R.id.clearRecordingButton:
                view.playSoundEffect(SoundEffectConstants.CLICK);
                clearRecordingDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.recordingSeekBar:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (recordHandler.playingInProgress()) {
                            try {
                                recordHandler.transitionTo(RecordHandler.PAUSED);
                            } catch (Exception e) {
                                alert("Error pausing recording: " + e.getMessage());
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        recordHandler.setPlayFrom();
                        break;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private void initializeClearRecordingDialog() {
        ConfirmationDialogBuilder builder = new ConfirmationDialogBuilder(this, "Are you sure you want clear the recording?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                try {
                    recordHandler.transitionTo(RecordHandler.EMPTY);
                    alert("Cleared recording successfully");
                } catch (Exception e) {
                    alert("Error clearing recording: " + e.getMessage());
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button
                dialog.dismiss();
            }
        });
        this.clearRecordingDialog = builder.create();
    }
}
