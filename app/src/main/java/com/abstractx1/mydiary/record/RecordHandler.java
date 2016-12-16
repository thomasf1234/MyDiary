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
import com.abstractx1.mydiary.MyDiaryApplication;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Utilities;
import com.abstractx1.mydiary.dialog_builders.ConfirmationDialogBuilder;
import com.abstractx1.mydiary.jobs.StartRecordingJob;
import com.abstractx1.mydiary.lib.states.State7;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tfisher on 16/11/2016.
 */

public class RecordHandler extends State7 {
    public static State DISABLED = State.ONE;
    public static State EMPTY = State.TWO;
    public static State RECORDING = State.THREE;
    public static State LOADED = State.FOUR;
    public static State PLAYING = State.FIVE;
    public static State PAUSED = State.SIX;
    public static State CANCELLED = State.SEVEN;

    private static final Map<State, State[]> validStateTransitions;
    static {
        Map<State, State[]> _validStateTransitions = new HashMap<State, State[]>();
        _validStateTransitions.put(DISABLED, new State[]{});
        _validStateTransitions.put(EMPTY, new State[]{RECORDING, CANCELLED});
        _validStateTransitions.put(RECORDING, new State[]{LOADED, CANCELLED});
        _validStateTransitions.put(LOADED, new State[]{EMPTY, PLAYING, CANCELLED});
        _validStateTransitions.put(PLAYING, new State[]{LOADED, PAUSED, CANCELLED});
        _validStateTransitions.put(PAUSED, new State[]{PLAYING, LOADED, EMPTY, CANCELLED});
        _validStateTransitions.put(CANCELLED, new State[]{DISABLED});
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
                         DataCollection dataCollection,
                         int currentMilliseconds) throws Exception {
        initialize(activity, recordButton, playButton, clearRecordingButton, recordingSeekBar, recordingDurationTextView, dataCollection);

        if (dataCollection.hasRecording()) {
            setUpNewRecordingPlayer();
            transitionTo(LOADED);
            setPlayFrom(currentMilliseconds);
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
        MyDiaryApplication.log("transitioned to DISABLED state");
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
        ButtonHelper.customizeAndroidStyle(activity, recordButton, android.R.drawable.ic_btn_speak_now, "Start Record");
        recordingDurationTextView.invalidate();
        MyDiaryApplication.log("transitioned to EMPTY state");
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
        MyDiaryApplication.log("transitioned to RECORDING state");
    }

    //LOADED
    @Override
    protected void onSetStateFOUR() throws Exception {
        if (!hasRecorder()) { setUpNewRecorder(); }
        if (recorder.isRecording()) {
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
        recordingSeekBar.setMax(recordingPlayer.getDuration());
        recordingSeekBar.setEnabled(true);
        setPlayFrom(recordingPlayer.getDuration());
        MyDiaryApplication.log("transitioned to LOADED state");
    }

    //PLAYING
    @Override
    protected void onSetStateFIVE() throws Exception {
        if (recordingSeekBar.getProgress() == recordingSeekBar.getMax()) {
            setPlayFrom(0);
        }
        recordingPlayer.play();
        activity.keepScreenAwake(true);
        ButtonHelper.customizeAndroidStyle(activity, playButton, android.R.drawable.ic_media_pause, "Pause Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.enable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(true);
        MyDiaryApplication.log("transitioned to PLAYING state");
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
        MyDiaryApplication.log("transitioned to PAUSED state");
    }

    //CANCELLED
    @Override
    protected void onSetStateSEVEN() throws Exception {
        cancelTimer();
        if (hasRecorder()) { cancelRecorder(); }
        if (hasRecordingPlayer()) { recordingPlayer.cancel(); }
        activity.keepScreenAwake(false);
        MyDiaryApplication.log("transitioned to CANCELLED state");
        transitionTo(DISABLED);
    }

    public boolean recordingInProgress() {
        return state == RECORDING;
    }

    public boolean playingInProgress() {
        return state == PLAYING;
    }

    public void setPlayFrom(int milliSeconds) {
        recordingPlayer.seekTo(milliSeconds);
        recordingSeekBar.setProgress(milliSeconds);
        recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(milliSeconds));
    }

    public int getCurrentSelectedMilliSeconds() {
        return recordingSeekBar.getProgress();
    }

    public void cancelRecording() throws Exception {
        transitionTo(LOADED);
        transitionTo(EMPTY);
    }

    private boolean hasSystemMicrophoneFeature() {
        return activity.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private boolean hasRecorder() { return recorder != null; }

    private boolean hasRecordingPlayer() { return recordingPlayer != null; }

    private void setUpNewRecorder() throws IOException {
        if (hasRecorder()) { cancelRecorder(); }
        this.recorder = new Recorder("question_" + dataCollection.getQuestionNumber());
    }

    private void setUpNewRecordingPlayer() throws IOException {
        if (hasRecordingPlayer()) { recordingPlayer.cancel(); }
        this.recordingPlayer = new RecordingPlayer();
        recordingPlayer.setInputFile(dataCollection.getRecording());
        recordingPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    transitionTo(LOADED);
                } catch (Exception e) {
                    activity.alert("Error when recording playback finished: " + e.getMessage());
                }
            }

        });
    }

    //TODO : Improve the safety from this block, some values may not be set, and be null.
    private void setUpUpdateUISchedule() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // This gets executed on the UI thread so it can safely modify Views
                        //need to check here as this can be delayed until after recording has finished
                        try {
                            if (state == RECORDING && hasRecorder() && recorder.isRecording()) {
                                recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(recorder.getRecordingDurationMilliSeconds()));
                            }
                            else if (hasRecordingPlayer()) {
                                if (dataCollection.hasRecording())
                                    recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(recordingPlayer.getCurrentPosition()));
                                if (state == PLAYING)
                                    recordingSeekBar.setProgress(recordingPlayer.getCurrentPosition());
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        }, 0, 800);
    }

    private void cancelRecorder() {
        if (recorder.isRecording()) {
            recorder.stop();
        }
        recorder.release();
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
        final RecordHandler recordHandler = this;
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (recordingInProgress()) {
                    try {
                        view.playSoundEffect(SoundEffectConstants.CLICK);
                        transitionTo(RecordHandler.LOADED);
                    } catch (Exception e) {
                        activity.alert("Error stopping recording: " + e.getMessage());
                    }
                } else {
                    try {
                        activity.triggerJob(new StartRecordingJob(activity, recordHandler));
                    } catch (Exception e) {
                        activity.alert("Error starting recording: " + e.getMessage());
                    }
                }
            }

        });
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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
            }
        });
        clearRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.playSoundEffect(SoundEffectConstants.CLICK);
                clearRecordingDialog.show();
            }
        });
        recordingSeekBar.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (playingInProgress()) {
                    try {
                        transitionTo(RecordHandler.PAUSED);
                    } catch (Exception e) {
                        activity.alert("Error pausing recording: " + e.getMessage());
                    }
                }
                return false;
            }
        });

        recordingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                if (fromUser) {
                    if (playingInProgress()) {
                        try {
                            transitionTo(RecordHandler.PAUSED);
                        } catch (Exception e) {
                            activity.alert("Error pausing recording: " + e.getMessage());
                        }
                    }
                    recordingPlayer.seekTo(progress);
                }
                recordingDurationTextView.setText(Utilities.formatMilliSecondsShort(progress));
            }
        });
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




