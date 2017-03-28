package xyz.grand.grandeur;

import java.util.Date;

/**
 * Created by Ferick Andrew on Mar 22, 2017.
 */

public class TimelineList {
    private String userName;
    private String timelineContent;
    private String timelineURL;
    private long timelineTime;

    public TimelineList(String userName, String timelineContent, String timelineURL, long timelineTime) {
        this.userName = userName;
        this.timelineContent = timelineContent;
        this.timelineURL = timelineURL;
        timelineTime = new Date().getTime();
    }

    public TimelineList() {}

    public String getUserName() { return userName; }

    public String getTimelineContent() {
        return timelineContent;
    }

    public String getTimelineURL() { return timelineURL; }

    public long getTimelineTime() {
        return timelineTime;
    }


    public void setUserName(String userName) { this.userName = userName; }

    public void setTimelineContent(String timelineContent) { this.timelineContent = timelineContent; }

    public void setTimelineURL(String timelineURL) { this.timelineURL = timelineURL; }

    public void setTimelineTime(long timelineTime) {
        this.timelineTime = timelineTime;
    }
}