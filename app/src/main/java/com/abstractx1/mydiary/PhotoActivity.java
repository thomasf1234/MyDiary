package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.abstractx1.mydiary.dialog_builders.ConfirmationDialogBuilder;
import com.abstractx1.mydiary.dialog_builders.DebugDialogBuilder;
import com.abstractx1.mydiary.dialogs.ScreenShotDialog;
import com.abstractx1.mydiary.jobs.GetAndSetExternalBitmapJob;

import java.io.IOException;

public class PhotoActivity extends MyDiaryActivity implements View.OnClickListener {
    public Animation scaleAnimation;
    public Button cameraButton, uploadButton, clearPictureButton, nextButton;
    private CameraHandler cameraHandler;
    private ImageView screenShotImageView;
    private AlertDialog clearImageDialog;
    private static int REQUEST_GET_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        cameraHandler = new CameraHandler(this, getCacheDir() + "/image3.jpg");
        initializeWidgets();
        initializeClearRecordingDialog();
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
        screenShotImageView.getLayoutParams().height = (int) (getDisplayWidth() / 1.2);
        screenShotImageView.getLayoutParams().width = (int) (getDisplayWidth() / 1.2);
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
                    clearImageDialog.show();
                } catch (Exception e) {
                    Toast.makeText(this, "An error occurred while clearing the selected photo." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.screenShotImageView:
                if(DataCollection.getInstance().hasImage()) {
                    ScreenShotDialog dialog = new ScreenShotDialog(this, DataCollection.getInstance().getImage());
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
                DataCollection.getInstance().setImage(cameraHandler.getImagePath());
                screenShotImageView.setImageBitmap(DataCollection.getInstance().getImage());
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

    private void initializeClearRecordingDialog() {
        ConfirmationDialogBuilder builder = new ConfirmationDialogBuilder(this, "Are you sure you want clear the image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                try {
                    DataCollection.getInstance().clearImage();
                    screenShotImageView.setImageBitmap(null);
                    screenShotImageView.invalidate();
                    ButtonHelper.disable(clearPictureButton);
                    alert("Cleared image successfully");
                } catch (Exception e) {
                    alert("Error clearing image: " + e.getMessage());
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
        this.clearImageDialog = builder.create();
    }
}
