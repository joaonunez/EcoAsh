package com.example.ecoash;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // IMPORT NECESARIO
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class AdminManageDeviceFragment extends Fragment {

    private DatabaseReference realtimeDatabase;
    private LinearLayout devicesContainer;
    private EditText searchInput;

    private List<HashMap<String, Object>> allDevices;
    private boolean isUpdatingTemperature = false; // Bandera para evitar bucles

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

        // Cargar dispositivos en tiempo real
        loadDevicesInRealTime();

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

    private void loadDevicesInRealTime() {
        allDevices = new ArrayList<>();

        // Listener para detectar cambios en tiempo real
        realtimeDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                addOrUpdateDevice(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                addOrUpdateDevice(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                removeDevice(snapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error al cargar dispositivos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrUpdateDevice(DataSnapshot snapshot) {
        HashMap<String, Object> device = (HashMap<String, Object>) snapshot.getValue();

        if (device != null) {
            device.put("id", snapshot.getKey());
            // Buscar si el dispositivo ya está en la lista
            boolean exists = false;
            for (int i = 0; i < allDevices.size(); i++) {
                if (allDevices.get(i).get("id").equals(device.get("id"))) {
                    allDevices.set(i, device); // Actualizar si existe
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                allDevices.add(device); // Agregar si no existe
            }
            syncTemperature(snapshot.getRef(), device); // Sincronizar temperaturas
            filterDevices(searchInput.getText().toString().trim());
        }
    }

    private void removeDevice(DataSnapshot snapshot) {
        String id = snapshot.getKey();
        if (id != null) {
            for (int i = 0; i < allDevices.size(); i++) {
                if (allDevices.get(i).get("id").equals(id)) {
                    allDevices.remove(i);
                    break;
                }
            }
            filterDevices(searchInput.getText().toString().trim());
        }
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
        TextView monoxide = cardView.findViewById(R.id.monoxide); // Monóxido de carbono

        // Configurar datos del dispositivo
        deviceName.setText((String) device.get("name"));
        userEmail.setText((String) device.getOrDefault("userEmail", "Sin asignar"));
        co2.setText("CO2: " + device.getOrDefault("CO2", 0) + " ppm");
        pm25.setText("PM2.5: " + device.getOrDefault("PM2_5", 0) + " µg/m³");
        pm10.setText("PM10: " + device.getOrDefault("PM10", 0) + " µg/m³");
        humidity.setText("Humedad: " + device.getOrDefault("humedad", 0) + " %");
        monoxide.setText("Monóxido de Carbono: " + device.getOrDefault("monoxido_carbono", 0) + " ppm");

        HashMap<String, Object> tempData = (HashMap<String, Object>) device.get("temperatura");
        if (tempData != null) {
            temperature.setText("Temperatura: " + tempData.get("celsius") + " °C / " + tempData.get("fahrenheit") + " °F");
        } else {
            temperature.setText("Temperatura: No disponible");
        }

        // Agregar la tarjeta al contenedor
        devicesContainer.addView(cardView);
    }

    private void syncTemperature(DatabaseReference deviceRef, HashMap<String, Object> device) {
        if (isUpdatingTemperature) return; // Prevenir bucles
        isUpdatingTemperature = true;

        DatabaseReference tempRef = deviceRef.child("temperatura");
        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> temp = (HashMap<String, Object>) snapshot.getValue();
                if (temp != null) {
                    double celsius = Double.parseDouble(temp.getOrDefault("celsius", 0).toString());
                    double fahrenheit = Double.parseDouble(temp.getOrDefault("fahrenheit", 0).toString());

                    double calculatedFahrenheit = (celsius * 9 / 5) + 32;
                    double calculatedCelsius = (fahrenheit - 32) * 5 / 9;

                    if (Math.abs(calculatedFahrenheit - fahrenheit) > 0.01) {
                        tempRef.child("fahrenheit").setValue(calculatedFahrenheit);
                    } else if (Math.abs(calculatedCelsius - celsius) > 0.01) {
                        tempRef.child("celsius").setValue(calculatedCelsius);
                    }
                }
                isUpdatingTemperature = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isUpdatingTemperature = false;
                Log.e("AdminSync", "Error al sincronizar temperaturas: " + error.getMessage());
            }
        });
    }
}
