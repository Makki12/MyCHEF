package com.example.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    EditText editLoginEmail, editLoginPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // 🔁 Eğer kullanıcı zaten giriş yaptıysa, direkt yönlendir
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            autoRedirect(currentUser.getUid());
            return;
        }

        TextView textGoToRegister = findViewById(R.id.textGoToRegister);

        textGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });


        editLoginEmail = findViewById(R.id.editLoginEmail);
        editLoginPassword = findViewById(R.id.editLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String email = editLoginEmail.getText().toString().trim();
            String password = editLoginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email ve şifre boş olamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Geçerli bir e-posta giriniz", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                autoRedirect(user.getUid());
                            }
                        } else {
                            Toast.makeText(this, "Giriş başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void autoRedirect(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean isChef = snapshot.child("isChef").getValue(Boolean.class);
                String activeRole = snapshot.child("activeRole").getValue(String.class);

                if (isChef != null && isChef) {
                    if ("chef".equals(activeRole)) {
                        startActivity(new Intent(LoginActivity.this, ChefPanelActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, YolcuPanelActivity.class));
                    }
                } else {
                    startActivity(new Intent(LoginActivity.this, YolcuPanelActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Veri alınamadı!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
