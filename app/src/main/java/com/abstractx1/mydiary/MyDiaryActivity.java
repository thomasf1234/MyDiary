package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.abstractx1.mydiary.dialogs.ExpiredDialog;
import com.example.demo.job.PermissionActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by tfisher on 23/11/2016.
 */

public abstract class MyDiaryActivity extends PermissionActivity {
    protected AlertDialog currentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        validateExpiry();
        super.onCreate(savedInstanceState);
        load(savedInstanceState);
    }

    public int getDisplayWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public int getDisplayHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public void alert(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void debugAlert(String message) {
        if (isInDebugMode()) { alert(message); }
    }

    public void toolTipUnderView(View view, String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity( Gravity.TOP, view.getLeft() - view.getWidth() / 2 - toast.getView().getWidth() / 2, view.getBottom());
        toast.show();
    }

    protected void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void keepScreenAwake(boolean keepAwake) {
        if (keepAwake)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    protected boolean isInDebugMode() {
        return getResources().getBoolean(R.bool.debug_mode);
    }

    protected void setupResearcher() {
        Resources res = getResources();
        String[] questions = res.getStringArray(R.array.questions);

        for (int i=0; i<questions.length; ++i) {
            String question = questions[i];
            int questionNumber = i + 1;
            Researcher.getInstance().getDataCollections().add(new DataCollection(questionNumber, question));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        MyDiaryApplication.log("Entered OnSaveInstanceState");
        for (DataCollection dataCollection : Researcher.getInstance().getDataCollections()) {
            String key = "answer" + dataCollection.getQuestionNumber();
            String value = dataCollection.getAnswer();
            MyDiaryApplication.log("storing key: '" + key + "' value: '" + value +"' on savedInstanceState");
            savedInstanceState.putString(key, value);
            if(dataCollection.hasRecording()) {
                key = "recordingPath" + dataCollection.getQuestionNumber();
                value = dataCollection.getRecording().getAbsolutePath();
                MyDiaryApplication.log("storing key: '" + key + "' value: '" + value +"' on savedInstanceState");
                savedInstanceState.putString(key, value);
            }
        }

        if (Researcher.getInstance().hasImagePath()) {
            String key = "imagePath";
            String value = Researcher.getInstance().getImagePath();
            savedInstanceState.putString(key, value);
            MyDiaryApplication.log("storing key: '" + key + "' value: '" + value +"' on savedInstanceState");
        }

        if (Researcher.getInstance().hasCaption()) {
            String key = "imageCaption";
            String value = Researcher.getInstance().getCaption();
            savedInstanceState.putString(key, value);
            MyDiaryApplication.log("storing key: '" + key + "' value: '" + value +"' on savedInstanceState");
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void load(Bundle savedInstanceState) {
        if (Researcher.getInstance().hasData()) {
            MyDiaryApplication.log("Researcher has data so not loading from savedInstanceState");
        } else {
            MyDiaryApplication.log("Researcher has no data so possibly loading from savedInstanceState");
            if (savedInstanceState == null) {
                MyDiaryApplication.log("savedInstanceState is null so initializing new dataCollections");
                setupResearcher();
            } else {
                MyDiaryApplication.log("savedInstanceState is not null so loading from savedInstanceState");
                setupResearcher();
                if (savedInstanceState.containsKey("imagePath")) {
                    debugAlert("image loaded");
                    try {
                        Researcher.getInstance().setImagePath(savedInstanceState.getString("imagePath"));
                    } catch (IOException e) {
                        MyDiaryApplication.log(e, "An error occurred loading the photo file.");
                        alert("An error occurred loading the photo file.");
                    }
                }
                if (savedInstanceState.containsKey("imageCaption")) {
                    debugAlert("image caption loaded");
                    Researcher.getInstance().setCaption(savedInstanceState.getString("imageCaption"));
                }
                for (DataCollection dataCollection : Researcher.getInstance().getDataCollections()) {
                    dataCollection.setAnswer(savedInstanceState.getString("answer" + dataCollection.getQuestionNumber()));
                    if (savedInstanceState.containsKey("recordingPath" + dataCollection.getQuestionNumber())) {
                        debugAlert("recording loaded");
                        dataCollection.setRecording(new File(savedInstanceState.getString("recordingPath" + dataCollection.getQuestionNumber())));
                    }
                }
            }
        }
    }

    protected void setKeyboardpushActivityUp() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void validateExpiry() {
        MyDiaryApplication application = (MyDiaryApplication) getApplication();
        if (application.hasExpired()) {
            ExpiredDialog.show(this);
        }
    }
}
