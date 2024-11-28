package com.example.ecoash;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txtName, txtEmail, txtPassword, txtAddress, txtBirthday;
    private Spinner spinnerGender;
    private Button btnRegister, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        // Referencias a los campos
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtAddress = findViewById(R.id.txtAddress);
        txtBirthday = findViewById(R.id.txtBirthday);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        // Configurar opciones de género
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Configurar selector de fecha
        txtBirthday.setOnClickListener(v -> showDatePicker());

        // Botón de registro
        btnRegister.setOnClickListener(v -> {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String address = txtAddress.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();
            String birthday = txtBirthday.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || birthday.isEmpty()) {
                Toast.makeText(RegisterUserActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(name, email, password, address, gender, birthday);
        });

        // Botón de volver
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            txtBirthday.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void registerUser(String name, String email, String password, String address, String gender, String birthday) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Crear datos del usuario con el rol por defecto "cliente"
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("email", email);
                            userData.put("address", address);
                            userData.put("gender", gender);
                            userData.put("birthday", birthday);
                            userData.put("role", "cliente"); // Rol por defecto

                            db.collection("users").document(currentUser.getUid()).set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterUserActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterUserActivity.this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterUserActivity.this, "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
