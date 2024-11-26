package com.example.ecoash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Device;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AdminDeviceAdapter extends RecyclerView.Adapter<AdminDeviceAdapter.AdminDeviceViewHolder> {

    private List<Device> devices;
    private DatabaseReference realtimeDatabase;

    public AdminDeviceAdapter(List<Device> devices, DatabaseReference realtimeDatabase) {
        this.devices = devices;
        this.realtimeDatabase = realtimeDatabase;
    }

    @NonNull
    @Override
    public AdminDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_device_card, parent, false);
        return new AdminDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDeviceViewHolder holder, int position) {
        Device device = devices.get(position);

        holder.deviceName.setText(device.getName() != null ? device.getName() : "Sin nombre");
        holder.userEmail.setText(device.getUserEmail() != null ? device.getUserEmail() : "Sin asignar");

        holder.deviceCO.setText("CO: " + (device.getCO() != null ? device.getCO() + " ppm" : "N/A"));
        holder.deviceCO2.setText("CO2: " + (device.getCO2() != null ? device.getCO2() + " ppm" : "N/A"));
        holder.devicePM25.setText("PM2.5: " + (device.getPM2_5() != null ? device.getPM2_5() + " µg/m³" : "N/A"));
        holder.devicePM10.setText("PM10: " + (device.getPM10() != null ? device.getPM10() + " µg/m³" : "N/A"));
        holder.deviceHumidity.setText("Humedad: " + (device.getHumedad() != null ? device.getHumedad() + " %" : "N/A"));

        if (device.getTemperatura() != null) {
            holder.deviceTemperature.setText(
                    "Temperatura: " +
                            (device.getTemperatura().get("celsius") != null ? device.getTemperatura().get("celsius") + " °C" : "N/A") +
                            " / " +
                            (device.getTemperatura().get("fahrenheit") != null ? device.getTemperatura().get("fahrenheit") + " °F" : "N/A")
            );
        } else {
            holder.deviceTemperature.setText("Temperatura: No disponible");
        }

        holder.deleteButton.setOnClickListener(v -> {
            String deviceId = device.getId();
            if (deviceId != null) {
                realtimeDatabase.child(deviceId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(holder.itemView.getContext(), "Dispositivo eliminado con éxito", Toast.LENGTH_SHORT).show();

                            // Verificar que el índice sea válido antes de eliminar
                            if (position >= 0 && position < devices.size()) {
                                devices.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, devices.size()); // Actualizar rangos
                            } else {
                                notifyDataSetChanged(); // Refrescar toda la lista si no es seguro
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Error al eliminar dispositivo", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class AdminDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName, userEmail, deviceCO, deviceCO2, devicePM25, devicePM10, deviceHumidity, deviceTemperature;
        Button deleteButton;

        public AdminDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            userEmail = itemView.findViewById(R.id.userEmail);
            deviceCO = itemView.findViewById(R.id.deviceCO);
            deviceCO2 = itemView.findViewById(R.id.deviceCO2);
            devicePM25 = itemView.findViewById(R.id.devicePM25);
            devicePM10 = itemView.findViewById(R.id.devicePM10);
            deviceHumidity = itemView.findViewById(R.id.deviceHumidity);
            deviceTemperature = itemView.findViewById(R.id.deviceTemperature);
            deleteButton = itemView.findViewById(R.id.deleteDeviceButton);
        }
    }
}
