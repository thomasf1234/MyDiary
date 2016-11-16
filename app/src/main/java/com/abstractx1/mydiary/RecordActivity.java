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
import android.widget.Toast;

import com.abstractx1.mydiary.record.RecordHandler;
import com.example.demo.job.PermissionActivity;

public class RecordActivity extends PermissionActivity implements View.OnTouchListener, View.OnLongClickListener {
    public Animation scaleAnimation;
    public Button recordButton;
    public Button playButton;

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
        //recordButton.setOnClickListener(this);
        recordButton.setOnTouchListener(this);
        recordButton.setOnLongClickListener(this);
        //playButton.setOnClickListener(this);
        playButton.setOnTouchListener(this);
        playButton.setOnLongClickListener(this);

        //disable the record button if we do not have camera
        if(!this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            ButtonHelper.disable(recordButton);
        }
    }



    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.recordButton:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setBackgroundResource(R.drawable.record_button_hover);
                        view.startAnimation(scaleAnimation);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackgroundResource(R.drawable.record_button);
                        view.clearAnimation();
                        scaleAnimation.cancel();
                        scaleAnimation.reset();
                        view.invalidate();
                        break;
                    }
                }
                break;
            case R.id.playButton:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setBackgroundResource(R.drawable.play_button_hover);
                        view.startAnimation(scaleAnimation);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackgroundResource(R.drawable.play_button);
                        view.clearAnimation();
                        scaleAnimation.cancel();
                        scaleAnimation.reset();
                        view.invalidate();
                        break;
                    }
                }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.recordButton:
                Toast.makeText(RecordActivity.this, "Record Audio", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.playButton:
                Toast.makeText(RecordActivity.this, "Play Recorded Audio", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return true;
        }
    }
}
