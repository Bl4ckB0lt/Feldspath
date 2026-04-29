#include <WiFiNINA.h>

#include "arduino_secrets.h"


void printData(){

  Serial.println("Board Information");

  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address :");
  Serial.println(ip);

  Serial.println();
  Serial.println("Network Information");
  Serial.print("SSID :");
  Serial.println(WiFi.SSID());

  long rssi = WiFi.RSSI();
  Serial.print("signal strenght RSSI :");
  Serial.println(rssi);

  
}

