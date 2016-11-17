package com.abstractx1.mydiary;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.abstractx1.mydiary.record.RecordHandler;
import com.example.demo.job.PermissionActivity;

public class RecordActivity extends PermissionActivity {
    public Animation scaleAnimation;
    public Button recordButton, playButton, clearRecordingButton;
    public SeekBar recordingSeekBar;

    private RecordHandler recordHandler;

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


        ButtonHelper.customize(this, recordButton, R.drawable.record_button, R.drawable.record_button_hover, scaleAnimation, "Record Audio");
        ButtonHelper.customize(this, playButton, R.drawable.play_button, R.drawable.play_button_hover, scaleAnimation, "Play Recorded Audio");
        ButtonHelper.customize(this, clearRecordingButton, R.drawable.delete_button, R.drawable.delete_button_hover, scaleAnimation, "Clear Audio");

        recordingSeekBar.setEnabled(false);
        ButtonHelper.disable(playButton);
        ButtonHelper.disable(clearRecordingButton);

        //disable the record button if we do not have camera
        if(!this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            ButtonHelper.disable(recordButton);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Utilities.showToolTip(this, "Recording Cancelled");
        //TODO : Cancel recording if currently recording
    }
}
