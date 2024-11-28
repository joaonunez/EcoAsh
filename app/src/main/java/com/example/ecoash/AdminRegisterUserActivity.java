package com.example.ecoash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminRegisterUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txtName, txtEmail, txtPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Asegurarnos de que esté vinculado con el XML correcto
        setContentView(R.layout.activity_admin_register_user);

        mAuth = FirebaseAuth.getInstance();

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(AdminRegisterUserActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            registerAdmin(name, email, password);
        });
    }

    private void registerAdmin(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Crear datos del administrador con rol "admin"
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("email", email);
                            userData.put("role", "com/example/ecoash/admin"); // Rol admin

                            db.collection("users").document(currentUser.getUid()).set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdminRegisterUserActivity.this, "Administrador registrado exitosamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AdminRegisterUserActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AdminRegisterUserActivity.this, "Error al guardar los datos del administrador", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(AdminRegisterUserActivity.this, "Error al registrar administrador: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
