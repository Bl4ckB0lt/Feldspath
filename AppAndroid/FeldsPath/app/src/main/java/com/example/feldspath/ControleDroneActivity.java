package com.example.feldspath;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
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
    private Button btnRetour;
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
        BTNAvancer = findViewById(R.id.BTNAvancer);
        BTNReculer = findViewById(R.id.BTNReculer);
        BTNTournerG = findViewById(R.id.BTNTournerG);
        BTNTournerD = findViewById(R.id.BTNTournerD);
        SBVitesse = findViewById(R.id.seekBarVitesse);

        btnRetour = findViewById(R.id.btn_retourMenuDepuisControle);

        ChipLamp = findViewById(R.id.chip);
        // Initialize MQTT Client
        initializeMQTT();

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        BTNAvancer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage("Feldspath/controle", "avancer");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    publishMessage("Feldspath/controle", "arreter");
                }
                return false;
            }
        });
        BTNReculer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage("Feldspath/controle", "reculer");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    publishMessage("Feldspath/controle", "arreter");
                }
                return false;
            }
        });
        BTNTournerG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage("Feldspath/controle", "tournerGa");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    publishMessage("Feldspath/controle", "arreter");
                }
                return false;
            }
        });
        BTNTournerD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    publishMessage("Feldspath/controle", "tournerDr");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    publishMessage("Feldspath/controle", "arreter");
                }
                return false;
            }
        });
        ChipLamp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (LampOn) {
                        publishMessage("Feldspath/light", "OFF");
                        LampOn = false;
                    } else {
                        publishMessage("Feldspath/light", "ON");
                        LampOn = true;
                    }
                }
                return false;
            }
        });
        SBVitesse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int valeur = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valeur = (int) (10 + progress * 1.4);
                Log.d("controleur", "" + valeur);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used in this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                publishMessage("Feldspath/vitesse", "" + valeur);
            }
        });
    }

    private void initializeMQTT() {
        try {
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            mqttClient.connect(options);
            mqttClient.subscribe("Feldspath/temperature", 1);
            mqttClient.subscribe("Feldspath/humidity", 1);
            mqttClient.subscribe("Feldspath/pressure", 1);
            mqttClient.subscribe("Feldspath/co2", 1);
            mqttClient.subscribe("Feldspath/gas_resistor", 1);
            mqttClient.subscribe("Feldspath/iaq", 1);
            mqttClient.subscribe("Feldspath/iaq_accuracy", 1);
            mqttClient.subscribe("Feldspath/static_iaq", 1);
            mqttClient.subscribe("Feldspath/voc", 1);
            mqttClient.subscribe("Feldspath/data", 1);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    Log.d("MQTT", "Message reçu sur [" + topic + "] : " + payload);

                    // Mise à jour de l'UI sur le thread principal
                    runOnUiThread(() -> {
                        if (topic.equals("Feldspath/statut")) {
                            // Traite le message ici, ex :
                        }
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used in this example
                }
            });
            Log.d("MQTT", "initializeMQTT: Réussie");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                Log.e("MQTT", "Failed to disconnect MQTT: " + e.getMessage());
            }
        }
    }
}