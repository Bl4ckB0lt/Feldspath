package com.example.feldspath;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

public class ParametreActivity extends AppCompatActivity {
    private EditText et_tempSeuil;
    private EditText et_humiditySeuil;

    private EditText et_gazSeuil;
    private Button btn_AcceptModifSeuil;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parametre);

        et_gazSeuil = findViewById(R.id.et_gazSeuil);
        et_humiditySeuil = findViewById(R.id.et_humiditySeuil);
        et_tempSeuil = findViewById(R.id.et_tempSeuil);

        et_gazSeuil.setText(String.valueOf(MainActivity.gazSeuil));
        et_humiditySeuil.setText(String.valueOf(MainActivity.humiditySeuil));
        et_tempSeuil.setText(String.valueOf(MainActivity.temperatureSeuil));

        btn_AcceptModifSeuil = findViewById(R.id.btn_AcceptModifSeuil);

        btn_AcceptModifSeuil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strGaz = et_gazSeuil.getText().toString();
                String strHumidite = et_humiditySeuil.getText().toString();
                String strTemp = et_tempSeuil.getText().toString();

                // On ne met à jour que si le champ n'est pas vide
                if (!strGaz.isEmpty()) MainActivity.gazSeuil = Float.parseFloat(strGaz);
                if (!strHumidite.isEmpty())MainActivity.humiditySeuil = Float.parseFloat(strHumidite);
                if (!strTemp.isEmpty()) MainActivity.temperatureSeuil = Float.parseFloat(strTemp);
                Toast.makeText(ParametreActivity.this, "Changement pris en compte", Toast.LENGTH_SHORT).show();
                Log.d("type",MainActivity.gazSeuil+"");
                Log.d("type",MainActivity.temperatureSeuil+"");
                Log.d("type",MainActivity.humiditySeuil+"");
                finish(); // Retour à l'écran précédent
            }
        });
    }
}

