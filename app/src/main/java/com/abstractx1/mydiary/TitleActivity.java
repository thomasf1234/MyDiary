package com.abstractx1.mydiary;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TitleActivity extends AppCompatActivity {
    public Button contactResearcherButton;
    public Button yesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        contactResearcherButton = (Button) findViewById(R.id.contactResearcherButton);
        yesButton = (Button) findViewById(R.id.yesTitleScreenButton);

        contactResearcherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailClient emailClient = new EmailClient(TitleActivity.this);
                emailClient.open("test@testtest.com");
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TitleActivity.this, "Move to main screen", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
