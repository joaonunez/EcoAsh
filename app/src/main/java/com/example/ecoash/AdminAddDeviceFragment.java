package com.example.ecoash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminAddDeviceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_admin_add_device, container, false);

        // Referenciar el botón
        Button btnRegisterAdminDevice = view.findViewById(R.id.btnRegisterAdminDevice);
        btnRegisterAdminDevice.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Registrar dispositivo del administrador clickeado", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

}
