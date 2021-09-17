package com.example.facechat.Classes;

import java.util.ArrayList;

public class User_status {
    private  String name,profile_image;
    private long lastupdate;
    private ArrayList<Status> statuses;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public long getLastupdate() {
        return lastupdate;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }
}
