package com.example.mychef;

public class ChefModel {
    public String uid;
    public String name;
    public String userType;
    public boolean isOnline;
    public String fcmToken;

    public ChefModel() {} // Firebase için boş constructor

    public ChefModel(String uid, String name, String userType, boolean isOnline, String fcmToken) {
        this.uid = uid;
        this.name = name;
        this.userType = userType;
        this.isOnline = isOnline;
        this.fcmToken = fcmToken;
    }
}
