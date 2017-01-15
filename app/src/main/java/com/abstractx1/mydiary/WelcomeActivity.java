package com.abstractx1.mydiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.abstractx1.mydiary.dialogs.HelpDialog;

public class WelcomeActivity extends MyDiaryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setTitle("Welcome");

        Button continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, TosActivity.class));
                finish();
            }
        });

        if (!isInDebugMode()) {
            GlobalApplicationValues.editResearcherEmailAddress(this, getString(R.string.default_researcher_email_address));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tos_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uninstallMenuOption:
                ((MyDiaryApplication) getApplication()).uninstall(this);
                return true;
            case R.id.contactResearcherMenuOption:
                HelpDialog.create(this).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
