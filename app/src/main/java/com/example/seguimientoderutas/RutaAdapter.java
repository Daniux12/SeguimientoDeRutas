package com.example.seguimientoderutas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RutaAdapter extends RecyclerView.Adapter<RutaAdapter.RutaViewHolder> {
    private Context context;
    private List<Ruta> rutaList;

    // Constructor del Adapter
    public RutaAdapter(Context context, List<Ruta> rutaList) {
        this.context = context;
        this.rutaList = rutaList;  // Asignamos la lista pasada al adaptador
    }

    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_ruta, parent, false);
        return new RutaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        Ruta ruta = rutaList.get(position);
        holder.routeIdTextView.setText(ruta.getRouteId());
    }

    @Override
    public int getItemCount() {
        return rutaList.size();
    }

    // ViewHolder que contiene las vistas de cada ítem
    public static class RutaViewHolder extends RecyclerView.ViewHolder {
        TextView routeIdTextView;

        public RutaViewHolder(View itemView) {
            super(itemView);
            routeIdTextView = itemView.findViewById(R.id.tvRouteId); // Asegúrate de que este ID exista en tu XML
        }
    }
}
