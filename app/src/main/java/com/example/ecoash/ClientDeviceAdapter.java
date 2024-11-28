package com.example.ecoash;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ClientDeviceAdapter extends RecyclerView.Adapter<ClientDeviceAdapter.ClientDeviceViewHolder> {

    private final List<Device> devices;
    private final Context context;
    private final DatabaseReference databaseReference;

    public ClientDeviceAdapter(Context context, List<Device> devices) {
        this.context = context;
        this.devices = devices;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");
    }

    @NonNull
    @Override
    public ClientDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.client_view_device_card, parent, false);
        return new ClientDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientDeviceViewHolder holder, int position) {
        Device device = devices.get(position);

        // Configurar datos del dispositivo
        holder.deviceName.setText(device.getName() != null ? device.getName() : "Sin nombre");
        holder.deviceId.setText("ID: " + (device.getId() != null ? device.getId() : "N/A"));
        holder.deviceCO.setText("CO: " + device.getCO() + " ppm");
        holder.deviceCO2.setText("CO2: " + device.getCO2() + " ppm");
        holder.devicePM25.setText("PM2.5: " + device.getPM2_5() + " µg/m³");
        holder.devicePM10.setText("PM10: " + device.getPM10() + " µg/m³");
        holder.deviceHumidity.setText("Humedad: " + device.getHumedad() + " %");

        double celsius = device.getTemperatura();
        double fahrenheit = (celsius * 9 / 5) + 32;
        holder.deviceTemperature.setText("Temperatura: " + celsius + " °C / " + fahrenheit + " °F");

        // Botón para copiar ID al portapapeles
        holder.copyIdButton.setOnClickListener(v -> copyToClipboard(device.getId()));

        // Botón para editar nombre
        holder.editNameButton.setOnClickListener(v -> showEditNameDialog(device, position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
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

    private void showEditNameDialog(Device device, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar nombre del dispositivo");
        builder.setMessage("Ingrese un nuevo nombre para este dispositivo:");

        // Campo de entrada
        final EditText input = new EditText(context);
        input.setText(device.getName());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Configurar botones
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                device.setName(newName);

                // Actualizar Firebase
                databaseReference.child(device.getId()).child("name").setValue(newName)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error al actualizar nombre", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    static class ClientDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName, deviceId, deviceCO, deviceCO2, devicePM25, devicePM10, deviceHumidity, deviceTemperature;
        ImageButton editNameButton, copyIdButton;

        public ClientDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            deviceId = itemView.findViewById(R.id.deviceId);
            deviceCO = itemView.findViewById(R.id.deviceCO);
            deviceCO2 = itemView.findViewById(R.id.deviceCO2);
            devicePM25 = itemView.findViewById(R.id.devicePM25);
            devicePM10 = itemView.findViewById(R.id.devicePM10);
            deviceHumidity = itemView.findViewById(R.id.deviceHumidity);
            deviceTemperature = itemView.findViewById(R.id.deviceTemperature);
            editNameButton = itemView.findViewById(R.id.editNameButton);
            copyIdButton = itemView.findViewById(R.id.copyIdButton);
        }
    }
}
