package com.example.mychef;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    public String name, email, phone;
    public Map<String, Boolean> roles;
    public String fcmToken; // 🔹 Eklenmesi gereken alan

    public UserModel() {}

    public UserModel(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roles = new HashMap<>();
        this.roles.put("chef", false);
        this.roles.put("yolcu", true);
    }
}

