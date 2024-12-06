package com.example.ecoash.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecoash.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AdminDeviceCreationFragment extends Fragment {

    private EditText deviceNameInput;
    private AutoCompleteTextView userEmailInput;
    private Button registerDeviceButton;

    private FirebaseFirestore firestore;
    private DatabaseReference realtimeDatabase;
    private List<String> userEmails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_device_creation, container, false);

        firestore = FirebaseFirestore.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");
        userEmails = new ArrayList<>();

        deviceNameInput = view.findViewById(R.id.deviceNameInput);
        userEmailInput = view.findViewById(R.id.userEmailInput);
        registerDeviceButton = view.findViewById(R.id.registerDeviceButton);

        setupUserEmailAutocomplete();
        registerDeviceButton.setOnClickListener(v -> registerDevice());

        return view;
    }

    private void setupUserEmailAutocomplete() {
        firestore.collection("users")
                .whereEqualTo("role", "cliente")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String email = document.getString("email");
                        if (email != null) {
                            userEmails.add(email);
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, userEmails);
                    userEmailInput.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al cargar correos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void registerDevice() {
        String deviceName = deviceNameInput.getText().toString().trim();
        String userEmail = userEmailInput.getText().toString().trim();

        if (deviceName.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, ingrese un nombre para el dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }

        String assignedEmail = userEmail.isEmpty() ? "Sin asignar" : userEmail;
        String dateOfCreation = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Crear el JSON de alertas inicial
        String alertId = UUID.randomUUID().toString(); // Generar ID único para la alerta
        HashMap<String, Object> alertData = new HashMap<>();
        alertData.put("titulo", "Este es un ejemplo de aviso");
        alertData.put("mensaje", "Así podrás recibir alertas de los cambios en tu dispositivo");
        alertData.put("fecha", dateOfCreation);

        HashMap<String, Object> alertsMap = new HashMap<>();
        alertsMap.put(alertId, alertData);

        // Crear el mapa de datos del dispositivo
        HashMap<String, Object> deviceData = new HashMap<>();
        deviceData.put("name", deviceName);
        deviceData.put("userEmail", assignedEmail);
        deviceData.put("dateOfCreation", dateOfCreation);
        deviceData.put("PM2_5", 0.0);
        deviceData.put("PM10", 0.0);
        deviceData.put("CO2", 0.0);
        deviceData.put("CO", 0.0);
        deviceData.put("humedad", 0.0);
        deviceData.put("temperatura", 0.0);
        deviceData.put("alertas", alertsMap); // Añadimos la alerta inicial aquí

        // Subir a Firebase Realtime Database
        realtimeDatabase.push().setValue(deviceData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Dispositivo registrado con éxito", Toast.LENGTH_SHORT).show();
                    deviceNameInput.setText("");
                    userEmailInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error al registrar el dispositivo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
