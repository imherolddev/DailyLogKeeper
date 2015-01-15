package com.imherolddev.dailylogkeeper.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.Toast;

import com.imherolddev.dailylogkeeper.R;
import com.imherolddev.dailylogkeeper.models.DailyLog;
import com.imherolddev.dailylogkeeper.persistence.PersistenceHelper;
import com.imherolddev.dailylogkeeper.fragments.DailyViewFragment.GetLogListener;
import com.imherolddev.dailylogkeeper.fragments.HelpDialog;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author imherolddev
 */
public class MainActivity extends ActionBarActivity implements GetLogListener, PersistenceHelper {

    public static final int ADD_ENTRY_REQUEST = 0;
    public static final int EDIT_ENTRY_REQUEST = 1;

    private final String LOGS_FILE = "logs.ser";

    private ArrayList<DailyLog> logs = new ArrayList<>();
    private int logCounter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);

        fab = (ImageButton) findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
        fab.setOutlineProvider(new ViewOutlineProvider() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void getOutline(View view, Outline outline) {
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(0, 0, size, size);
            }
        });

        logs = readLogs();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        logs = readLogs();
        logCounter = sharedPreferences.getInt(PersistenceHelper.KEY_LOG_COUNT, logs.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.add_entry:
                Intent intent = new Intent(this, NewLogActivity.class);
                intent.putExtra(NewLogActivity.UID_EXTRA, this.logCounter);
                intent.putExtra(NewLogActivity.JOB_NAMES_EXTRA, bundleJobNames());
                startActivityForResult(intent, ADD_ENTRY_REQUEST);
                return true;

            case R.id.action_help:
                HelpDialog dialog = new HelpDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_help_title));
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void fabClick(View v) {

        Intent intent = new Intent(this, NewLogActivity.class);
        intent.putExtra(NewLogActivity.UID_EXTRA, this.logCounter);
        intent.putExtra(NewLogActivity.JOB_NAMES_EXTRA, bundleJobNames());
        startActivityForResult(intent, ADD_ENTRY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case ADD_ENTRY_REQUEST:
                if (resultCode == RESULT_OK) {

                    logs.add(0, (DailyLog) data.getSerializableExtra("log"));
                    saveLogs(logs);
                    logCounter++;
                    editor.putInt(PersistenceHelper.KEY_LOG_COUNT, logCounter);

                } else if (resultCode == RESULT_CANCELED) {
                    toast(R.string.log_canceled);
                }
                break;

            case EDIT_ENTRY_REQUEST:

                if (resultCode == RESULT_OK) {

                    DailyLog edited = (DailyLog) data.getSerializableExtra("log");

                    for (DailyLog log : logs) {
                        if (log.getUID() == edited.getUID()) {
                            logs.set(logs.indexOf(log), edited);
                            break;
                        }
                    }

                    saveLogs(logs);

                } else if (resultCode == RESULT_CANCELED) {

                    int logId = data.getIntExtra("UID", -1);

                    if (data.getBooleanExtra("discard", false)) {

                        logs.remove(logId);
                        saveLogs(logs);
                        toast(R.string.log_discarded);

                    } else {
                        toast(R.string.log_canceled);
                    }

                }
                break;

            default:
                toast(R.string.oops);

        }

    }

    @Override
    public void saveLogs(ArrayList<DailyLog> logs) {

        try {

            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(
                    LOGS_FILE, MODE_PRIVATE));
            oos.writeObject(logs);
            oos.close();

        } catch (IOException ioex) {
            toast(R.string.serial);
        }

    }

    @Override
    public ArrayList<DailyLog> readLogs() {

        File logFile = getBaseContext().getFileStreamPath(LOGS_FILE);
        ArrayList<DailyLog> logArrayList = new ArrayList<>();

        if (logFile.exists()) {

            try {

                ObjectInputStream ois = new ObjectInputStream(
                        openFileInput(LOGS_FILE));

                logArrayList = (ArrayList<DailyLog>) ois.readObject();
                ois.close();

            } catch (IOException | ClassNotFoundException ioex) {
                toast(R.string.serial);
            }

        }

        return logArrayList;
    }

    private void toast(int id) {
        Toast.makeText(this, getString(id), Toast.LENGTH_LONG).show();
    }

    @Override
    public ArrayList<DailyLog> getLogs() {
        return logs;
    }

    //Bundle Job names to AutoCompleteTextView in NewLog
    private String[] bundleJobNames() {

        ArrayList<String> strings = new ArrayList<>();

        for (DailyLog log : logs) {

            strings.add(log.getJobName());

        }

        HashSet<String> jobNames = new HashSet<>(strings);

        return jobNames.toArray(new String[jobNames.size()]);

    }

}
