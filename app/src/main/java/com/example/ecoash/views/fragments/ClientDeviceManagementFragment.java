package com.example.ecoash.views.fragments;

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

import com.example.ecoash.R;
import com.example.ecoash.adapters.DeviceAdapter;
import com.example.ecoash.models.Device;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClientDeviceManagementFragment extends Fragment {

    private static final String TAG = "ClientDeviceMgmt";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private DeviceAdapter deviceAdapter;
    private FirebaseAuth auth;
    private DatabaseReference realtimeDatabase;
    private List<Device> devices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_device_management, container, false);

        auth = FirebaseAuth.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        devices = new ArrayList<>();
        // Ahora se usa DeviceAdapter con isAdmin = false y sin onDeleteListener
        deviceAdapter = new DeviceAdapter(getContext(), devices, false, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(deviceAdapter);

        loadDevices();

        return view;
    }

    private void loadDevices() {
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;

        if (userEmail == null) {
            Log.e(TAG, "Usuario no autenticado. No se puede cargar dispositivos.");
            Toast.makeText(getContext(), "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Iniciando la carga de dispositivos para el usuario: " + userEmail);
        progressBar.setVisibility(View.VISIBLE);

        realtimeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                devices.clear();

                if (!snapshot.exists()) {
                    Log.w(TAG, "No se encontraron dispositivos en la base de datos.");
                }

                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    try {
                        Device device = deviceSnapshot.getValue(Device.class);
                        Log.d(TAG, "Dispositivo encontrado en Firebase: " + deviceSnapshot.getValue());

                        if (device != null) {
                            Log.d(TAG, "Procesando dispositivo con ID: " + deviceSnapshot.getKey());
                            if (userEmail.equals(device.getUserEmail())) {
                                device.setId(deviceSnapshot.getKey());
                                devices.add(device);
                                Log.d(TAG, "Dispositivo añadido a la lista: " + device.getName());
                            } else {
                                Log.d(TAG, "Dispositivo ignorado (usuario no coincide): " + device.getName());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error procesando datos del dispositivo: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Error procesando datos del dispositivo", Toast.LENGTH_SHORT).show();
                    }
                }

                progressBar.setVisibility(View.GONE);
                deviceAdapter.notifyDataSetChanged();
                emptyView.setVisibility(devices.isEmpty() ? View.VISIBLE : View.GONE);

                Log.d(TAG, "Carga completada. Total de dispositivos cargados: " + devices.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error al cargar dispositivos desde Firebase: " + error.getMessage(), error.toException());
                Toast.makeText(getContext(), "Error al cargar dispositivos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
