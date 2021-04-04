#include <SPIMemory.h>

uint32_t stepCountAddr = 20000; //address of step count 
uint32_t stepWidthAddr = 30000; //address of step width
uint32_t sessionTimeAddr = 40000; //address of total time of session
uint32_t leftArmCountAddr = 50000; //address of left arm swings
uint32_t rightArmCountAddr = 60000; //address of right arm swings


#if defined(ARDUINO_SAMD_ZERO) && defined(SERIAL_PORT_USBVIRTUAL)
// Required for Serial on Zero based boards
#define Serial SERIAL_PORT_USBVIRTUAL
#endif

#if defined (SIMBLEE)
#define BAUD_RATE 250000
#define RANDPIN 1
#else
#define BAUD_RATE 115200
#define RANDPIN A0
#endif


//SPIFlash flash(SS1, &SPI1);       //Use this constructor if using an SPI bus other than the default SPI. Only works with chips with more than one hardware SPI bus
SPIFlash flash(4); // initlize it


#define STATE_START 0
#define STATE_2 1
#define STATE_3 2

// port numbers
const int trig = 7;
const int echo = 6;
//right outer = 3
//left outer = 2

//variables - PIR sensors (belt)
int calibrationTime = 15;
int pirPin1 = 5;                 // PIR Out pin - LEFT ARM
int pirPin2 = 4;                 //PIR Out pin - RIGHT ARM
int pirStat1 = LOW;                   // PIR status: LEFT ARM
int pirStat2 = LOW;              //PIR status: RIGHT ARM
int rightcount = 0;
int leftcount = 0;
int loopcounter_left = 0;
int loopcounter_right = 0;
int loopy = 0;

// variables - ultrasonic sensor (right ankle)
float duration;

float distance = 0.00;
float cal_distance = 0.00;
float dist_rec = 0.00;
float timestamp3 = 0.00;
float timestamp4 = 0.00;
float upper = 20.00;
float lower = 2.00;
float widthLoopCounter = 0.00;
float totalWidth = 0.00;

int stepcount = 0;
bool move_occur = false;
bool result;

int state;


void setup() {
  //LEDs - one RED, one GREEN
  pinMode(10, OUTPUT); //GREEN
  pinMode(9, OUTPUT); //RED
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);
  digitalWrite(trig, LOW);
  digitalWrite(9, HIGH); //turn RED on - show that device isn't recording yet!
  Serial.begin(BAUD_RATE); // begin serial communication >> CHANGE THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

#if defined (ARDUINO_SAMD_ZERO) || (__AVR_ATmega32U4__)
  while (!Serial) ; // Wait for Serial monitor to open
#endif

  flash.begin();
  randomSeed(analogRead(RANDPIN));
  
  //set state
  state = STATE_START;
  move_occur = false;
