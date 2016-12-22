package com.abstractx1.mydiary;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.abstractx1.mydiary.adapters.QuestionsArrayAdapter;
import com.abstractx1.mydiary.dialogs.IntroductionDialog;
import com.abstractx1.mydiary.dialogs.ResearcherEmailDialog;
import com.abstractx1.mydiary.dialogs.ScreenShotDialog;
import com.abstractx1.mydiary.jobs.DebugPrintFilesJob;
import com.abstractx1.mydiary.jobs.GetAndSetExternalBitmapJob;
import com.abstractx1.mydiary.jobs.SendDataJob;

import java.io.IOException;


public class TitleActivity extends MyDiaryActivity {
    public static int REQUEST_GET_FROM_GALLERY = 2;
    public ListView questionsListView;
    public CameraHandler cameraHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isInDebugMode() && !GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) {
            GlobalApplicationValues.editResearcherEmailAddress(this, getString(R.string.default_researcher_email_address));
            showIntroductionDialog();
        }
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
            case R.id.contactResearcherMenuOption:
                EmailClient emailClient = new EmailClient(this);
                emailClient.open(GlobalApplicationValues.getResearcherEmailAddress(this), "Contact Researcher");
                return true;
            case R.id.imageButtonMenuOption:
                AlertDialog alertDialog = ScreenShotDialog.create(this);
                alertDialog.show();
                return true;
            case R.id.sendButtonMenuOption:
                try {
                    triggerJob(new SendDataJob(this));
                } catch (Exception e) {
                    alert("Please contact the researcher, for some reason we could not send data.");
                }
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
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraHandler.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Researcher.getInstance().setImagePath(cameraHandler.getImagePath());
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                AlertDialog alertDialog = ScreenShotDialog.create(this);
                alertDialog.show();
            } catch (IOException e) {
                MyDiaryApplication.log(e, "An error occurred reading the photo file.");
                alert("An error occurred reading the photo file.");
            }
        } else if(requestCode == REQUEST_GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                triggerJob(new GetAndSetExternalBitmapJob(this, selectedImage));
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
