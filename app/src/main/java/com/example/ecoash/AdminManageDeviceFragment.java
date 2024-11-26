package com.example.ecoash;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminManageDeviceFragment extends Fragment {

    private DatabaseReference realtimeDatabase;
    private LinearLayout devicesContainer;
    private EditText searchInput;
    private Spinner filterSpinner;

    private List<HashMap<String, Object>> allDevices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_device, container, false);

        // Inicializar Firebase Realtime Database
        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");

        // Referenciar elementos del layout
        devicesContainer = view.findViewById(R.id.devicesContainer);
        searchInput = view.findViewById(R.id.searchInput);
        filterSpinner = view.findViewById(R.id.filterSpinner);

        // Inicializar lista de dispositivos
        allDevices = new ArrayList<>();

        // Configurar Spinner
        setupFilterSpinner();

        // Configurar búsqueda en tiempo real
        setupSearchListener();

        // Cargar dispositivos en tiempo real
        loadDevicesInRealTime();

        return view;
    }

    private void setupFilterSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"Buscar por correo", "Ver dispositivos sin usuario"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    searchInput.setVisibility(View.VISIBLE); // Mostrar campo de búsqueda
                    filterDevices(searchInput.getText().toString().trim());
                } else {
                    searchInput.setVisibility(View.GONE); // Ocultar campo de búsqueda
                    filterUnassignedDevices();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
        if (!isAdded() || getContext() == null) {
            Log.e("AdminManageDeviceFragment", "Fragmento no adjunto al contexto.");
            return;
        }

        HashMap<String, Object> device = (HashMap<String, Object>) snapshot.getValue();
        if (device != null) {
            device.put("id", snapshot.getKey());

            // Buscar y actualizar el dispositivo si ya existe
            boolean exists = false;
            for (int i = 0; i < allDevices.size(); i++) {
                if (allDevices.get(i).get("id").equals(device.get("id"))) {
                    allDevices.set(i, device);
                    exists = true;
                    break;
                }
            }

            // Si no existe, agregarlo
            if (!exists) {
                allDevices.add(device);
            }

            // Actualizar visualización
            if (filterSpinner.getSelectedItemPosition() == 0) {
                filterDevices(searchInput.getText().toString().trim());
            } else {
                filterUnassignedDevices();
            }
        }
    }

    private void removeDevice(DataSnapshot snapshot) {
        if (!isAdded() || getContext() == null) {
            Log.e("AdminManageDeviceFragment", "Fragmento no adjunto al contexto.");
            return;
        }

        String id = snapshot.getKey();
        if (id != null) {
            allDevices.removeIf(device -> device.get("id").equals(id));

            // Actualizar visualización
            if (filterSpinner.getSelectedItemPosition() == 0) {
                filterDevices(searchInput.getText().toString().trim());
            } else {
                filterUnassignedDevices();
            }
        }
    }

    private void filterDevices(String query) {
        if (!isAdded() || getContext() == null) return;

        devicesContainer.removeAllViews();

        for (HashMap<String, Object> device : allDevices) {
            String userEmail = (String) device.get("userEmail");

            if (userEmail != null && userEmail.toLowerCase().contains(query.toLowerCase())) {
                viewDeviceCard(device);
            }
        }
    }

    private void filterUnassignedDevices() {
        if (!isAdded() || getContext() == null) return;

        devicesContainer.removeAllViews();

        for (HashMap<String, Object> device : allDevices) {
            String userEmail = (String) device.get("userEmail");

            if (userEmail == null || userEmail.equalsIgnoreCase("Sin asignar")) {
                viewDeviceCard(device);
            }
        }
    }

    private void viewDeviceCard(HashMap<String, Object> device) {
        if (!isAdded() || getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View cardView = inflater.inflate(R.layout.device_card, devicesContainer, false);

        // Referenciar elementos del card
        TextView deviceName = cardView.findViewById(R.id.deviceName);
        TextView userEmail = cardView.findViewById(R.id.userEmail);
        Button deleteButton = cardView.findViewById(R.id.deleteDeviceButton);

        // Configurar datos del dispositivo
        deviceName.setText((String) device.get("name"));
        userEmail.setText((String) device.getOrDefault("userEmail", "Sin asignar"));

        // Configurar botón de eliminar
        deleteButton.setOnClickListener(v -> {
            String deviceId = (String) device.get("id");
            if (deviceId != null) {
                realtimeDatabase.child(deviceId).removeValue().addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Dispositivo eliminado con éxito", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al eliminar dispositivo", Toast.LENGTH_SHORT).show();
                });
            }
        });

        devicesContainer.addView(cardView);
    }
}
