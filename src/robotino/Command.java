/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotino;

import java.util.ArrayList;

public class Command {   
    
   // Calculations
    public float getAngleToRotate(Sensor s) {
        float deg = 0;
        //turn right
        if (s.getNumberOfSensor() < 3) {
            deg = (s.getNumberOfSensor() * -40);
        } //turn left
        else if (s.getNumberOfSensor() > 6) {
            deg = ((9 - s.getNumberOfSensor()) * 40);
        }
        if(s.getNumberOfSensor() == 0) {
            deg = 75.0f;
        }
        
        return deg;
    }
    public float getAngleToRotate(ArrayList<Sensor> sensors, int numberOfDetectedSensors) {
        //check, if more than 2 sensors are detected
        boolean left = false;
        double a = 0.0;
        double b = 0.0;
        double c = 0.0;
        double alpha = 0.0;
        int beta = 0;
        double beta2;
        double gamma = 0.0;
        
       if (sensors.size() > 2) {
            
           if(isSensor0(sensors)) {
               return 180.0f;
           }
           
           if(checkFrontSensor(sensors.get(0)) && checkFrontSensor(sensors.get(1)) && checkFrontSensor(sensors.get(2))){
               return 180.0f;
           }
           
           if(sensors.get(0).getNumberOfSensor() < 4){
               return -90.0f;
           }else{
               return 90.0f;
           }
             
              } else {
           //check, if sensors are next to each other
           int differenz = (int) (sensors.get(0).getNumberOfSensor() - sensors.get(1).getNumberOfSensor());
           if(differenz < -1) {
               gamma = 40.0;
               if(sensors.get(0).getNumberOfSensor() < 5) {
                   if(sensors.get(0).getNumberOfSensor() == 0 && sensors.get(1).getNumberOfSensor() == 8){
                       return -110.0f;
                   }else if(sensors.get(0).getNumberOfSensor() == 0 && sensors.get(1).getNumberOfSensor() == 1){
                       return 110.0f;
                   }
                   
                   //linke Seite (Drehwinkel muss - sein)
                   a = sensors.get(0).getDistance();
                   b = sensors.get(1).getDistance();
                   left = true;
               } else{
                   //rechte Seite (Drehwinkel muss + sein)
                   a = sensors.get(1).getDistance();
                   b = sensors.get(0).getDistance();
           }
               c = Math.sqrt(a * a + b * b -2 * a * b * Math.cos(gamma));
               beta = (int) Math.acos((b * b - c * c -a + a) / (-2 * c * a));
               double gamma2 = 180.0 - beta;
             
               if(beta == 90) {
                   beta2 = (90.0 - gamma2);
               } else {
                   beta2 = (90.0 - (90.0 - gamma2));
               }
              
               if(left) {
                   return(float) -beta2;
               }else{
                   return(float) beta2; 
               }
           } else {
               return 40.0f;
           }
       }
    }
        private boolean checkFrontSensor(Sensor s){
        boolean isFront = false;
        if(s.getNumberOfSensor() == 0){
            isFront = true;
        }else if(s.getNumberOfSensor() == 1){
            isFront = true;
        }else if(s.getNumberOfSensor() == 8){
            isFront = true;
        }
        
        return isFront;
    }
    
    private boolean isSensor0(ArrayList<Sensor> sensors){
        boolean is0 = false;
        
        for(Sensor s: sensors){
            if(s.getNumberOfSensor()==0){
            is0 = true;
            }
        }
        return is0;
       }
               
    
   
  
    public float speedUpOfSpeedUp(float v, float t) {
        float a = 0.0f;
        a = v / t;
        return a;
    }
    public float getAngularSpeed(float deg) {
        float w = 0.0f;
        w = (float) (2 * Math.PI * (deg / 360));
        return w;
    }
// Driving    
    public void driveForward(float[] inArray, float[] outArray, float a) {
        outArray[0] = inArray[0] + a; //x mit a
        outArray[1] = inArray[1]; //y
    }
    public void driveBack(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0] - 0.1f;
        outArray[1] = inArray[1];
    }
    public void driveLeft(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0];
        outArray[1] = inArray[1] + 0.005f;
    }
    public void driveRight(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0];
        outArray[1] = inArray[1] - 0.5f;
    }
    public void driveDiagonal(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0] + 0.005f;
        outArray[1] = inArray[1] + 0.005f;
    }
    public void rotate(float[] inArray, float[] outArray, int deg) {
        //auf den Punkt drehen !!! Überarbeiten
        float rad = 2 * (float) Math.PI / 360.0f * deg;
        outArray[0] = (float) Math.cos(rad) * inArray[0] - (float) Math.sin(rad) * inArray[1];
        outArray[1] = (float) Math.sin(rad) * inArray[0] + (float) Math.cos(rad) * inArray[1];
    }
    public void rotateInPlace(float[] v, float deg) {
        float rad = 2 * (float) Math.PI / 360.0f * deg;
        float tmp = v[0];
        v[0] = (float) Math.cos(rad) * v[0] - (float) Math.sin(rad) * v[1];
        v[1] = (float) Math.sin(rad) * tmp + (float) Math.cos(rad) * v[1];
    }
}



