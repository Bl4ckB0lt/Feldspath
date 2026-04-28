package com.example.feldspath;

import androidx.room.*;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Zone.class,
                        parentColumns = "id_zone",
                        childColumns = "id_zone"
                )
        })
public class DonneesCapteur {
    @PrimaryKey
    private long date; // stocké en millisecondes (long)
    private float gaz;
    private float humidite;
    private float temperature;
    private boolean valeursAberrantes;

    private long id_zone;


    public DonneesCapteur(float gaz, float humidite, float temperature, boolean valeursAberrantes, long id_zone) {
        this.date = System.currentTimeMillis(); //Date automatique en millisecondes du temps présent;
        this.gaz = gaz;
        this.humidite = humidite;
        this.temperature = temperature;
        this.valeursAberrantes = valeursAberrantes;
        this.id_zone = id_zone;
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

    public long getId_zone() {
        return id_zone;
    }

    public void setId_zone(long id_zone) {
        this.id_zone = id_zone;
    }
}
