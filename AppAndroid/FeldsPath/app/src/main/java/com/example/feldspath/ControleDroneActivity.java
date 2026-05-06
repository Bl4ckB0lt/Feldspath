package com.example.feldspath;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

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
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ControleDroneActivity extends AppCompatActivity {
    ImageButton BTNAvancer;
    ImageButton BTNReculer;
    ImageButton BTNTournerG;
    ImageButton BTNTournerD;
    ImageButton Btn_enregistrerAudio;

    ImageButton Btn_prendreDonnee;

    SeekBar SBVitesse;
    Chip ChipLamp;
    Boolean LampOn = false;
    TextView TVTemp;
    TextView TVHum;
    TextView TVCo2;
    private AppDatabase db;
    private Button btnRetour;
    // variables utiles hors xml
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;

    private MqttClient mqttClient;
    private static final String BROKER_URL = "tcp://10.218.228.169:1883"; // rappel ancien brocker : broker.emqx.io      nouveau : 192.168.64.2
    // ssl ws wss tcp
    private static final String CLIENT_ID = "AndroidDroneController";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(this);
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
        Btn_prendreDonnee = findViewById(R.id.Btn_prendreDonnee);

        Btn_enregistrerAudio = findViewById(R.id.btn_enregistrerAudio);
        SBVitesse = findViewById(R.id.seekBarVitesse);
        TVTemp = findViewById(R.id.TVTemp);
        TVHum = findViewById(R.id.TVHum);
        TVCo2 = findViewById(R.id.TVCo2);

        btnRetour = findViewById(R.id.btn_retourMenuDepuisControle);

        ChipLamp = findViewById(R.id.chip);
        // Initialize MQTT Client
        initializeMQTT();

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMessage("Feldspath/controle", "arreter");
                finish();
            }
        });
        Btn_enregistrerAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecordingAndSend();
                }
            }
        });

        Btn_prendreDonnee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DonneesCapteur dataFormat;

                String TVCO2VAL = TVCo2.getText().toString();
                String TVHumVAL = TVHum.getText().toString();
                String TVTempVAL = TVTemp.getText().toString();
                if (TVHum.getCurrentTextColor() == 0xFFFF0000) {
                    dataFormat = new DonneesCapteur(Float.parseFloat(TVCO2VAL), Float.parseFloat(TVHumVAL), Float.parseFloat(TVTempVAL), true, 1);
                } else {
                    dataFormat = new DonneesCapteur(Float.parseFloat(TVCO2VAL), Float.parseFloat(TVHumVAL), Float.parseFloat(TVTempVAL), false, 1);
                }
                db.dataDao().insert(dataFormat);
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
                    if (topic.equals("Feldspath/data")) {
                        String[] data = payload.split("/");
                        Float[] dataF = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
                        for (int i = 0; i < data.length; i++) {
                            dataF[i] = Float.parseFloat(data[i]);
                        }
                        boolean aberrant = false;
                        if (dataF[0] >= MainActivity.temperatureSeuil || dataF[1] >= MainActivity.humiditySeuil || dataF[3] >= MainActivity.gazSeuil) {
                            aberrant = true;
                        }
                        DonneesCapteur dataFormat = new DonneesCapteur(dataF[3], dataF[1], dataF[0], aberrant, MainActivity.idzoneActuelle);
                        db.dataDao().insert(dataFormat);
                        runOnUiThread(() -> {
                            if (dataFormat.getValeursAberrantes()) {
                                TVCo2.setTextColor(0xFFFF0000);
                                TVHum.setTextColor(0xFFFF0000);
                                TVTemp.setTextColor(0xFFFF0000);
                            } else {
                                TVCo2.setTextColor(0xFF00FF00);
                                TVHum.setTextColor(0xFF00FF00);
                                TVTemp.setTextColor(0xFF00FF00);
                            }
                            TVTemp.setText(data[0]);
                            TVHum.setText(data[1]);
                            TVCo2.setText(data[3]);
                        });
                    }
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
    //--------------------------------------------
    // ------------FONCTION POUR LE MICRO----------------
    //--------------------------------------------------
    private void startRecording() {
        // Vérifie la permission micro au runtime (Android 6+)
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
            return;
        }

        audioFilePath = getCacheDir().getAbsolutePath() + "/audio_"
                + System.currentTimeMillis() + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(this, "Enregistrement en cours...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("AUDIO", "Erreur prepare/start : " + e.getMessage());
        }
    }

    private void stopRecordingAndSend() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        isRecording = false;
        Toast.makeText(this, "Enregistrement terminé, envoi...", Toast.LENGTH_SHORT).show();

        // Envoi dans un thread séparé (jamais sur le UI thread)
        new Thread(() -> sendAudioFile(audioFilePath)).start();
    }

    private void sendAudioFile(String filePath) {
        String serverUrl = "http://10.218.228.169:8080/upload"; // ← adapte l'IP et le port

        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            Log.e("AUDIO", "Fichier introuvable : " + filePath);
            return;
        }

        try {
            String boundary = "---boundary_feldspath";
            HttpURLConnection conn = (HttpURLConnection) new URL(serverUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream os = conn.getOutputStream();
                 FileInputStream fis = new FileInputStream(audioFile)) {

                // En-tête multipart
                String header = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"file\"; filename=\""
                        + audioFile.getName() + "\"\r\n"
                        + "Content-Type: audio/3gpp\r\n\r\n";
                os.write(header.getBytes());

                // Contenu du fichier
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                // Fin multipart
                os.write(("\r\n--" + boundary + "--\r\n").getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            Log.d("AUDIO", "Réponse serveur : " + responseCode);

            runOnUiThread(() -> {
                if (responseCode == 200) {
                    Toast.makeText(this, "Audio envoyé ✓", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erreur envoi : " + responseCode, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            Log.e("AUDIO", "Erreur envoi : " + e.getMessage());
            runOnUiThread(() ->
                    Toast.makeText(this, "Envoi échoué", Toast.LENGTH_SHORT).show());
        }
    }
}