package com.example.facechat.Classes;

public class Message {
    private String message,sender_id,message_id;
    private long time_stamp;
    private int feeling=-1;
    private String Image_url;

    public String getImage_url() {
        return Image_url;
    }

    public void setImage_url(String image_url) {
        Image_url = image_url;
    }

    public Message(){

    }
    public Message(String message, String sender_id,long time_stamp) {
        this.message = message;
        this.time_stamp=time_stamp;
        this.sender_id = sender_id;

    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public int getFeeling() {
        return feeling;
    }
}
