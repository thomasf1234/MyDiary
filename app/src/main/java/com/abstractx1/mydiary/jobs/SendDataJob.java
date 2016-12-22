package com.abstractx1.mydiary.jobs;

import android.Manifest;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.abstractx1.mydiary.Compress;
import com.abstractx1.mydiary.DataCollection;
import com.abstractx1.mydiary.EmailClient;
import com.abstractx1.mydiary.GlobalApplicationValues;
import com.abstractx1.mydiary.MyDiaryApplication;
import com.abstractx1.mydiary.Researcher;
import com.abstractx1.mydiary.Utilities;
import com.example.demo.job.PermissionJob;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfisher on 16/12/2016.
 */

public class SendDataJob extends PermissionJob {
    public static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int PERMISSION_REQUEST_ID = 2;

    public SendDataJob(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    @Override
    public void perform() throws Exception {
        EmailClient sendDataEmailClient = new EmailClient(appCompatActivity);
        try {
            List<File> files = new ArrayList<>();
            File file = File.createTempFile("answers", ".txt");
            FileWriter writer = new FileWriter(file);

            for (DataCollection dataCollection : Researcher.getInstance().getDataCollections()) {
                /** Saving the contents to the file*/
                writer.write(dataCollection.getFullQuestion());
                writer.write("\n\n");
                writer.write("Answer: ");
                writer.write(dataCollection.getAnswer());
                writer.write("\n\n\n");

                MyDiaryApplication.log("Adding: question" + dataCollection.getQuestionNumber() + " to the zip");

                if (dataCollection.hasRecording()) {
                    files.add(dataCollection.getRecording());
                    MyDiaryApplication.log("Adding: " + dataCollection.getRecording().getAbsolutePath() + " to the zip");
                }
            }

            if (Researcher.getInstance().hasCaption()) {
                writer.write("Caption: ");
                writer.write(Researcher.getInstance().getCaption());
                writer.write("\n\n");
            }

            writer.close();
            files.add(file);

            if (Researcher.getInstance().hasImagePath()) {
                files.add(new File(Researcher.getInstance().getImagePath()));
            }

            String[] filePaths = new String[files.size()];
            for (int i=0; i< filePaths.length; ++i) {
                filePaths[i] = files.get(i).getAbsolutePath();
            }
            /** Closing the writer object */
            String subject = "MyDiary entry";

            File externalDir = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDiary_data");
            if (!(externalDir.exists() && externalDir.isDirectory())) {
                externalDir.mkdir();
            }

            String zipPath = externalDir.getAbsolutePath() + File.separator + Utilities.getDateTime() + "-MyDiary.zip";
            new Compress(filePaths, zipPath).zip();
            sendDataEmailClient.open(GlobalApplicationValues.getResearcherEmailAddress(appCompatActivity), subject, new String[]{zipPath});
        } catch (Exception e) {
            Utilities.alert(appCompatActivity, "Error compressing: " + e.getMessage());
        }
    }

    @Override
    public int getPermissionRequestId() {
        return PERMISSION_REQUEST_ID;
    }

    @Override
    public String[] getPermissions() {
        return PERMISSIONS;
    }
}
