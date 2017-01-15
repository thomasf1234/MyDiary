package com.abstractx1.mydiary.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.AlertDialog;

import com.abstractx1.mydiary.ButtonHelper;
import com.abstractx1.mydiary.Device;
import com.abstractx1.mydiary.MyDiaryApplication;
import com.abstractx1.mydiary.R;
import com.abstractx1.mydiary.Researcher;
import com.abstractx1.mydiary.TitleActivity;
import com.abstractx1.mydiary.jobs.GetAndSetExternalBitmapJob;

import java.io.IOException;

/**
 * Created by tfisher on 27/10/2016.
 */
//http://www.mkyong.com/android/android-custom-dialog-example/
public class ScreenShotDialog {
    public static AlertDialog create(final TitleActivity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.screen_shot_dialog, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Additional Information");

        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        ;
                    }
                });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                ImageView imageView = (ImageView) alertDialog.findViewById(R.id.screenShotDialogImage);
                int imageWidth = (int) Math.floor(imageView.getWidth() / 1.1);
                int imageHeight = imageWidth;

                imageView.getLayoutParams().width = imageWidth;
                imageView.getLayoutParams().height = imageHeight;

                imageView.setBackgroundColor(Color.LTGRAY);
                if (Researcher.getInstance().hasImage()) {
                    imageView.setImageBitmap(Researcher.getInstance().getImage());
                } else {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }

                if (!activity.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Button screenShotButton = (Button) alertDialog.findViewById(R.id.screenShotButton);
                    screenShotButton.setEnabled(false);
                    screenShotButton.setVisibility(View.GONE);
                }

                Button screenShotButton = (Button) alertDialog.findViewById(R.id.screenShotButton);
                screenShotButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            MyDiaryApplication.log("About to take picture");
                            activity.getCameraHandler().dispatchTakePictureIntent();
                        } catch (IOException e) {
                            MyDiaryApplication.log(e, "An error occurred while taking the photo.");
                            activity.alert("An error occurred while taking the photo.");
                        }
                    }
                });

                if (!Device.hasCameraApp(activity)) {
                    ButtonHelper.toggleAvailable(screenShotButton, false);
                }

                Button uploadButton = (Button) alertDialog.findViewById(R.id.uploadButton);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyDiaryApplication.log("About to upload picture");
                        try {
                            activity.triggerJob(new GetAndSetExternalBitmapJob(activity));
                        } catch (Exception e) {
                            MyDiaryApplication.log(e, "An error occurred reading the photo file.");
                            activity.alert("An error occurred reading the photo file.");
                        }
                    }
                });

                EditText captionEditText = (EditText) alertDialog.findViewById(R.id.screenShotDialogCaptionInput);
                captionEditText.setText(Researcher.getInstance().getCaption());
                captionEditText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        Researcher.getInstance().setCaption(cs.toString());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                    }

                });
            }
        });

        return alertDialog;
    }
}