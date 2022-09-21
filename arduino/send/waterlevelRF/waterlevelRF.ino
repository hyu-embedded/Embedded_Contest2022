#include <SPI.h>
#include <RF24.h>

// ce, csn ppins
RF24 radio(9,10);

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
  const char text[] = "This is Waterlevel-RF";
  radio.write(&text, sizeof(text));
  Serial.println(text);

  delay(1000);

}
