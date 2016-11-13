package com.abstractx1.mydiary;

import android.widget.Button;

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
}