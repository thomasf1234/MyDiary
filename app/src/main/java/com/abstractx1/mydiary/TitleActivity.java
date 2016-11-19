package com.abstractx1.mydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class TitleActivity extends AppCompatActivity implements View.OnClickListener {
    public Button contactResearcherButton;
    public Button yesButton;
    public static String RESEARCH_PARTICIPANT = "com.abstractx1.permission.RESEARCH_PARTICIPANT";
    public static int MY_PERMISSIONS_REQUEST_RESEARCH_PARTICIPANT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        validateParticipantInformedOfStudy();
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
        if (!hasPermission(RESEARCH_PARTICIPANT)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(false);
            alertBuilder.setTitle("Permission required");
            alertBuilder.setMessage("By granting the following permission, you accept and agree to the following terms and conditions: 1) You are a participant taking part in a research study, 2) the data you send from this app will only be to the researcher");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(TitleActivity.this, new String[]{RESEARCH_PARTICIPANT}, MY_PERMISSIONS_REQUEST_RESEARCH_PARTICIPANT);
                }
            });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_RESEARCH_PARTICIPANT) {
            if (!hasPermission(RESEARCH_PARTICIPANT)) {
                Utilities.showToolTip(this, "Exiting now");
                finish();
            }
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
