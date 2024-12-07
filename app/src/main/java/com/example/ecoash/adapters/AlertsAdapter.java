package com.example.ecoash.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.R;
import com.example.ecoash.models.Alert;

import java.util.List;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {

    public interface OnAlertDeleteListener {
        void onDeleteAlert(Alert alert, int position);
    }

    private Context context;
    private List<Alert> alerts;
    private OnAlertDeleteListener onAlertDeleteListener;

    public AlertsAdapter(Context context, List<Alert> alerts) {
        this.context = context;
        this.alerts = alerts;
    }

    public void setOnAlertDeleteListener(OnAlertDeleteListener listener) {
        this.onAlertDeleteListener = listener;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alert_card, parent, false);
        return new AlertViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert alert = alerts.get(position);
        holder.alertTitle.setText(alert.getTitulo());
        holder.alertMessage.setText(alert.getMensaje());
        holder.alertType.setText("Tipo: " + alert.getColor());
        holder.alertTimestamp.setText(alert.getFecha());

        // Ajustar color de fondo según el color de la alerta
        String color = alert.getColor();
        if (color != null) {
            switch (color.toLowerCase()) {
                case "rojo":
                    holder.alertBackground.setBackgroundColor(Color.RED);
                    break;
                case "azul":
                    holder.alertBackground.setBackgroundColor(Color.BLUE);
                    break;
                case "verde":
                    holder.alertBackground.setBackgroundColor(Color.GREEN);
                    break;
                default:
                    holder.alertBackground.setBackgroundColor(Color.DKGRAY);
                    break;
            }
        } else {
            holder.alertBackground.setBackgroundColor(Color.DKGRAY);
        }

        // Listener del botón X
        holder.deleteAlertButton.setOnClickListener(v -> {
            if (onAlertDeleteListener != null) {
                onAlertDeleteListener.onDeleteAlert(alert, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView alertTitle, alertMessage, alertType, alertTimestamp;
        ImageButton deleteAlertButton;
        LinearLayout alertBackground;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            alertTitle = itemView.findViewById(R.id.alertTitle);
            alertMessage = itemView.findViewById(R.id.alertMessage);
            alertType = itemView.findViewById(R.id.alertType);
            alertTimestamp = itemView.findViewById(R.id.alertTimestamp);
            deleteAlertButton = itemView.findViewById(R.id.deleteAlertButton);
            alertBackground = itemView.findViewById(R.id.alertBackground);
        }
    }
}
