package com.example.ecoash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button backButton; // Botón de volver
    private Button registerButton;
    private EditText nameField, emailField, passwordField, confirmPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializa FirebaseAuth y FirebaseFirestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a los elementos del diseño
        backButton = findViewById(R.id.backButton);
        registerButton = findViewById(R.id.registerButton);
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);

        // Configura el botón de "Volver"
        backButton.setOnClickListener(v -> {
            // Navegar al MainActivity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Configura el botón de registro
        registerButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(name, email, password);
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Guardar datos en Firestore
                        String uid = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(uid, name, email);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error al registrarse: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, String name, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);

        db.collection("users").document(uid).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        // Regresar al MainActivity cuando se presione el botón "Atrás"
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
