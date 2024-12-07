package com.example.ecoash.views.fragments;

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

import com.example.ecoash.R;
import com.example.ecoash.adapters.AlertsAdapter;
import com.example.ecoash.models.Alert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private DatabaseReference alertsRef; // Referencia a las alertas del dispositivo actual

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

        // Obtenemos el deviceId una sola vez
        realtimeDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
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
                    emptyView.setVisibility(View.VISIBLE);
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
        alertsRef = realtimeDatabase.child(deviceId).child("alertas");

        // Removemos cualquier listener previo
        alertsRef.removeEventListener(childEventListener);

        // Agregamos un ChildEventListener para manejar alertas una a una
        alertsRef.addChildEventListener(childEventListener);

        // Como acabamos de agregar el listener, asumimos que no sabemos si hay alertas.
        // Podríamos hacer una consulta inicial para saber si mostrar o no la vista vacía,
        // pero en cuanto aparezca la primera alerta, se actualizará.
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            try {
                HashMap<String, String> alertMap = (HashMap<String, String>) snapshot.getValue();
                if (alertMap != null) {
                    Alert newAlert = new Alert(
                            alertMap.get("fecha"),
                            alertMap.get("mensaje"),
                            alertMap.get("titulo"),
                            alertMap.get("color")
                    );

                    // Insertar la nueva alerta en orden descendente por fecha
                    int insertPosition = findInsertPosition(newAlert);
                    alerts.add(insertPosition, newAlert);
                    alertsAdapter.notifyItemInserted(insertPosition);

                    emptyView.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al procesar la alerta: " + e.getMessage(), e);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            // Si las alertas se modifican, podríamos manejarlo aquí
            // En este caso, asumimos que las alertas no se modifican, solo se agregan.
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            // Manejo si se elimina una alerta (no es obligatorio si no se necesita)
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            // No se requiere por ahora.
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "Error al escuchar alertas: " + error.getMessage());
        }
    };

    /**
     * Encuentra la posición donde insertar la nueva alerta para mantener el orden por fecha (descendente).
     * Las alertas más recientes van al principio.
     */
    private int findInsertPosition(Alert newAlert) {
        Date newAlertDate = parseDate(newAlert.getFecha());
        if (newAlertDate == null) {
            // Si no se puede parsear la fecha, la ponemos al final
            return alerts.size();
        }

        for (int i = 0; i < alerts.size(); i++) {
            Alert existingAlert = alerts.get(i);
            Date existingDate = parseDate(existingAlert.getFecha());
            if (existingDate != null) {
                // Queremos orden descendente, así que si la nueva es más reciente, va antes
                if (newAlertDate.after(existingDate)) {
                    return i;
                }
            }
        }

        // Si no es más reciente que ninguna, va al final
        return alerts.size();
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }
}
