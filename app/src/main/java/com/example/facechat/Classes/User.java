package com.example.facechat.Classes;

public class User {
    private String uni_id,name,phone_number,profile_picture;

    public User(){

    }
    public User(String uni_id, String name, String phone_number, String profile_picture) {
        this.uni_id = uni_id;
        this.name = name;
        this.phone_number = phone_number;
        this.profile_picture = profile_picture;
    }

    public String getName() {
        return name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getUni_id() {
        return uni_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public void setUni_id(String uni_id) {
        this.uni_id = uni_id;
    }

}
