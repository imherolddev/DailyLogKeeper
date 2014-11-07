package com.imherolddev.dailylogkeeper.persistance;

import com.imherolddev.dailylogkeeper.DailyLog;

import java.util.ArrayList;

/**
 * Created by imherolddev on 11/1/14.
 */
public interface PersistenceHelper {

    public void saveLogs(ArrayList<DailyLog> logArrayList);
    public ArrayList<DailyLog> readLogs();

}
