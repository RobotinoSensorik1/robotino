/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotino;

import java.util.ArrayList;

/**
 *
 * @author Saskia
 */
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
        return deg;
    }

    public float getAngleToRotate(ArrayList<Sensor> sensors, int numberOfDetectedSensors) {

        //check, if more than 2 sensors are detected
        if (sensors.size() > 2) {

        } else {
            //check, if sensors are next to each other
            int differenz = (int) (sensors.get(0).getNumberOfSensor() - sensors.get(1).getNumberOfSensor());
            if (differenz == 1 || differenz == -1) {
                double a = sensors.get(0).getDistance();
                double b = sensors.get(1).getDistance();
                double gamma = 40.0;
                double c = Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(gamma));
                
                //c = a * sin(γ) / sin(90)
                
            } else if (differenz == 2 || differenz == -2) {
                return 40f*3;
            } else if (differenz > 2 || differenz < -2) {
                return 180f;
            }

        }

//        float deg = 0;
//        float a = 0.0f;
//        float b = 0.0f;
//        float alpha = 0.0f;
//        if (sensors.size() < 3) {
//            //get nearest distance (min)
//            
//            
//            
//            //a=0; b=1
//            //0-1=-1
//            //1-0=1
//            int differenz = (int) (sensors.get(0).getNumberOfSensor() - sensors.get(1).getNumberOfSensor());
//            if ((differenz == -1) || (differenz == 1)) {
//                switch (differenz) {
//                    case 1:
//                        a = sensors.get(1).getDistance() + 0.5f;
//                        b = sensors.get(0).getDistance() + 0.5f;
//                        break;
//                    case -1:
//                        a = sensors.get(0).getDistance() + 0.5f;
//                        b = sensors.get(1).getDistance() + 0.5f;
//                        break;
//                }
//                
//                
//            }
//
//        }
        return 50f;
    }
//    public double wayOfspeedUp(double a, double t) {
//        //constant speed up
//        double s = 0.0;
//        s=(a/2)+(t*t);
//        return s;
//    }
//    
//    public double wayOfConstantSpeed(double v, double t){
//        double s = 0.0;
//        s= v*t;
//        return s;
//    }
//    
//    public double timeOfConstantSpeed(double s, double v){
//        double t = 0.0;
//        t = s/v;
//        return t;
//    }
//    
//    public double timeOfSpeedUp(double a,double s){
//        double t = 0.0;
//        t=Math.sqrt((2*s/a));
//        return t;
//    }
//    

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
