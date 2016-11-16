package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import com.abstractx1.mydiary.dialogs.ScreenShotDialog;
import com.abstractx1.mydiary.jobs.GetAndSetExternalBitmapJob;
import com.example.demo.job.PermissionActivity;

import java.io.IOException;

public class PhotoActivity extends PermissionActivity implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {
    public Animation scaleAnimation;
    public Button cameraButton;
    public Button uploadButton;
    private CameraHandler cameraHandler;
    private ImageView screenShotImageView;
    private static int REQUEST_GET_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        cameraHandler = new CameraHandler(this, getCacheDir() + "/image3.jpg");
        initializeWidgets();
    }

    public void initializeWidgets() {
        this.scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        this.cameraButton = (Button) findViewById(R.id.cameraButton);
        this.uploadButton = (Button) findViewById(R.id.uploadButton);
        this.screenShotImageView = (ImageView) findViewById(R.id.screenShotImageView);
        cameraButton.setOnClickListener(this);
        cameraButton.setOnTouchListener(this);
        cameraButton.setOnLongClickListener(this);
        uploadButton.setOnClickListener(this);
        uploadButton.setOnTouchListener(this);
        uploadButton.setOnLongClickListener(this);

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
                try {
                    cameraHandler.dispatchTakePictureIntent();
                    break;
                } catch (Exception e) {
                    Toast.makeText(this, "An error occurred while taking the photo." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.uploadButton:
                try {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_GET_FROM_GALLERY);
                } catch (Exception e) {
                    Toast.makeText(this, "An error occurred while uploading the photo." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.screenShotImageView:
                if(DataCollector.getInstance().hasImage()) {
                    ScreenShotDialog dialog = new ScreenShotDialog(this, DataCollector.getInstance().getImage());
                    dialog.show();
                } else {
                    Toast.makeText(this, "An image has not been taken.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.cameraButton:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setBackgroundResource(R.drawable.camera_button_hover);
                        view.startAnimation(scaleAnimation);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackgroundResource(R.drawable.camera_button);
                        view.clearAnimation();
                        scaleAnimation.cancel();
                        scaleAnimation.reset();
                        view.invalidate();
                        break;
                    }
                }
                break;
            case R.id.uploadButton:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setBackgroundResource(R.drawable.upload_button_hover);
                        view.startAnimation(scaleAnimation);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackgroundResource(R.drawable.upload_button);
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
            case R.id.cameraButton:
                Toast.makeText(PhotoActivity.this, "Take Picture", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.uploadButton:
                Toast.makeText(PhotoActivity.this, "Upload Picture", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.screenShotImageView:
                Toast.makeText(PhotoActivity.this, "View Picture", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return true;
        }
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
                DataCollector.getInstance().setImage(cameraHandler.getImagePath());
                screenShotImageView.setImageBitmap(DataCollector.getInstance().getImage());
                screenShotImageView.invalidate();
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred reading the photo file:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == REQUEST_GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                triggerJob(new GetAndSetExternalBitmapJob(this, selectedImage, screenShotImageView));
            } catch (Exception e) {
                Toast.makeText(this, "An error occurred reading the photo file:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(getResources().getBoolean(R.bool.debug_mode))
            inflater.inflate(R.menu.debug_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.debug:
                showDebugDialog();
                return true;
            case R.id.clearCache:
                Utilities.clearCache(getApplicationContext());
                Toast.makeText(this, "Cleared cache", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int getDisplayWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public int getDisplayHeight() {
        return getDisplayMetrics().heightPixels;
    }

    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }
}
