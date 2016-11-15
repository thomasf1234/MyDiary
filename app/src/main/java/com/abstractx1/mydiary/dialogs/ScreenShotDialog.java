package com.abstractx1.mydiary.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.abstractx1.mydiary.R;

/**
 * Created by tfisher on 27/10/2016.
 */

//http://www.mkyong.com/android/android-custom-dialog-example/
public class ScreenShotDialog extends Dialog {
    private Bitmap image;

    public ScreenShotDialog(Activity activity, Bitmap image) {
        super(activity);
        this.image = image;
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.screen_shot_dialog);
        setTitle(R.string.screen_shot_dialog_title);
        ImageView imageView = (ImageView) findViewById(R.id.screenShotDialogImage);
        imageView.setImageBitmap(image);
    }
}
