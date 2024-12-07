package com.example.ecoash.models;

public class Alert {
    private String fecha;
    private String mensaje;
    private String titulo;
    private String color;
    private String key; // Clave de la alerta en Firebase

    public Alert() {
        // Constructor vacío para Firebase
    }

    public Alert(String fecha, String mensaje, String titulo, String color) {
        this.fecha = fecha;
        this.mensaje = mensaje;
        this.titulo = titulo;
        this.color = color;
    }

    // Getters y Setters
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "fecha='" + fecha + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", titulo='" + titulo + '\'' +
                ", color='" + color + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
