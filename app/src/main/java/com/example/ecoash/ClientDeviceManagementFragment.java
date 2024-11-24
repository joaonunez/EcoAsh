package com.example.ecoash;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientDeviceManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ClientDeviceAdapter deviceAdapter;
    private DatabaseReference realtimeDatabase;
    private FirebaseAuth auth;
    private List<HashMap<String, Object>> devices;

    // Bandera para deshabilitar el listener temporalmente
    private boolean isUpdatingTemperature = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_device_management, container, false);

        // Inicializar Firebase Realtime Database y Authentication
        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");
        auth = FirebaseAuth.getInstance();

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        devices = new ArrayList<>();
        deviceAdapter = new ClientDeviceAdapter(devices);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(deviceAdapter);

        // Cargar dispositivos asociados al cliente
        loadClientDevices();

        return view;
    }

    private void loadClientDevices() {
        String userEmail = auth.getCurrentUser().getEmail(); // Obtener correo del cliente logueado

        progressBar.setVisibility(View.VISIBLE);

        // Listener para detectar cambios en tiempo real
        realtimeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                devices.clear(); // Limpiar lista antes de actualizar

                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    HashMap<String, Object> device = (HashMap<String, Object>) deviceSnapshot.getValue();

                    // Verificar que el dispositivo esté asignado al cliente
                    if (device != null && userEmail.equals(device.get("userEmail"))) {
                        device.put("id", deviceSnapshot.getKey()); // Agregar el ID del dispositivo
                        devices.add(device);
                        setupTemperatureSync(deviceSnapshot.getRef()); // Configurar sincronización de temperatura
                    }
                }

                // Notificar al adaptador para actualizar la vista
                deviceAdapter.notifyDataSetChanged();

                // Mostrar un mensaje si no hay dispositivos asignados
                emptyView.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar dispositivos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Configuración para sincronización de temperatura en tiempo real
    private void setupTemperatureSync(DatabaseReference deviceRef) {
        DatabaseReference tempRef = deviceRef.child("temperatura");

        tempRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateTemperature(snapshot, tempRef);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateTemperature(snapshot, tempRef);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TemperatureSync", "Error en sincronización: " + error.getMessage());
            }
        });
    }

    // Método para actualizar la temperatura en tiempo real
    private void updateTemperature(DataSnapshot snapshot, DatabaseReference tempRef) {
        // Prevenir actualizaciones recíprocas
        if (isUpdatingTemperature) return;

        String key = snapshot.getKey();
        Object value = snapshot.getValue();

        if (key == null || value == null) return;

        if (key.equals("celsius")) {
            // Convertir Celsius a Fahrenheit
            double celsius = Double.parseDouble(value.toString());
            double calculatedFahrenheit = (celsius * 9 / 5) + 32;

            // Obtener el valor actual de Fahrenheit en la base de datos
            tempRef.child("fahrenheit").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot fahrenheitSnapshot) {
                    Object currentFahrenheit = fahrenheitSnapshot.getValue();

                    if (currentFahrenheit != null) {
                        double existingFahrenheit = Double.parseDouble(currentFahrenheit.toString());

                        // Actualizar solo si la diferencia es mayor que la tolerancia (0.01)
                        if (Math.abs(existingFahrenheit - calculatedFahrenheit) > 0.01) {
                            isUpdatingTemperature = true; // Deshabilitar listener
                            tempRef.child("fahrenheit").setValue(calculatedFahrenheit).addOnCompleteListener(task -> {
                                isUpdatingTemperature = false; // Rehabilitar listener
                            });
                        }
                    } else {
                        // Si no existe, simplemente actualizamos
                        isUpdatingTemperature = true; // Deshabilitar listener
                        tempRef.child("fahrenheit").setValue(calculatedFahrenheit).addOnCompleteListener(task -> {
                            isUpdatingTemperature = false; // Rehabilitar listener
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TemperatureSync", "Error al leer Fahrenheit: " + error.getMessage());
                }
            });
        } else if (key.equals("fahrenheit")) {
            // Convertir Fahrenheit a Celsius
            double fahrenheit = Double.parseDouble(value.toString());
            double calculatedCelsius = (fahrenheit - 32) * 5 / 9;

            // Obtener el valor actual de Celsius en la base de datos
            tempRef.child("celsius").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot celsiusSnapshot) {
                    Object currentCelsius = celsiusSnapshot.getValue();

                    if (currentCelsius != null) {
                        double existingCelsius = Double.parseDouble(currentCelsius.toString());

                        // Actualizar solo si la diferencia es mayor que la tolerancia (0.01)
                        if (Math.abs(existingCelsius - calculatedCelsius) > 0.01) {
                            isUpdatingTemperature = true; // Deshabilitar listener
                            tempRef.child("celsius").setValue(calculatedCelsius).addOnCompleteListener(task -> {
                                isUpdatingTemperature = false; // Rehabilitar listener
                            });
                        }
                    } else {
                        // Si no existe, simplemente actualizamos
                        isUpdatingTemperature = true; // Deshabilitar listener
                        tempRef.child("celsius").setValue(calculatedCelsius).addOnCompleteListener(task -> {
                            isUpdatingTemperature = false; // Rehabilitar listener
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TemperatureSync", "Error al leer Celsius: " + error.getMessage());
                }
            });
        }
    }
}
