package com.example.feldspath;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ZoneDAO {

    @Query("SELECT * FROM zone")
    LiveData<List<Zone>> getAll();

    @Query("SELECT * FROM zone WHERE id_zone = :uneId")
    LiveData<Zone> getZoneById(long uneId);
    @Query("SELECT libelle_zone FROM zone WHERE id_zone = :uneId")
    String getNomZoneById(long uneId);
    @Insert
    void insert(Zone zone);

    @Delete
    void delete(Zone zone);
}
