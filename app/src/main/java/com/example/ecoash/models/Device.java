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
        // Eliminamos la inicialización de lastValues para permitir que Firebase lo maneje
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
        // Eliminamos la inicialización de lastValues para permitir que Firebase lo maneje
    }

    // Métodos Getters y Setters

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
        // Guardar el valor anterior en lastValues antes de actualizar
        if (this.lastValues == null) {
            this.lastValues = new HashMap<>();
        }
        this.lastValues.put("CO", this.CO);
        this.CO = CO;
    }

    public int getCO2() {
        return CO2;
    }

    public void setCO2(int CO2) {
        if (this.lastValues == null) {
            this.lastValues = new HashMap<>();
        }
        this.lastValues.put("CO2", this.CO2);
        this.CO2 = CO2;
    }

    public int getPM10() {
        return PM10;
    }

    public void setPM10(int PM10) {
        if (this.lastValues == null) {
            this.lastValues = new HashMap<>();
        }
        this.lastValues.put("PM10", this.PM10);
        this.PM10 = PM10;
    }

    public int getPM2_5() {
        return PM2_5;
    }

    public void setPM2_5(int PM2_5) {
        if (this.lastValues == null) {
            this.lastValues = new HashMap<>();
        }
        this.lastValues.put("PM2_5", this.PM2_5);
        this.PM2_5 = PM2_5;
    }

    public int getHumedad() {
        return humedad;
    }

    public void setHumedad(int humedad) {
        if (this.lastValues == null) {
            this.lastValues = new HashMap<>();
        }
        this.lastValues.put("humedad", this.humedad);
        this.humedad = humedad;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        if (this.lastValues == null) {
            this.lastValues = new HashMap<>();
        }
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

    /**
     * Método opcional para obtener el valor de una métrica de forma genérica.
     * Facilita la obtención de valores sin necesidad de múltiples getters.
     */
    public Object getMetricValue(String metric) {
        switch (metric) {
            case "CO":
                return getCO();
            case "CO2":
                return getCO2();
            case "PM10":
                return getPM10();
            case "PM2_5":
                return getPM2_5();
            case "humedad":
                return getHumedad();
            case "temperatura":
                return getTemperatura();
            default:
                return null;
        }
    }

    /**
     * Método opcional para establecer el valor de una métrica de forma genérica.
     * Facilita la actualización de valores sin necesidad de múltiples setters.
     */
    public void setMetricValue(String metric, int value) {
        switch (metric) {
            case "CO":
                setCO(value);
                break;
            case "CO2":
                setCO2(value);
                break;
            case "PM10":
                setPM10(value);
                break;
            case "PM2_5":
                setPM2_5(value);
                break;
            case "humedad":
                setHumedad(value);
                break;
            case "temperatura":
                setTemperatura(value);
                break;
            default:
                // Manejar métricas desconocidas si es necesario
                break;
        }
    }
}
