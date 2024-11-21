package com.example.ecoash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AutoCompleteTextView emailEditText; // Cambiado a AutoCompleteTextView
    private EditText passwordEditText;
    private Button loginButton;
    private Button backButton; // Botón "Volver"

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPreferences";
    private static final String PREF_EMAILS_KEY = "SavedEmails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Referencias a vistas
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton); // Botón "Volver"

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Cargar correos almacenados en SharedPreferences
        loadSavedEmails();

        // Configura el botón de inicio de sesión
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Configura el botón "Volver" para regresar al MainActivity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Guardar correo al iniciar sesión correctamente
                            saveEmailToSharedPreferences(email);

                            // Ir a WelcomeUserActivity
                            Intent intent = new Intent(LoginActivity.this, WelcomeUserActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveEmailToSharedPreferences(String email) {
        Set<String> savedEmails = sharedPreferences.getStringSet(PREF_EMAILS_KEY, new HashSet<>());
        savedEmails.add(email);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(PREF_EMAILS_KEY, savedEmails);
        editor.apply();
    }

    private void loadSavedEmails() {
        Set<String> savedEmails = sharedPreferences.getStringSet(PREF_EMAILS_KEY, new HashSet<>());

        // Configurar el adaptador para el autocompletado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, savedEmails.toArray(new String[0]));
        emailEditText.setAdapter(adapter);

        // Habilitar el menú desplegable al hacer clic en el campo
        emailEditText.setThreshold(1);
    }

    @Override
    public void onBackPressed() {
        // Regresar al MainActivity al presionar el botón físico "Atrás"
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
