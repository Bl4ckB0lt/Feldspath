package com.example.feldspath;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ZoneDAO {

    @Query("SELECT * FROM Zone")
    LiveData<List<Zone>> getAll();

    @Query("SELECT * FROM Zone WHERE id_zone = :uneId")
    LiveData<Zone> getZoneById(long uneId);

    @Insert
    void insert(Zone uneId);

    @Delete
    void delete(Zone uneId);
}
