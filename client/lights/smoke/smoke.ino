#include <Bridge.h>
#include <YunServer.h>
#include <YunClient.h>
#include <Servo.h>

YunServer server;
YunClient net;
Servo smoke;

void setup() {
  
  Serial.begin(9600);
  
  Bridge.begin();
  
  smoke.attach(5);
  
  server.listenOnLocalhost();
  
  server.begin();
  
}

void loop() {

  YunClient client = server.accept();
  
  if (client) {
    process(client);
    client.stop();
  }

  delay(50); // Poll every 50ms  

}

void process(YunClient client) {

  String command = client.readStringUntil('/');

  client.println(command);  


  // Check if the url contains the word "servo"
  if (command == "servo") {
    servoCommand(client);
  }

}

void servoCommand(YunClient client) {

  int pin;
  int value;

  // Get the servo Pin
  pin = client.parseInt();

  client.println(pin);
  
  // Check if the url string contains a value (/servo/6/VALUE)
  if (client.read() == '/') {
    
    value = client.parseInt();

    client.println(value);
    
    smoke.write(value);
    
    
    /*
    // smoke
    if (pin == 5) {
      smoke.write(value);
      if (value == 25) { // smoke on
        mqttClient.publish("Aurduino/HomeAutomation.Servo/101/event","{'Value':'1'}");
      } else {
        mqttClient.publish("Aurduino/HomeAutomation.Servo/101/event","{'Value':'0'}");
      }
    }
    */

  }

}
