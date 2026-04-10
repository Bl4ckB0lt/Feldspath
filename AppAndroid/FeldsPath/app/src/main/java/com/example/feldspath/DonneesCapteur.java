package com.example.feldspath;

import java.sql.Timestamp;

public class DonneesCapteur {
    private Timestamp Date;
    private float gaz;
    private float humidite;
    private float temperature;

    public DonneesCapteur(float gaz, float humidite, float temperature) {
        this.gaz = gaz;
        this.humidite = humidite;
        this.temperature = temperature;
    }

    public Timestamp getDate() {
        return Date;
    }

    public float getGaz() {
        return gaz;
    }

    public void setGaz(float gaz) {
        this.gaz = gaz;
    }

    public float getHumidite() {
        return humidite;
    }

    public void setHumidite(float humidite) {
        this.humidite = humidite;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
