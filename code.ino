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
