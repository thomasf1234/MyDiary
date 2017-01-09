package com.abstractx1.mydiary;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;


import com.abstractx1.mydiary.adapters.QuestionsArrayAdapter;
import com.abstractx1.mydiary.broadcast_receivers.MyDiaryBroadcastReceiver;
import com.abstractx1.mydiary.camera.CameraHandler;
import com.abstractx1.mydiary.dialogs.EditAlarmDialog;
import com.abstractx1.mydiary.dialogs.ExpiredDialog;
import com.abstractx1.mydiary.dialogs.HelpDialog;
import com.abstractx1.mydiary.dialogs.IntroductionDialog;
import com.abstractx1.mydiary.dialogs.ResearcherEmailDialog;
import com.abstractx1.mydiary.dialogs.ScreenShotDialog;
import com.abstractx1.mydiary.dialogs.SendDialog;
import com.abstractx1.mydiary.dialogs.ViewTermsAndConditionsDialog;
import com.abstractx1.mydiary.jobs.DebugPrintFilesJob;

import java.io.IOException;


public class TitleActivity extends MyDiaryActivity {
    public static int REQUEST_GET_FROM_GALLERY = 2;
    public ListView questionsListView;
    public FloatingActionButton sendFloatingActionButton;
    public CameraHandler cameraHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isInDebugMode() && !GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) {
            GlobalApplicationValues.editResearcherEmailAddress(this, getString(R.string.default_researcher_email_address));
            showIntroductionDialog();
        }
        MyDiaryApplication.setAlarm(this, false);
        this.cameraHandler = new CameraHandler(this, getCacheDir() + "/image.jpg");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        questionsListView = (ListView) findViewById(R.id.questionsListView);

        ArrayAdapter<DataCollection> questionsArrayAdapter = new QuestionsArrayAdapter(this,
                R.layout.questions_listview_item, Researcher.getInstance().getDataCollections());


        questionsListView.setAdapter(questionsArrayAdapter);

        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                int questionNumber = position + 1;
                Intent intent = InputActivity.newStartIntent(TitleActivity.this, questionNumber);
                startActivity(intent);
            }
        });

        sendFloatingActionButton = (FloatingActionButton) findViewById(R.id.sendFloatingActionButton);
        sendFloatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SendDialog.create(TitleActivity.this).show();
            }

        });

        sendFloatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                toolTipUnderView(view, "Submit Answers");
                return true;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        MyDiaryApplication.log("Resuming TitleActivity");

        if (questionsListView != null) {
            ((QuestionsArrayAdapter) questionsListView.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editResearcherEmailAddressMenuOption:
                AlertDialog editResearcherEmailAddressdialog =  ResearcherEmailDialog.create(this);
                editResearcherEmailAddressdialog.show();
                return true;
            case R.id.editAlarmMenuOption:
                EditAlarmDialog.create(this).show();
                return true;
            case R.id.termsAndConditionsMenuOption:
                ViewTermsAndConditionsDialog.create(this).show();
                return true;
            case R.id.uninstallMenuOption:
                ((MyDiaryApplication) getApplication()).uninstall(this);
                return true;
            case R.id.contactResearcherMenuOption:
                HelpDialog.create(this).show();
                return true;
            case R.id.testNotificationMenuOption:
                Intent intent = new Intent(this, MyDiaryBroadcastReceiver.class);
                intent.setAction(MyDiaryBroadcastReceiver.NOTIFICATION);
                intent.putExtra(MyDiaryBroadcastReceiver.ACTION_ID, MyDiaryBroadcastReceiver.ACTION_REMINDER);
                sendBroadcast(intent);
                return true;
            case R.id.imageButtonMenuOption:
                AlertDialog alertDialog = ScreenShotDialog.create(this);
                alertDialog.show();
                this.currentDialog = alertDialog;
                return true;
            case R.id.debugMenuOption:
                try {
                    triggerJob(new DebugPrintFilesJob(this));
                } catch (Exception e) {
                    alert("Could not print files");
                }
                return true;
            case R.id.clearCacheMenuOption:
                Utilities.clearCache(getApplicationContext());
                alert("Cleared cache");
                return true;
            case R.id.expireMenuOption:
                expireApplication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if (!isInDebugMode())
        {
            menu.findItem(R.id.debugMenuOption).setVisible(false);
            menu.findItem(R.id.clearCacheMenuOption).setVisible(false);
            menu.findItem(R.id.testNotificationMenuOption).setVisible(false);
            menu.findItem(R.id.expireMenuOption).setVisible(false);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraHandler.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Researcher.getInstance().setImagePath(cameraHandler.getImagePath());
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (this.currentDialog != null) {
                    ImageView screenShotImageView = (ImageView) currentDialog.findViewById(R.id.screenShotDialogImage);
                    screenShotImageView.setImageBitmap(Researcher.getInstance().getImage());
                }
            } catch (IOException e) {
                MyDiaryApplication.log(e, "An error occurred reading the photo file.");
                alert("An error occurred reading the photo file.");
            }
        } else if(requestCode == REQUEST_GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Researcher.getInstance().setImagePath(CameraHandler.getImagePath(this, selectedImage));
                if (this.currentDialog != null) {
                    ImageView screenShotImageView = (ImageView) currentDialog.findViewById(R.id.screenShotDialogImage);
                    screenShotImageView.setImageBitmap(Researcher.getInstance().getImage());
                }
            } catch (Exception e) {
                MyDiaryApplication.log(e, "An error occurred reading the photo file.");
                alert("An error occurred reading the photo file.");
            }
        }
    }

    public CameraHandler getCameraHandler() {
        return cameraHandler;
    }
    private void showIntroductionDialog() {
        AlertDialog alertDialog = IntroductionDialog.create(this);
        alertDialog.show();
    }
}
