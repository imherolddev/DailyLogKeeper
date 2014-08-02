/**
 *
 */
package com.imherolddev.dailylogkeeper.view_fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.imherolddev.dailylogkeeper.DailyLog;
import com.imherolddev.dailylogkeeper.MainActivity;
import com.imherolddev.dailylogkeeper.NewLogActivity;
import com.imherolddev.dailylogkeeper.R;

import java.util.ArrayList;

/**
 * @author imherolddev
 */
public class DailyViewFragment extends ListFragment {

    public interface GetLogListener {

        public ArrayList<DailyLog> getLog();

    }

    /**
     * logs to pass to the adapter
     */
    private ArrayList<DailyLog> logs;

    private GetLogListener activity;

    private DayViewAdapter dayView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        activity = (GetLogListener) getActivity();
        logs = activity.getLog();

        dayView = new DayViewAdapter(getActivity(), logs);
        setListAdapter(dayView);

        registerForContextMenu(getListView());

    }

    @Override
    public void onResume() {

        super.onResume();
        logs.clear();
        logs.addAll(activity.getLog());
        dayView.notifyDataSetChanged();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        DailyLog log = logs.get(position);

        Intent intent = new Intent(getActivity(), NewLogActivity.class);
        intent.putExtra("UID", position);
        intent.putExtra("jobName", log.getJobName());
        intent.putExtra("logTitle", log.getTitle());
        intent.putExtra("logEntry", log.getLogEntry());

        getActivity().startActivityForResult(intent,
                MainActivity.EDIT_ENTRY_REQUEST);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.longpress_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            case R.id.action_send:
                sendLog(info.position);
                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }

    public void sendLog(int position) {

        DailyLog logToSend = logs.get(position);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailTo:"));
        emailIntent.setType("text/plain");

        String subject =
                logToSend.getJobName() +
                        " - " +
                        logToSend.getTitle();
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        String body =
                logToSend.getLogEntry() +
                        "\n" +
                        "\n" +
                        getString(R.string.email_sig);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        startActivity(Intent.createChooser(emailIntent, getString(R.string.send)));

    }

}
