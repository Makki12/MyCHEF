package com.example.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {

    TextView textName, textEmail, textPhone;
    Button btnEditInfo, btnBecomeChef, btnLogout, btnSwitchRole;
    LinearLayout conversationsLayout;
    FirebaseUser currentUser;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnBecomeChef = findViewById(R.id.btnBecomeChef);
        btnLogout = findViewById(R.id.btnLogout);
        btnSwitchRole = findViewById(R.id.btnSwitchRole); // XML’e eklemeyi unutma
        conversationsLayout = findViewById(R.id.conversationsLayout);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        if (currentUser != null) {
            String uid = currentUser.getUid();
            usersRef.child(uid).get().addOnSuccessListener(snapshot -> {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                Boolean isChef = snapshot.child("isChef").getValue(Boolean.class);
                String activeRole = snapshot.child("activeRole").getValue(String.class);

                textName.setText(name != null ? name : "");
                textEmail.setText(email != null ? email : "");
                textPhone.setText(phone != null ? phone : "");

                if (isChef == null || !isChef) {
                    btnBecomeChef.setVisibility(View.VISIBLE);
                    btnSwitchRole.setVisibility(View.GONE);
                } else {
                    btnBecomeChef.setVisibility(View.GONE);
                    btnSwitchRole.setVisibility(View.VISIBLE);

                    if ("chef".equals(activeRole)) {
                        btnSwitchRole.setText("Tarif Yolcusu Hesabına Geç");
                        btnSwitchRole.setOnClickListener(v -> switchRole(uid, "passenger"));
                    } else {
                        btnSwitchRole.setText("Şef Hesabına Geç");
                        btnSwitchRole.setOnClickListener(v -> switchRole(uid, "chef"));
                    }
                }
            });
        }

        btnEditInfo.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        btnBecomeChef.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, BecomeChefActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void switchRole(String uid, String targetRole) {
        usersRef.child(uid).child("activeRole").setValue(targetRole)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Rol değiştirildi: " + targetRole, Toast.LENGTH_SHORT).show();
                    if ("chef".equals(targetRole)) {
                        startActivity(new Intent(this, ChefPanelActivity.class));
                    } else {
                        startActivity(new Intent(this, YolcuPanelActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Rol değiştirilemedi!", Toast.LENGTH_SHORT).show());
    }
}
