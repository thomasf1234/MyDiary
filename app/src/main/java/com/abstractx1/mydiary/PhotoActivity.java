package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
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

public class PhotoActivity extends PermissionActivity implements View.OnClickListener {
    public Animation scaleAnimation;
    public Button cameraButton, uploadButton, clearPictureButton, nextButton;
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
        this.clearPictureButton = (Button) findViewById(R.id.clearPictureButton);
        this.nextButton = (Button) findViewById(R.id.nextButtonToRecordActivity);
        this.screenShotImageView = (ImageView) findViewById(R.id.screenShotImageView);

        ButtonHelper.customize(this, cameraButton, R.drawable.camera_button, R.drawable.camera_button_hover, scaleAnimation, "Take Picture");
        ButtonHelper.customize(this, uploadButton, R.drawable.upload_button, R.drawable.upload_button_hover, scaleAnimation, "Upload Picture");
        ButtonHelper.customize(this, clearPictureButton, R.drawable.delete_button, R.drawable.delete_button_hover, scaleAnimation, "Clear Selected Picture");

        cameraButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        clearPictureButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        
        ButtonHelper.disable(clearPictureButton);

        screenShotImageView.setOnClickListener(this);

        //TODO: get smallest of height or width and use
        screenShotImageView.getLayoutParams().height = (int) (getDisplayWidth() / 1.5);
        screenShotImageView.getLayoutParams().width = (int) (getDisplayWidth() / 1.5);
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
            case R.id.clearPictureButton:
                try {
                    DataCollector.getInstance().clearImage();
                    screenShotImageView.setImageBitmap(null);
                    screenShotImageView.invalidate();
                    ButtonHelper.disable(clearPictureButton);
                } catch (Exception e) {
                    Toast.makeText(this, "An error occurred while clearing the selected photo." + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            case R.id.nextButtonToRecordActivity:
                Intent intent = new Intent(this, RecordActivity.class);
                startActivity(intent);
                break;
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
                ButtonHelper.enable(clearPictureButton);
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred reading the photo file:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == REQUEST_GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                triggerJob(new GetAndSetExternalBitmapJob(this, selectedImage, screenShotImageView, clearPictureButton));
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