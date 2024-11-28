package com.example.ecoash.device;

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

    public Device() {
        // Constructor vacío para Firebase
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
        this.CO = CO;
    }

    public int getCO2() {
        return CO2;
    }

    public void setCO2(int CO2) {
        this.CO2 = CO2;
    }

    public int getPM10() {
        return PM10;
    }

    public void setPM10(int PM10) {
        this.PM10 = PM10;
    }

    public int getPM2_5() {
        return PM2_5;
    }

    public void setPM2_5(int PM2_5) {
        this.PM2_5 = PM2_5;
    }

    public int getHumedad() {
        return humedad;
    }

    public void setHumedad(int humedad) {
        this.humedad = humedad;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
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
}
