package com.example.mychef;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    EditText editEmail, editPhone, editPassword;
    Button btnSaveChanges;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        editEmail.setText(email);
                        editPhone.setText(phone);
                    });
        }

        btnSaveChanges.setOnClickListener(v -> {
            String newEmail = editEmail.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();

            if (newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "E-posta ve telefon boş olamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid())
                    .child("email").setValue(newEmail);
            FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid())
                    .child("phone").setValue(newPhone);

            if (!newPassword.isEmpty()) {
                if (newPassword.length() < 6) {
                    Toast.makeText(this, "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentUser.updatePassword(newPassword)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Şifre güncellendi", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Şifre değiştirilemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            Toast.makeText(this, "Bilgiler güncellendi", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
