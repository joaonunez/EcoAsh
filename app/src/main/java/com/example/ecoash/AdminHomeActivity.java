package com.example.ecoash;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Referencia a la barra de navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Cargar el primer fragmento al iniciar
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AdminAddDeviceFragment())
                    .commit();
        }

        // Manejar la selección de elementos en la barra de navegación inferior
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_add) { // Navegar al fragmento de añadir dispositivo
                selectedFragment = new AdminAddDeviceFragment();
            } else if (item.getItemId() == R.id.nav_manage) { // Navegar al fragmento de administrar dispositivo
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
