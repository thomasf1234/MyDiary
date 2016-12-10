package com.abstractx1.mydiary.record;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.ButtonHelper;
import com.abstractx1.mydiary.DataCollection;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Utilities;
import com.abstractx1.mydiary.dialog_builders.ConfirmationDialogBuilder;
import com.abstractx1.mydiary.jobs.StartRecordingJob;
import com.abstractx1.mydiary.lib.states.State6;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tfisher on 16/11/2016.
 */


public class RecordHandler extends State6 implements View.OnClickListener, View.OnTouchListener {
    public static State DISABLED = State.ONE;
    public static State EMPTY = State.TWO;
    public static State RECORDING = State.THREE;
    public static State READY = State.FOUR;
    public static State PLAYING = State.FIVE;
    public static State PAUSED = State.SIX;

    private static final Map<State, State[]> validStateTransitions;
    static {
        Map<State, State[]> _validStateTransitions = new HashMap<State, State[]>();
        _validStateTransitions.put(DISABLED, new State[]{});
        _validStateTransitions.put(EMPTY, new State[]{RECORDING});
        _validStateTransitions.put(RECORDING, new State[]{READY});
        _validStateTransitions.put(READY, new State[]{EMPTY, PLAYING});
        _validStateTransitions.put(PLAYING, new State[]{READY, PAUSED});
        _validStateTransitions.put(PAUSED, new State[]{PLAYING, READY, EMPTY});
        validStateTransitions = Collections.unmodifiableMap(_validStateTransitions);
    }

    private MyDiaryActivity activity;
    private ImageButton recordButton, playButton, clearRecordingButton;
    private SeekBar recordingSeekBar;
    private TextView recordingDurationTextView;
    private DataCollection dataCollection;
    private Recorder recorder;
    private RecordingPlayer recordingPlayer;
    private Handler handler;
    private Timer timer;
    private AlertDialog clearRecordingDialog;

    public RecordHandler(MyDiaryActivity activity,
                         ImageButton recordButton,
                         ImageButton playButton,
                         ImageButton clearRecordingButton,
                         SeekBar recordingSeekBar,
                         TextView recordingDurationTextView,
                         DataCollection dataCollection) throws Exception {
        initialize(activity, recordButton, playButton, clearRecordingButton, recordingSeekBar, recordingDurationTextView, dataCollection);

        if (dataCollection.hasRecording()) {
            setUpNewRecordingPlayer();
            transitionTo(READY);
        } else {
            State initialState = hasSystemMicrophoneFeature() ? EMPTY : DISABLED;
            transitionTo(initialState);
        }
    }

    private void initialize(MyDiaryActivity activity,
                            ImageButton recordButton,
                            ImageButton playButton,
                            ImageButton clearRecordingButton,
                            SeekBar recordingSeekBar,
                            TextView recordingDurationTextView,
                            DataCollection dataCollection) {
        this.activity = activity;
        this.recordButton = recordButton;
        this.playButton = playButton;
        this.clearRecordingButton = clearRecordingButton;
        this.recordingSeekBar = recordingSeekBar;
        this.recordingDurationTextView = recordingDurationTextView;
        this.dataCollection = dataCollection;
        this.handler = new Handler();
        initializeClearRecordingDialog();
        setEventListeners();
        setUpUpdateUISchedule();
    }

