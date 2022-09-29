#include <RF24.h>
#include <RF24_config.h>
#include <nRF24L01.h>

#include <SPI.h>


RF24 radio(7,8);
const int trig = 6;
const int echo = 5;
const int num_iteration = 5;


int angle = 0;
//float distance = 0;

float height = 33;  // layer height
float dist_mtow; // height installing water sensor
const float alpha = 0.3;
float waterlevel = 0;




void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  // Set Microwave Sensor
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);

  // Set RF24
  radio.begin();
  radio.setPALevel(RF24_PA_MAX);
  radio.setChannel(0x76);
  radio.openWritingPipe(0xF0F0F0F0E2LL);
  radio.enableDynamicPayloads();
  radio.powerUp();

  dist_mtow = 29.5; // height installing water sensor

}

void loop() {

  waterlevel = get_waterlevel(num_iteration);
  Serial.print("Waterlevel: ");
  Serial.print(waterlevel);
  Serial.println("cm");

  String result = String(int(waterlevel * 100), DEC);

  for (int i = 0; i < 20; i++) {
      radio.write(result.c_str(), result.length());
      delay(50);
  }
}

float get_height(int num_iteration) {
    int count = 0;
    float sum = 0;

    while(count < num_iteration) {
        digitalWrite(trig, LOW);
        delayMicroseconds(2);
        digitalWrite(trig, HIGH);
        delayMicroseconds(10);
        unsigned long duration = pulseIn(echo, HIGH);
        float distance = ((float)(340*duration)/10000)/2;
//        Serial.print("Distance: ");
//        Serial.println(distance);
        Serial.println(dist_mtow);
        if (distance >= (1-alpha)*dist_mtow && distance <= (1+alpha)*dist_mtow) {
            sum += distance;
            Serial.print("Distance: ");
            Serial.println(distance);
            count++;
        }
        delay(100); 
    }
    dist_mtow = sum / num_iteration;

    return dist_mtow;
}

float get_waterlevel(int num_iteration) {
    return height - get_height(num_iteration);
}
