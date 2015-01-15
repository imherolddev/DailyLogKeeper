package com.imherolddev.dailylogkeeper.persistence;

import com.imherolddev.dailylogkeeper.models.DailyLog;

import java.util.ArrayList;

/**
 * Created by imherolddev on 11/1/14.
 */
public interface PersistenceHelper {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DATE_FORMAT = "dateFormat";
    public static final String KEY_LOG_COUNT = "logCount";

    public void saveLogs(ArrayList<DailyLog> logArrayList);
    public ArrayList<DailyLog> readLogs();

}
