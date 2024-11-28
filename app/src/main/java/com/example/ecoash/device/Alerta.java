package com.example.ecoash.device;

public class Alerta {

    private String titulo;
    private String mensaje;
    private String fecha;

    // Constructor vacío requerido por Firebase
    public Alerta() {
    }

    public Alerta(String titulo, String mensaje, String fecha) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
