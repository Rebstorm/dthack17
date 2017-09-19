

/*
  ESP8266 Blink by Simon Peter
  Blink the blue LED on the ESP-01 module
  This example code is in the public domain

  The blue LED on the ESP-01 module is connected to GPIO1
  (which is also the TXD pin; so we cannot use Serial.print() at the same time)

  Note that this sketch uses LED_BUILTIN to find the pin with the internal LED
*/
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

#define LEDBlau D4

void setup() {
  Serial.begin(9600);
  char pass[] = "#dthack17";
  char ssid[] = "TelekomHackathon";
  int status;
  pinMode(LEDBlau, OUTPUT);     // Initialize the LED_BUILTIN pin as an output
  pinMode(LED_BUILTIN, OUTPUT);
  Serial.print("Trying to connect to ");
  Serial.println(ssid);
  status = WiFi.begin(ssid, pass);
  status = WiFi.waitForConnectResult();
  if (status != WL_CONNECTED) {
    Serial.println("Connection Failed");
    while (true) {}
  }
  Serial.println("Connected.");
  Serial.print("MAC Addr: ");
  Serial.println(WiFi.macAddress());
  Serial.print("IP Addr:  ");
  Serial.println(WiFi.localIP());
  Serial.print("Subnet:   ");
  Serial.println(WiFi.subnetMask());
  Serial.print("Gateway:  ");
  Serial.println(WiFi.gatewayIP());
  Serial.print("DNS Addr: ");
  Serial.println(WiFi.dnsIP());
  Serial.print("Channel:  ");
  Serial.println(WiFi.channel());
  Serial.print("Status: ");
  Serial.println(WiFi.status());

}

// the loop function runs over and over again forever
void loop() {
  HTTPClient http;
  http.begin("http://labs.basti.site/?beaconid=8644d8ef-b649-4a86-b40f-382f89d0bcd0&raw=1");
  int httpCode = http.GET();
  Serial.print("HTTPStatus: ");
  Serial.println(httpCode);

  String payload = http.getString();
  Serial.print("HTTPPayload: ");
  Serial.println(payload);
  http.end();
  if (payload == "0") {
    Serial.print("Translate: ");
    Serial.println("off");
    digitalWrite(LEDBlau, HIGH);  
    digitalWrite(LED_BUILTIN, HIGH);
  }
  if (payload == "1") {
    Serial.print("Translate: ");
    Serial.println("Gruen");
    digitalWrite(LEDBlau, LOW);
    digitalWrite(LED_BUILTIN, HIGH);
  }
  if (payload == "2") {
    Serial.print("Translate: ");
    Serial.println("Rot");
    digitalWrite(LEDBlau, HIGH);   
    digitalWrite(LED_BUILTIN, LOW);
  }
  if (payload == "3") {
    Serial.print("Translate: ");
    Serial.println("Gelb");
  }
  if (payload == "4") {
    Serial.print("Translate: ");
    Serial.println("Gelb blink");
  }
  delay(2000);                      // Wait for two seconds (to demonstrate the active low LED)
}
