package com.abstractx1.mydiary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.abstractx1.mydiary.dialogs.IntroductionDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TitleActivity extends AppCompatActivity implements View.OnClickListener {
    public Button contactResearcherButton;
    public Button yesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!new EmailClient(this).hasChosenEmailAppPackage()) { validateParticipantInformedOfStudy();}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        contactResearcherButton = (Button) findViewById(R.id.contactResearcherButton);
        yesButton = (Button) findViewById(R.id.yesTitleScreenButton);

        contactResearcherButton.setOnClickListener(this);
        yesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contactResearcherButton:
                EmailClient emailClient = new EmailClient(TitleActivity.this);
                emailClient.open(getResources().getString(R.string.to_email_address));
                break;
            case R.id.yesTitleScreenButton:
                Intent intent = new Intent(this, PhotoActivity.class);
                startActivity(intent);
                //finish(); //will prevent users from going back
                break;
        }
    }

//    http://stackoverflow.com/questions/8631095/android-preventing-going-back-to-the-previous-activity

    private void validateParticipantInformedOfStudy() {
        AlertDialog alertDialog = IntroductionDialog.create(this);
        alertDialog.show();
    }
}
