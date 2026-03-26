package com.example.feldspath;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ControleDroneActivity extends AppCompatActivity {
    ImageButton BTNAvancer;
    ImageButton BTNReculer;
    ImageButton BTNTournerG;
    ImageButton BTNTournerD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controle_drone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BTNAvancer = findViewById(R.id.BTNAvancer);
        BTNReculer = findViewById(R.id.BTNReculer);
        BTNTournerG = findViewById(R.id.BTNTournerG);
        BTNTournerD = findViewById(R.id.BTNTournerD);

        BTNAvancer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    Log.d("BTNAvancer", "onTouch: Appuyé");
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("BTNAvancer", "onTouch: Laché");
                }
                return false;
            }
        });
        BTNReculer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    Log.d("BTNReculer", "onTouch: Appuyé");
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("BTNReculer", "onTouch: Laché");
                }
                return false;
            }
        });
        BTNTournerG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    Log.d("BTNTournerG", "onTouch: Appuyé");
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("BTNTournerG", "onTouch: Laché");
                }
                return false;
            }
        });
        BTNTournerD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    Log.d("BTNTournerD", "onTouch: Appuyé");
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("BTNTournerD", "onTouch: Laché");
                }
                return false;
            }
        });
    }

}