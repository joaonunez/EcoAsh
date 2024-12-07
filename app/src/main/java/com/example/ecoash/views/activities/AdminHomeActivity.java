package com.example.ecoash.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecoash.R;
import com.example.ecoash.adapters.AdminViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Referencias a las vistas
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);

        // Configurar ViewPager2 con un adaptador
        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Configurar listener para sincronizar BottomNavigationView con ViewPager2
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.nav_manage) {
                viewPager.setCurrentItem(1, true);
                return true;
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
                return true;
            }
            return false;
        });

        // Sincronizar ViewPager2 con BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_add);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_manage);
                        break;
                }
            }
        });

        // Establecer el fragmento inicial si es la primera vez que se carga la actividad
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(0);
        }
    }

    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();
        borrarSesionUsuario();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void borrarSesionUsuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
