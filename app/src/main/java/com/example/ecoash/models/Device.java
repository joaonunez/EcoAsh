package com.example.ecoash.models;

import java.util.HashMap;

public class Device {
    private String id;
    private String userEmail;
    private String name; // Nombre del dispositivo
    private int CO;
    private int CO2;
    private int PM10;
    private int PM2_5;
    private int humedad;
    private int temperatura;
    private String dateOfCreation;
    private HashMap<String, HashMap<String, String>> alertas; // Alertas del dispositivo
    private HashMap<String, Object> lastValues; // Documento para controlar los valores anteriores

    public Device() {
        // Constructor vacío para Firebase
        this.initializeLastValues();
    }

    public Device(String id, String userEmail, String name, int CO, int CO2, int PM10, int PM2_5, int humedad, int temperatura, String dateOfCreation, HashMap<String, HashMap<String, String>> alertas) {
        this.id = id;
        this.userEmail = userEmail;
        this.name = name;
        this.CO = CO;
        this.CO2 = CO2;
        this.PM10 = PM10;
        this.PM2_5 = PM2_5;
        this.humedad = humedad;
        this.temperatura = temperatura;
        this.dateOfCreation = dateOfCreation;
        this.alertas = alertas;
        this.initializeLastValues();
    }

    private void initializeLastValues() {
        this.lastValues = new HashMap<>();
        // Inicialmente, lastValues se setea con los valores iniciales.
        // En cuanto se actualicen las métricas, lastValues guardará el valor previo.
        this.lastValues.put("CO", this.CO);
        this.lastValues.put("CO2", this.CO2);
        this.lastValues.put("PM10", this.PM10);
        this.lastValues.put("PM2_5", this.PM2_5);
        this.lastValues.put("humedad", this.humedad);
        this.lastValues.put("temperatura", this.temperatura);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCO() {
        return CO;
    }

    public void setCO(int CO) {
        // Antes de actualizar, guardamos el valor actual en lastValues como valor anterior.
        this.lastValues.put("CO", this.CO);
        this.CO = CO;
    }

    public int getCO2() {
        return CO2;
    }

    public void setCO2(int CO2) {
        this.lastValues.put("CO2", this.CO2);
        this.CO2 = CO2;
    }

    public int getPM10() {
        return PM10;
    }

    public void setPM10(int PM10) {
        this.lastValues.put("PM10", this.PM10);
        this.PM10 = PM10;
    }

    public int getPM2_5() {
        return PM2_5;
    }

    public void setPM2_5(int PM2_5) {
        this.lastValues.put("PM2_5", this.PM2_5);
        this.PM2_5 = PM2_5;
    }

    public int getHumedad() {
        return humedad;
    }

    public void setHumedad(int humedad) {
        this.lastValues.put("humedad", this.humedad);
        this.humedad = humedad;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.lastValues.put("temperatura", this.temperatura);
        this.temperatura = temperatura;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public HashMap<String, HashMap<String, String>> getAlertas() {
        return alertas;
    }

    public void setAlertas(HashMap<String, HashMap<String, String>> alertas) {
        this.alertas = alertas;
    }

    public HashMap<String, Object> getLastValues() {
        return lastValues;
    }

    public void setLastValues(HashMap<String, Object> lastValues) {
        this.lastValues = lastValues;
    }
}
