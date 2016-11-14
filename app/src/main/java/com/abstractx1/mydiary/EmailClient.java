package com.abstractx1.mydiary;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by tfisher on 12/11/2016.
 */

public class EmailClient {
    private Activity activity;

    public EmailClient(Activity activity) {
        this.activity = activity;
    }

    public void open(String toAddress) {
        String[] TO = {toAddress};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        try {
            Intent gmailIntent = Intent.createChooser(emailIntent, "Send mail...");
            activity.startActivity(gmailIntent);
            activity.finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
