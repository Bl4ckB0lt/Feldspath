package com.example.feldspath;

import static com.example.feldspath.MainActivity.idzoneActuelle;

import android.media.MediaRecorder;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
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


import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ControleDroneActivity extends AppCompatActivity {
    ImageButton BTNAvancer;
    ImageButton BTNReculer;
    ImageButton BTNTournerG;
    ImageButton BTNTournerD;
    Button BTNMusique;
    ImageButton Btn_enregistrerAudio;
    ImageButton Btn_prendreDonnee;

    SeekBar SBVitesse;
    Chip ChipLamp;
    Boolean LampOn = false;
    TextView TVTemp;
    TextView TVHum;
    TextView TVCo2;
    TextView TVZone;
    private AppDatabase db;
    private Button btnRetour;
    // variables utiles hors xml
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;

    private MqttClient mqttClient;
    private static final String BROKER_URL = "tcp://10.177.12.169:1883";
    // ssl ws wss tcp
    private static final String CLIENT_ID = "AndroidDroneController";
    private static final String serverUrl = "http://10.177.12.139:8080"; // ← adapte l'IP et le port

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
        BTNMusique = findViewById(R.id.btn_musique);
        Btn_enregistrerAudio = findViewById(R.id.btn_enregistrerAudio);
        SBVitesse = findViewById(R.id.seekBarVitesse);
        TVTemp = findViewById(R.id.TVTemp);
        TVHum = findViewById(R.id.TVHum);
        TVCo2 = findViewById(R.id.TVCo2);
        TVZone = findViewById(R.id.TVZone);

        btnRetour = findViewById(R.id.btn_retourMenuDepuisControle);

        ChipLamp = findViewById(R.id.chip);
        // Initialize MQTT Client
        new Thread(this::initializeMQTT).start();
        TVZone.setText("zone: "+ db.zoneDAO().getNomZoneById(MainActivity.idzoneActuelle));
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
        BTNMusique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMessage("Feldspath/musique", "message_vide");
            }
        });
        Btn_prendreDonnee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DonneesCapteur dataFormat;

                String TVCO2VAL = TVCo2.getText().toString();
                String TVHumVAL = TVHum.getText().toString();
                String TVTempVAL = TVTemp.getText().toString();
                boolean aberant = false;
                if (TVHum.getCurrentTextColor() == 0xFFFF0000) {
                    aberant = true;
                }
                Log.d("btnReg", TVCO2VAL+" : "+Float.parseFloat(TVCO2VAL));
                Log.d("btnReg", TVHumVAL+" : "+Float.parseFloat(TVHumVAL));
                Log.d("btnReg", TVTempVAL+" : "+Float.parseFloat(TVTempVAL));
                dataFormat = new DonneesCapteur(Float.parseFloat(TVCO2VAL), Float.parseFloat(TVHumVAL), Float.parseFloat(TVTempVAL), aberant, MainActivity.idzoneActuelle);
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
                //publishMessage("Feldspath/musique", "avancer");
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
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    Log.d("MQTT", "Message reçu sur [" + topic + "] : " + payload);

                    if (topic.equals("Feldspath/data")) {
                        String[] data = payload.split("/");
                        Float[] dataF = new Float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
                        for (int i = 0; i < data.length; i++) {
                            dataF[i] = Float.parseFloat(data[i]);
                        }
                        boolean aberrant = dataF[0] >= MainActivity.temperatureSeuil
                                || dataF[1] >= MainActivity.humiditySeuil
                                || dataF[3] >= MainActivity.gazSeuil;

                        if (aberrant) {
                            DonneesCapteur dataFormat = new DonneesCapteur(
                                    dataF[3], dataF[1], dataF[0], aberrant, MainActivity.idzoneActuelle);
                            db.dataDao().insert(dataFormat);
                        }
                        //  UI uniquement ici
                        runOnUiThread(() -> {
                            int color = aberrant ? 0xFFFF0000 : 0xFF00FF00;
                            TVCo2.setTextColor(color);
                            TVHum.setTextColor(color);
                            TVTemp.setTextColor(color);
                            TVTemp.setText(data[0]);
                            TVHum.setText(data[1]);
                            TVCo2.setText(data[3]);
                        });
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
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
                Log.e("MQTT", "Failed to publish: " + e.getMessage());
            }
        } else {
            Log.w("MQTT", "MQTT pas encore connecté, message ignoré : " + message);
            // Optionnel : Toast court pour informer l'utilisateur
            runOnUiThread(() ->
                    Toast.makeText(this, "Connexion en cours...", Toast.LENGTH_SHORT).show());
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
        // Vérifie la permission micro au runtime
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
            return;
        }
        // outputfile du fichier audio créée
        audioFilePath = getCacheDir().getAbsolutePath() + "/audio_enregistrement.3gpp";

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
        Toast.makeText(this, "Enregistrement terminé, conversion...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                // 1. Convertir le 3GPP/AMR en WAV
                String wavPath = getCacheDir().getAbsolutePath() + "/audio_converted.wav";
                convertAmrToWav(audioFilePath, wavPath);

                // 2. Envoyer le WAV
                sendAudioFile(wavPath);

            } catch (Exception e) {
                Log.e("AUDIO", "Erreur conversion : " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(this, "Erreur conversion", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void convertAmrToWav(String inputPath, String outputPath) throws Exception {

        // --- 1. Extraire le flux AMR avec MediaExtractor ---
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(inputPath);

        MediaFormat format = null;
        int audioTrack = -1;
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat f = extractor.getTrackFormat(i);
            if (f.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                audioTrack = i;
                format = f;
                break;
            }
        }
        if (audioTrack == -1) throw new Exception("Aucune piste audio trouvée");

        extractor.selectTrack(audioTrack);

        int sampleRate    = 16000; //format.getInteger(MediaFormat.KEY_SAMPLE_RATE);   // 8000 Hz pour AMR_NB
        int channelCount  = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT); // 1 (mono)
        String mime       = format.getString(MediaFormat.KEY_MIME);

        // --- 2. Décoder avec MediaCodec → PCM brut ---
        MediaCodec codec = MediaCodec.createDecoderByType(mime);
        codec.configure(format, null, null, 0);
        codec.start();

        ByteArrayOutputStream pcmStream = new ByteArrayOutputStream();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        boolean endOfStream = false;

        while (!endOfStream) {
            // Alimenter l'encodeur
            int inIndex = codec.dequeueInputBuffer(10000);
            if (inIndex >= 0) {
                ByteBuffer inputBuffer = codec.getInputBuffer(inIndex);
                inputBuffer.clear();
                int sampleSize = extractor.readSampleData(inputBuffer, 0);

                if (sampleSize < 0) {
                    codec.queueInputBuffer(inIndex, 0, 0, 0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    endOfStream = true;
                } else {
                    codec.queueInputBuffer(inIndex, 0, sampleSize,
                            extractor.getSampleTime(), 0);
                    extractor.advance();
                }
            }

            // Récupérer les données PCM décodées
            int outIndex = codec.dequeueOutputBuffer(bufferInfo, 10000);
            if (outIndex >= 0) {
                ByteBuffer outputBuffer = codec.getOutputBuffer(outIndex);
                byte[] chunk = new byte[bufferInfo.size];
                outputBuffer.get(chunk);
                pcmStream.write(chunk);
                codec.releaseOutputBuffer(outIndex, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    endOfStream = true;
                }
            }
        }

        codec.stop();
        codec.release();
        extractor.release();

        byte[] pcmData = pcmStream.toByteArray();

        // --- 3. Écrire le fichier WAV avec header ---
        writeWavFile(outputPath, pcmData, sampleRate, channelCount);

        Log.d("AUDIO", "Conversion OK → " + outputPath
                + " (" + pcmData.length + " bytes PCM)");
    }

    private void writeWavFile(String path, byte[] pcmData,
                              int sampleRate, int channels) throws IOException {
        int bitsPerSample = 16;
        int byteRate      = sampleRate * channels * bitsPerSample / 8;
        int blockAlign    = channels * bitsPerSample / 8;
        int dataSize      = pcmData.length;
        int chunkSize     = 36 + dataSize;

        try (FileOutputStream fos = new FileOutputStream(path)) {
            // RIFF header
            fos.write("RIFF".getBytes());
            fos.write(intToBytes(chunkSize));
            fos.write("WAVE".getBytes());

            // fmt chunk
            fos.write("fmt ".getBytes());
            fos.write(intToBytes(16));               // taille du chunk fmt
            fos.write(shortToBytes((short) 1));      // PCM = 1
            fos.write(shortToBytes((short) channels));
            fos.write(intToBytes(sampleRate));
            fos.write(intToBytes(byteRate));
            fos.write(shortToBytes((short) blockAlign));
            fos.write(shortToBytes((short) bitsPerSample));

            // data chunk
            fos.write("data".getBytes());
            fos.write(intToBytes(dataSize));
            fos.write(pcmData);
        }
    }


    private byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(value).array();
    }

    private byte[] shortToBytes(short value) {
        return ByteBuffer.allocate(2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(value).array();
    }

    private void sendAudioFile(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            Log.e("AUDIO", "Fichier introuvable : " + filePath);
            return;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(serverUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "audio/wav");
            conn.setRequestProperty("Content-Length", String.valueOf(audioFile.length()));
            Log.d("AUDIO", "Taille WAV : " + audioFile.length() + " bytes");

            try (OutputStream os = conn.getOutputStream();
                 FileInputStream fis = new FileInputStream(audioFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
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