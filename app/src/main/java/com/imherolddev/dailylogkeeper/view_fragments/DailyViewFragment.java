/**
 *
 */
package com.imherolddev.dailylogkeeper.view_fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.imherolddev.dailylogkeeper.DailyLog;
import com.imherolddev.dailylogkeeper.MainActivity;
import com.imherolddev.dailylogkeeper.NewLogActivity;
import com.imherolddev.dailylogkeeper.R;
import com.imherolddev.dailylogkeeper.persistance.PersistenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author imherolddev
 */
public class DailyViewFragment extends ListFragment implements AbsListView.MultiChoiceModeListener {

    public interface GetLogListener {

        public ArrayList<DailyLog> getLogs();

    }

    /**
     * logs to pass to the adapter
     */
    private ArrayList<DailyLog> logs;

    private GetLogListener logListener;
    private PersistenceHelper persistenceHelper;

    private DayViewAdapter dayView;
    private ListView listView;
    private TextView empty;

    private int selectionCount;
    private ArrayList<Integer> selectionList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        logListener = (GetLogListener) getActivity();
        logs = logListener.getLogs();

        persistenceHelper = (PersistenceHelper) getActivity();

        dayView = new DayViewAdapter(getActivity(), logs);
        setListAdapter(dayView);

        listView = getListView();
        setEmptyText(getString(R.string.no_logs));
        TextView empty = (TextView) getListView().getEmptyView();
        empty.setTextSize(getResources().getDimension(R.dimen.empty_text_size));
        listView.setDivider(null);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);

    }

    @Override
    public void onResume() {

        super.onResume();
        logs.clear();
        logs.addAll(logListener.getLogs());
        dayView.notifyDataSetChanged();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        DailyLog log = logs.get(position);

        Intent intent = new Intent(getActivity(), NewLogActivity.class);
        intent.putExtra(NewLogActivity.LOG_EXTRA, log);

        getActivity().startActivityForResult(intent,
                MainActivity.EDIT_ENTRY_REQUEST);

    }

    public void sendLog(ArrayList<Integer> ids) {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailTo:"));
        emailIntent.setType("text/plain");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getActivity());
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {sharedPreferences.getString
                (PersistenceHelper.KEY_EMAIL,
                "")});

        String subject = sharedPreferences.getString(PersistenceHelper.KEY_USERNAME, "user") + " - Daily Logs";
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        StringBuilder body = new StringBuilder();
        String lastJob = "";
        SimpleDateFormat sdf = new SimpleDateFormat(sharedPreferences.getString(PersistenceHelper.KEY_DATE_FORMAT,
                "MMM d, yyyy"));

        for (Integer id : ids) {

            for (DailyLog log : logs) {

                if (id == log.getUID()) {

                    if (!log.getJobName().equals(lastJob)) {

                        lastJob = log.getJobName();
                        body.append(log.getJobName()).append(":").append("\n")
                                .append("\n");

                    }

                    body.append(log.getTitle()).append("\n")
                            .append(log.getLogEntry()).append("\n")
                            .append("\n")
                            .append(sdf.format(log.getCreation())).append("\n")
                            .append("\n");

                    break;

                }

            }

        }

        body.append(getString(R.string.email_sig));
        emailIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send)));

    }

    /**
     * Called when an item is checked or unchecked during selection mode.
     *
     * @param mode     The {@link android.view.ActionMode} providing the selection mode
     * @param position Adapter position of the item that was checked or unchecked
     * @param id       Adapter ID of the item that was checked or unchecked
     * @param checked  <code>true</code> if the item is now checked, <code>false</code>
     */
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        if (checked) {
            selectionCount ++;
            selectionList.add(logs.get(position).getUID());
        } else {
            selectionCount--;
            for (Integer p : selectionList) {
                if (p == logs.get(position).getUID()) {
                    selectionList.remove(p);
                    break;
                }
            }
        }

        mode.setSubtitle(String.valueOf(selectionCount + " " + getString(R.string.selected)));

    }

    /**
     * Called when action mode is first created. The menu supplied will be used to
     * generate action buttons for the action mode.
     *
     * @param mode ActionMode being created
     * @param menu Menu used to populate action buttons
     * @return true if the action mode should be created, false if entering this
     * mode should be aborted.
     */
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        selectionCount = 0;
        selectionList = new ArrayList<>();

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.longpress_menu, menu);

        mode.setTitle(R.string.action_mode_title);

        return true;

    }

    /**
     * Called to refresh an action mode's action menu whenever it is invalidated.
     *
     * @param mode ActionMode being prepared
     * @param menu Menu used to populate action buttons
     * @return true if the menu or action mode was updated, false otherwise.
     */
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    /**
     * Called to report a user click on an action button.
     *
     * @param mode The current ActionMode
     * @param item The item that was clicked
     * @return true if this callback handled the event, false if the standard MenuItem
     * invocation should continue.
     */
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_share:

                sendLog(selectionList);
                mode.finish();

                return true;

            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.title_alert_discard)
                        .setMessage(R.string.msg_alert_discard)

                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                for (Integer id : selectionList) {

                                    for (DailyLog log : logs) {

                                        if (id == log.getUID()) {
                                            logs.remove(log);
                                            break;
                                        }

                                    }

                                }

                                persistenceHelper.saveLogs(logs);
                                dayView.notifyDataSetChanged();
                            }
                        })

                        .setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();

                mode.finish();
                return true;

            default:
                return false;

        }

    }

    /**
     * Called when an action mode is about to be exited and destroyed.
     *
     * @param mode The current ActionMode being destroyed
     */
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionCount = 0;
    }


}
