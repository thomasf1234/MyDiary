package com.abstractx1.mydiary;

import android.app.AlertDialog;
import android.content.Context;
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

/**
 * Created by tfisher on 23/11/2016.
 */

public abstract class MyDiaryActivity extends PermissionActivity {
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
}
