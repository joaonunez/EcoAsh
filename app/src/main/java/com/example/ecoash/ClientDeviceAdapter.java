package com.example.ecoash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class ClientDeviceAdapter extends RecyclerView.Adapter<ClientDeviceAdapter.ClientDeviceViewHolder> {

    private List<HashMap<String, Object>> devices;

    public ClientDeviceAdapter(List<HashMap<String, Object>> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public ClientDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_view_device_card, parent, false);
        return new ClientDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientDeviceViewHolder holder, int position) {
        HashMap<String, Object> device = devices.get(position);

        // Configurar el nombre como título (centrado y sin prefijo)
        holder.deviceName.setText((String) device.getOrDefault("name", "Sin nombre"));

        // Configurar el ID del dispositivo
        holder.deviceId.setText("ID: " + device.get("id"));

        // Configurar los demás campos
        holder.deviceCO.setText("CO: " + device.getOrDefault("CO", "N/A") + " ppm");
        holder.deviceCO2.setText("CO2: " + device.getOrDefault("CO2", "N/A") + " ppm");
        holder.deviceMonoxide.setText("Monóxido de Carbono: " + device.getOrDefault("monoxido_carbono", "N/A") + " ppm");
        holder.devicePM25.setText("PM2.5: " + device.getOrDefault("PM2_5", "N/A") + " µg/m³");
        holder.devicePM10.setText("PM10: " + device.getOrDefault("PM10", "N/A") + " µg/m³");
        holder.deviceHumidity.setText("Humedad: " + device.getOrDefault("humedad", "N/A") + " %");

        // Configurar la temperatura (mostrando tanto Celsius como Fahrenheit si están disponibles)
        HashMap<String, Object> temperature = (HashMap<String, Object>) device.get("temperatura");
        if (temperature != null) {
            holder.deviceTemperature.setText(
                    "Temperatura: " + temperature.getOrDefault("celsius", "N/A") + " °C / " +
                            temperature.getOrDefault("fahrenheit", "N/A") + " °F"
            );
        } else {
            holder.deviceTemperature.setText("Temperatura: No disponible");
        }
    }


    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ClientDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceId, deviceName, deviceCO, deviceCO2, deviceMonoxide, devicePM25, devicePM10, deviceHumidity, deviceTemperature;

        public ClientDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceId = itemView.findViewById(R.id.deviceId);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceCO = itemView.findViewById(R.id.deviceCO);
            deviceCO2 = itemView.findViewById(R.id.deviceCO2);
            deviceMonoxide = itemView.findViewById(R.id.deviceMonoxide); // Nueva referencia
            devicePM25 = itemView.findViewById(R.id.devicePM25);
            devicePM10 = itemView.findViewById(R.id.devicePM10);
            deviceHumidity = itemView.findViewById(R.id.deviceHumidity);
            deviceTemperature = itemView.findViewById(R.id.deviceTemperature);
        }
    }

}
