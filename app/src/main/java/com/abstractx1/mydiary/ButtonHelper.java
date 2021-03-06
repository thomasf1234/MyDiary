package com.abstractx1.mydiary;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by tfisher on 26/10/2016.
 */

public class ButtonHelper {
    public static void enable(View button) {
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
    }

    public static void toggleAvailable(View button, boolean enabled) {
        if(enabled) {
            button.setEnabled(true);
            button.setAlpha(1);
        } else {
            button.setEnabled(false);
            button.setAlpha(0.4f);
        }
    }

    public static void disable(View button) {
        button.setEnabled(false);
        button.setVisibility(View.INVISIBLE);
    }

    public static void customize(final MyDiaryActivity activity, Button button, final int defaultImage, final int hoverImage, final Animation hoverAnimation, final String tooltip) {
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
                activity.alert(tooltip);
                return true;
            }
        });
        button.setBackgroundResource(defaultImage);
        button.invalidate();
    }

    public static void customizeAndroidStyle(final MyDiaryActivity activity, ImageButton button, final int drawableResourceId, final String tooltip) {
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                activity.alert(tooltip);
                return true;
            }
        });
        button.setImageResource(drawableResourceId);
        button.invalidate();
    }
}