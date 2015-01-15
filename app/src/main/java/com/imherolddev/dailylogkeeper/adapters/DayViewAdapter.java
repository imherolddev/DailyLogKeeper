/**
 *
 */
package com.imherolddev.dailylogkeeper.adapters;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.imherolddev.dailylogkeeper.models.DailyLog;
import com.imherolddev.dailylogkeeper.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author imherolddev
 */
public class DayViewAdapter extends ArrayAdapter<DailyLog> {

    private Context context;
    private ArrayList<DailyLog> logs;

    public DayViewAdapter(Context context, ArrayList<DailyLog> logs) {

        super(context, R.layout.daily_view);

        this.context = context;
        this.logs = logs;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DailyLog log = getItem(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.daily_view, null);
        }

        TextView jobTitle = (TextView) convertView.findViewById(R.id.tv_job_title);
        jobTitle.setText(log.getJobName());

        TextView description = (TextView) convertView.findViewById(R.id.tv_description);
        description.setText(log.getTitle());

        TextView content = (TextView) convertView.findViewById(R.id.tv_content);
        content.setText(log.getLogEntry());

        TextView created = (TextView) convertView.findViewById(R.id.tv_created);
        SimpleDateFormat df = new SimpleDateFormat(PreferenceManager.getDefaultSharedPreferences
                (context).getString("dateFormat", "MM/dd/yyyy"));
        created.setText(df.format(log.getCreation()));

        return convertView;

    }

    @Override
    public int getCount() {

        return logs.size();
    }

    @Override
    public DailyLog getItem(int position) {

        return logs.get(position);
    }

    @Override
    public long getItemId(int position) {

        return logs.indexOf(getItem(position));
    }

}
