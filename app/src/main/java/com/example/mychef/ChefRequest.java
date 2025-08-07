package com.example.mychef;

public class ChefRequest {
    private String userId;
    private String userName;
    private long timestamp;

    public ChefRequest() {}

    public ChefRequest(String userId, String userName, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public long getTimestamp() {
        return timestamp;
    }
}