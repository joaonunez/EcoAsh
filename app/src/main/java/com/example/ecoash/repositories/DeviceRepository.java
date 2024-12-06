package com.example.ecoash.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ecoash.models.Alert;
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

    public interface AlertCallback {
        void onNewAlert();
    }

    public static void monitorMetricsForCurrentUser(AlertCallback callback) {
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

                        // Escuchar cambios en todas las métricas del dispositivo
                        monitorMetrics(deviceId, callback);
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

    private static void monitorMetrics(String deviceId, AlertCallback callback) {
        DatabaseReference deviceRef = devicesRef.child(deviceId);

        // Monitorear temperatura
        monitorMetric(deviceId, deviceRef.child("temperatura"), "Temperatura", "°C",
                30.0, "Temperatura elevada", "rojo",
                5.0, "Temperatura baja", "azul",
                "Temperatura en rango normal", "verde", callback);

        // Monitorear CO
        monitorMetric(deviceId, deviceRef.child("CO"), "CO (Monóxido de Carbono)", "ppm",
                9.0, "Niveles altos de CO", "rojo",
                0.0, "CO en niveles bajos", "azul",
                "CO en niveles normales", "verde", callback);

        // Monitorear CO2
        monitorMetric(deviceId, deviceRef.child("CO2"), "CO2 (Dióxido de Carbono)", "ppm",
                1000.0, "CO2 elevado", "rojo",
                400.0, "CO2 en niveles bajos", "azul",
                "CO2 en niveles normales", "verde", callback);

        // Monitorear PM2.5
        monitorMetric(deviceId, deviceRef.child("PM2_5"), "PM2.5 (Partículas finas)", "µg/m³",
                35.0, "Partículas finas altas", "rojo",
                0.0, "Partículas finas bajas", "azul",
                "Partículas finas normales", "verde", callback);

        // Monitorear PM10
        monitorMetric(deviceId, deviceRef.child("PM10"), "PM10 (Partículas gruesas)", "µg/m³",
                50.0, "Partículas gruesas altas", "rojo",
                0.0, "Partículas gruesas bajas", "azul",
                "Partículas gruesas normales", "verde", callback);

        // Monitorear Humedad
        monitorMetric(deviceId, deviceRef.child("humedad"), "Humedad", "%",
                70.0, "Humedad alta", "rojo",
                30.0, "Humedad baja", "azul",
                "Humedad en niveles normales", "verde", callback);
    }

    private static void monitorMetric(String deviceId, DatabaseReference metricRef, String metricName, String unit,
                                      double highThreshold, String highMessage, String highColor,
                                      double lowThreshold, String lowMessage, String lowColor,
                                      String normalMessage, String normalColor, AlertCallback callback) {
        metricRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double value = snapshot.getValue(Double.class);
                if (value == null) {
                    Log.w(TAG, "El valor de " + metricName + " no está disponible para el dispositivo con ID: " + deviceId);
                    return;
                }

                Log.d(TAG, "Nuevo valor detectado para " + metricName + ": " + value);

                // Crear alerta basada en el valor de la métrica
                createAlert(deviceId, metricName, value, unit, highThreshold, highMessage, highColor,
                        lowThreshold, lowMessage, lowColor, normalMessage, normalColor, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al escuchar cambios en " + metricName + ": " + error.getMessage());
            }
        });
    }

    private static void createAlert(String deviceId, String metricName, Double value, String unit,
                                    double highThreshold, String highMessage, String highColor,
                                    double lowThreshold, String lowMessage, String lowColor,
                                    String normalMessage, String normalColor, AlertCallback callback) {
        DatabaseReference alertsRef = devicesRef.child(deviceId).child("alertas");
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        String mensaje;
        String titulo;
        String color;

        if (value > highThreshold) {
            mensaje = highMessage + ": " + value + " " + unit;
            titulo = metricName + " alta";
            color = highColor;
        } else if (value < lowThreshold) {
            mensaje = lowMessage + ": " + value + " " + unit;
            titulo = metricName + " baja";
            color = lowColor;
        } else {
            mensaje = normalMessage + ": " + value + " " + unit;
            titulo = metricName + " normal";
            color = normalColor;
        }

        Alert alert = new Alert(fecha, mensaje, titulo, color);

        // Agregar la alerta a Firebase
        alertsRef.push().setValue(alert).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Alerta creada: " + mensaje);
            callback.onNewAlert(); // Notificar a la UI que hay una nueva alerta
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al crear alerta: " + e.getMessage());
        });
    }
}
