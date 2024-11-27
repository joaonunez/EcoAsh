package com.example.ecoash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Alerta;

import java.util.ArrayList;
import java.util.List;

public class ClientAlertsFragment extends Fragment {

    private RecyclerView alertsRecyclerView;
    private AlertAdapter alertAdapter;
    private List<Alerta> alertas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        alertas = new ArrayList<>();

        // Inicializar RecyclerView
        alertAdapter = new AlertAdapter(alertas);
        alertsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alertsRecyclerView.setAdapter(alertAdapter);

        // Simular alertas para probar
        cargarAlertas();
    }

    private void cargarAlertas() {
        // Aquí puedes usar la API de ChatGPT para generar alertas.
        if (alertas.isEmpty()) {
            alertas.add(new Alerta("Sin alertas", "No hay alertas activas por ahora."));
        } else {
            alertas.add(new Alerta("Alerta Crítica", "La temperatura ha superado el límite seguro."));
        }
        alertAdapter.notifyDataSetChanged();
    }
}
