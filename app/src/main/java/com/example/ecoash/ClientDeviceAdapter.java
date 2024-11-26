package com.example.ecoash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Device;

import java.util.List;

public class ClientDeviceAdapter extends RecyclerView.Adapter<ClientDeviceAdapter.ClientDeviceViewHolder> {

    private List<Device> devices;

    public ClientDeviceAdapter(List<Device> devices) {
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
        Device device = devices.get(position);

        holder.deviceName.setText(device.getName() != null ? device.getName() : "Sin nombre");
        holder.deviceId.setText("ID: " + (device.getId() != null ? device.getId() : "N/A"));
        holder.deviceCO.setText("CO: " + (device.getCO() != null ? device.getCO() + " ppm" : "N/A"));
        holder.deviceCO2.setText("CO2: " + (device.getCO2() != null ? device.getCO2() + " ppm" : "N/A"));
        holder.devicePM25.setText("PM2.5: " + (device.getPM2_5() != null ? device.getPM2_5() + " µg/m³" : "N/A"));
        holder.devicePM10.setText("PM10: " + (device.getPM10() != null ? device.getPM10() + " µg/m³" : "N/A"));
        holder.deviceHumidity.setText("Humedad: " + (device.getHumedad() != null ? device.getHumedad() + " %" : "N/A"));

        // Conversión dinámica de Celsius a Fahrenheit
        if (device.getTemperatura() != null) {
            double celsius = device.getTemperatura();
            double fahrenheit = (celsius * 9 / 5) + 32;
            holder.deviceTemperature.setText("Temperatura: " + celsius + " °C / " + fahrenheit + " °F");
        } else {
            holder.deviceTemperature.setText("Temperatura: No disponible");
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ClientDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceId, deviceName, deviceCO, deviceCO2, devicePM25, devicePM10, deviceHumidity, deviceTemperature;

        public ClientDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceId = itemView.findViewById(R.id.deviceId);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceCO = itemView.findViewById(R.id.deviceCO);
            deviceCO2 = itemView.findViewById(R.id.deviceCO2);
            devicePM25 = itemView.findViewById(R.id.devicePM25);
            devicePM10 = itemView.findViewById(R.id.devicePM10);
            deviceHumidity = itemView.findViewById(R.id.deviceHumidity);
            deviceTemperature = itemView.findViewById(R.id.deviceTemperature);
        }
    }
}
