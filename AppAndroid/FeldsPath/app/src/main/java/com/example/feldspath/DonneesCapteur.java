package com.example.feldspath;

import androidx.room.*;
@Entity
public class DonneesCapteur {
    @PrimaryKey(autoGenerate = true)
    private long date; // stocké en millisecondes (long)
    private float gaz;
    private float humidite;
    private float temperature;

    public DonneesCapteur(float gaz, float humidite, float temperature) {
        this.gaz = gaz;
        this.humidite = humidite;
        this.temperature = temperature;
        this.date = System.currentTimeMillis(); //Date automatique en millisecondes du temps présent
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getHumidite() {
        return humidite;
    }

    public void setHumidite(float humidite) {
        this.humidite = humidite;
    }

    public float getGaz() {
        return gaz;
    }

    public void setGaz(float gaz) {
        this.gaz = gaz;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
