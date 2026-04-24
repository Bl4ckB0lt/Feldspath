package com.example.feldspath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    //test
    FloatingActionButton button;
    Button b_controle_drone;
    Button b_gestionData;

    Button b_parametre;

    public static float gazSeuil;
    public static float humiditySeuil;
    public static float temperatureSeuil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        b_controle_drone = findViewById(R.id.ControleDrone);
        b_gestionData = findViewById(R.id.gestionData);
        b_parametre = findViewById(R.id.btn_parametre);

        b_controle_drone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent unIntent = new Intent(getApplicationContext(),ControleDroneActivity.class);
                startActivity(unIntent);
            }
        });
        b_gestionData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent unIntent = new Intent(getApplicationContext(), GestionDonneeActivity.class);
                startActivity(unIntent);
            }
        });

        b_parametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent unIntent = new Intent(getApplicationContext(), ParametreActivity.class);
                startActivity(unIntent);
            }
        });

        gazSeuil = 700;
        humiditySeuil = 40;
        temperatureSeuil = 30;
    }
}