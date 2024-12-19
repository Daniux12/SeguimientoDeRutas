package com.example.seguimientoderutas;

public class Ruta {
    private String routeId;      // ID de la ruta
    private double latitude;     // Latitud
    private double longitude;    // Longitud

    // Constructor vacío para Firebase
    public Ruta() {
    }

    // Constructor con parámetros para latitud, longitud y routeId
    public Ruta(String routeId, double latitude, double longitude) {
        this.routeId = routeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Ruta(String routeId) {
    }

    // Métodos getters y setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}