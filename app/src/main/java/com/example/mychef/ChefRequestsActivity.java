package com.example.mychef;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ChefRequestsActivity extends AppCompatActivity {

    LinearLayout layoutRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_requests);

        layoutRequests = findViewById(R.id.layoutRequests);

        String chefUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chefRequests");

        ref.orderByChild("chefId").equalTo(chefUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        layoutRequests.removeAllViews();

                        for (DataSnapshot item : snapshot.getChildren()) {
                            String requestId = item.getKey();
                            String userName = item.child("chefName").getValue(String.class);
                            String status = item.child("status").getValue(String.class);

                            // Ana yatay layout
                            LinearLayout requestLayout = new LinearLayout(ChefRequestsActivity.this);
                            requestLayout.setOrientation(LinearLayout.HORIZONTAL);
                            requestLayout.setPadding(24, 24, 24, 24);
                            requestLayout.setGravity(Gravity.CENTER_VERTICAL);

                            // TextView
                            TextView tv = new TextView(ChefRequestsActivity.this);
                            tv.setText("🧑 " + userName + " - Durum: " + status);
                            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

                            // Kabul butonu
                            Button btnAccept = new Button(ChefRequestsActivity.this);
                            btnAccept.setText("Kabul");
                            btnAccept.setOnClickListener(v -> {
                                updateStatus(requestId, "accepted");
                            });

                            // Reddet butonu
                            Button btnReject = new Button(ChefRequestsActivity.this);
                            btnReject.setText("Reddet");
                            btnReject.setOnClickListener(v -> {
                                updateStatus(requestId, "rejected");
                            });

                            // Layout'a ekle
                            requestLayout.addView(tv);
                            requestLayout.addView(btnAccept);
                            requestLayout.addView(btnReject);

                            layoutRequests.addView(requestLayout);
                        }

                        if (layoutRequests.getChildCount() == 0) {
                            Toast.makeText(ChefRequestsActivity.this, "Henüz istek yok.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(ChefRequestsActivity.this, "İstekler alınamadı!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStatus(String requestId, String newStatus) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("chefRequests").child(requestId);
        requestRef.child("status").setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "İstek " + newStatus.toUpperCase() + " olarak güncellendi.", Toast.LENGTH_SHORT).show();
                    //sendResponseNotificationToPassenger(requestId, newStatus); // 🔔 Ekleyeceğimiz satır
                    recreate();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
