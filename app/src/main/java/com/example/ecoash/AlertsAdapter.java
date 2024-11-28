package com.example.ecoash;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Alert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {
    private final Context context;
    private final List<Alert> alerts;
    private final long recentThreshold = 60000; // Considera las alertas recientes en 1 minuto

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

        // Asignar valores a los TextViews
        holder.title.setText(alert.getTitulo() != null ? alert.getTitulo() : "Sin título");
        holder.message.setText(alert.getMensaje() != null ? alert.getMensaje() : "Sin mensaje");
        holder.date.setText(alert.getFecha() != null ? alert.getFecha() : "Fecha desconocida");

        // Determinar el tipo y color de alerta
        String alertType = alert.getColor() != null ? alert.getColor().toLowerCase() : "desconocido";
        String alertDescription;

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

        holder.alertType.setText(alertDescription);

        // Aplicar animación de parpadeo si la alerta es reciente
        if (isRecentAlert(alert.getFecha())) {
            startBlinkAnimation(holder.itemView);
        } else {
            holder.itemView.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    private boolean isRecentAlert(String alertDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date alertTime = dateFormat.parse(alertDate);
            long currentTime = System.currentTimeMillis();
            return alertTime != null && currentTime - alertTime.getTime() <= recentThreshold;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startBlinkAnimation(View view) {
        AlphaAnimation blinkAnimation = new AlphaAnimation(1.0f, 0.3f); // Parpadeo de opacidad
        blinkAnimation.setDuration(500); // Duración de cada parpadeo
        blinkAnimation.setRepeatCount(20); // Parpadeo durante 10 segundos
        blinkAnimation.setRepeatMode(AlphaAnimation.REVERSE);
        view.startAnimation(blinkAnimation);

        // Detener animación después de 10 segundos
        new Handler().postDelayed(view::clearAnimation, 10000);
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
