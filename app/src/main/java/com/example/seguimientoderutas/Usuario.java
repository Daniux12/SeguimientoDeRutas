package com.example.seguimientoderutas;

public class Usuario {

    private String nombre;
    private String email;
    private String password;

    // Constructor vacío necesario para Firebase
    public Usuario(String nombre, String email) {
    }

    // Constructor con parámetros
    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

