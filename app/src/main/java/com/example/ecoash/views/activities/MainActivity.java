package com.example.ecoash.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoash.R;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Comprobar si hay una sesión activa
        verificarSesionActiva();

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // Botones para ir a Login y Registro
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al Login
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al Registro
                Intent intent = new Intent(MainActivity.this, RegisterUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verificarSesionActiva() {
        SharedPreferences sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        boolean sesionIniciada = sharedPreferences.getBoolean("sesionIniciada", false);
        String role = sharedPreferences.getString("role", "");

        if (sesionIniciada) {
            Intent intent;
            if ("admin".equals(role)) {
                intent = new Intent(this, AdminHomeActivity.class);
            } else if ("cliente".equals(role)) {
                intent = new Intent(this, ClientHomeActivity.class);
            } else {
                return; // Si no hay rol, no hacemos nada
            }
            startActivity(intent);
            finish(); // Finalizamos MainActivity
        }
    }
}
