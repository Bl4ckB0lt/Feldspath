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
        BTNAvancer = findViewById(R.id.BTNAvancer);
        BTNReculer = findViewById(R.id.BTNReculer);
        BTNTournerG = findViewById(R.id.BTNTournerG);
        BTNTournerD = findViewById(R.id.BTNTournerD);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controle_drone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BTNAvancer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    Log.d("BTNAvancer", "onTouch: DOWN");
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("BTNAvancer", "onTouch: UP");
                }
                return false;
            }
        });
    }

}