package com.example.feldspath;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZoneActivity extends AppCompatActivity {

    private AppDatabase db;

    private Spinner sp_ZoneActuelle;
    private EditText et_newZone;
    private Button btn_ajoutNewZone;
    private Button btn_retourzone;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // pour faire le DAO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp_ZoneActuelle = findViewById(R.id.sp_ZoneActuelle);
        et_newZone = findViewById(R.id.et_newZone);
        btn_ajoutNewZone = findViewById(R.id.btn_ajoutNewZone);
        btn_retourzone = findViewById(R.id.btn_retourzone);

        btn_retourzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }});
        btn_ajoutNewZone.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Zone zoneAajouter;
                        String texte = et_newZone.getText().toString();
                        if (!texte.isEmpty()) {

                            zoneAajouter = new Zone(texte);

                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    db.zoneDAO().insert(zoneAajouter);

                                    // Le Toast doit se faire sur le UI thread
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ZoneActivity.this, "Zone ajoutée !", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ZoneActivity.this, "NON , il faut mettre du texte", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Nettoyage propre
    }
}



