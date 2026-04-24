package com.example.feldspath;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DonneesCapteur.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DonnesCapteurDAO dataDao();

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 1;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getDatabase(final Context context) {
        // on ne crée pas le singleton car il existe deja
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class, "base-nom"
                            ).addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("", "Création du jeu d'essai");

            //INSTANCE.databaseWriteExecutor.execute(() -> {
            //    INSTANCE.categorieDao().insert(new Categorie(1, "Maison"));

            //});

        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

}


