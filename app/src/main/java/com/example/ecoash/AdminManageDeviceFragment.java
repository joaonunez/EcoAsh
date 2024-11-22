package com.example.ecoash;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminManageDeviceFragment extends Fragment {

    private FirebaseFirestore firestore;
    private LinearLayout devicesContainer;
    private EditText searchInput;

    private List<DocumentSnapshot> allDevices; // Lista para almacenar todos los dispositivos

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_device, container, false);

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance();

        // Referenciar contenedor donde se renderizan las tarjetas
        devicesContainer = view.findViewById(R.id.devicesContainer);
        searchInput = view.findViewById(R.id.searchInput); // Referenciar el campo de búsqueda

        // Configurar escucha para el filtro de búsqueda
        setupSearchListener();

        // Cargar dispositivos
        loadDevices();

        return view;
    }

    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDevices(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita implementar
            }
        });
    }

    private void loadDevices() {
        firestore.collection("dispositivos").get()
                .addOnSuccessListener(querySnapshot -> {
                    allDevices = new ArrayList<>(querySnapshot.getDocuments()); // Guardar todos los dispositivos
                    devicesContainer.removeAllViews(); // Limpiar vistas previas

                    // Mostrar todos los dispositivos al inicio
                    filterDevices("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar dispositivos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterDevices(String query) {
        devicesContainer.removeAllViews(); // Limpiar las tarjetas actuales

        for (DocumentSnapshot document : allDevices) {
            String deviceId = document.getId();
            String deviceName = document.getString("name");
            String userEmail = document.getString("userEmail");

            // Filtrar dispositivos cuyo correo coincida parcialmente con la búsqueda
            if (userEmail != null && userEmail.toLowerCase().contains(query.toLowerCase())) {
                addDeviceCard(deviceId, deviceName, userEmail);
            } else if (userEmail == null || userEmail.equalsIgnoreCase("Sin asignar")) {
                addDeviceCard(deviceId, deviceName, "Sin asignar");
            }
        }
    }

    private void addDeviceCard(String deviceId, String deviceName, String userEmail) {
        // Crear un CardView programáticamente
        CardView cardView = new CardView(getContext());
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardView.setCardElevation(8);
        cardView.setRadius(16);
        cardView.setUseCompatPadding(true);

        // Crear un LinearLayout para el contenido del CardView
        LinearLayout cardContent = new LinearLayout(getContext());
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(24, 24, 24, 24);

        // Crear vistas para los datos del dispositivo
        TextView deviceNameView = new TextView(getContext());
        deviceNameView.setText("Dispositivo: " + deviceName);
        deviceNameView.setTextSize(18);
        deviceNameView.setPadding(0, 0, 0, 8);

        TextView deviceIdView = new TextView(getContext());
        deviceIdView.setText("ID: " + deviceId);
        deviceIdView.setPadding(0, 0, 0, 8);

        TextView userEmailView = new TextView(getContext());
        userEmailView.setText("Correo: " + userEmail);

        // Agregar las vistas al contenido del CardView
        cardContent.addView(deviceNameView);
        cardContent.addView(deviceIdView);
        cardContent.addView(userEmailView);

        // Agregar el contenido al CardView
        cardView.addView(cardContent);

        // Agregar el CardView al contenedor principal
        devicesContainer.addView(cardView);
    }
}
