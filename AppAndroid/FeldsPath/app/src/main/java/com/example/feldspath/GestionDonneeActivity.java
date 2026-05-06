package com.example.feldspath;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GestionDonneeActivity extends AppCompatActivity {
    //------OBJ GRAPHIQUE ------
    private Spinner sp_trie;
    private CalendarView cv_trieParDate;
    private RecyclerView rv_data;
    private Button btnRetour2;
// ------VARIABLES GLOBALES----

    // null = pas de filtre, true = aberrantes seulement, false = normales seulement
    private Boolean filtreAberrantes = null;
    private AppDatabase db;
    private ArrayList<DonneesCapteur> lstToutesLesDonnees;
    private DonneesRecyclerViewAdapter adapter = new DonneesRecyclerViewAdapter();
    // -1 = pas de filtre date
    private long idDateFiltre = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestiondonne);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sp_trie = findViewById(R.id.sp_trie);
        cv_trieParDate = findViewById(R.id.CV_trieParDate);
        rv_data = findViewById(R.id.rv_affichageData);
        btnRetour2 = findViewById(R.id.btn_retourdepuisGestionData);

        //------GESTION DU BTN DE RETOUR-------
        btnRetour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Toutes", "Aberrantes", "Normales"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_trie.setAdapter(spinnerAdapter);

        sp_trie.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        filtreAberrantes = null;
                        break; // Toutes
                    case 1:
                        filtreAberrantes = true;
                        break; // Aberrantes seulement
                    case 2:
                        filtreAberrantes = false;
                        break; // Normales seulement
                }
                filtrerDataAAfficher();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                filtreAberrantes = null;
                filtrerDataAAfficher();
            }
        });

        //---- recyler View ----
        rv_data.setLayoutManager(new LinearLayoutManager(this));
        rv_data.setAdapter(adapter);
//----------GESTION DU CALENDAR VIEW-------
        cv_trieParDate.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {

            // On reconstruit minuit du jour sélectionné en millisecondes
            // month+1 car CalendarView donne les mois de 0 à 11
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month, dayOfMonth, 0, 0, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            idDateFiltre = cal.getTimeInMillis(); // début du jour (minuit)

            filtrerDataAAfficher();
        });

        chargerToutesLesDonnees();

    }

    private void filtrerDataAAfficher() {
        ArrayList<DonneesCapteur> dataFiltrees = new ArrayList<>();
        for (DonneesCapteur data : lstToutesLesDonnees) {


            // On saute la ligne donnée si elle ne correspond pas à
            // if (idCatFiltre != -1 && note.getID_UneCategorie() != idCatFiltre) continue;
            //  on vérifie que la note est dans le même jour que la date sélectionnée
            if (idDateFiltre != -1) {
                // Début du jour sélectionné (minuit)
                long debutJour = idDateFiltre;

                // Fin du jour sélectionné (23h59m59s)
                long finJour = debutJour + 24 * 60 * 60 * 1000 - 1;

                // On saute la note si sa date est en dehors de ce jour
                if (data.getDate() < debutJour || data.getDate() > finJour) continue;
            }
            if (filtreAberrantes != null && filtreAberrantes != data.getValeursAberrantes()) continue;
            dataFiltrees.add(data);
        }
        adapter.setLstData(dataFiltrees);
    }

    private void chargerToutesLesDonnees() {
        db.dataDao().getAll().observe(this, resultats -> {
            // Appelé automatiquement à chaque changement en base
            lstToutesLesDonnees = new ArrayList<>(resultats);
            filtrerDataAAfficher();
        });


    }

}