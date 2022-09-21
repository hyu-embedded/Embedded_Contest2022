#include <printf.h>
#include <RF24_config.h>
#include <RF24.h>
#include <nRF24L01.h>

#include<SPI.h>
#include<RF24.h>
RF24 radio(7,8);



int water_pin =A0;
int waterlevel = 0;
int threshold = 0;

void setup() {
  // put your setup code b  here, to run once:
  radio.begin();
  radio.setPALevel(RF24_PA_MAX);
  radio.setChannel(0x76);
  radio.openWritingPipe(0xF0F0F0F0E2LL);
  radio.enableDynamicPayloads();
  radio.powerUp();

  // water sensor
  Serial.begin(115200);
  pinMode(A0, INPUT);


  
}
void loop() {
  // put your main code here, to run repeatedly:
  waterlevel = analogRead(A0);

  if (waterlevel > threshold) {
    String result = String(waterlevel, DEC);
    radio.write(result.c_str(), result.length());
    Serial.println(result); 
  }
  
  
  //const char text[] = "Hello World! yeongbin~";
  //radio.write(&text, sizeof(text));
  //Serial.println(text);

  
  delay(10); 

}
