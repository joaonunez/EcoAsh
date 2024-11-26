package com.example.ecoash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Alerta;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private final List<Alerta> alertas;

    public AlertAdapter(List<Alerta> alertas) {
        this.alertas = alertas;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alert_item_card, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alerta alerta = alertas.get(position);
        holder.tituloTextView.setText(alerta.getTitulo());
        holder.mensajeTextView.setText(alerta.getMensaje());
    }

    @Override
    public int getItemCount() {
        return alertas.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView, mensajeTextView;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.alertTitleTextView);
            mensajeTextView = itemView.findViewById(R.id.alertMessageTextView);
        }
    }
}