    //DISABLED
    @Override
    protected void onSetStateONE() throws Exception {
        activity.keepScreenAwake(false);
        ButtonHelper.disable(recordButton);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);
    }

    //EMPTY
    @Override
    protected void onSetStateTWO() throws Exception {
        activity.keepScreenAwake(false);
        if (dataCollection.hasRecording()) { dataCollection.clearRecording(); }
        ButtonHelper.enable(recordButton);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setProgress(0);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);
        recordingDurationTextView.invalidate();
    }

    //RECORDING
    @Override
    protected void onSetStateTHREE() throws Exception {
        setUpNewRecorder();
        recorder.record();
        activity.keepScreenAwake(true);
        ButtonHelper.enable(recordButton);
        ButtonHelper.customizeAndroidStyle(activity, recordButton, R.drawable.ic_media_stop, "Stop Recording");
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
    }

    //READY
    @Override
    protected void onSetStateFOUR() throws Exception {
        if (hasRecorder() && recorder.isRecording()) {
            recorder.stop();
            dataCollection.setRecording(recorder.getOutputFile());
            setUpNewRecordingPlayer();
        }
        activity.keepScreenAwake(false);
        ButtonHelper.customizeAndroidStyle(activity, recordButton, android.R.drawable.ic_btn_speak_now, "Start Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.customizeAndroidStyle(activity, playButton, android.R.drawable.ic_media_play, "Play Recording");
        ButtonHelper.enable(playButton);
        ButtonHelper.enable(clearRecordingButton);
        recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(recordingPlayer.getDuration()));
        recordingSeekBar.setMax(recordingPlayer.getDuration());
        recordingSeekBar.setEnabled(true);
        recordingSeekBar.setProgress(recordingSeekBar.getMax());
    }

    //PLAYING
    @Override
    protected void onSetStateFIVE() throws Exception {
        if (recordingSeekBar.getProgress() == recordingSeekBar.getMax()) {
            recordingSeekBar.setProgress(0);
        }
        recordingPlayer.play();
        activity.keepScreenAwake(true);
        ButtonHelper.customizeAndroidStyle(activity, playButton, android.R.drawable.ic_media_pause, "Pause Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.enable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(true);
    }

    //PAUSED
    @Override
    protected void onSetStateSIX() throws Exception {
        recordingPlayer.pause();
        activity.keepScreenAwake(false);
        ButtonHelper.customizeAndroidStyle(activity, playButton, android.R.drawable.ic_media_play, "Resume Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.enable(playButton);
        ButtonHelper.enable(clearRecordingButton);
        recordingSeekBar.setEnabled(true);
    }

    public boolean recordingInProgress() {
        return state == RECORDING;
    }

    public boolean playingInProgress() {
        return state == PLAYING;
    }

    public void setPlayFrom() {
        recordingPlayer.seekTo(getCurrentSelectedMilliSeconds());
        recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(recordingPlayer.getCurrentPosition()));
    }

    public int getCurrentSelectedMilliSeconds() {
        return recordingSeekBar.getProgress();
    }

    public void cancelRecording() throws Exception {
        transitionTo(READY);
        transitionTo(EMPTY);
    }

    // This gets executed in a non-UI thread:
    public void updatePlayingDuration() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                //need to check here as this can be delayed until after playing has finished
                if (playingInProgress()) {
                    recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(recordingPlayer.getCurrentPosition()));
                    recordingSeekBar.setProgress(recordingPlayer.getCurrentPosition());
                }
            }
        });
    }

    public void onDestroy() {
        cancelTimer();
        if (hasRecorder()) { cancelRecorder(); }
        if (hasRecordingPlayer()) { cancelRecordingPlayer(); }
        activity.keepScreenAwake(false);
        //Utilities.showToolTip(activity, "Stopping playing/recording and wiping timer tasks as Activity Stopping");
    }

    private boolean hasSystemMicrophoneFeature() {
        return activity.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private boolean hasRecorder() { return recorder != null; }

    private boolean hasRecordingPlayer() { return recordingPlayer != null; }

    private void setUpNewRecorder() throws IOException {
        if (hasRecorder()) { cancelRecorder(); }
        this.recorder = new Recorder();
    }

    private void setUpNewRecordingPlayer() throws IOException {
        if (hasRecordingPlayer()) { cancelRecordingPlayer(); }
        this.recordingPlayer = new RecordingPlayer();
        recordingPlayer.setInputFile(dataCollection.getRecording());
        recordingPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    //Can be called if paused near the end, so must allow transition from PAUSED to READY
                    transitionTo(READY);
                } catch (Exception e) {
                    activity.alert("Error when recording playback finished: " + e.getMessage());
                }
            }

        });
    }

    private void setUpUpdateUISchedule() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (recordingInProgress())
                    updateRecordingDuration();
                else if (playingInProgress())
                    updatePlayingDuration();
            }
        }, 0, 10);
    }

    // This gets executed in a non-UI thread:
    private void updateRecordingDuration() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                //need to check here as this can be delayed until after recording has finished
                if (recordingInProgress())
                    recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(recorder.getRecordingDurationMilliSeconds()));
            }
        });
    }

    private void cancelRecorder() {
        if (recorder.isRecording()) {
            recorder.stop();
        }
        recorder.release();
    }

    private void cancelRecordingPlayer() {
        if (recordingPlayer.isPlaying()) {
            recordingPlayer.stop();
        }
        recordingPlayer.release();
    }

    private void cancelTimer() {
        timer.cancel();
        timer.purge();
    }

    @Override
    protected Map<State, State[]> getValidStateTransitions() {
        return validStateTransitions;
    }

    private void setEventListeners() {
        recordButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        clearRecordingButton.setOnClickListener(this);
        recordingSeekBar.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recordButtonChild:
                if (recordingInProgress()) {
                    try {
                        view.playSoundEffect(SoundEffectConstants.CLICK);
                        transitionTo(RecordHandler.READY);
                    } catch (Exception e) {
                        activity.alert("Error stopping recording: " + e.getMessage());
                    }
                } else {
                    try {
                        activity.triggerJob(new StartRecordingJob(activity, this));
                    } catch (Exception e) {
                        activity.alert("Error starting recording: " + e.getMessage());
                    }
                }
                break;
            case R.id.playButtonChild:
                if (playingInProgress()) {
                    try {
                        transitionTo(RecordHandler.PAUSED);
                    } catch (Exception e) {
                        activity.alert("Error pausing recording: " + e.getMessage());
                    }
                } else {
                    try {
                        transitionTo(RecordHandler.PLAYING);
                    } catch (Exception e) {
                        activity.alert("Error playing recording: " + e.getMessage());
                    }
                }
                break;
            case R.id.clearRecordingButtonChild:
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
            case R.id.recordingSeekBarChild:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (playingInProgress()) {
                            try {
                                transitionTo(RecordHandler.PAUSED);
                            } catch (Exception e) {
                                activity.alert("Error pausing recording: " + e.getMessage());
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        setPlayFrom();
                        break;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private void initializeClearRecordingDialog() {
        ConfirmationDialogBuilder builder = new ConfirmationDialogBuilder(activity, "Are you sure you want clear the recording?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                try {
                    transitionTo(RecordHandler.EMPTY);
                    activity.alert("Cleared recording successfully");
                } catch (Exception e) {
                    activity.alert("Error clearing recording: " + e.getMessage());
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




