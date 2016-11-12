package com.abstractx1.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class TitleActivity extends AppCompatActivity implements View.OnClickListener {
    public Button contactResearcherButton;
    public Button yesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        findViewById(R.id.contactResearcherButton).setOnClickListener(this);
        findViewById(R.id.yesTitleScreenButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contactResearcherButton:
                EmailClient emailClient = new EmailClient(TitleActivity.this);
                emailClient.open("test@testtest.com");
                break;
            case R.id.yesTitleScreenButton:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
