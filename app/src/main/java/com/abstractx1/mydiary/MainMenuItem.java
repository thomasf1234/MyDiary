package com.abstractx1.mydiary;

import android.graphics.drawable.Drawable;

/**
 * Created by tfisher on 24/11/2016.
 */

public class MainMenuItem {
    String label;
    Drawable icon;
    Class<? extends MyDiaryActivity> activityClass;

    public MainMenuItem(String label, Drawable icon, Class<? extends MyDiaryActivity> activityClass) {
        this.label = label;
        this.icon = icon;
        this.activityClass = activityClass;
    }

    public String getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Class<? extends MyDiaryActivity> getActivityClass() { return activityClass; }
}
