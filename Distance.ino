/*
* Ultrasonic Sensor HC-SR04 and Arduino Tutorial
*
* by Dejan Nedelkovski,
* www.HowToMechatronics.com
*
*/
// defines pins numbers
const int trigPinPost = 9;
const int echoPinPost = 10;
const int trigPinDel = 5;
const int echoPinDel = 6;
const int BAUDRATE = 9600;
// defines variables
long durationPost;
long durationDel;
int distancePost;
int distanceDel;

void setup() {
   pinMode(trigPinPost, OUTPUT); // Sets the trigPin as an Output
   pinMode(echoPinPost, INPUT); // Sets the echoPin as an Input
   pinMode(trigPinDel, OUTPUT); // Sets the trigPin as an Output
   pinMode(echoPinDel, INPUT); // Sets the echoPin as an Input
   Serial.begin(BAUDRATE); // Starts the serial communication
}
void loop() {
   // Clears the trigPin
   digitalWrite(trigPinPost, LOW);
   delayMicroseconds(2);
   // Sets the trigPin on HIGH state for 10 micro seconds
   digitalWrite(trigPinPost, HIGH);
   delayMicroseconds(10);
   digitalWrite(trigPinPost, LOW);
   // Reads the echoPin, returns the sound wave travel time in microseconds
   durationPost = pulseIn(echoPinPost, HIGH);
   // Calculating the distance
   distancePost= durationPost*0.034/2;
   // Prints the distance on the Serial Monitor
   Serial.print("DP");
   Serial.print(distancePost);
   Serial.print("\n");

   delay(500);
   
      // Clears the trigPin
   digitalWrite(trigPinDel, LOW);
   delayMicroseconds(2);
   // Sets the trigPin on HIGH state for 10 micro seconds
   digitalWrite(trigPinDel, HIGH);
   delayMicroseconds(10);
   digitalWrite(trigPinDel, LOW);
   // Reads the echoPin, returns the sound wave travel time in microseconds
   durationDel = pulseIn(echoPinDel, HIGH);
   // Calculating the distance
   distanceDel = durationDel*0.034/2;
   // Prints the distance on the Serial Monitor
   Serial.print("DD");
   Serial.print(distanceDel);
   Serial.print("\n");

   delay(500);
}
