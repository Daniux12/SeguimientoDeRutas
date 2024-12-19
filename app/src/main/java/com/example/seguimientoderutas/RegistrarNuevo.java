package com.example.seguimientoderutas;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegistrarNuevo extends AppCompatActivity {

    private EditText editTextNombre, editTextEmail, editTextPassword;
    private Button btnRegistrarse;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_nuevo);

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Funcionalidad para registrar al usuario
        btnRegistrarse.setOnClickListener(v -> {
            String nombre = editTextNombre.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validación de los campos
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegistrarNuevo.this, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Agregar un Log para depuración
            Log.d("RegistrarNuevo", "Intentando registrar con correo: " + email);

            // Registrar al usuario en Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Si la autenticación es exitosa, redirigir al usuario al inicio de sesión
                            Toast.makeText(RegistrarNuevo.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                            finish();  // Cerrar esta actividad y volver al login
                        } else {
                            // Si ocurre un error al registrar
                            Log.e("RegistrarNuevo", "Error al registrar el usuario", task.getException());
                            Toast.makeText(RegistrarNuevo.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
