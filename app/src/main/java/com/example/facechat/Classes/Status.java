package com.example.facechat.Classes;

public class Status {

    private String Image_url;
    private long timeStamp;

    public Status()
    {

    }
    public Status(String image_url, long timeStamp) {
        Image_url = image_url;
        this.timeStamp = timeStamp;
    }

    public String getImage_url() {
        return Image_url;
    }

    public void setImage_url(String image_url) {
        Image_url = image_url;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
