package com.example.ecoash.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecoash.views.fragments.AdminDeviceCreationFragment;
import com.example.ecoash.views.fragments.AdminManageDeviceFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminDeviceCreationFragment(); // Fragmento de agregar dispositivos
            case 1:
                return new AdminManageDeviceFragment(); // Fragmento de gestionar dispositivos
            default:
                throw new IllegalArgumentException("Posición desconocida: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Número de fragmentos
    }
}