//   Serial.print("Calibrating ");
//  for (int i = 0; i < calibrationTime; i++){
//    Serial.print(".");
//    delay(1000);
//  }
  Serial.println(" done");
  Serial.println("Sensor Active");
  digitalWrite(9, LOW); //turn RED LED off - device is ready to record
}
void loop() {
 
  //*************************************************************************
  //Connected to USB, has to wait for USB to send data
//Serial.println("before usb if statement");
if(Serial.available()){
  //if arduino sends h, read and send data
  delay(100);
  String cd = Serial.readString();  
  delay(100);
  if(cd=="upload"){
    //read data and send
     String outputString = " ";
     outputString="Start uploading...";
     Serial.print(outputString);
     
       //*************************************DELETE THIS*************************************************************
        //*************************************FOR TESTING ONLY*************************************************************
       String sC = String("034");
       flash.writeStr(stepCountAddr,sC); //write function address and string
       flash.writeStr(stepWidthAddr,sC); //write function address and string
       flash.writeStr(sessionTimeAddr,sC); //write function address and string
       flash.writeStr(leftArmCountAddr,sC); //write function address and string
       flash.writeStr(rightArmCountAddr,sC); //write function address and string
      //****************************************************************************************************************
      //****************************************************************************************************************


       if (flash.readStr(stepCountAddr, outputString)) { //read functio
        //check if the number is greater than 999 or less
         outputString = validate(outputString);
         Serial.print("stepCount:");
         delay(1000);
         writeString(outputString);
         delay(100);
 
      
      }

      if (flash.readStr(stepWidthAddr, outputString)) { //read function  
         //check if the number is greater than 999 or less
         outputString = validate(outputString); 
         Serial.print("stepWidth:"); 
         delay(1000);
         writeString(outputString);
         delay(100);
      
      }
      if (flash.readStr(sessionTimeAddr, outputString)) { //read function
         //check if the number is greater than 999 or less
         outputString = validate(outputString);
         Serial.print("sessionTime:"); 
         delay(1000);
         writeString(outputString);
         delay(100);
      
      }
      if (flash.readStr(leftArmCountAddr, outputString)) { //read function
        //check if the number is greater than 999 or less
         outputString = validate(outputString);
        Serial.print("leftArmCount:"); 
         delay(1000);
         writeString(outputString);
         delay(100);
      
      }
      if (flash.readStr(rightArmCountAddr, outputString)) { //read function
        //check if the number is greater than 999 or less
         outputString = validate(outputString);
        Serial.print("rightArmCount:");
         delay(1000);
         writeString(outputString);
         delay(100);
      
      }

      
    }
  
 }

  //************************* end transffering data **************************


  //**************************************************************************
  digitalWrite(10, HIGH); //turn Green LED ON!
  switch(state)
  {
    case STATE_START: //starting state
    
    Serial.println("in state 1");
      cal_distance = return_distance();
      pirStat1 = digitalRead(pirPin1); //left
    pirStat2 = digitalRead(pirPin2); //right
      if ((pirStat1 == 1) && (loopcounter_left == 0)) {
        loopcounter_left++;
        if (loopcounter_left == 1) {
          leftcount++;
        }
      }

      if ((pirStat2 == 1) && (loopcounter_right == 0)) {
        loopcounter_right++;
        if (loopcounter_right == 1) {
          rightcount++;
        }
      }
      if(cal_distance > upper) { //make sure person isn't standing still and has begun to walk
        timestamp3 = millis(); //movement begins; going to need to subtract waiting period for walking session total time
        state = STATE_2; //move to next state
        stepcount++;
        loopcounter_right = 0;
        loopcounter_left = 0;
      }
    break;
    case STATE_2:
      loopy++;
      timestamp4 = millis() - timestamp3;
      if (loopy == 1) {
      Serial.println("Elapsed time: " + String(timestamp4));
      }
      cal_distance = return_distance();
      pirStat1 = digitalRead(pirPin1); //left
      pirStat2 = digitalRead(pirPin2); //right
      
      if ((pirStat1 == 1) && (loopcounter_left == 0)) {
        loopcounter_left++;
        if (loopcounter_left == 1) {
          leftcount++;
        }
      }

      if ((pirStat2 == 1) && (loopcounter_right == 0)) {
        loopcounter_right++;
        if (loopcounter_right == 1) {
          rightcount++;
        }
      }
      
      
      
      if((cal_distance < upper) && (lower < cal_distance)) {
        move_occur = true;
        dist_rec = cal_distance;
        
        //Serial.println("!!!Distance: " + String(dist_rec) + " cm");
        
      }
      if((cal_distance > upper) && (move_occur)) {
        
        //Serial.println("!Move occur " + String(move_occur));
        //record dist_rec to MEMORY here (record final measurement before feet pass out of parallel)
      //
        
        stepcount++;
        widthLoopCounter++;
        totalWidth += dist_rec;
        move_occur = false;
        loopcounter_right = 0;
        loopcounter_left = 0;
        loopy = 0;
        
        
        Serial.println("Current step count: " + String(stepcount));
        Serial.println("LEFT SWING COUNT: " + String(leftcount));
        Serial.println("RIGHT SWING COUNT: " + String(rightcount));
        Serial.println("RECORDED Distance TO MEMORY: " + String(dist_rec) + " cm");
        state = STATE_3;

        //Saving all the variables
        saveToMemory();
      }
    break;
    case STATE_3:
      loopy++;
      timestamp4 = millis() - timestamp3;
      if (loopy == 1) {
      Serial.println("elapsed time: " + String(timestamp4));
      }
      
      cal_distance = return_distance();
      pirStat1 = digitalRead(pirPin1); //left
      pirStat2 = digitalRead(pirPin2); //right
      
      if ((pirStat1 == 1) && (loopcounter_left == 0)) {
        loopcounter_left++;
        if (loopcounter_left == 1) {
          leftcount++;
        }
      }

      if ((pirStat2 == 1) && (loopcounter_right == 0)) {
        loopcounter_right++;
        if (loopcounter_right == 1) {
          rightcount++;
        }
      }
      //Serial.println("3rd state cal Distance: " + String(cal_distance) + " cm");
      if((lower < cal_distance) && (cal_distance < upper)) { //record first instant of feet passing in parallel
        
        move_occur = true;
        dist_rec = cal_distance;
        //Serial.println("state 3Distance: " + String(dist_rec) + " cm");
        
      }
      if((cal_distance > upper) && (move_occur)) {
        //Serial.println("BEGUN TO MOVEDistance: " + String(cal_distance) + " cm");
        //Serial.println("Move occur " + String(move_occur));
        //record dist_rec to MEMORY here (record final measurement before feet pass out of parallel)
      //
      
        
        stepcount++;
        widthLoopCounter++;
        totalWidth += dist_rec;
        move_occur=false;
        loopcounter_right = 0;
        loopcounter_left = 0;
        loopy = 0;
        Serial.println("RECORDED Distance: " + String(dist_rec) + " cm");
        
        Serial.println("Current step count: " + String(stepcount));
        Serial.println("LEFT SWING COUNT: " + String(leftcount));
        Serial.println("RIGHT SWING COUNT: " + String(rightcount));
        state = STATE_2;

        //Saving dist_rec, stepcount, leftswing, and right swing to memory
        
        saveToMemory();
        
      }
      
    break;
  }




}

