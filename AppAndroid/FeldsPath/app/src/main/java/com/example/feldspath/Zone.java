package com.example.feldspath;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;


@Entity(tableName = "zone")
public class Zone {
    @PrimaryKey(autoGenerate = true)
    long id_zone;
    String libelle_zone;

    public Zone( String libelle_zone) {
        this.libelle_zone = libelle_zone;
    }


    public long getId_zone() {
        return id_zone;
    }

    public void setId_zone(long id_zone) {
        this.id_zone = id_zone;
    }

    public String getLibelle_zone() {
        return libelle_zone;
    }

    public void setLibelle_zone(String libelle_zone) {
        this.libelle_zone = libelle_zone;
    }

}
