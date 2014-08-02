/**
 *
 */
package com.imherolddev.dailylogkeeper;

import java.io.Serializable;
import java.util.Date;

/**
 * @author imherolddev
 */
public class DailyLog implements Serializable {

    private static final long serialVersionUID = 0L;

    /**
     * Unique identifier
     */
    private int UID;
    /**
     * Job name
     */
    private String jobName;
    /**
     * Log title
     */
    private String title;
    /**
     * Log creation date
     */
    private Date creation;
    /**
     * Log edited date
     */
    private Date edited;
    /**
     * Log entry
     */
    private String logEntry;

    /**
     * Constructor with title
     *
     * @param title - the title to set
     */
    public DailyLog(int UID, String jobName, String title, String logEntry) {

        this.UID = UID;
        this.jobName = jobName;
        this.title = title;
        this.logEntry = logEntry;

        creation = new Date();

    }

    public int getUID() {
        return this.UID;
    }

    public void setUID(int newID) {
        this.UID = newID;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        edited = new Date();
        this.title = title;
    }

    public Date getCreation() {
        return creation;
    }

    public Date getEdited() {
        return edited;
    }

    public String getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(String logEntry) {
        this.logEntry = logEntry;
    }


}