void saveToMemory(){
  Serial.println("Writing to Memory");
  String sC = String(stepcount); //converting int to string
  String sW = String(totalWidth / widthLoopCounter, 3); //converting float to string
  String sT = String(timestamp4);
  String sLAC = String(leftcount);
  String sRAC = String(rightcount);
  flash.writeStr(stepCountAddr, sC); //write function address and string
  flash.writeStr(stepWidthAddr, sW); 
  flash.writeStr(sessionTimeAddr, sT);
  flash.writeStr(leftArmCountAddr, sLAC);
  flash.writeStr(rightArmCountAddr, sRAC);
   

  
}

//function declaration - everytime we need to find out what the DISTANCE is, we call this function
float return_distance() {
  // read echo and calculate distance
      
      digitalWrite(trig, HIGH); 
      delayMicroseconds(4000); 
      digitalWrite(trig, LOW); 
    duration = pulseIn(echo, HIGH);
    distance = duration * 0.034 / 2;
    //Serial.println("Distance: " + String(distance) + " cm");
    return distance; //return the value of distance
}

//Reads a string from Serial
bool readSerialStr(String &inputStr) {
  if (!Serial)
    Serial.begin(115200);
  while (Serial.available()) {
    inputStr = Serial.readStringUntil('\n');
    Serial.println(inputStr);
    return true;
  }
  return false;
}


//trasfer data
 void writeString(String stringData) { // Used to serially push out a String with Serial.write()

  for (int i = 0; i < stringData.length(); i++)
  {
    int bytesSent546 = Serial.write(stringData[i]);   // Push each char 1 by 1 on each loop pass
       delay(1000);
  }

}// end writeString


 String  validate(String outputString){

        if (outputString.length()<1){
          return "000" ;
        }
        
        else if (outputString.length()<2){
          return "00" +  outputString;
        }
        else if (outputString.length()<3){
          return "0" +  outputString;
        }
       else if (outputString.length()<4){
          return outputString;
        }

}
