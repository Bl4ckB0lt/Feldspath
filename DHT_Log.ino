#include <Arduino_MKRIoTCarrier.h>
#include <WiFiNINA.h>
#include <PubSubClient.h>
#include "arduino_secrets.h"

char ssid[] = SECRET_SSID;
char pass[] = SECRET_PASS;
int status  = WL_IDLE_STATUS;

// === Configuration MQTT ===
const char* mqttServer = "broker.emqx.io";
const int   mqttPort   = 1883;
const char* mqttUser   = "";
const char* mqttPass   = "";

MKRIoTCarrier carrier;
WiFiClient   wifiClient;
PubSubClient mqtt(wifiClient);

void setup() {
  Serial.begin(9600);
  while (!Serial);

  carrier.begin();

  Serial.print("Connexion au Wi-Fi : ");
  Serial.println(ssid);
  while (status != WL_CONNECTED) {
    status = WiFi.begin(ssid, pass);
    delay(10000);
  }
  Serial.println("Wi-Fi connecté !");

  mqtt.setServer(mqttServer, mqttPort);
}

void loop() {
  if (!mqtt.connected()) {
    reconnectMQTT();
  }
  mqtt.loop();

  float temperature  = carrier.Env.readTemperature();
  float humidity     = carrier.Env.readHumidity();
  float pressure     = carrier.Pressure.readPressure();
  float co2          = carrier.AirQuality.readCO2();
  float gasResistor  = carrier.AirQuality.readGasResistor();
  float iaq          = carrier.AirQuality.readIAQ();
  float iaqAccuracy  = carrier.AirQuality.readIAQAccuracy();
  float staticIaq    = carrier.AirQuality.readStaticIAQ();
  float voc          = carrier.AirQuality.readVOC();

  Serial.println("--- Nouvelles mesures ---");
  Serial.print("Température : "); Serial.println(temperature);
  Serial.print("Humidité    : "); Serial.println(humidity);
  Serial.print("Pression    : "); Serial.println(pressure);
  Serial.print("CO2         : "); Serial.println(co2);
  Serial.print("Gas Resistor: "); Serial.println(gasResistor);
  Serial.print("IAQ         : "); Serial.println(iaq);
  Serial.print("IAQ Accuracy: "); Serial.println(iaqAccuracy);
  Serial.print("Static IAQ  : "); Serial.println(staticIaq);
  Serial.print("VOC         : "); Serial.println(voc);

  carrier.display.fillScreen(ST77XX_BLACK);
  carrier.display.setTextColor(ST77XX_WHITE);
  carrier.display.setTextSize(2);
  carrier.display.setCursor(20, 50);
  carrier.display.print("Temp: "); carrier.display.print((int)temperature); carrier.display.println(" C");
  carrier.display.print("Hum:  "); carrier.display.print((int)humidity);    carrier.display.println(" %");
  carrier.display.print("Pres: "); carrier.display.println((int)pressure);
  carrier.display.print("CO2:  "); carrier.display.println((int)co2);
  carrier.display.print("IAQ:  "); carrier.display.println((int)iaq);
  carrier.display.print("VOC:  "); carrier.display.println((int)voc);

  envoyerDonneesMQTT(temperature, humidity, pressure, co2, gasResistor, iaq, iaqAccuracy, staticIaq, voc);

  delay(3000);
}

void reconnectMQTT() {
  while (!mqtt.connected()) {
    Serial.print("Connexion MQTT...");
    if (mqtt.connect("ArduinoMKR", mqttUser, mqttPass)) {
      Serial.println(" connecté !");
    } else {
      Serial.print(" échec, rc=");
      Serial.println(mqtt.state());
      delay(5000);
    }
  }
}

void envoyerDonneesMQTT(float temp, float hum, float pres, float co2,
                         float gas, float iaq, float iaqAcc, float siaq, float voc) {
  String payload = "";
  payload += String(temp, 2) + "/";
  payload += String(hum, 2)  + "/";
  payload += String(pres, 2) + "/";
  payload += String(co2, 2)  + "/";
  payload += String(gas, 2)  + "/";
  payload += String(iaq, 2)  + "/";
  payload += String(iaqAcc, 2) + "/";
  payload += String(siaq, 2) + "/";
  payload += String(voc, 2);

  mqtt.publish("Feldspath/data", payload.c_str());

  carrier.display.print("Données envoyées : " + payload);
}