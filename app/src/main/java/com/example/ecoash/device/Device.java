package com.example.ecoash.device;

import java.util.Map;

public class Device {

    private String id;
    private String name;
    private String userEmail;
    private Integer CO;
    private Integer CO2;
    private Integer PM10;
    private Integer PM2_5;
    private Integer humedad;
    private Map<String, Object> temperatura;

    // Constructor vacío necesario para Firebase
    public Device() {}

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getCO() {
        return CO;
    }

    public void setCO(Integer CO) {
        this.CO = CO;
    }

    public Integer getCO2() {
        return CO2;
    }

    public void setCO2(Integer CO2) {
        this.CO2 = CO2;
    }

    public Integer getPM10() {
        return PM10;
    }

    public void setPM10(Integer PM10) {
        this.PM10 = PM10;
    }

    public Integer getPM2_5() {
        return PM2_5;
    }

    public void setPM2_5(Integer PM2_5) {
        this.PM2_5 = PM2_5;
    }

    public Integer getHumedad() {
        return humedad;
    }

    public void setHumedad(Integer humedad) {
        this.humedad = humedad;
    }

    public Map<String, Object> getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Map<String, Object> temperatura) {
        this.temperatura = temperatura;
    }
}
