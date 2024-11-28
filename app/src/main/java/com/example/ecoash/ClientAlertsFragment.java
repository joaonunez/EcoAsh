package com.example.ecoash;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoash.device.Alert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ClientAlertsFragment extends Fragment {

    private static final String TAG = "ClientAlertsFragment";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private AlertsAdapter alertsAdapter;
    private DatabaseReference realtimeDatabase;
    private List<Alert> alerts;

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts_view, container, false);

        recyclerView = view.findViewById(R.id.alertsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        alerts = new ArrayList<>();
        alertsAdapter = new AlertsAdapter(requireContext(), alerts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(alertsAdapter);

        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");
        auth = FirebaseAuth.getInstance();

        loadDeviceIdAndAlerts();

        return view;
    }

    private void loadDeviceIdAndAlerts() {
        String userEmail = auth.getCurrentUser().getEmail();
        if (userEmail == null) {
            Log.e(TAG, "Usuario no logueado o email no disponible");
            Toast.makeText(getContext(), "Error: No se puede obtener el email del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        realtimeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String deviceId = null;

                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    String email = deviceSnapshot.child("userEmail").getValue(String.class);
                    if (email != null && email.equals(userEmail)) {
                        deviceId = deviceSnapshot.getKey();
                        break;
                    }
                }

                if (deviceId != null) {
                    loadAlerts(deviceId);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No se encontraron dispositivos asociados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al buscar dispositivo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAlerts(String deviceId) {
        realtimeDatabase.child(deviceId).child("alertas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alerts.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot alertSnapshot : snapshot.getChildren()) {
                        try {
                            HashMap<String, String> alertMap = (HashMap<String, String>) alertSnapshot.getValue();
                            if (alertMap != null) {
                                Alert alert = new Alert(
                                        alertMap.get("fecha"),
                                        alertMap.get("mensaje"),
                                        alertMap.get("titulo"),
                                        alertMap.get("color")
                                );
                                alerts.add(alert);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error al procesar la alerta: " + e.getMessage(), e);
                        }
                    }

                    alerts.sort((alert1, alert2) -> {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date date1 = dateFormat.parse(alert1.getFecha());
                            Date date2 = dateFormat.parse(alert2.getFecha());
                            return date2.compareTo(date1); // Orden descendente
                        } catch (Exception e) {
                            return 0;
                        }
                    });
                }

                progressBar.setVisibility(View.GONE);
                alertsAdapter.notifyDataSetChanged();
                emptyView.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar alertas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
