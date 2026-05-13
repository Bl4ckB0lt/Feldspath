package com.example.feldspath;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZoneActivity extends AppCompatActivity {

    private AppDatabase db;
    //------------ VAR Graphiques ---------
    private Spinner sp_ZoneActuelle;
    private EditText et_newZone;
    private Button btn_ajoutNewZone;
    private Button btn_retourzone;

    //--------- VAR DE CODE --------
    private ArrayAdapter<String> spinnerAdapter;
    private List<Zone> zoneList = new ArrayList<>(); // pour avoir une liste de zone
    private List<String> zoneNames = new ArrayList<>();

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


        // gestion de l'adapter du spinner
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, zoneNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_ZoneActuelle.setAdapter(spinnerAdapter);

        // le spinner se met à jour automatiquement et affiche le nom des zones et affiche en 1 er la zone actuelle
        db.zoneDAO().getAll().observe(this, new Observer<List<Zone>>() {
            @Override
            public void onChanged(List<Zone> zones) {
                zoneNames.clear();
                zoneList.clear();
                for (Zone z : zones) {
                    zoneNames.add(z.getLibelle_zone());
                    zoneList.add(z);
                }
                spinnerAdapter.notifyDataSetChanged();

                // Repositionnement sur la zone déjà sélectionnée
                for (int i = 0; i < zoneList.size(); i++) {
                    if (zoneList.get(i).getId_zone() == MainActivity.idzoneActuelle) {
                        sp_ZoneActuelle.setSelection(i);
                        break;
                    }
                }
            }
        });
        sp_ZoneActuelle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!zoneList.isEmpty()) {
                    MainActivity.idzoneActuelle = zoneList.get(position).getId_zone();
                    Toast.makeText(ZoneActivity.this, "Zone : " + zoneList.get(position).libelle_zone, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ne Rien à faire a preciser sinon ca fait de la merde avec l'adapter view
            }
        });


        btn_retourzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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



