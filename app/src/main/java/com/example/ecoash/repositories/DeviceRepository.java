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
        DatabaseReference lastValuesRef = deviceRef.child("lastValues");

        // Monitorear cada métrica con umbrales y colores
        monitorMetricWithThresholdAndLevels(deviceId, deviceRef.child("temperatura"), lastValuesRef.child("temperatura"),
                "Temperatura", "°C", 5.0,
                30.0, "Temperatura elevada", "rojo",
                5.0, "Temperatura baja", "azul",
                "Temperatura en rango normal", "verde", callback);

        monitorMetricWithThresholdAndLevels(deviceId, deviceRef.child("CO"), lastValuesRef.child("CO"),
                "CO (Monóxido de Carbono)", "ppm", 5.0,
                9.0, "Niveles altos de CO", "rojo",
                0.0, "CO en niveles bajos", "azul",
                "CO en niveles normales", "verde", callback);

        monitorMetricWithThresholdAndLevels(deviceId, deviceRef.child("CO2"), lastValuesRef.child("CO2"),
                "CO2 (Dióxido de Carbono)", "ppm", 50.0,
                1000.0, "CO2 elevado", "rojo",
                400.0, "CO2 en niveles bajos", "azul",
                "CO2 en niveles normales", "verde", callback);

        monitorMetricWithThresholdAndLevels(deviceId, deviceRef.child("PM2_5"), lastValuesRef.child("PM2_5"),
                "PM2.5 (Partículas finas)", "µg/m³", 5.0,
                35.0, "Partículas finas altas", "rojo",
                0.0, "Partículas finas bajas", "azul",
                "Partículas finas normales", "verde", callback);

        monitorMetricWithThresholdAndLevels(deviceId, deviceRef.child("PM10"), lastValuesRef.child("PM10"),
                "PM10 (Partículas gruesas)", "µg/m³", 10.0,
                50.0, "Partículas gruesas altas", "rojo",
                0.0, "Partículas gruesas bajas", "azul",
                "Partículas gruesas normales", "verde", callback);

        monitorMetricWithThresholdAndLevels(deviceId, deviceRef.child("humedad"), lastValuesRef.child("humedad"),
                "Humedad", "%", 10.0,
                70.0, "Humedad alta", "rojo",
                30.0, "Humedad baja", "azul",
                "Humedad en niveles normales", "verde", callback);
    }

    private static void monitorMetricWithThresholdAndLevels(String deviceId, DatabaseReference metricRef, DatabaseReference lastValueRef,
                                                            String metricName, String unit, double threshold,
                                                            double highThreshold, String highMessage, String highColor,
                                                            double lowThreshold, String lowMessage, String lowColor,
                                                            String normalMessage, String normalColor, AlertCallback callback) {
        metricRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double currentValue = snapshot.getValue(Double.class);
                if (currentValue == null) {
                    Log.w(TAG, "El valor de " + metricName + " no está disponible.");
                    return;
                }

                lastValueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot lastValueSnapshot) {
                        Double lastValue = lastValueSnapshot.getValue(Double.class);

                        if (lastValue == null || Math.abs(currentValue - lastValue) >= threshold) {
                            Log.d(TAG, metricName + " ha cambiado significativamente: " + currentValue);

                            // Determinar nivel de alerta
                            String mensaje;
                            String titulo;
                            String color;

                            if (currentValue > highThreshold) {
                                mensaje = highMessage + ": " + currentValue + " " + unit;
                                titulo = metricName + " alta";
                                color = highColor;
                            } else if (currentValue < lowThreshold) {
                                mensaje = lowMessage + ": " + currentValue + " " + unit;
                                titulo = metricName + " baja";
                                color = lowColor;
                            } else {
                                mensaje = normalMessage + ": " + currentValue + " " + unit;
                                titulo = metricName + " estable";
                                color = normalColor;
                            }

                            // Crear alerta
                            createAlert(deviceId, mensaje, titulo, color, callback);

                            // Actualizar último valor
                            lastValueRef.setValue(currentValue);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error al leer el último valor de " + metricName + ": " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al escuchar cambios en " + metricName + ": " + error.getMessage());
            }
        });
    }

    private static void createAlert(String deviceId, String mensaje, String titulo, String color, AlertCallback callback) {
        DatabaseReference alertsRef = devicesRef.child(deviceId).child("alertas");
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Alert alert = new Alert(fecha, mensaje, titulo, color);

        alertsRef.push().setValue(alert).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Alerta creada: " + mensaje);
            callback.onNewAlert();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al crear alerta: " + e.getMessage());
        });
    }
}
