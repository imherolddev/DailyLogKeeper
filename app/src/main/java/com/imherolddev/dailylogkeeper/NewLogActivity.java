package com.imherolddev.dailylogkeeper;

import com.imherolddev.dailylogkeeper.maps.MapActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class NewLogActivity extends ActionBarActivity {

    Intent returnIntent;
    Bundle extras;

    private EditText et_jobName;
    private EditText et_logTitle;
    private EditText et_logEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_log);

        extras = getIntent().getExtras();

        et_jobName = (EditText) findViewById(R.id.et_job_name);
        et_logTitle = (EditText) findViewById(R.id.et_title);
        et_logEntry = (EditText) findViewById(R.id.et_entry);

    }

    @Override
    public View onCreatePanelView(int featureId) {

        if (!(extras == null)) {

            if (extras.size() > 1) {

                et_jobName.setText(extras.getString("jobName"));
                et_logTitle.setText(extras.getString("logTitle"));
                et_logEntry.setText(extras.getString("logEntry"));

            }

        }

        return null;

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

            case R.id.action_settings:
                return true;

            case R.id.action_map:
                startActivity(new Intent(this, MapActivity.class));
                return true;

            case R.id.action_save:

                DailyLog log = new DailyLog(extras.getInt("UID"), et_jobName.getText()
                        .toString(), et_logTitle.getText().toString(), et_logEntry
                        .getText().toString());

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

}
