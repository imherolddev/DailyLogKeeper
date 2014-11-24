package com.imherolddev.dailylogkeeper.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.imherolddev.dailylogkeeper.R;
import com.imherolddev.dailylogkeeper.models.DailyLog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewLogActivity extends ActionBarActivity {

    public static final String LOG_EXTRA = "log_extra";
    public static final String JOB_NAMES_EXTRA = "job_names";
    public static final String UID_EXTRA = "UID";

    Intent returnIntent;
    Bundle extras;

    private AutoCompleteTextView et_jobName;
    private EditText et_logTitle;
    private EditText et_logEntry;

    private Date preEditDate;
    private boolean edited = false;

    private int uid;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_log);

        Toolbar toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_jobName = (AutoCompleteTextView) findViewById(R.id.et_job_name);
        et_logTitle = (EditText) findViewById(R.id.et_title);
        et_logEntry = (EditText) findViewById(R.id.et_entry);

        if (savedInstanceState != null) {

            et_jobName.setText(savedInstanceState.getString("jobName"));
            et_logTitle.setText(savedInstanceState.getString("logTitle"));
            et_logEntry.setText(savedInstanceState.getString("logEntry"));


        }

        extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey(JOB_NAMES_EXTRA)) {

                String[] jobNameArray = extras.getStringArray(JOB_NAMES_EXTRA);

                ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jobNameArray);
                et_jobName.setAdapter(arrayAdapter);

            }

            if (extras.containsKey(UID_EXTRA)) {
                uid = extras.getInt(UID_EXTRA);
            }

            if (extras.containsKey(LOG_EXTRA)) {

                DailyLog log = (DailyLog) extras.getSerializable(LOG_EXTRA);

                uid = log.getUID();
                et_jobName.setText(log.getJobName());
                et_logTitle.setText(log.getTitle());
                et_logEntry.setText(log.getLogEntry());

                preEditDate = log.getCreation();
                log.setEdited(new Date(System.currentTimeMillis()));

                edited = true;

            }

        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle save) {

        save.putString(et_jobName.getText().toString(), "");
        save.putString(et_logTitle.getText().toString(), "");
        save.putString(et_logEntry.getText().toString(), "");

        super.onSaveInstanceState(save);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_save:

                Date date;
                if (!edited) {
                    date = new Date();
                } else {
                    date = preEditDate;
                }
                DailyLog log = new DailyLog(uid, et_jobName.getText()
                        .toString(), et_logTitle.getText().toString(), et_logEntry
                        .getText().toString(), date);
                log.setPhotoUri(currentPhotoPath);

                returnIntent = new Intent();
                returnIntent.putExtra("log", log);
                setResult(RESULT_OK, returnIntent);

                finish();
                return true;

            case R.id.action_discard:

                returnIntent = new Intent();
                returnIntent.putExtra("UID", extras.getInt("UID"));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_alert_discard)
                        .setMessage(R.string.msg_alert_discard)

                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                returnIntent.putExtra("discard", true);
                                finish();
                            }
                        })

                        .setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                returnIntent.putExtra("discard", false);
                            }
                        }).create().show();

                setResult(RESULT_CANCELED, returnIntent);
                return true;

            case R.id.action_photo:

                showPhotoDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onBackPressed() {

        returnIntent = new Intent().putExtra("discard", false);
        setResult(RESULT_CANCELED, returnIntent);

        super.onBackPressed();

    }

    private void showPhotoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.action_photo_title)
                .setItems(R.array.choose_add_photo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:

                                //launch photo gallery, get URI of selected


                                break;

                            case 1:

                                //launch camera, get URI of photo
                                dispatchTakePicture();

                                break;

                        }

                    }
                });

        builder.create().show();

    }

    private File createImageFile() throws IOException {

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + time + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;

    }

    private void dispatchTakePicture() {

        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure there is a camera available
        if (photoIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ioex) {
                //Error occurred
            }

            if (photoFile != null) {

                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(photoIntent, 1);

            }

        }

    }

    private void galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            galleryAddPic();
        }

    }

}
