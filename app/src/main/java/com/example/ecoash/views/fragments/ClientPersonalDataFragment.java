package com.example.ecoash.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecoash.R;
import com.example.ecoash.views.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClientPersonalDataFragment extends Fragment {

    public ClientPersonalDataFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_personal_data, container, false);

        // Referencias a los elementos
        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtEmail = view.findViewById(R.id.txtEmail);
        TextView txtAddress = view.findViewById(R.id.txtAddress);
        TextView txtBirthday = view.findViewById(R.id.txtBirthday);
        TextView txtGender = view.findViewById(R.id.txtGender);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Cargar datos del usuario
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        txtName.setText("Nombre: " + documentSnapshot.getString("name"));
                        txtEmail.setText("Correo: " + documentSnapshot.getString("email"));
                        txtAddress.setText("Dirección: " + documentSnapshot.getString("address"));
                        txtBirthday.setText("Cumpleaños: " + documentSnapshot.getString("birthday"));
                        txtGender.setText("Género: " + documentSnapshot.getString("gender"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show());

        // Funcionalidad del botón de cerrar sesión
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
