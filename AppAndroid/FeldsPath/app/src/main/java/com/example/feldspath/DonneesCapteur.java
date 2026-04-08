package com.example.feldspath;

public class DonneesCapteur {
    float gaz;
    float humidite;
    double temperature;

    public DonneesCapteur(float gaz, float humidite, double temperature) {
        this.gaz = gaz;
        this.humidite = humidite;
        this.temperature = temperature;
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

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
