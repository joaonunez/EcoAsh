package com.example.ecoash;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ecoash.device.Device;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeviceRepository {

    private static final DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference("dispositivos");

    public interface DevicesCallback {
        void onSuccess(List<Device> devices);
        void onError(Exception e);
    }

    // Obtener todos los dispositivos
    public static void getAllDevices(DevicesCallback callback) {
        devicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Device> devices = new ArrayList<>();
                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    try {
                        Device device = deviceSnapshot.getValue(Device.class);
                        if (device != null) {
                            device.setId(deviceSnapshot.getKey());
                            devices.add(device);
                        }
                    } catch (Exception e) {
                        Log.e("DeviceRepository", "Error parsing device: " + e.getMessage());
                    }
                }
                callback.onSuccess(devices);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    // Obtener dispositivos por usuario
    public static void getDevicesByUser(String userEmail, DevicesCallback callback) {
        if (userEmail == null || userEmail.isEmpty()) {
            callback.onError(new IllegalArgumentException("User email is null or empty"));
            return;
        }

        devicesRef.orderByChild("userEmail").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Device> devices = new ArrayList<>();
                        for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                            try {
                                Device device = deviceSnapshot.getValue(Device.class);
                                if (device != null) {
                                    device.setId(deviceSnapshot.getKey());
                                    devices.add(device);

                                    // Log dentro del bloque try, donde `device` está definido
                                    Log.d("DeviceRepository", "Device loaded: " + device.getName() + " (ID: " + device.getId() + ")");
                                }
                            } catch (Exception e) {
                                Log.e("DeviceRepository", "Error parsing device: " + e.getMessage());
                            }
                        }
                        callback.onSuccess(devices);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

}
