package com.example.ecoash.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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

import com.example.ecoash.R;
import com.example.ecoash.models.Device;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private static final String TAG = "DeviceAdapter";
    private final List<Device> devices;
    private final boolean isAdmin;
    private final OnDeleteListener onDeleteListener;
    private final DatabaseReference databaseReference;
    private Context context;

    // Interfaz heredada del AdminDeviceAdapter, no se elimina ninguna funcionalidad
    public interface OnDeleteListener {
        void onDelete(Device device);
    }

    public DeviceAdapter(Context context, List<Device> devices, boolean isAdmin, OnDeleteListener onDeleteListener) {
        this.context = context;
        this.devices = devices;
        this.isAdmin = isAdmin;
        this.onDeleteListener = onDeleteListener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = isAdmin ? R.layout.admin_device_card : R.layout.client_view_device_card;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        // Actualizamos el contexto con el del parent por seguridad
        this.context = parent.getContext();
        return new DeviceViewHolder(view, isAdmin);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);

        // Configuración común
        String name = (device.getName() != null) ? device.getName() : "Sin nombre";
        holder.deviceName.setText(name);

        double celsius = device.getTemperatura();
        double fahrenheit = (celsius * 9 / 5) + 32;
        if (holder.deviceTemperature != null) {
            holder.deviceTemperature.setText(celsius + " °C / " + fahrenheit + " °F");
        }

        if (holder.deviceCO != null) holder.deviceCO.setText(device.getCO() + " ppm");
        if (holder.deviceCO2 != null) holder.deviceCO2.setText(device.getCO2() + " ppm");
        if (holder.devicePM25 != null) holder.devicePM25.setText(device.getPM2_5() + " µg/m³");
        if (holder.devicePM10 != null) holder.devicePM10.setText(device.getPM10() + " µg/m³");
        if (holder.deviceHumidity != null) holder.deviceHumidity.setText(device.getHumedad() + " %");

        // Modo Admin
        if (isAdmin) {
            if (holder.userEmail != null) {
                holder.userEmail.setText(device.getUserEmail() == null ? "Sin asignar" : device.getUserEmail());
            }

            if (holder.editEmailButton != null) {
                holder.editEmailButton.setOnClickListener(v -> showEditEmailDialog(context, device, position));
            }

            if (holder.deleteButton != null) {
                holder.deleteButton.setOnClickListener(v -> {
                    Log.d(TAG, "Eliminar dispositivo solicitado: " + device.getId());
                    if (onDeleteListener != null) {
                        onDeleteListener.onDelete(device);
                    }
                });
            }

            // Eliminamos la llamada a updateLastValue para evitar sobrescribir lastValues en onBindViewHolder

        } else {
            // Modo Cliente
            if (holder.deviceId != null) {
                holder.deviceId.setText("ID: " + (device.getId() != null ? device.getId() : "N/A"));
            }

            if (holder.copyIdButton != null) {
                holder.copyIdButton.setOnClickListener(v -> copyToClipboard(device.getId()));
            }

            if (holder.editNameButton != null) {
                holder.editNameButton.setOnClickListener(v -> showEditNameDialog(device, position));
            }

            // Eliminamos la llamada a updateLastValue para evitar sobrescribir lastValues en onBindViewHolder
        }

        // Escuchar cambios en métricas
        listenForMetricChanges(device.getId(), "CO");
        listenForMetricChanges(device.getId(), "CO2");
        listenForMetricChanges(device.getId(), "PM10");
        listenForMetricChanges(device.getId(), "PM2_5");
        listenForMetricChanges(device.getId(), "humedad");
        listenForMetricChanges(device.getId(), "temperatura");
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    private void showEditEmailDialog(Context context, Device device, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar correo");
        builder.setMessage("Ingrese un nuevo correo para este dispositivo:");

        final EditText input = new EditText(context);
        input.setText(device.getUserEmail());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newEmail = input.getText().toString().trim();
            if (!newEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                device.setUserEmail(newEmail);
                databaseReference.child(device.getId()).child("userEmail").setValue(newEmail)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Correo actualizado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Error al actualizar correo", Toast.LENGTH_SHORT).show());
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEditNameDialog(Device device, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar nombre del dispositivo");
        builder.setMessage("Ingrese un nuevo nombre para este dispositivo:");

        final EditText input = new EditText(context);
        input.setText(device.getName());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                device.setName(newName);
                databaseReference.child(device.getId()).child("name").setValue(newName)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Error al actualizar nombre", Toast.LENGTH_SHORT).show());
                notifyItemChanged(position);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) {
            Toast.makeText(context, "ID no disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Device ID", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "ID copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }

    public void updateDevices(List<Device> newDevices) {
        Log.d(TAG, "Actualizando lista de dispositivos. Tamaño anterior: " + devices.size() + ", Nuevo tamaño: " + newDevices.size());
        devices.clear();
        devices.addAll(newDevices);
        notifyDataSetChanged();
    }

    /**
     * Escucha cambios en una métrica específica del dispositivo.
     * Actualiza lastValues en Firebase solo si hay un cambio real en la métrica.
     *
     * @param deviceId ID del dispositivo
     * @param metric   Nombre de la métrica a escuchar
     */
    private void listenForMetricChanges(String deviceId, String metric) {
        DatabaseReference metricRef = databaseReference.child(deviceId).child(metric);
        DatabaseReference lastValueRef = databaseReference.child(deviceId).child("lastValues").child(metric);

        metricRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double newValueDouble = snapshot.getValue(Double.class);
                if (newValueDouble != null) {
                    int newValue = newValueDouble.intValue();

                    int position = findDevicePositionById(deviceId);
                    if (position != -1) {
                        Device device = devices.get(position);

                        Object oldValueObj = device.getMetricValue(metric);
                        if (oldValueObj != null && oldValueObj instanceof Integer) {
                            int oldValue = (Integer) oldValueObj;

                            if (newValue != oldValue) {
                                // Actualizar lastValues en Firebase con el valor anterior
                                lastValueRef.setValue(oldValue)
                                        .addOnSuccessListener(aVoid -> {
                                            // Actualizar el valor oficial en el objeto local
                                            device.setMetricValue(metric, newValue);
                                            notifyItemChanged(position);
                                            Log.d(TAG, "lastValues actualizados correctamente para " + metric + " en dispositivo " + deviceId);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error al actualizar lastValues para " + metric + " en dispositivo " + deviceId + ": " + e.getMessage());
                                        });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al escuchar cambios en " + metric + ": " + error.getMessage());
            }
        });
    }

    /**
     * Encuentra la posición del dispositivo en la lista por su ID.
     *
     * @param deviceId ID del dispositivo
     * @return Posición en la lista o -1 si no se encuentra
     */
    private int findDevicePositionById(String deviceId) {
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getId().equals(deviceId)) {
                return i;
            }
        }
        return -1;
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName, userEmail, deviceId, deviceCO, deviceCO2, devicePM25, devicePM10, deviceHumidity, deviceTemperature;
        ImageButton editEmailButton, copyIdButton, editNameButton;
        Button deleteButton;

        public DeviceViewHolder(@NonNull View itemView, boolean isAdmin) {
            super(itemView);
            // Campos comunes
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceCO = itemView.findViewById(R.id.deviceCO);
            deviceCO2 = itemView.findViewById(R.id.deviceCO2);
            devicePM25 = itemView.findViewById(R.id.devicePM25);
            devicePM10 = itemView.findViewById(R.id.devicePM10);
            deviceHumidity = itemView.findViewById(R.id.deviceHumidity);
            deviceTemperature = itemView.findViewById(R.id.deviceTemperature);

            if (isAdmin) {
                // admin_device_card.xml
                userEmail = itemView.findViewById(R.id.userEmail);
                editEmailButton = itemView.findViewById(R.id.editEmailButton);
                deleteButton = itemView.findViewById(R.id.deleteDeviceButton);

                deviceId = null;
                copyIdButton = null;
                editNameButton = null;
            } else {
                // client_view_device_card.xml
                deviceId = itemView.findViewById(R.id.deviceId);
                editNameButton = itemView.findViewById(R.id.editNameButton);
                copyIdButton = itemView.findViewById(R.id.copyIdButton);

                userEmail = null;
                editEmailButton = null;
                deleteButton = null;
            }
        }
    }
}
