package com.example.ecoash;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Alert;

import java.util.List;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {
    private final Context context;
    private final List<Alert> alerts;

    public AlertsAdapter(Context context, List<Alert> alerts) {
        this.context = context;
        this.alerts = alerts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alert alert = alerts.get(position);

        // Validar los campos antes de asignarlos
        String title = alert.getTitulo() != null ? alert.getTitulo() : "Sin título";
        String message = alert.getMensaje() != null ? alert.getMensaje() : "Sin mensaje";
        String date = alert.getFecha() != null ? alert.getFecha() : "Fecha desconocida";

        holder.title.setText(title);
        holder.message.setText(message);
        holder.date.setText(date);

        // Aplicar color basado en el campo "color"
        if ("rojo".equalsIgnoreCase(alert.getColor())) {
            holder.title.setTextColor(Color.RED);
            holder.message.setTextColor(Color.RED);
        } else if ("azul".equalsIgnoreCase(alert.getColor())) {
            holder.title.setTextColor(Color.BLUE);
            holder.message.setTextColor(Color.BLUE);
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.message.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.alertTitle);
            message = itemView.findViewById(R.id.alertMessage);
            date = itemView.findViewById(R.id.alertTimestamp);
        }
    }
}
