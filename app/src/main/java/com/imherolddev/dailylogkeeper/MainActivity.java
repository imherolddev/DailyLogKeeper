package com.imherolddev.dailylogkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.imherolddev.dailylogkeeper.settings.SettingsActivity;
import com.imherolddev.dailylogkeeper.view_fragments.DailyViewFragment.GetLogListener;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author imherolddev
 */
public class MainActivity extends ActionBarActivity implements
        OnNavigationListener, GetLogListener {

    public static final int ADD_ENTRY_REQUEST = 0;
    public static final int EDIT_ENTRY_REQUEST = 1;

    private final String LOGS_FILE = "logs.ser";

    private ArrayList<DailyLog> logs = new ArrayList<>();
    private int logCounter;

    private ActionBar actionBar;
    private SpinnerAdapter changeView;

    private FragmentManager fm;

    // private DailyViewFragment dvf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        actionBar = this.getSupportActionBar();
        changeView = ArrayAdapter.createFromResource(this.getSupportActionBar()
                        .getThemedContext(), R.array.action_view_list,
                android.R.layout.simple_spinner_dropdown_item
        );
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(changeView, this);

        // dvf = (DailyViewFragment) fm.findFragmentById(R.id.fragment1);

        readLogs();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readLogs();
        logCounter = logs.size();
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
                intent.putExtra("UID", this.logCounter);
                startActivityForResult(intent, ADD_ENTRY_REQUEST);
                return true;

            case R.id.action_help:
                HelpDialog dialog = new HelpDialog();
                dialog.show(fm, getString(R.string.dialog_help_title));
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }

    }


    @Override
    public boolean onNavigationItemSelected(int arg0, long arg1) {
        // TODO Add logic for changing views
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case ADD_ENTRY_REQUEST:
                if (resultCode == RESULT_OK) {

                    logs.add(0, (DailyLog) data.getSerializableExtra("log"));
                    saveLogs(logs);

                } else if (resultCode == RESULT_CANCELED) {
                    toast(R.string.log_canceled);
                }
                break;

            case EDIT_ENTRY_REQUEST:

                if (resultCode == RESULT_OK) {

                    DailyLog edited = (DailyLog) data.getSerializableExtra("log");

                    logs.set(edited.getUID(), edited);
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

    private void saveLogs(ArrayList<DailyLog> logs) {

        try {

            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(
                    LOGS_FILE, MODE_PRIVATE));
            oos.writeObject(logs);
            oos.close();

        } catch (IOException ioex) {
            toast(R.string.serial);
        }

    }

    @SuppressWarnings("unchecked")
    private void readLogs() {

        File logFile = getBaseContext().getFileStreamPath(LOGS_FILE);

        if (logFile.exists()) {

            try {

                ObjectInputStream ois = new ObjectInputStream(
                        openFileInput(LOGS_FILE));

                logs = (ArrayList<DailyLog>) ois.readObject();
                ois.close();

            } catch (IOException | ClassNotFoundException ioex) {

                logs = new ArrayList<DailyLog>();
                toast(R.string.serial);

            }

        } else {

            toast(R.string.no_logs);

        }

    }

    private void toast(int id) {
        Toast.makeText(this, getString(id), Toast.LENGTH_LONG).show();
    }

    @Override
    public ArrayList<DailyLog> getLog() {
        return logs;
    }

}
