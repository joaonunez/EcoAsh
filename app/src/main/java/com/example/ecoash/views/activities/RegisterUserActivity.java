package com.example.ecoash.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoash.R;
import com.example.ecoash.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txtName, txtEmail, txtPassword, txtAddress, txtPhone;
    private Button btnRegister, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        // Referencias a los campos
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        // Botón de registro
        btnRegister.setOnClickListener(v -> {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String address = txtAddress.getText().toString().trim();
            String phone = txtPhone.getText().toString().trim();

            // Validación de campos
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Registro del usuario
            registerUser(name, email, password, address, phone);
        });

        // Botón de volver
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser(String name, String email, String password, String address, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Crear instancia de User
                            User newUser = new User(
                                    currentUser.getUid(),
                                    name,
                                    email,
                                    address,
                                    phone,
                                    "cliente", // Rol por defecto
                                    null // Sin foto de perfil inicialmente
                            );

                            // Guardar datos del usuario en Firestore
                            db.collection("users").document(currentUser.getUid()).set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterUserActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterUserActivity.this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterUserActivity.this, "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
