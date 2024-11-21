package com.example.ecoash;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonalDataFragment extends Fragment {

    public PersonalDataFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_data, container, false);

        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtEmail = view.findViewById(R.id.txtEmail);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            txtName.setText("Nombre: " + currentUser.getDisplayName());
            txtEmail.setText("Correo: " + currentUser.getEmail());
        } else {
            txtName.setText("Nombre: No disponible");
            txtEmail.setText("Correo: No disponible");
        }

        // Funcionalidad del botón de cerrar sesión
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Cierra sesión en Firebase
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();

            // Redirige al usuario al LoginActivity
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
