package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.dialog_builders.ConfirmationDialogBuilder;
import com.abstractx1.mydiary.jobs.StartRecordingJob;
import com.abstractx1.mydiary.record.RecordHandler;
import com.example.demo.job.PermissionActivity;

public class RecordActivity extends PermissionActivity implements View.OnClickListener, View.OnTouchListener {
    public Animation scaleAnimation;
    public Button recordButton, playButton, clearRecordingButton;
    public SeekBar recordingSeekBar;
    public TextView recordingDurationTextView;

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
        this.recordButton = (Button) findViewById(R.id.recordButton);
        this.playButton = (Button) findViewById(R.id.playButton);
        this.clearRecordingButton = (Button) findViewById(R.id.clearRecordingButton);
        this.recordingSeekBar = (SeekBar) findViewById(R.id.recordingSeekBar);
        this.recordingDurationTextView = (TextView) findViewById(R.id.recordingDurationTextView);

        ButtonHelper.customize(this, recordButton, R.drawable.record_button, R.drawable.record_button_hover, scaleAnimation, "Record Audio");
        ButtonHelper.customize(this, playButton, R.drawable.play_button, R.drawable.play_button_hover, scaleAnimation, "Play Recorded Audio");
        ButtonHelper.customize(this, clearRecordingButton, R.drawable.delete_button, R.drawable.delete_button_hover, scaleAnimation, "Clear Audio");

        recordButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        clearRecordingButton.setOnClickListener(this);
        recordingSeekBar.setOnTouchListener(this);

        try {
            recordHandler = new RecordHandler(this,
                    scaleAnimation,
                    recordButton,
                    playButton,
                    clearRecordingButton,
                    recordingSeekBar,
                    recordingDurationTextView);
        } catch (Exception e) {
            Utilities.showToolTip(this, "Could not instantiate RecordHandler: " + e.getMessage());
        }
        initializeClearRecordingDialog();
    }



    @Override
    protected void onPause() {
        // Another activity is taking focus (this activity is about to be "paused").
        if (recordHandler.recordingInProgress()) {
            try {
                recordHandler.cancelRecording();
                Utilities.showToolTip(this, "Recording Cancelled");
            } catch (Exception e) {
                Utilities.showToolTip(this, "Error cancelling recording: " + e.getMessage());
            }
        }
        else if (recordHandler.playingInProgress()) {
            try {
                recordHandler.transitionTo(RecordHandler.PAUSED);
                Utilities.showToolTip(this, "Recording playback paused");
            } catch (Exception e) {
                Utilities.showToolTip(this, "Error pausing the recording playback: " + e.getMessage());
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
            Utilities.showToolTip(this, "Error Destroying the Record Handler: " + e.getMessage());
        }
        super.onDestroy();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recordButton:
                if (recordHandler.recordingInProgress()) {
                    try {
                        recordHandler.transitionTo(RecordHandler.READY);
                    } catch (Exception e) {
                        Utilities.showToolTip(this, "Error stopping recording: " + e.getMessage());
                    }
                } else {
                    try {
                        triggerJob(new StartRecordingJob(this, recordHandler));
                    } catch (Exception e) {
                        Utilities.showToolTip(this, "Error starting recording: " + e.getMessage());
                    }
                }
                break;
            case R.id.playButton:
                if (recordHandler.playingInProgress()) {
                    try {
                        recordHandler.transitionTo(RecordHandler.PAUSED);
                    } catch (Exception e) {
                        Utilities.showToolTip(this, "Error pausing recording: " + e.getMessage());
                    }
                } else {
                    try {
                        recordHandler.transitionTo(RecordHandler.PLAYING);
                    } catch (Exception e) {
                        Utilities.showToolTip(this, "Error playing recording: " + e.getMessage());
                    }
                }
                break;
            case R.id.clearRecordingButton:
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
                                Utilities.showToolTip(this, "Error pausing recording: " + e.getMessage());
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        recordHandler.setPlayFrom();
                        recordHandler.updatePlayingDuration();
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
                } catch (Exception e) {
                    Utilities.showToolTip(RecordActivity.this, "Error clearing recording: " + e.getMessage());
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
