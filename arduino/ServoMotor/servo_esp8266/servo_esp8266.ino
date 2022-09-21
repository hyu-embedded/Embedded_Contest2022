#include <ArduinoWiFiServer.h>
#include <BearSSLHelpers.h>
#include <CertStoreBearSSL.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiAP.h>
#include <ESP8266WiFiGeneric.h>
#include <ESP8266WiFiGratuitous.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266WiFiSTA.h>
#include <ESP8266WiFiScan.h>
#include <ESP8266WiFiType.h>
#include <WiFiClient.h>
#include <WiFiClientSecure.h>
#include <WiFiClientSecureBearSSL.h>
#include <WiFiServer.h>
#include <WiFiServerSecure.h>
#include <WiFiServerSecureBearSSL.h>
#include <WiFiUdp.h>

#include <Servo.h>

#define WIFI_SSID "HYU_IoT602"
#define WIFI_PASS "hyuiot602"
#define UDP_PORT 4210


WiFiUDP UDP;
Servo servo;
const uint8_t servoPin = D8;   
int angle = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);

  // Begin WiFi.
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  // Loop continuously while WiFi is not connected
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(100);
    Serial.print(".");
  }

  // Connected to WiFi
  Serial.println();
  Serial.print("Connected! IP address: ");
  Serial.println(WiFi.localIP());

  servo.attach(servoPin);

}

void loop() {
  // put your main code here, to run repeatedly:
  // scan from 0 to 180 degrees
  for (angle=10; angle <= 180; angle += 10) {
      //servo.write(angle);
      Serial.print("angle: ");
      Serial.print(angle);
      delay(100);
  }
  // now scan back from 180 to 0 degrees
  for (angle=180; angle >=0; angle -= 10) {
      //servo.write(angle);
      Serial.print("angle: ");
      Serial.print(angle);
      delay(100);
  }

}
