package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.abstractx1.mydiary.dialog_builders.DebugDialogBuilder;
import com.abstractx1.mydiary.dialogs.ResetChosenEmailClientDialog;
import com.example.demo.job.PermissionActivity;

import java.io.File;

/**
 * Created by tfisher on 23/11/2016.
 */

public abstract class MyDiaryActivity extends PermissionActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if (!isInDebugMode())
        {
            menu.findItem(R.id.debugMenuOption).setVisible(false);
            menu.findItem(R.id.clearCacheMenuOption).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.emailPreferenceMenuOption:
                ResetChosenEmailClientDialog.create(this).show();
                return true;
            case R.id.contactResearcherMenuOption:
                EmailClient emailClient = new EmailClient(this);
                emailClient.open(getResources().getString(R.string.to_email_address));
                return true;
            case R.id.debugMenuOption:
                AlertDialog.Builder builder = new DebugDialogBuilder(this);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.clearCacheMenuOption:
                Utilities.clearCache(getApplicationContext());
                alert("Cleared cache");
                return true;
            case android.R.id.home:
                onBackPressed();
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
        for (DataCollection dataCollection : Researcher.getInstance().getDataCollections()) {
            savedInstanceState.putString("answer" + dataCollection.getQuestionNumber(), dataCollection.getAnswer());
            if(dataCollection.hasRecording()) {
                if (isInDebugMode()) { alert("recording saved"); }
                savedInstanceState.putString("recordingPath" + dataCollection.getQuestionNumber(), dataCollection.getRecording().getAbsolutePath());
            }
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void load(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            debugAlert("Loading");
            if (!Researcher.getInstance().hasData()) {
                setupResearcher();
                for (DataCollection dataCollection : Researcher.getInstance().getDataCollections()) {
                    dataCollection.setAnswer(savedInstanceState.getString("answer" + dataCollection.getQuestionNumber()));
                    if (savedInstanceState.containsKey("recordingPath" + dataCollection.getQuestionNumber())) {
                        debugAlert("recording loaded");
                        dataCollection.setRecording(new File(savedInstanceState.getString("recordingPath" + dataCollection.getQuestionNumber())));
                    }
                }
            }
        } else {
            setupResearcher();
        }
    }

    protected void setKeyboardpushActivityUp() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
}
//60s (stereo) = 1.168MB
//60s (mono)   = 1.176MB
//60s (stereo 3gp) = 0.110MB