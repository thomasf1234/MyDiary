package com.abstractx1.mydiary.record;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.ButtonHelper;
import com.abstractx1.mydiary.DataCollector;
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
        _validStateTransitions.put(PAUSED, new State[]{PLAYING, EMPTY});
        validStateTransitions = Collections.unmodifiableMap(_validStateTransitions);
    }

    private Activity activity;
    private Animation hoverAnimation;
    private Button recordButton, playButton, clearRecordingButton;
    private SeekBar recordingSeekBar;
    private TextView recordingDurationTextView;
    private Recorder recorder;
    private RecordingPlayer recordingPlayer;
    private Handler handler;
    private Timer timer;

    public RecordHandler(Activity activity,
                         Animation hoverAnimation,
                         Button recordButton,
                         Button playButton,
                         Button clearRecordingButton,
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
        State initialState = hasMic() ? EMPTY : DISABLED;
        transitionTo(initialState);
        setUpUpdateUISchedule();
    }

    @Override
    protected void onSetStateONE() throws Exception {
        keepScreenAwake(false);
        ButtonHelper.disable(recordButton);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);
    }

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

    @Override
    protected void onSetStateTHREE() throws Exception {
        keepScreenAwake(true);
        ButtonHelper.enable(recordButton);
        ButtonHelper.customize(activity, recordButton, R.drawable.stop_button, R.drawable.stop_button_hover, hoverAnimation, "Stop Recording");
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);

    }

    @Override
    protected void onSetStateFOUR() throws Exception {
        keepScreenAwake(false);
        ButtonHelper.customize(activity, recordButton, R.drawable.record_button, R.drawable.record_button_hover, hoverAnimation, "Start Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.customize(activity, playButton, R.drawable.play_button, R.drawable.play_button_hover, hoverAnimation, "Play Recording");
        ButtonHelper.enable(playButton);
        ButtonHelper.enable(clearRecordingButton);
        recordingSeekBar.setEnabled(true);
        recordingDurationTextView.setText(Utilities.formatSeconds((int) recorder.getRecordingDuration()));
        recordingSeekBar.setProgress(recordingSeekBar.getMax());
    }

    @Override
    protected void onSetStateFIVE() throws Exception {
        keepScreenAwake(true);
        ButtonHelper.customize(activity, playButton, R.drawable.pause_button, R.drawable.pause_button_hover, hoverAnimation, "Pause Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.enable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(true);
    }

    @Override
    protected void onSetStateSIX() throws Exception {
        keepScreenAwake(false);
        ButtonHelper.customize(activity, playButton, R.drawable.play_button, R.drawable.play_button_hover, hoverAnimation, "Resume Recording");
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

    public void setUpNewRecorder() throws Exception {
        this.recorder = new Recorder();
    }

    public void setUpNewRecordingPlayer() throws IOException {
        this.recordingPlayer = new RecordingPlayer();
        recordingPlayer.setInputFile(DataCollector.getInstance().getRecording());
        recordingPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    transitionTo(READY);
                } catch (Exception e) {
                    Utilities.showToolTip(activity, "Error when recording playback finished: " + e.getMessage());
                }
            }

        });
    }

    public void startRecording() throws Exception {
        recorder.record();
        transitionTo(RECORDING);
    }

    public void startPlaying() throws Exception {
        recordingPlayer.play();
        transitionTo(PLAYING);
    }

    public void setPlayFrom() {
        recordingPlayer.seekTo(getCurrentSelectedMilliSeconds());
    }

    public int getCurrentSelectedMilliSeconds() {
        return (int) Math.floor((recordingSeekBar.getProgress() / 100f) * recorder.getRecordingDurationMilliSeconds());
    }

    public void pausePlaying() throws Exception {
        recordingPlayer.pause();
        transitionTo(PAUSED);
    }

    public void stopRecording() throws Exception {
        recorder.stop();
        DataCollector.getInstance().setRecording(recorder.getOutputFile());
        setUpNewRecordingPlayer();
        transitionTo(READY);
    }

    public void clearRecording() throws Exception {
        transitionTo(EMPTY);
    }

    public void cancelRecording() throws Exception {
        stopRecording();
        clearRecording();
    }

    private boolean hasMic() {
        return activity.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void setUpUpdateUISchedule() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (recordingInProgress())
                    updateRecordingDuration();
                else if (playingInProgress())
                    updatePlayingDuration();
            }
        }, 0, 20);
    }

    // This gets executed in a non-UI thread:
    public void updateRecordingDuration() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                recordingDurationTextView.setText(Utilities.formatSeconds((int) recorder.getRecordingDuration()));
            }
        });
    }

    // This gets executed in a non-UI thread:
    public void updatePlayingDuration() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                recordingDurationTextView.setText(Utilities.formatSeconds((int) recordingPlayer.getPlayingDuration()));
                int progress = (int) Math.floor(recordingPlayer.getPlayingDuration() / (float) recorder.getRecordingDuration() * 100);
                recordingSeekBar.setProgress(progress);
            }
        });
    }

    public void onDestroy() {
        cancelTimer();
        if (recorder.isRecording()) {
            recorder.stop();
        }
        recorder.release();
        if (recordingPlayer.isPlaying()) {
            recordingPlayer.stop();
        }
        recordingPlayer.release();
        keepScreenAwake(false);
        Utilities.showToolTip(activity, "Stopping playing/recording and wiping timer tasks as Activity Stopping");
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




