package com.example.ecoash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AdminAddDeviceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_add_device, container, false);

        // Referenciar el botón
        Button btnRegisterAdminDevice = view.findViewById(R.id.btnRegisterAdminDevice);
        btnRegisterAdminDevice.setOnClickListener(v -> {
            // Redirigir al fragmento de creación de dispositivo
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new AdminDeviceCreationFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
