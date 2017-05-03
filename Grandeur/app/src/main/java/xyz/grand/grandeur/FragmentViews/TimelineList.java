package xyz.grand.grandeur.FragmentViews;

import android.net.Uri;

import java.util.Date;

/**
 * Created by Ferick Andrew on Mar 22, 2017.
 */

public class TimelineList {
    private String userName;
    private String timelineContent;
    private String timelineImage;
    private String timelineTime;

    public TimelineList(String userName, String timelineContent, String timelineImage, String timelineTime) {
        this.userName = userName;
        this.timelineContent = timelineContent;
        this.timelineImage = timelineImage;
        this.timelineTime = timelineTime;
    }

    public TimelineList() {}

    public String getUserName() { return userName; }

    public String getTimelineContent() {
        return timelineContent;
    }

    public String getTimelineImage() { return timelineImage; }

    public String getTimelineTime() {
        return timelineTime;
    }


    public void setUserName(String userName) { this.userName = userName; }

    public void setTimelineContent(String timelineContent) { this.timelineContent = timelineContent; }

    public void setTimelineImage(String timelineImage) { this.timelineImage = timelineImage; }

    public void setTimelineTime(String timelineTime) {
        this.timelineTime = timelineTime;
    }
}