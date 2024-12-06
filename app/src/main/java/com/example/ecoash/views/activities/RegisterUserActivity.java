package com.example.ecoash.views.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoash.R;
import com.example.ecoash.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class RegisterUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText txtName, txtEmail, txtPassword, txtAddress, txtBirthday;
    private RadioGroup radioGroupGender;
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
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        // Configurar selector de fecha
        txtBirthday.setOnClickListener(v -> showDatePicker());

        // Botón de registro
        btnRegister.setOnClickListener(v -> {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String address = txtAddress.getText().toString().trim();
            String birthday = txtBirthday.getText().toString().trim();

            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            String gender;

            if (selectedGenderId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedGenderId);
                gender = selectedRadioButton.getText().toString();
            } else {
                Toast.makeText(this, "Seleccione un género", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validación de campos
            if (name.isEmpty()) {
                Toast.makeText(this, "El campo nombre está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "El campo correo está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "El campo contraseña está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (address.isEmpty()) {
                Toast.makeText(this, "El campo dirección está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (birthday.isEmpty()) {
                Toast.makeText(this, "El campo fecha de nacimiento está vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            // Registro del usuario
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

                            // Crear instancia de User
                            User newUser = new User(
                                    currentUser.getUid(),
                                    name,
                                    email,
                                    address,
                                    gender,
                                    birthday,
                                    "cliente", // Rol por defecto
                                    null // Sin foto de perfil inicialmente
                            );

                            // Guardar datos del usuario en Firestore
                            db.collection("users").document(currentUser.getUid()).set(newUser)
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
