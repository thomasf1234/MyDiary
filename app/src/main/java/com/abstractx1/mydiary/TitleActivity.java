package com.abstractx1.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.abstractx1.mydiary.adapters.MainMenuArrayAdapter;
import com.abstractx1.mydiary.dialogs.IntroductionDialog;

import java.util.ArrayList;
import java.util.List;


public class TitleActivity extends MyDiaryActivity {
    public ListView mainMenuListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!GlobalApplicationValues.hasAcceptedTermsAndConditions(this)) { showIntroductionDialog();}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        mainMenuListView = (ListView) findViewById(R.id.mainMenuListView);
        final List<MainMenuItem> mainMenuItems = new ArrayList<MainMenuItem>();
        mainMenuItems.add(new MainMenuItem("Recording Screen", ContextCompat.getDrawable(this, R.drawable.record_button), RecordActivity.class));
        mainMenuItems.add(new MainMenuItem("Camera Screen", ContextCompat.getDrawable(this, R.drawable.camera_button), PhotoActivity.class));

        MainMenuArrayAdapter adapter = new MainMenuArrayAdapter(this, R.layout.main_menu_listview_item, mainMenuItems);
        mainMenuListView.setAdapter(adapter);

        mainMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(TitleActivity.this, mainMenuItems.get(position).getActivityClass());
                startActivity(intent);
            }
        });
    }


    private void showIntroductionDialog() {
        AlertDialog alertDialog = IntroductionDialog.create(this);
        alertDialog.show();
    }
}
