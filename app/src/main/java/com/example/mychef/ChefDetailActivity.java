package com.example.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChefDetailActivity extends AppCompatActivity {

    TextView nameText, onlineStatus, textChefBio;
    Button btnSendRequest;
    ChefModel chef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_detail);

        nameText = findViewById(R.id.textChefName);
        onlineStatus = findViewById(R.id.textChefStatus);
        textChefBio = findViewById(R.id.textChefBio); // 👈 EKLENDİ
        btnSendRequest = findViewById(R.id.btnSendRequest);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("chefName")) {
            chef = new ChefModel(
                    intent.getStringExtra("chefUid"),
                    intent.getStringExtra("chefName"),
                    intent.getStringExtra("userType"),
                    intent.getBooleanExtra("isOnline", false),
                    intent.getStringExtra("fcmToken")
            );

            String bio = intent.getStringExtra("bio");

            nameText.setText("Şef: " + chef.name);
            onlineStatus.setText(chef.isOnline ? "Çevrimiçi" : "Çevrimdışı");
            textChefBio.setText((bio != null && !bio.isEmpty()) ? bio : "Şef hakkında açıklama yok."); // 👈 EKLENDİ
        }

        btnSendRequest.setOnClickListener(v -> {
            RequestHelper.sendRequestToChef(this, chef);
        });
    }
}
