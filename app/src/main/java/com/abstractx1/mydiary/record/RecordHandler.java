package com.abstractx1.mydiary.record;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abstractx1.mydiary.ButtonHelper;
import com.abstractx1.mydiary.DataCollector;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Utilities;
import com.abstractx1.mydiary.lib.states.State6;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tfisher on 16/11/2016.
 */


public class RecordHandler extends State6 {
    public static State DISABLED = State.ONE;
    public static State EMPTY = State.TWO;
    public static State RECORDING = State.THREE;
    public static State IDLE = State.FOUR;
    public static State PLAYING = State.FIVE;
    public static State PAUSED = State.SIX;

    private Activity activity;
    private Animation hoverAnimation;
    private Button recordButton, playButton, clearRecordingButton;
    private SeekBar recordingSeekBar;
    private TextView recordingDurationTextView;
    private Recorder recorder;
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
        setState(initialState);
    }

    @Override
    protected void onSetStateONE() throws Exception {
        ButtonHelper.disable(recordButton);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);
    }

    @Override
    protected void onSetStateTWO() throws Exception {
        if (DataCollector.getInstance().hasRecording()) { DataCollector.getInstance().clearRecording(); }
        ButtonHelper.enable(recordButton);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);
    }

    @Override
    protected void onSetStateTHREE() throws Exception {
        ButtonHelper.enable(recordButton);
        ButtonHelper.customize(activity, recordButton, R.drawable.stop_button, R.drawable.stop_button_hover, hoverAnimation, "Stop Recording");
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);

    }

    @Override
    protected void onSetStateFOUR() throws Exception {
        ButtonHelper.customize(activity, recordButton, R.drawable.record_button, R.drawable.record_button_hover, hoverAnimation, "Start Recording");
        ButtonHelper.disable(recordButton);
        ButtonHelper.enable(playButton);
        ButtonHelper.enable(clearRecordingButton);
        recordingSeekBar.setEnabled(true);
        recordingDurationTextView.setText(Utilities.formatSeconds(recorder.getRecordingDuration()));
    }

    public boolean recordingInProgress() {
        return state == RECORDING;
    }

    public void setUpNewRecorder() throws Exception {
        this.recorder = new Recorder();
    }

    public void startRecording() throws Exception {
        setUpTimer();
        recorder.record();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRecordingDurationTextView();
            }
        }, 0, 200);
        setState(RECORDING);
    }

    public void stopRecording() throws Exception {
        recorder.stop();
        timer.cancel();
        DataCollector.getInstance().setRecording(recorder.getOutputFile());
        setState(IDLE);
    }

    private boolean hasMic() {
        return activity.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void setUpTimer() {
      if (timer == null)
          timer = new Timer();
      else {
          timer.cancel();
          timer = new Timer();
      }
    }

    // This gets executed in a non-UI thread:
    public void updateRecordingDurationTextView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                recordingDurationTextView.setText(Utilities.formatSeconds(recorder.getRecordingDuration()));
            }
        });
    }
}




