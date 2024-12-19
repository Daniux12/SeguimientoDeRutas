package com.example.seguimientoderutas;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HistorialRutasActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private RutaAdapter rutaAdapter;
    private List<Ruta> rutaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_rutas);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("rutas");
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rutaList = new ArrayList<>();
        rutaAdapter = new RutaAdapter(this, rutaList);
        recyclerView.setAdapter(rutaAdapter);

        loadHistorial();
    }

    private void loadHistorial() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rutaList.clear();
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    String routeId = routeSnapshot.getKey();
                    if (routeId != null) {
                        rutaList.add(new Ruta(routeId));
                    }
                }
                rutaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HistorialRutasActivity.this, "Error al cargar el historial", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

