package com.abstractx1.mydiary;

import android.app.Activity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by tfisher on 26/10/2016.
 */

public class ButtonHelper {
    public static final int SEMI_TRANSPARENT = 127;
    public static final int OPAQUE = 255;

    public static void enable(Button button) {
        button.setEnabled(true);
        button.setAlpha(1);
        button.getBackground().setAlpha(OPAQUE);
    }

    public static void disable(Button button) {
        button.setEnabled(false);
        button.getBackground().setAlpha(SEMI_TRANSPARENT);
    }

    public static void customize(final Activity activity, Button button, final int defaultImage, final int hoverImage, final Animation hoverAnimation, final String tooltip) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.setBackgroundResource(hoverImage);
                        view.startAnimation(hoverAnimation);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        view.setBackgroundResource(defaultImage);
                        view.clearAnimation();
                        hoverAnimation.cancel();
                        hoverAnimation.reset();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utilities.showToolTip(activity, tooltip);
                return true;
            }
        });
    }
}