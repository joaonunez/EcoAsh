package com.example.ecoash;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.ecoash.R;


public class ClientHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Configurar el listener de navegación
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_devices) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.nav_personal_data) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.nav_alerts) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });

        // Sincronizar la selección de ViewPager con BottomNavigation
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
    }
}
