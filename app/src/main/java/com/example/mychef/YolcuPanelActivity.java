package com.example.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class YolcuPanelActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    Button buttonFilter, buttonNextPage;
    ImageView imageAI, imageTools, imageIngredients, myChefLogo;
    LinearLayout chefContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yolcu_panel);

        Toolbar toolbar = findViewById(R.id.toolbarYolcu);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        myChefLogo = findViewById(R.id.myChefLogo);
        buttonFilter = findViewById(R.id.buttonFilter);
        imageAI = findViewById(R.id.imageAI);
        imageTools = findViewById(R.id.imageTools);
        imageIngredients = findViewById(R.id.imageIngredients);
        buttonNextPage = findViewById(R.id.buttonNextPage);
        chefContainer = findViewById(R.id.chefContainer);

        buttonFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Filtre özelliği daha aktif değil.", Toast.LENGTH_SHORT).show();
        });

        imageIngredients.setOnClickListener(v -> {
            startActivity(new Intent(YolcuPanelActivity.this, MalzemeActivity.class));
        });

        imageAI.setOnClickListener(v -> {
            Toast.makeText(this, "Yapay Zeka ekranı henüz aktif değil.", Toast.LENGTH_SHORT).show();
        });

        imageTools.setOnClickListener(v -> {
            startActivity(new Intent(YolcuPanelActivity.this, ToolsActivity.class));
        });

        buttonNextPage.setOnClickListener(v -> {
            Toast.makeText(this, "Sonraki sayfa yakında...", Toast.LENGTH_SHORT).show();
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                Toast.makeText(this, "Ana Sayfa", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_ingredients) {
                startActivity(new Intent(this, MalzemeActivity.class));
            } else if (id == R.id.menu_tools) {
                startActivity(new Intent(this, ToolsActivity.class));
            } else if (id == R.id.menu_solutions) {
                Toast.makeText(this, "Pratik Çözümler", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_live) {
                Toast.makeText(this, "MyChef Canlı", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_support) {
                Toast.makeText(this, "Yardım Masası", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }

            drawerLayout.closeDrawers();
            return true;
        });

        loadOnlineChefs();
    }

    private void loadOnlineChefs() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference chefsRef = FirebaseDatabase.getInstance().getReference("chefs");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chefContainer.removeAllViews();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    Boolean isChef = userSnap.child("isChef").getValue(Boolean.class);
                    Boolean isOnline = userSnap.child("isOnline").getValue(Boolean.class);
                    String name = userSnap.child("name").getValue(String.class);
                    String uid = userSnap.getKey();
                    String fcmToken = userSnap.child("fcmToken").getValue(String.class);

                    if (Boolean.TRUE.equals(isChef)) {
                        // Chef açıklaması için chefs tablosuna eriş
                        chefsRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot chefSnap) {
                                String description = chefSnap.child("description").getValue(String.class);

                                TextView textView = new TextView(YolcuPanelActivity.this);
                                String onlineStatus = Boolean.TRUE.equals(isOnline) ? "Çevrimiçi" : "Çevrimdışı";
                                textView.setText("👨‍🍳 " + name + " - " + onlineStatus);
                                textView.setTextSize(16);
                                textView.setPadding(24, 24, 24, 24);
                                textView.setBackgroundColor(ContextCompat.getColor(YolcuPanelActivity.this, R.color.teal_200));
                                textView.setTextColor(ContextCompat.getColor(YolcuPanelActivity.this, android.R.color.white));

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );
                                params.setMargins(0, 16, 0, 16);
                                textView.setLayoutParams(params);

                                textView.setOnClickListener(v -> {
                                    Intent intent = new Intent(YolcuPanelActivity.this, ChefDetailActivity.class);
                                    intent.putExtra("chefUid", uid);
                                    intent.putExtra("chefName", name);
                                    intent.putExtra("isOnline", isOnline);
                                    intent.putExtra("fcmToken", fcmToken);
                                    intent.putExtra("bio", description); // ✔ açıklamayı gönder
                                    startActivity(intent);
                                });

                                chefContainer.addView(textView);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(YolcuPanelActivity.this, "Şef detayları alınamadı.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(YolcuPanelActivity.this, "Şefler alınamadı: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
