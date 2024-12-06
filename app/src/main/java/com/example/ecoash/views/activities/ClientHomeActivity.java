package com.example.ecoash.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecoash.R;
import com.example.ecoash.adapters.ViewPagerAdapter;
import com.example.ecoash.repositories.DeviceRepository;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClientHomeActivity extends AppCompatActivity {

    private BadgeDrawable badgeDrawable;
    private int unreadNotifications = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        // Vincular las vistas del layout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Configurar el adaptador para el ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Inicializar el BadgeDrawable para las notificaciones
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_alerts);
        badgeDrawable.setVisible(false); // Ocultar inicialmente

        // Sincronizar la selección de los botones con el ViewPager
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_devices) {
                viewPager.setCurrentItem(0); // Primer fragmento: Gestión de dispositivos
                return true;
            } else if (itemId == R.id.nav_personal_data) {
                viewPager.setCurrentItem(1); // Segundo fragmento: Datos personales
                return true;
            } else if (itemId == R.id.nav_alerts) {
                viewPager.setCurrentItem(2); // Tercer fragmento: Alertas
                resetBadge(); // Reiniciar el contador de notificaciones al entrar a la vista de alertas
                return true;
            }
            return false;
        });

        // Sincronizar el ViewPager con el BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_devices);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_personal_data);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_alerts);
                        resetBadge(); // Reiniciar el contador de notificaciones
                        break;
                }
            }
        });

        // Lógica para monitorear métricas automáticamente y actualizar las notificaciones
        DeviceRepository.monitorMetricsForCurrentUser(this::onNewAlert);
    }

    // Método para actualizar el badge cuando hay nuevas alertas
    private void onNewAlert() {
        unreadNotifications++;
        badgeDrawable.setNumber(unreadNotifications);
        badgeDrawable.setVisible(true); // Mostrar el badge
    }

    // Método para reiniciar el badge al entrar en la vista de alertas
    private void resetBadge() {
        unreadNotifications = 0;
        badgeDrawable.clearNumber();
        badgeDrawable.setVisible(false); // Ocultar el badge
    }
}
