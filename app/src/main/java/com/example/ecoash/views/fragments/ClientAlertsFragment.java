package com.example.ecoash.views.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button deleteAllAlertsButton;
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
        deleteAllAlertsButton = view.findViewById(R.id.deleteAllAlertsButton);

        alerts = new ArrayList<>();
        alertsAdapter = new AlertsAdapter(requireContext(), alerts);

        // Listener para eliminar alertas individuales
        alertsAdapter.setOnAlertDeleteListener(this::onDeleteAlertClicked);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(alertsAdapter);

        realtimeDatabase = FirebaseDatabase.getInstance().getReference("dispositivos");
        auth = FirebaseAuth.getInstance();

        deleteAllAlertsButton.setOnClickListener(v -> confirmDeleteAllAlerts());

        loadDeviceIdAndAlerts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al volver a la vista de alertas, si hay alertas, desplazamos al inicio.
        if (!alerts.isEmpty()) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void loadDeviceIdAndAlerts() {
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
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
                    deleteAllAlertsButton.setVisibility(View.GONE);
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

        // Agregamos directamente el ChildEventListener para mostrar todas las alertas
        alertsRef.addChildEventListener(childEventListener);

        progressBar.setVisibility(View.GONE);
        refreshEmptyViewAndButtonVisibility();
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
                    // Guardamos la key de la alerta para poder eliminarla
                    newAlert.setKey(snapshot.getKey());

                    // Insertar la alerta en orden descendente por fecha
                    int insertPosition = findInsertPosition(newAlert);
                    alerts.add(insertPosition, newAlert);
                    alertsAdapter.notifyItemInserted(insertPosition);

                    refreshEmptyViewAndButtonVisibility();

                    // Si el fragmento está visible y la alerta se insertó al inicio, desplazamos arriba
                    if (isVisible() && insertPosition == 0) {
                        recyclerView.smoothScrollToPosition(0);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al procesar la alerta: " + e.getMessage(), e);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            // Lógica opcional para alertas existentes modificadas
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            String keyRemoved = snapshot.getKey();
            if (keyRemoved != null) {
                int indexToRemove = findAlertIndexByKey(keyRemoved);
                if (indexToRemove != -1) {
                    alerts.remove(indexToRemove);
                    alertsAdapter.notifyItemRemoved(indexToRemove);
                    refreshEmptyViewAndButtonVisibility();
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            // No requerido actualmente
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "Error al escuchar alertas: " + error.getMessage());
        }
    };

    private int findAlertIndexByKey(String key) {
        for (int i = 0; i < alerts.size(); i++) {
            if (alerts.get(i).getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private int findInsertPosition(Alert newAlert) {
        Date newAlertDate = parseDate(newAlert.getFecha());
        if (newAlertDate == null) {
            // Si no se puede parsear la fecha, se coloca al final
            return alerts.size();
        }

        for (int i = 0; i < alerts.size(); i++) {
            Alert existingAlert = alerts.get(i);
            Date existingDate = parseDate(existingAlert.getFecha());
            if (existingDate != null) {
                // Queremos orden descendente
                if (newAlertDate.after(existingDate)) {
                    return i;
                }
            }
        }
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

    private void refreshEmptyViewAndButtonVisibility() {
        emptyView.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
        deleteAllAlertsButton.setVisibility(alerts.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void onDeleteAlertClicked(Alert alert, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar alerta")
                .setMessage("¿Desea eliminar esta alerta?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (alertsRef != null && alert.getKey() != null) {
                        alertsRef.child(alert.getKey()).removeValue().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Alerta eliminada", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al eliminar alerta", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void confirmDeleteAllAlerts() {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar todas las alertas")
                .setMessage("¿Está seguro de que desea eliminar todas las alertas?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (alertsRef != null) {
                        alertsRef.removeValue().addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Todas las alertas fueron eliminadas", Toast.LENGTH_SHORT).show();
                            alerts.clear();
                            alertsAdapter.notifyDataSetChanged();
                            refreshEmptyViewAndButtonVisibility();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al eliminar las alertas", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
