package com.example.ecoash;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Importar los fragmentos correspondientes
import com.example.ecoash.AdminDeviceCreationFragment;
import com.example.ecoash.AdminManageDeviceFragment;
import com.example.ecoash.ClientAlertsFragment;
import com.example.ecoash.ClientDeviceManagementFragment;
import com.example.ecoash.ClientPersonalDataFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final FragmentActivity fragmentActivity;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity; // Inicializar el fragmentActivity
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Verifica si el fragment pertenece al administrador
        if (fragmentActivity instanceof AdminHomeActivity) {
            switch (position) {
                case 0:
                    return new AdminDeviceCreationFragment(); // Fragmento para agregar dispositivo (admin)
                case 1:
                    return new AdminManageDeviceFragment(); // Fragmento para administrar dispositivos (admin)
                default:
                    return new AdminDeviceCreationFragment(); // Fragmento predeterminado para el admin
            }
        }
        // Verifica si el fragment pertenece al cliente
        else if (fragmentActivity instanceof ClientHomeActivity) {
            switch (position) {
                case 0:
                    return new ClientDeviceManagementFragment(); // Gestión de dispositivos
                case 1:
                    return new ClientPersonalDataFragment(); // Perfil
                case 2:
                    return new ClientAlertsFragment(); // Fragmento de alertas (sin argumentos)
                default:
                    return new ClientDeviceManagementFragment(); // Fragmento predeterminado para el cliente
            }
        }
        // Fragmento vacío en caso de error
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        // Si es una actividad del administrador
        if (fragmentActivity instanceof AdminHomeActivity) {
            return 2; // Admin: Agregar, Administrar
        }
        // Si es una actividad del cliente
        else if (fragmentActivity instanceof ClientHomeActivity) {
            return 3; // Cliente: Dispositivos, Personal, Alertas
        }
        // En caso de que no sea ninguna actividad válida
        return 0;
    }
}
