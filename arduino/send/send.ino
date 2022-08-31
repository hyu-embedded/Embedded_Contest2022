#include<SPI.h>
#include<RF24.h>

// ce, csn ppins
RF24 radio(9, 10);

//const int water_pin = D5;


void setup() {
  // put your setup code here, to run once:
  radio.begin();
  radio.setPALevel(RF24_PA_MAX);
  radio.setChannel(0x76);
  radio.openWritingPipe(0xF0F0F0F0E1LL);
  radio.enableDynamicPayloads();
  radio.powerUp();

  // water sensor
  Serial.begin(115200);
}

void loop() {
  // put your main code here, to run repeatedly:
  const char text[] = "Hello World!";
  radio.write(&text, sizeof(text));
  Serial.println(text);
  
  
  // send output of water sensor to raspberry pi
  // int to char array: https://arduino.stackexchange.com/questions/42986/convert-int-to-char
  //int val = analogRead(water_pin);
  //char resut[16];
  //itoa(val, result, 10);
  //radio.write(&result, sizeof(result));
  
  delay(1000); 

}
