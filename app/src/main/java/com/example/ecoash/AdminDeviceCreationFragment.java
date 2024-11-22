package com.example.ecoash;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDeviceCreationFragment extends Fragment {

    private EditText deviceNameInput;
    private AutoCompleteTextView userEmailInput;
    private Button registerDeviceButton;

    private FirebaseFirestore firestore;
    private List<String> userEmails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_device_creation, container, false);

        // Inicializar Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Inicializar lista para correos
        userEmails = new ArrayList<>();

        // Referenciar elementos de la vista
        deviceNameInput = view.findViewById(R.id.deviceNameInput);
        userEmailInput = view.findViewById(R.id.userEmailInput);
        registerDeviceButton = view.findViewById(R.id.registerDeviceButton);

        // Configurar autocompletado para el correo del usuario
        setupUserEmailAutocomplete();

        // Acción para registrar el dispositivo
        registerDeviceButton.setOnClickListener(v -> registerDevice());

        return view;
    }

    private void setupUserEmailAutocomplete() {
        // Consultar la colección de usuarios con rol cliente
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
                    // Configurar el adaptador de autocompletado
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, userEmails);
                    userEmailInput.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al cargar correos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void registerDevice() {
        String deviceName = deviceNameInput.getText().toString().trim();
        String userEmail = userEmailInput.getText().toString().trim();

        if (deviceName.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, ingrese un nombre para el dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si no se proporciona un correo, el dispositivo será registrado como "sin asignar"
        String assignedEmail = userEmail.isEmpty() ? "Sin asignar" : userEmail;

        // Datos iniciales del dispositivo
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("name", deviceName);
        deviceData.put("userEmail", assignedEmail);
        deviceData.put("PM2.5", 0.0);
        deviceData.put("PM10", 0.0);
        deviceData.put("CO2", 0.0);
        deviceData.put("CO", 0.0);
        deviceData.put("monoxido_carbono", 0.0);
        deviceData.put("temperatura", new HashMap<String, Double>() {{
            put("celsius", 0.0);
            put("fahrenheit", 0.0);
        }});
        deviceData.put("humedad", 0.0);

        // Guardar en Firestore
        firestore.collection("dispositivos")
                .add(deviceData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Dispositivo registrado con éxito", Toast.LENGTH_SHORT).show();
                    deviceNameInput.setText(""); // Limpiar campos
                    userEmailInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al registrar el dispositivo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
