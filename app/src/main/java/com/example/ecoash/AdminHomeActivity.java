package com.example.ecoash;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Referenciar el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Verificar que la vista no sea nula
        if (bottomNavigationView == null) {
            throw new NullPointerException("bottomNavigationView no está correctamente inicializado");
        }

        // Establecer fragmento inicial al cargar la actividad
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminDeviceCreationFragment())
                    .commit();
        }

        // Configurar listener para cambiar entre fragmentos
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) { // Fragmento para agregar dispositivos
                selectedFragment = new AdminDeviceCreationFragment();
            } else if (itemId == R.id.nav_manage) { // Fragmento para gestionar usuarios
                selectedFragment = new AdminManageDeviceFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}
