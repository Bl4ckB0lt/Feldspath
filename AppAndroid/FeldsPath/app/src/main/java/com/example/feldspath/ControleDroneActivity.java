package com.example.feldspath;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ControleDroneActivity extends AppCompatActivity {

    ImageButton BTNAvancer;
    ImageButton BTNReculer;
    ImageButton BTNTournerG;
    ImageButton BTNTournerD;
    SeekBar SBVitesse;
    Chip ChipLamp;
    Boolean LampOn = false;
    private MqttClient mqttClient;
    private static final String BROKER_URL = "tcp://broker.emqx.io:1883";
    private static final String CLIENT_ID = "AndroidDroneController";

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

        BTNAvancer   = findViewById(R.id.BTNAvancer);
        BTNReculer   = findViewById(R.id.BTNReculer);
        BTNTournerG  = findViewById(R.id.BTNTournerG);
        BTNTournerD  = findViewById(R.id.BTNTournerD);
        SBVitesse    = findViewById(R.id.seekBarVitesse);
        ChipLamp     = findViewById(R.id.chip);

        initializeMQTT();

        BTNAvancer.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                publishMessage("Feldspath/controle", "avancer");
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                publishMessage("Feldspath/controle", "arreter");
            return false;
        });

        BTNReculer.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                publishMessage("Feldspath/controle", "reculer");
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                publishMessage("Feldspath/controle", "arreter");
            return false;
        });

        BTNTournerG.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                publishMessage("Feldspath/controle", "tournerGa");
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                publishMessage("Feldspath/controle", "arreter");
            return false;
        });

        BTNTournerD.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                publishMessage("Feldspath/controle", "tournerDr");
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                publishMessage("Feldspath/controle", "arreter");
            return false;
        });

        ChipLamp.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                toggleLamp();
            }
            return false;
        });

        SBVitesse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int valeur = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valeur = (int) (10 + progress * 1.4);
                Log.d("controleur", "" + valeur);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                publishMessage("Feldspath/vitesse", "" + valeur);
            }
        });
    }


    private void toggleLamp() {
        if (LampOn) {
            publishMessage("Feldspath/light", "OFF");
            LampOn = false;
            Toast.makeText(this, "Lampe éteinte", Toast.LENGTH_SHORT).show();
        } else {
            publishMessage("Feldspath/light", "ON");
            LampOn = true;
            Toast.makeText(this, "Lampe allumée", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_controle_drone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_lampe) {
            toggleLamp();
            return true;

        } else if (id == R.id.menu_deconnecter) {
            disconnectMQTT();
            Toast.makeText(this, "MQTT déconnecté", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.menu_parametres) {
            // TODO : ouvrir une activité Paramètres
            Toast.makeText(this, "Paramètres (à implémenter)", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.menu_retour) {
            finish(); // retour à MainActivity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeMQTT() {
        try {
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            mqttClient.connect(options);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) { }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) { }
            });
        } catch (MqttException e) {
            Log.e("MQTT", "Failed to initialize MQTT: " + e.getMessage());
        }
    }

    private void publishMessage(String topic, String message) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(0);
                mqttClient.publish(topic, mqttMessage);
            } catch (MqttException e) {
                Log.e("MQTT", "Failed to publish message: " + e.getMessage());
            }
        } else {
            Log.w("MQTT", "MQTT client not connected");
        }
    }

    private void disconnectMQTT() {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                Log.e("MQTT", "Failed to disconnect MQTT: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectMQTT();
    }
}
