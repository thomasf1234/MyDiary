package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public Animation scaleAnimation;
    public Button cameraButton;
    private CameraHandler cameraHandler;
    private ImageView screenShotImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraHandler = new CameraHandler(this, getCacheDir() + "/image3.jpg");
        initializeWidgets();
    }

    public void initializeWidgets() {
        this.scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        this.cameraButton = (Button) findViewById(R.id.cameraButton);
        this.screenShotImageView = (ImageView) findViewById(R.id.screenShotImageView);
        cameraButton.setOnClickListener(this);
        cameraButton.setOnTouchListener(this);

        screenShotImageView.setOnClickListener(this);

        screenShotImageView.getLayoutParams().height = getDisplayWidth() / 2;
        screenShotImageView.getLayoutParams().width = getDisplayWidth() / 2;
        screenShotImageView.setBackgroundColor(Color.BLACK);

        //disable the camera button if we do not have camera
        if(!this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            ButtonHelper.disable(cameraButton);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cameraButton:
                view.startAnimation(scaleAnimation);
                try {
                    cameraHandler.dispatchTakePictureIntent();
                    break;
                } catch (Exception e) {
                    Toast.makeText(this, "An error occurred while taking the photo." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.screenShotImageView:
                try {
                    ScreenShotDialog dialog = new ScreenShotDialog(this, cameraHandler.getBitmap());
                    dialog.show();
                    break;
                } catch (IOException e) {
                    Toast.makeText(this, "An error occurred while clicking the photo." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                view.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                view.getBackground().clearColorFilter();
                view.invalidate();
                break;
            }
        }
        return false;
    }

    private void showDebugDialog() {
        AlertDialog.Builder builder = new DebugDialogBuilder(this);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraHandler.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                screenShotImageView.setImageBitmap(cameraHandler.getBitmap());
                screenShotImageView.invalidate();
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred reading the photo file:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(getResources().getBoolean(R.bool.debug_mode))
            inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.debug:
                showDebugDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getDisplayWidth() {
        return getDisplayMetrics().widthPixels;
    }

    private int getDisplayHeight() {
        return getDisplayMetrics().heightPixels;
    }

    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }
}
