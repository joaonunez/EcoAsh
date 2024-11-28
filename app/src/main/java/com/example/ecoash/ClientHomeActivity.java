package com.example.ecoash;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClientHomeActivity extends AppCompatActivity {

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
                        break;
                }
            }
        });

        // Lógica para monitorear temperatura automáticamente
        DeviceRepository.monitorTemperatureForCurrentUser();
    }
}
