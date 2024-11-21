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

public class ClientDeviceManagementFragment extends Fragment {

    public ClientDeviceManagementFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_device_management, container, false);

        Button btnRegisterDevice = view.findViewById(R.id.btnRegisterDevice);
        btnRegisterDevice.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Registrar dispositivo clickeado", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
