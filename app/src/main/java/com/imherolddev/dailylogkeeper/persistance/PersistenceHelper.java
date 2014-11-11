package com.imherolddev.dailylogkeeper.persistance;

import com.imherolddev.dailylogkeeper.DailyLog;

import java.util.ArrayList;

/**
 * Created by imherolddev on 11/1/14.
 */
public interface PersistenceHelper {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DATE_FORMAT = "dateFormat";

    public void saveLogs(ArrayList<DailyLog> logArrayList);
    public ArrayList<DailyLog> readLogs();

}
