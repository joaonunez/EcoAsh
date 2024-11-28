package com.example.ecoash;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ecoash.device.Alerta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeviceRepository {

    private static final DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("dispositivos");
    private static final String TAG = "DeviceRepository";

    // Escucha alertas para un dispositivo en específico
    public static void getAlertsForCurrentDevice(List<Alerta> alertas, AlertsUpdateCallback callback) {
        String deviceId = "TU_ID_DEL_DISPOSITIVO"; // Sustituye por lógica para obtener el ID del dispositivo actual
        DatabaseReference alertsRef = devicesRef.child(deviceId).child("alerts");

        alertsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Alerta> updatedAlerts = new ArrayList<>();
                for (DataSnapshot alertSnapshot : snapshot.getChildren()) {
                    Alerta alerta = alertSnapshot.getValue(Alerta.class);
                    if (alerta != null) {
                        updatedAlerts.add(alerta);
                    }
                }
                Log.d(TAG, "Alertas actualizadas: " + updatedAlerts.size());
                callback.onAlertsUpdated(updatedAlerts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al escuchar alertas: " + error.getMessage());
            }
        });
    }

    public interface AlertsUpdateCallback {
        void onAlertsUpdated(List<Alerta> updatedAlerts);
    }
}
