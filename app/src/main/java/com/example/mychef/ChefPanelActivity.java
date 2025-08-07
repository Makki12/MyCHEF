package com.example.mychef;

import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ChefPanelActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private LinearLayout confirmedContainer, pendingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_panel);

        Toolbar toolbar = findViewById(R.id.toolbarChef);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayoutChef);
        navigationView = findViewById(R.id.navigationViewChef);
        confirmedContainer = findViewById(R.id.confirmedContainer);
        pendingContainer = findViewById(R.id.pendingContainer);

        Button btnProfile = findViewById(R.id.btnViewProfile);
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return true;
        });

        loadRequests();
    }

    private void loadRequests() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chefRequests");

        ref.orderByChild("chefId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                confirmedContainer.removeAllViews();
                pendingContainer.removeAllViews();

                for (DataSnapshot item : snapshot.getChildren()) {
                    String requestId = item.getKey();
                    String status = item.child("status").getValue(String.class);
                    String user = item.child("chefName").getValue(String.class);

                    if ("accepted".equals(status)) {
                        TextView tv = new TextView(ChefPanelActivity.this);
                        tv.setText("🧑 " + user + " - ONAYLANDI");
                        tv.setPadding(16, 16, 16, 16);
                        confirmedContainer.addView(tv);

                    } else if ("pending".equals(status)) {
                        LinearLayout itemLayout = new LinearLayout(ChefPanelActivity.this);
                        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                        itemLayout.setPadding(16, 16, 16, 16);

                        TextView tv = new TextView(ChefPanelActivity.this);
                        tv.setText("🧑 " + user);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                        Button btnAccept = new Button(ChefPanelActivity.this);
                        btnAccept.setText("Kabul");
                        btnAccept.setOnClickListener(v -> updateStatus(requestId, "accepted"));

                        Button btnReject = new Button(ChefPanelActivity.this);
                        btnReject.setText("Reddet");
                        btnReject.setOnClickListener(v -> updateStatus(requestId, "rejected"));

                        itemLayout.addView(tv);
                        itemLayout.addView(btnAccept);
                        itemLayout.addView(btnReject);

                        pendingContainer.addView(itemLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChefPanelActivity.this, "Veri alınamadı: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String requestId, String newStatus) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("chefRequests").child(requestId);
        requestRef.child("status").setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Durum güncellendi: " + newStatus.toUpperCase(), Toast.LENGTH_SHORT).show();
                    loadRequests(); // Güncel listeyi yenile
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            ref.child("isOnline").setValue(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("isOnline").setValue(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
