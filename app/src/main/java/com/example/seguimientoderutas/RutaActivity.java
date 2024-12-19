package com.example.seguimientoderutas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(); // Executor para tareas en segundo plano

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference mDatabase;
    private Button btnIniciarRuta, btnTerminarRuta, btnVerHistorial;
    private boolean isRecording = false;
    private String routeId = ""; // ID de la ruta
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);

        Log.d("RutaActivity", "onCreate: Actividad creada");

        try {
            // Inicializar Firebase y Location Services
            mAuth = FirebaseAuth.getInstance();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mDatabase = FirebaseDatabase.getInstance().getReference("rutas");

            // Inicializar mapa
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            // Inicializar los botones
            btnIniciarRuta = findViewById(R.id.btnIniciarRuta);
            btnTerminarRuta = findViewById(R.id.btnTerminarRuta);
            btnVerHistorial = findViewById(R.id.btnVerHistorial);

            // Establecer OnClickListener para iniciar la grabación
            btnIniciarRuta.setOnClickListener(v -> startRecordingRoute());

            // Establecer OnClickListener para terminar la grabación
            btnTerminarRuta.setOnClickListener(v -> stopRecordingRoute());

            // Establecer OnClickListener para ver el historial de rutas
            btnVerHistorial.setOnClickListener(v -> openHistorialRutaActivity());

            // Obtener el ID del usuario
            userId = mAuth.getCurrentUser().getUid();
            Log.d("RutaActivity", "onCreate: Usuario ID: " + userId);

            // Verificar permisos de ubicación y solicitar si es necesario
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                // Si el permiso ya está concedido, inicializa el mapa
                initializeMap();
            }
        } catch (Exception e) {
            Log.e("RutaActivity", "Error en onCreate", e);
        }
    }

    private void initializeMap() {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        } catch (Exception e) {
            Log.e("RutaActivity", "Error en initializeMap", e);
        }
    }

    private void startRecordingRoute() {
        if (isRecording) {
            Toast.makeText(this, "Ya estás grabando una ruta.", Toast.LENGTH_SHORT).show();
            return;
        }

        isRecording = true;
        routeId = mDatabase.push().getKey(); // Crear un ID único para esta ruta
        if (routeId == null || routeId.isEmpty()) {
            Log.e("RutaActivity", "startRecordingRoute: Error al generar routeId");
            Toast.makeText(this, "Error al generar el ID de la ruta", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();
        Log.d("RutaActivity", "startRecordingRoute: Grabación iniciada, routeId: " + routeId);

        // Continuar registrando la ubicación en tiempo real
        recordLocationContinuously();
    }


    private void stopRecordingRoute() {
        if (!isRecording) {
            Toast.makeText(this, "No estás grabando ninguna ruta.", Toast.LENGTH_SHORT).show();
            return;
        }

        isRecording = false;
        Toast.makeText(this, "Grabación de ruta terminada", Toast.LENGTH_SHORT).show();
        Log.d("RutaActivity", "stopRecordingRoute: Grabación terminada");

        // Aquí no cerramos la actividad, solo dejamos de grabar
    }

    private void recordLocationContinuously() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        executor.execute(() -> {
            try {
                Location location = fusedLocationClient.getLastLocation().getResult();
                if (location != null && isRecording) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    long timestamp = System.currentTimeMillis(); // Timestamp de la ubicación

                    Log.d("RutaActivity", "Ubicación obtenida: Lat: " + latitude + ", Lng: " + longitude);

                    // Crear el mapa de datos con la ubicación y timestamp
                    Map<String, Object> locationData = new HashMap<>();
                    locationData.put("latitude", latitude);
                    locationData.put("longitude", longitude);
                    locationData.put("timestamp", timestamp);

                    // Guardar la ubicación en Firebase bajo el usuario y la ruta correspondiente
                    mDatabase.child(userId).child(routeId).push().setValue(locationData)
                            .addOnSuccessListener(aVoid -> Log.d("RutaActivity", "Ubicación guardada en Firebase"))
                            .addOnFailureListener(e -> Log.e("RutaActivity", "Error al guardar ubicación", e));
                }
            } catch (Exception e) {
                Log.e("RutaActivity", "Error obteniendo ubicación", e);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
            });
        }
    }

    private void showRouteOnMap() {
        mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear(); // Limpiar el mapa antes de mostrar la nueva ruta
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot locationSnapshot : routeSnapshot.getChildren()) {
                        Map<String, Object> locationData = (Map<String, Object>) locationSnapshot.getValue();
                        if (locationData != null) {
                            Double latitude = (Double) locationData.get("latitude");
                            Double longitude = (Double) locationData.get("longitude");

                            if (latitude != null && longitude != null) {
                                LatLng location = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(location).title("Posición"));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RutaActivity.this, "Error al cargar la ruta", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openHistorialRutaActivity() {
        Intent intent = new Intent(RutaActivity.this, HistorialRutasActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeMap();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
