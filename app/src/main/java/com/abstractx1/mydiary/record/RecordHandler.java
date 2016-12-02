package com.abstractx1.mydiary.record;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.ButtonHelper;
import com.abstractx1.mydiary.DataCollector;
import com.abstractx1.mydiary.MyDiaryActivity;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Utilities;
import com.abstractx1.mydiary.lib.states.State6;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tfisher on 16/11/2016.
 */


public class RecordHandler extends State6 {
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
    private Animation hoverAnimation;
    private ImageButton recordButton, playButton, clearRecordingButton;
    private SeekBar recordingSeekBar;
    private TextView recordingDurationTextView;
    private Recorder recorder;
    private RecordingPlayer recordingPlayer;
    private Handler handler;
    private Timer timer;

    public RecordHandler(MyDiaryActivity activity,
                         Animation hoverAnimation,
                         ImageButton recordButton,
                         ImageButton playButton,
                         ImageButton clearRecordingButton,
                         SeekBar recordingSeekBar,
                         TextView recordingDurationTextView) throws Exception {
        this.activity = activity;
        this.hoverAnimation = hoverAnimation;
        this.recordButton = recordButton;
        this.playButton = playButton;
        this.clearRecordingButton = clearRecordingButton;
        this.recordingSeekBar = recordingSeekBar;
        this.recordingDurationTextView = recordingDurationTextView;
        this.handler = new Handler();
        State initialState = hasSystemMicrophoneFeature() ? EMPTY : DISABLED;
        transitionTo(initialState);
        setUpUpdateUISchedule();
    }

    //DISABLED
    @Override
    protected void onSetStateONE() throws Exception {
        keepScreenAwake(false);
        ButtonHelper.disable(recordButton);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);
    }

    //EMPTY
    @Override
    protected void onSetStateTWO() throws Exception {
        keepScreenAwake(false);
        if (DataCollector.getInstance().hasRecording()) { DataCollector.getInstance().clearRecording(); }
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
        keepScreenAwake(true);
        ButtonHelper.enable(recordButton);
        ButtonHelper.customizeAndroidStyle(activity, recordButton, android.R.drawable.presence_audio_busy, "Stop Recording");
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
    }

    //READY
    @Override
    protected void onSetStateFOUR() throws Exception {
        if (recorder.isRecording()) {
            recorder.stop();
            DataCollector.getInstance().setRecording(recorder.getOutputFile());
            setUpNewRecordingPlayer();
        }
        keepScreenAwake(false);
        ButtonHelper.customizeAndroidStyle(activity, recordButton, android.R.drawable.ic_btn_speak_now, "Start Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.customizeAndroidStyle(activity, playButton, android.R.drawable.ic_media_play, "Play Recording");
        ButtonHelper.enable(playButton);
        ButtonHelper.enable(clearRecordingButton);
        recordingDurationTextView.setText(Utilities.formatMilliSeconds(recordingPlayer.getDuration()));
        recordingSeekBar.setMax(recordingPlayer.getDuration());
        recordingSeekBar.setEnabled(true);
        recordingSeekBar.setProgress(recordingSeekBar.getMax());
    }

    //PLAYING
    @Override
    protected void onSetStateFIVE() throws Exception {
        recordingPlayer.play();
        keepScreenAwake(true);
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
        keepScreenAwake(false);
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
        recordingDurationTextView.setText(Utilities.formatMilliSeconds(recordingPlayer.getCurrentPosition()));
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
                    recordingDurationTextView.setText(Utilities.formatMilliSeconds(recordingPlayer.getCurrentPosition()));
                    recordingSeekBar.setProgress(recordingPlayer.getCurrentPosition());
                }
            }
        });
    }

    public void onDestroy() {
        cancelTimer();
        if (hasRecorder()) { cancelRecorder(); }
        if (hasRecordingPlayer()) { cancelRecordingPlayer(); }
        keepScreenAwake(false);
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
        recordingPlayer.setInputFile(DataCollector.getInstance().getRecording());
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
                    recordingDurationTextView.setText(Utilities.formatMilliSeconds(recorder.getRecordingDurationMilliSeconds()));
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

    private void keepScreenAwake(boolean keepAwake) {
        if (keepAwake)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected Map<State, State[]> getValidStateTransitions() {
        return validStateTransitions;
    }
}




