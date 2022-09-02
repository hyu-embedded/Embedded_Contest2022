const int trig = 2;
const int echo = 4;

//초음파 : 29us에 1cm를 이동

//delayMicroseconds(us)
//duration = pulseIn(pin, HIGH);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(trig,OUTPUT); //trig 발사
  pinMode(echo,INPUT); //echo 받기
}
float distance = 0;
void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(trig,LOW);
  delayMicroseconds(2);
  digitalWrite(trig,HIGH);
  delayMicroseconds(10);
  digitalWrite(trig,LOW);
  unsigned long duration = pulseIn(echo, HIGH);
  Serial.print("Duration: ");
  Serial.println(duration);
  distance = ((float)(340*duration)/10000)/2;
  //if(distance <= 0.5) return;
  Serial.print(distance);
  Serial.println("CM");
  delay(100);
}
