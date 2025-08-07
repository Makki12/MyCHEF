package com.example.mychef;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            // Bildirim alma işlemi burada ele alınabilir (gerekirse)
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (uid != null) {
            FirebaseDatabase.getInstance("https://mychef-d5aa1-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("users")
                    .child(uid)
                    .child("fcmToken")
                    .setValue(token);
        }
    }
}