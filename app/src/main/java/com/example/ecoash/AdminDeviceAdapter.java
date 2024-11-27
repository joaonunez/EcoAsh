package com.example.ecoash;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Device;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminDeviceAdapter extends RecyclerView.Adapter<AdminDeviceAdapter.DeviceViewHolder> {

    private static final String TAG = "AdminDeviceAdapter";
    private final List<Device> devices;
    private final OnDeleteListener onDeleteListener;
    private final DatabaseReference databaseReference;

    public interface OnDeleteListener {
        void onDelete(Device device);
    }

    public AdminDeviceAdapter(List<Device> devices, OnDeleteListener onDeleteListener) {
        this.devices = devices;
        this.onDeleteListener = onDeleteListener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_device_card, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);

        // Configurar los valores
        holder.deviceName.setText(device.getName() == null ? "Nombre desconocido" : device.getName());
        holder.userEmail.setText(device.getUserEmail() == null ? "Sin asignar" : device.getUserEmail());

        holder.deviceCO.setText(device.getCO() + " ppm");
        holder.deviceCO2.setText(device.getCO2() + " ppm");
        holder.devicePM25.setText(device.getPM2_5() + " µg/m³");
        holder.devicePM10.setText(device.getPM10() + " µg/m³");
        holder.deviceHumidity.setText(device.getHumedad() + " %");
        holder.deviceTemperature.setText(device.getTemperatura() + " °C / " +
                (device.getTemperatura() * 9 / 5 + 32) + " °F");

        // Listener para el botón de editar correo
        holder.editEmailButton.setOnClickListener(v -> showEditEmailDialog(holder.itemView.getContext(), device, position));

        // Listener para el botón de eliminar
        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "Eliminar dispositivo solicitado: " + device.getId());
            onDeleteListener.onDelete(device);
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    private void showEditEmailDialog(Context context, Device device, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar correo");
        builder.setMessage("Ingrese un nuevo correo para este dispositivo:");

        // Crear el campo de entrada
        final EditText input = new EditText(context);
        input.setText(device.getUserEmail());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Configurar botones
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newEmail = input.getText().toString().trim();
            if (!newEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                device.setUserEmail(newEmail);

                // Actualizar en Firebase
                databaseReference.child(device.getId()).child("userEmail").setValue(newEmail)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Correo actualizado", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error al actualizar correo", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(context, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void updateDevices(List<Device> newDevices) {
        Log.d(TAG, "Actualizando lista de dispositivos. Tamaño anterior: " + devices.size() + ", Nuevo tamaño: " + newDevices.size());
        devices.clear();
        devices.addAll(newDevices);
        notifyDataSetChanged();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName, userEmail, deviceCO, deviceCO2, devicePM25, devicePM10, deviceHumidity, deviceTemperature;
        ImageButton editEmailButton;
        Button deleteButton;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            deviceName = itemView.findViewById(R.id.deviceName);
            userEmail = itemView.findViewById(R.id.userEmail);
            deviceCO = itemView.findViewById(R.id.deviceCO);
            deviceCO2 = itemView.findViewById(R.id.deviceCO2);
            devicePM25 = itemView.findViewById(R.id.devicePM25);
            devicePM10 = itemView.findViewById(R.id.devicePM10);
            deviceHumidity = itemView.findViewById(R.id.deviceHumidity);
            deviceTemperature = itemView.findViewById(R.id.deviceTemperature);
            editEmailButton = itemView.findViewById(R.id.editEmailButton);
            deleteButton = itemView.findViewById(R.id.deleteDeviceButton);
        }
    }
}
