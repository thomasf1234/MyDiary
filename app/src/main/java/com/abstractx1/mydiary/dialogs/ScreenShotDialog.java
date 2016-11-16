package com.abstractx1.mydiary.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.abstractx1.mydiary.PhotoActivity;
import com.abstractx1.mydiary.R;

/**
 * Created by tfisher on 27/10/2016.
 */

//http://www.mkyong.com/android/android-custom-dialog-example/
public class ScreenShotDialog extends Dialog {
    private Bitmap image;
    private PhotoActivity activity;

    public ScreenShotDialog(PhotoActivity activity, Bitmap image) {
        super(activity);
        this.activity = activity;
        this.image = image;
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.screen_shot_dialog);
        setTitle(R.string.screen_shot_dialog_title);
        ImageView imageView = (ImageView) findViewById(R.id.screenShotDialogImage);
        imageView.getLayoutParams().height = (int) Math.floor(activity.getDisplayHeight() / 1.5);
        imageView.getLayoutParams().width = (int) Math.floor(activity.getDisplayWidth() / 1.5);
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setImageBitmap(image);
    }
}
