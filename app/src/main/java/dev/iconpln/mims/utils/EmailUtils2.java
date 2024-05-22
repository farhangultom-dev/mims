package dev.iconpln.mims.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

//HARUS TETAP JAVA
public class EmailUtils2 {
    public static void sendEmail(Activity activity, String[] mailto, String subject, String message, File attachment) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        String to[] = mailto;
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        if (attachment != null) {
            Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getApplicationContext().getPackageName() + ".provider", attachment);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        activity.startActivity(Intent.createChooser(emailIntent, message));
    }
}
