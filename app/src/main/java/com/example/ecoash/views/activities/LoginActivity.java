package com.example.ecoash.views.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoash.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private ImageButton viewPasswordButton;
    private Button loginButton, backButton, btnAdminRegister;
    private TextView loginButtonTextView;
    private boolean isPasswordVisible = false;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPreferences";
    private static final String PREF_EMAILS_KEY = "SavedEmails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los elementos de la interfaz
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        viewPasswordButton = findViewById(R.id.viewPasswordButton);
        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton);
        btnAdminRegister = findViewById(R.id.btnAdminRegister);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Cargar correos almacenados
        loadSavedEmails();

        // Mostrar/Ocultar contraseña
        viewPasswordButton.setOnClickListener(view -> togglePasswordVisibility());

        // Acción del botón "Iniciar sesión"
        loginButton.setOnClickListener(view -> {
            // Cambiar el texto del botón a "Autenticando..."
            loginButton.setText("Autenticando...");

            // Aplicar animación al texto del botón
            animateButtonText();

            // Cambiar el color del fondo del botón
            animateButtonBackgroundColor();

            // Simular un retraso para la autenticación
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    resetLoginButton(); // Restablecer el botón
                } else {
                    loginUser(email, password); // Método original para iniciar sesión
                }
            }, 2000); // Retraso de 2 segundos
        });

        // Acción del botón "Volver"
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Acción del botón "Soy administrador"
        btnAdminRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminRegisterUserActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");
                                            saveEmailToSharedPreferences(email);
                                            if ("admin".equals(role)) {
                                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                                startActivity(intent);
                                            } else if ("cliente".equals(role)) {
                                                Intent intent = new Intent(LoginActivity.this, ClientHomeActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Rol no reconocido", Toast.LENGTH_SHORT).show();
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                            resetLoginButton(); // Restablecer botón
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(LoginActivity.this, "Error al verificar el rol", Toast.LENGTH_SHORT).show();
                                        resetLoginButton(); // Restablecer botón
                                    });
                        }
                    } else {
                        // Mostrar un toast indicando que el usuario no existe
                        Toast.makeText(LoginActivity.this, "Usuario no existe o credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        resetLoginButton(); // Restablecer botón
                    }
                });
    }

    private void resetLoginButton() {
        loginButton.setText("Iniciar sesión");
        loginButton.setBackgroundResource(R.drawable.custom_button);
    }

    private void animateButtonBackgroundColor() {
        ObjectAnimator colorAnimation = ObjectAnimator.ofObject(
                loginButton,
                "backgroundColor",
                new ArgbEvaluator(),
                getResources().getColor(R.color.purple_200),
                getResources().getColor(R.color.teal_700)
        );
        colorAnimation.setDuration(2000);
        colorAnimation.start();
    }

    private void animateButtonText() {
        TranslateAnimation animation = new TranslateAnimation(-50, 0, 0, 0);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(2000);
        loginButton.getText(); // Aplica la animación únicamente al texto
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, savedEmails.toArray(new String[0]));
        emailEditText.setAdapter(adapter);
        emailEditText.setThreshold(1);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setSelection(passwordEditText.getText().length());
            viewPasswordButton.setImageResource(R.drawable.ic_eye_closed);
        } else {
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordEditText.setSelection(passwordEditText.getText().length());
            viewPasswordButton.setImageResource(R.drawable.ic_eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
