package com.example.feldspath;

import androidx.room.*;
@Entity
public class DonneesCapteur {
    @PrimaryKey
    private long date; // stocké en millisecondes (long)
    private float gaz;
    private float humidite;
    private float temperature;
    private boolean valeursAberrantes;


    public DonneesCapteur( float gaz, float humidite, float temperature, boolean valeursAberrantes) {
        this.date = System.currentTimeMillis(); //Date automatique en millisecondes du temps présent;
        this.gaz = gaz;
        this.humidite = humidite;
        this.temperature = temperature;
        this.valeursAberrantes = valeursAberrantes;
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

    public boolean getValeursAberrantes() {
        return valeursAberrantes;
    }

    public void setValeursAberrantes(boolean valeursAberrantes) {
        this.valeursAberrantes = valeursAberrantes;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

}
