package com.abstractx1.mydiary.record;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abstractx1.mydiary.ButtonHelper;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.lib.states.State6;

/**
 * Created by tfisher on 16/11/2016.
 */


public class RecordHandler extends State6 {
    private State DISABLED = State.ONE;
    private State EMPTY = State.TWO;
    private State RECORDING = State.THREE;
    private State IDLE = State.FOUR;
    private State PLAYING = State.FIVE;
    private State PAUSED = State.SIX;

    private Activity activity;
    private Button recordButton, playButton, clearRecordingButton;
    private SeekBar recordingSeekBar;
    private TextView recordingDurationTextView;

    public RecordHandler(Activity activity,
                         Button recordButton,
                         Button playButton,
                         Button clearRecordingButton,
                         SeekBar recordingSeekBar,
                         TextView recordingDurationTextView) throws Exception {
        this.activity = activity;
        this.recordButton = recordButton;
        this.playButton = playButton;
        this.clearRecordingButton = clearRecordingButton;
        this.recordingSeekBar = recordingSeekBar;
        this.recordingDurationTextView = recordingDurationTextView;
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
        ButtonHelper.enable(recordButton);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);
        recordingSeekBar.setEnabled(false);
        recordingDurationTextView.setText(R.string.zero_seconds);
    }

    private boolean hasMic() {
        return activity.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }
}




