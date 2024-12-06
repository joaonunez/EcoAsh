package com.example.ecoash.models;

public class Alert {
    private String fecha;
    private String mensaje;
    private String titulo;
    private String color; // Nuevo atributo

    public Alert() {
        // Constructor vacío para Firebase
    }

    public Alert(String fecha, String mensaje, String titulo, String color) {
        this.fecha = fecha;
        this.mensaje = mensaje;
        this.titulo = titulo;
        this.color = color; // Asignar el valor del color
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

    @Override
    public String toString() {
        return "Alert{" +
                "fecha='" + fecha + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", titulo='" + titulo + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
