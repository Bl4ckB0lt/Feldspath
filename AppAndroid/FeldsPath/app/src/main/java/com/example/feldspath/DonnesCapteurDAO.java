package com.example.feldspath;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao

public interface DonnesCapteurDAO {
        @Query("SELECT * FROM DonneesCapteur")
        LiveData<List<DonneesCapteur>> getAll();

        @Query("SELECT * FROM DonneesCapteur WHERE date = :uneId")
        LiveData<DonneesCapteur> getDonneesCapteurById(int uneId);

        @Insert
        void insert(DonneesCapteur uneId);

        @Delete
        void delete(DonneesCapteur uneId);

    }
