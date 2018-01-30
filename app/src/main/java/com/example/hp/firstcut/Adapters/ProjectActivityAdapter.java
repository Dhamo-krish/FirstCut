package com.example.hp.firstcut.Adapters;

/**
 * Created by HP on 1/24/2018.
 */

public class ProjectActivityAdapter {
    String activities;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ProjectActivityAdapter(String activities, String url) {
        this.activities = activities;
        this.url = url;
    }

    String url;

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public ProjectActivityAdapter(String activities) {

        this.activities = activities;
    }
}
