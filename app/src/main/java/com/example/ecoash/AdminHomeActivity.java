package com.example.ecoash;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminHomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Referencias a las vistas
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Verificar que las vistas no sean nulas
        if (viewPager == null || bottomNavigationView == null) {
            throw new NullPointerException("viewPager o bottomNavigationView no están correctamente inicializados");
        }

        // Configurar el adaptador del ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Manejar la selección en el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) { // ID del menú de admin
                viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.nav_manage) { // ID del menú de admin
                viewPager.setCurrentItem(1, true);
                return true;
            }
            return false;
        });


        // Cambiar el estado del BottomNavigationView al deslizar el ViewPager2
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
    }
}
