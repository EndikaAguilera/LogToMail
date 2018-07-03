package com.thisobeystudio.logtomail;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_SETTING = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // simple button that will try to send log .txt via email
        findViewById(R.id.send_log_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLogcatMail();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if permission granted send log .txt via email
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendLogcatMail();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Log.d(getLocalClassName(), "shouldShowRequestPermissionRationale = true");

        } else {
            // show go to app setting dialog to grant permission
            goToAppSettingsDialog();
        }

    }

    /**
     * Simple dialog that lets user got to app settings to grant permissions
     */
    private void goToAppSettingsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(R.string.app_settings_dialog_title);
        alertDialogBuilder.setMessage(R.string.app_settings_dialog_message);
        alertDialogBuilder.setPositiveButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(android.R.string.no), null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    /**
     * @return true if permission is granted
     */
    private boolean isWriteExternalStoragePermissionStateGranted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(getLocalClassName(), "Got WRITE_EXTERNAL_STORAGE permissions");
            return true;
        } else {
            Log.e(getLocalClassName(), "Don't have WRITE_EXTERNAL_STORAGE permissions");
            return false;
        }
    }

    /**
     * Send log as .txt file via email
     */
    private void sendLogcatMail() {

        boolean isGranted = isWriteExternalStoragePermissionStateGranted();

        if (!isGranted) {
            // grant this permission also grants READ_EXTERNAL_STORAGE permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {

            // save logcat in file
            File outputFile = new File(Environment.getExternalStorageDirectory(),
                    "logcat.txt");
            try {
                Runtime.getRuntime().exec(
                        "logcat -f " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //send file using email
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Set type to "email"
            emailIntent.setType("vnd.android.cursor.dir/email");
            String to[] = {"YOUR_EMAIL_HERE_@gmail.com"};                       // fixme replace email
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            Uri uri = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", outputFile);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            // the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject Here..."); // fixme replace subject

            // finally start start send email activity
            //startActivity(emailIntent);
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

        }
    }

}
