package com.example.seguimientoderutas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class IniciarSesion extends AppCompatActivity {

    private EditText editTextTextEmailAddress, editTextNumberPassword;
    private Button btnIniciarSesion, btnRegistrar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        // Inicializar vistas
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);  // Ahora es correo en lugar de nombre
        editTextNumberPassword = findViewById(R.id.editTextNumberPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();

        // Configurar el evento de inicio de sesión
        btnIniciarSesion.setOnClickListener(v -> {
            String correo = editTextTextEmailAddress.getText().toString().trim();
            String password = editTextNumberPassword.getText().toString().trim();

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(IniciarSesion.this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Iniciar sesión con correo y contraseña
            mAuth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(IniciarSesion.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(IniciarSesion.this, RutaActivity.class));
                            finish();
                        } else {
                            Toast.makeText(IniciarSesion.this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Redirigir a la pantalla de registro
        btnRegistrar.setOnClickListener(v -> startActivity(new Intent(IniciarSesion.this, RegistrarNuevo.class)));
    }
}
