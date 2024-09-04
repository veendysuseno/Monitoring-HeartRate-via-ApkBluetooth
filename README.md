# Heart Rate Monitor with PulseSensor, LCD, Buzzer, and HC-05 with Arduino Uno

This Arduino project is designed to measure heart rate using a PulseSensor, display the results on an LCD screen, and provide feedback through a buzzer. Additionally, the heart rate data is sent via Bluetooth using the HC-05 module.

## Components Used

- Arduino Uno
- PulseSensor
- LCD 16x2 with I2C module
- LED
- Buzzer
- HC-05 Bluetooth module
- Jumper wires
- Breadboard

## Circuit Diagram

1. **PulseSensor**: Connect the signal pin to A1, VCC to 5V, and GND to GND.
2. **LCD**: Connect the SDA and SCL pins to the corresponding SDA and SCL pins on the Arduino. Connect VCC to 5V and GND to GND.
3. **LED**: Connect the anode (+) to digital pin 13 and the cathode (-) to GND.
4. **Buzzer**: Connect the positive pin to digital pin 4 and the negative pin to GND.
5. **HC-05**: Connect the TX pin to pin 10, RX pin to pin 11, VCC to 5V, and GND to GND.

## Code

The code is written in C++ and includes the necessary libraries to interface with the PulseSensor, LCD, and HC-05 Bluetooth module. The heart rate is measured and displayed on the LCD, and the data is also sent via Bluetooth.

```cpp
#define USE_ARDUINO_INTERRUPTS true
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <PulseSensorPlayground.h>
#include <SoftwareSerial.h>

LiquidCrystal_I2C lcd(0x27, 16, 2);
const int PulseWire = 1;
const int pinLED = 13;
const int pinBuzzer = 4;
const int Threshold = 550;
int bpm = 0;

PulseSensorPlayground pulseSensor;
SoftwareSerial BTSerial(10, 11); // RX, TX untuk HC-05

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600); // Inisialisasi komunikasi serial dengan HC-05
  lcd.begin(16, 2);
  lcd.backlight();
  pulseSensor.analogInput(PulseWire);
  pulseSensor.blinkOnPulse(pinLED);
  pulseSensor.setThreshold(Threshold);
  pinMode(pinBuzzer, OUTPUT);

  if (pulseSensor.begin()) {
    Serial.println("Cek Detak Jantungmu!");
    BTSerial.println("Cek Detak Jantungmu!"); // Kirim pesan ke HC-05
    delay(1000);
    lcd.setCursor(0, 0);
    lcd.print("Cek Jantungmu");
    delay(1000);
    lcd.clear();
  }
}

void loop() {
  bpm = pulseSensor.getBeatsPerMinute();

  if (pulseSensor.sawStartOfBeat()) {
    delay(20);
    for (int i = 11; i >= 0; i--) {
      lcd.setCursor(0, 0);
      lcd.print("Menghitung . . .");
      Serial.println("Menghitung . . . .");
      BTSerial.println("Menghitung . . . ."); // Kirim pesan ke HC-05
      delay(500);
      lcd.clear();
    }
    buzz();
    lcd_tampil();
  }
}

void buzz() {
  if (bpm > 0) {
    digitalWrite(pinBuzzer, HIGH);
    delay(250);
    digitalWrite(pinBuzzer, LOW);
    delay(250);
  }
}

void lcd_tampil() {
  Serial.print("BPM : ");
  Serial.println(bpm);
  BTSerial.print("BPM : ");    // Kirim data BPM ke HC-05
  BTSerial.println(bpm);
  delay(2000);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Nilai BPM Anda");
  lcd.setCursor(0, 1);
  lcd.print("> BPM = ");
  lcd.print(bpm);
  delay(3000);
  lcd.clear();
}
```

## How It Works

- The PulseSensor measures the heart rate, which is then processed by the Arduino.
- The measured BPM (Beats Per Minute) is displayed on the LCD.
- A buzzer provides auditory feedback each time a beat is detected.
- The BPM data is sent via Bluetooth to a paired device using the HC-05 module.

## Usage

- Upload the code to your Arduino Uno.
- Connect the components as described in the circuit diagram.
- Pair the HC-05 Bluetooth module with your device (e.g., smartphone, computer).
- Monitor the heart rate data on the LCD and receive it via Bluetooth.

## Conclusion

- This heart rate monitor project effectively combines hardware and software to provide real-time heart rate measurements. By integrating the PulseSensor with an LCD display and adding Bluetooth capability, the project offers a versatile solution for monitoring heart rate data. Whether for educational purposes or personal health tracking, this project demonstrates how Arduino can be used to develop practical and interactive health monitoring systems.
"# Monitoring-HeartRate-via-ApkBluetooth" 
