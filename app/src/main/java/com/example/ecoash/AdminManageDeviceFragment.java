package com.example.ecoash;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminManageDeviceFragment extends Fragment {

    private DatabaseReference realtimeDatabase;
    private LinearLayout devicesContainer;
    private EditText searchInput;

    private List<HashMap<String, Object>> allDevices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_device, container, false);

        // Inicializar Realtime Database
        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");

        // Referenciar elementos del layout
        devicesContainer = view.findViewById(R.id.devicesContainer);
        searchInput = view.findViewById(R.id.searchInput);

        // Configurar búsqueda en tiempo real
        setupSearchListener();

        // Cargar dispositivos desde la base de datos
        loadDevices();

        return view;
    }

    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDevices(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDevices() {
        realtimeDatabase.get()
                .addOnSuccessListener(dataSnapshot -> {
                    allDevices = new ArrayList<>();
                    devicesContainer.removeAllViews();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Object> device = (HashMap<String, Object>) snapshot.getValue();
                        if (device != null) {
                            device.put("id", snapshot.getKey());
                            allDevices.add(device);
                        }
                    }

                    // Mostrar todos los dispositivos inicialmente
                    filterDevices("");
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al cargar dispositivos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void filterDevices(String query) {
        devicesContainer.removeAllViews();

        for (HashMap<String, Object> device : allDevices) {
            String userEmail = (String) device.get("userEmail");

            if (userEmail != null && userEmail.toLowerCase().contains(query.toLowerCase())) {
                viewDeviceCard(device);
            } else if (userEmail == null || userEmail.equalsIgnoreCase("Sin asignar")) {
                viewDeviceCard(device);
            }
        }
    }

    private void viewDeviceCard(HashMap<String, Object> device) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View cardView = inflater.inflate(R.layout.device_card, devicesContainer, false);

        // Referenciar elementos del card
        TextView deviceName = cardView.findViewById(R.id.deviceName);
        TextView userEmail = cardView.findViewById(R.id.userEmail);
        TextView co2 = cardView.findViewById(R.id.co2);
        TextView pm25 = cardView.findViewById(R.id.pm25);
        TextView pm10 = cardView.findViewById(R.id.pm10);
        TextView humidity = cardView.findViewById(R.id.humidity);
        TextView temperature = cardView.findViewById(R.id.temperature);

        // Configurar datos del dispositivo
        deviceName.setText((String) device.get("name"));
        userEmail.setText((String) device.getOrDefault("userEmail", "Sin asignar")); // Sin prefijo redundante
        co2.setText("CO2: " + device.getOrDefault("CO2", 0) + " ppm");
        pm25.setText("PM2.5: " + device.getOrDefault("PM25", 0) + " µg/m³");
        pm10.setText("PM10: " + device.getOrDefault("PM10", 0) + " µg/m³");
        humidity.setText("Humedad: " + device.getOrDefault("humedad", 0) + " %");

        HashMap<String, Object> tempData = (HashMap<String, Object>) device.get("temperatura");
        if (tempData != null) {
            temperature.setText("Temperatura: " + tempData.get("celsius") + " °C / " + tempData.get("fahrenheit") + " °F");
        } else {
            temperature.setText("Temperatura: No disponible");
        }

        // Agregar la tarjeta al contenedor
        devicesContainer.addView(cardView);
    }
}
