package com.example.feldspath;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GestionDonneeActivity extends AppCompatActivity {
//------OBJ GRAPHIQUE ------
    private Spinner sp_trie;
    private CalendarView cv_trieParDate;
    private RecyclerView rv_data;
    private Button btnRetour2;
// ------VARIABLES GLOBALES----

    private ArrayList<DonneesCapteur> lstToutesLesDonnees;
    private DonneesRecyclerViewAdapter adapter = new DonneesRecyclerViewAdapter();
    // -1 = pas de filtre date
    private long idDateFiltre = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.gestiondonne);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sp_trie = findViewById(R.id.sp_trie);
        cv_trieParDate = findViewById(R.id.CV_trieParDate);
        rv_data = findViewById(R.id.rv_affichageData);
        btnRetour2 = findViewById(R.id.btn_retourdepuisGestionData);
        btnRetour2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cv_trieParDate.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {

            // On reconstruit minuit du jour sélectionné en millisecondes
            // month+1 car CalendarView donne les mois de 0 à 11
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month, dayOfMonth, 0, 0, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            idDateFiltre = cal.getTimeInMillis(); // début du jour (minuit)

            filtrerDataAAfficher();
        });

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
            dataFiltrees.add(data);
        }
        adapter.setLstData(dataFiltrees);
    }

}