package com.example.ecoash.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecoash.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
        registerDeviceButton.setOnClickListener(v -> {
            hideKeyboard();
            checkUserDevice();
        });

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

    private void checkUserDevice() {
        String userEmail = userEmailInput.getText().toString().trim();

        // Si el email está vacío, se asume "Sin asignar" y se continúa directamente
        if (userEmail.isEmpty()) {
            registerDevice();
            return;
        }

        // Verificar si el usuario ya tiene un dispositivo asignado
        realtimeDatabase.orderByChild("userEmail").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // El usuario ya tiene un dispositivo
                            showCustomToast("Ese usuario ya tiene un dispositivo asignado");
                        } else {
                            // El usuario no tiene un dispositivo, podemos registrarlo
                            registerDevice();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Error al verificar dispositivo: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

        // Crear la estructura de datos del dispositivo
        HashMap<String, Object> deviceData = new HashMap<>();
        deviceData.put("name", deviceName);
        deviceData.put("userEmail", assignedEmail);
        deviceData.put("dateOfCreation", dateOfCreation);
        deviceData.put("CO", 0);
        deviceData.put("CO2", 0);
        deviceData.put("PM10", 0);
        deviceData.put("PM2_5", 0);
        deviceData.put("humedad", 0);
        deviceData.put("temperatura", 0);

        // Crear la sección lastValues con solo las unidades de medición
        HashMap<String, Object> lastValues = new HashMap<>();
        lastValues.put("CO", 0);
        lastValues.put("CO2", 0);
        lastValues.put("PM10", 0);
        lastValues.put("PM2_5", 0);
        lastValues.put("humedad", 0);
        lastValues.put("temperatura", 0);

        deviceData.put("lastValues", lastValues);

        realtimeDatabase.push().setValue(deviceData)
                .addOnSuccessListener(aVoid -> {
                    deviceNameInput.setText("");
                    userEmailInput.setText("");
                    Toast.makeText(requireContext(), "Dispositivo registrado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Asumiendo que en custom_toast.xml hay un TextView con id toastMessage
        // y un ImageView con id toastImage (no es necesario modificar el imageView ya que ya tiene el src en el layout)
        // Solo agregamos el texto
        androidx.appcompat.widget.AppCompatTextView textView = layout.findViewById(R.id.toastMessage);
        textView.setText(message);

        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
