package com.example.mychef;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BecomeChefActivity extends AppCompatActivity {

    EditText editSpecialty, editDescription, editPrice;
    Button btnSave;
    FirebaseAuth mAuth;
    DatabaseReference chefsRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_chef);

        editSpecialty = findViewById(R.id.editSpecialty);
        editDescription = findViewById(R.id.editDescription);
        editPrice = findViewById(R.id.editPrice);
        btnSave = findViewById(R.id.btnSaveChef);
        mAuth = FirebaseAuth.getInstance();

        chefsRef = FirebaseDatabase.getInstance().getReference("chefs");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        btnSave.setOnClickListener(v -> {
            String specialty = editSpecialty.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String price = editPrice.getText().toString().trim();
            String uid = mAuth.getCurrentUser().getUid();

            if (specialty.isEmpty() || description.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> chefData = new HashMap<>();
            chefData.put("uid", uid);
            chefData.put("specialty", specialty);
            chefData.put("description", description);
            chefData.put("price", price);

            // Şef bilgilerini kaydet
            chefsRef.child(uid).setValue(chefData);

            // Kullanıcının rolünü ve şef oluşunu güncelle
            Map<String, Object> updates = new HashMap<>();
            updates.put("isChef", true);
            updates.put("activeRole", "chef");

            usersRef.child(uid).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Chef profili oluşturuldu", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
