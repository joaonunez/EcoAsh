package com.example.ecoash;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ecoash.device.Alert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeviceRepository {

    private static final DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("dispositivos");
    private static final String TAG = "DeviceRepository";

    public static void monitorTemperatureForCurrentUser() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Log.e(TAG, "El usuario no está logueado.");
            return;
        }

        devicesRef.orderByChild("userEmail").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                        String deviceId = deviceSnapshot.getKey();
                        Log.d(TAG, "Dispositivo encontrado para el usuario: " + deviceId);

                        // Escuchar cambios en la temperatura del dispositivo
                        monitorTemperature(deviceId);
                    }
                } else {
                    Log.e(TAG, "No se encontró un dispositivo asociado al usuario.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al buscar el dispositivo: " + error.getMessage());
            }
        });
    }

    private static void monitorTemperature(String deviceId) {
        DatabaseReference deviceRef = devicesRef.child(deviceId);

        deviceRef.child("temperatura").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double temperatura = snapshot.getValue(Double.class);
                if (temperatura == null) {
                    Log.w(TAG, "La temperatura no está disponible para el dispositivo con ID: " + deviceId);
                    return;
                }

                Log.d(TAG, "Nueva temperatura detectada: " + temperatura);

                // Crear alerta basada en la temperatura
                createAlertBasedOnTemperature(deviceId, temperatura);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al escuchar cambios en la temperatura: " + error.getMessage());
            }
        });
    }

    private static void createAlertBasedOnTemperature(String deviceId, Double temperatura) {
        DatabaseReference alertsRef = devicesRef.child(deviceId).child("alertas");
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        String mensaje;
        String titulo;
        String color;

        if (temperatura > 30) {
            mensaje = "La temperatura ha subido a " + temperatura + "°C.";
            titulo = "Temperatura elevada";
            color = "rojo";
        } else if (temperatura < 5) {
            mensaje = "La temperatura ha bajado a " + temperatura + "°C.";
            titulo = "Temperatura baja";
            color = "azul";
        } else {
            mensaje = "La temperatura está normal a " + temperatura + "°C.";
            titulo = "Temperatura normal";
            color = "negro";
        }

        Alert alert = new Alert(fecha, mensaje, titulo, color);

        // Agregar la alerta a Firebase
        alertsRef.push().setValue(alert).addOnSuccessListener(aVoid ->
                Log.d(TAG, "Alerta creada: " + mensaje)
        ).addOnFailureListener(e ->
                Log.e(TAG, "Error al crear alerta: " + e.getMessage())
        );
    }
}
