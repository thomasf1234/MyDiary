package com.abstractx1.mydiary;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends MyDiaryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isInDebugMode() && !GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            startActivity(new Intent(this, TitleActivity.class));

        }

        finish();
    }
}
