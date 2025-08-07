package com.example.mychef;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {

    public static void sendRequestToChef(Context context, ChefModel chef) {
        String passengerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("chefRequests");

        // Daha önce aynı şefe pending istek atılmış mı kontrol et
        requestsRef.orderByChild("passengerId").equalTo(passengerUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean alreadySent = false;

                        for (DataSnapshot item : snapshot.getChildren()) {
                            String targetChefId = item.child("chefId").getValue(String.class);
                            String status = item.child("status").getValue(String.class);

                            if (chef.uid.equals(targetChefId) && "pending".equals(status)) {
                                alreadySent = true;
                                break;
                            }
                        }

                        if (alreadySent) {
                            Toast.makeText(context, "Zaten bu şefe bir istek gönderdiniz. Lütfen yanıtı bekleyin.", Toast.LENGTH_LONG).show();
                        } else {
                            createNewRequest(context, chef, passengerUid);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(context, "Kontrol sırasında hata oluştu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void createNewRequest(Context context, ChefModel chef, String passengerUid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(passengerUid);
        userRef.child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String passengerToken = snapshot.getValue(String.class);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chefRequests").push();

                Map<String, Object> requestData = new HashMap<>();
                requestData.put("chefId", chef.uid);
                requestData.put("chefName", chef.name);
                requestData.put("timestamp", System.currentTimeMillis());
                requestData.put("status", "pending");
                requestData.put("passengerToken", passengerToken);
                requestData.put("passengerId", passengerUid);

                ref.setValue(requestData)
                        .addOnSuccessListener(unused -> Toast.makeText(context, "İstek başarıyla gönderildi.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "İstek gönderilemedi!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "FCM token alınamadı!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
