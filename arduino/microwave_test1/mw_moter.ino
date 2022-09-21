#include <RF24.h>
#include <RF24_config.h>
#include <nRF24L01.h>

#include <SPI.h>
#include <Servo.h>


Servo servo;
RF24 radio(7,8);
const int trig = 6;
const int echo = 5;
const int num_iteration = 100;


int angle = 0;
//float distance = 0;

float height = 180;
const float alpha = 0.05;
float waterlevel = 0;




void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  // Set Microwave Sensor
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);

  // Set sg90 Servo motor
  servo.attach(2);

  // Set RF24
  radio.begin();
  radio.setPALevel(RF24_PA_MAX);
  radio.setChannel(0x76);
  radio.openWritingPipe(0xF0F0F0F0E1LL);
  radio.enableDynamicPayloads();
  radio.powerUp();

  Serial.println("Measure actual height");
  height = get_height(num_iteration);
  Serial.print("Actual height is about: ");
  Serial.print(height);
  Serial.println("cm");

  Serial.println("Measure waterlevel");
  waterlevel = get_waterlevel(num_iteration);
  Serial.print("Waterlevel is about: ");
  Serial.print(waterlevel);
  Serial.println("cm");

}

void loop() {
  // put your main code here, to run repeatedly:
  //const char text[] = "Hi!!";
  //radio.write(&text, sizeof(text));
  //Serial.println(text);
  //delay(10);

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

    while(count <= num_iteration) {
        digitalWrite(trig, LOW);
        delayMicroseconds(2);
        digitalWrite(trig, HIGH);
        delayMicroseconds(10);
        unsigned long duration = pulseIn(echo, HIGH);
        float distance = ((float)(340*duration)/10000)/2;

        if (distance >= (1-alpha)*height && distance <= (1+alpha)*height) {
            sum += distance;
            count++;
        }
        delay(100); 
    }
    float result = sum / num_iteration;
    return result;
}

float get_waterlevel(int num_iteration) {
    return height - get_height(num_iteration);
}
