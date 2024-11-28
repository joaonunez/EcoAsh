package com.example.ecoash;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

        // Tipo de alerta
        String alertType = alert.getColor() != null ? alert.getColor().toLowerCase() : "desconocido";
        String alertDescription;

        // Asignar descripción y colores basados en el nivel
        switch (alertType) {
            case "rojo":
                alertDescription = "Nivel Crítico";
                holder.alertBackground.setBackgroundColor(Color.parseColor("#FFEBEE")); // Fondo rojo claro
                holder.alertCard.setBackgroundColor(Color.parseColor("#FFCDD2")); // Borde rojo claro
                break;
            case "azul":
                alertDescription = "Nivel Bajo";
                holder.alertBackground.setBackgroundColor(Color.parseColor("#E3F2FD")); // Fondo azul claro
                holder.alertCard.setBackgroundColor(Color.parseColor("#BBDEFB")); // Borde azul claro
                break;
            case "verde":
                alertDescription = "Nivel Estable";
                holder.alertBackground.setBackgroundColor(Color.parseColor("#E8F5E9")); // Fondo verde claro
                holder.alertCard.setBackgroundColor(Color.parseColor("#C8E6C9")); // Borde verde claro
                break;
            default:
                alertDescription = "Nivel Desconocido";
                holder.alertBackground.setBackgroundColor(Color.parseColor("#F5F5F5")); // Fondo gris claro
                holder.alertCard.setBackgroundColor(Color.parseColor("#EEEEEE")); // Borde gris claro
                break;
        }

        // Actualizar texto con descripción del nivel
        holder.alertType.setText(alertDescription);
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, date, alertType;
        LinearLayout alertBackground, alertCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.alertTitle);
            message = itemView.findViewById(R.id.alertMessage);
            date = itemView.findViewById(R.id.alertTimestamp);
            alertType = itemView.findViewById(R.id.alertType);
            alertBackground = itemView.findViewById(R.id.alertBackground);
            alertCard = itemView.findViewById(R.id.alertCard);
        }
    }
}
