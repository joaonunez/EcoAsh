package com.example.ecoash;

import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "ClientAlertsFragment";
    private RecyclerView alertsRecyclerView;
    private AlertAdapter alertAdapter;
    private List<Alerta> alertas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alerts_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alertsRecyclerView = view.findViewById(R.id.alertsRecyclerView);
        alertas = new ArrayList<>();

        alertAdapter = new AlertAdapter(alertas);
        alertsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alertsRecyclerView.setAdapter(alertAdapter);

        // Cargar alertas ya existentes
        cargarAlertas();
    }

    private void cargarAlertas() {
        Log.d(TAG, "Cargando alertas existentes...");
        DeviceRepository.getAlertsForCurrentDevice(alertas, updatedAlerts -> {
            alertas.clear();
            alertas.addAll(updatedAlerts);
            alertAdapter.notifyDataSetChanged();
            Log.d(TAG, "Alertas actualizadas en el RecyclerView.");
        });
    }
}
