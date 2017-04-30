/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotino;

/**
 *
 * @author Saskia
 */
public class Command {
    
    public void driveForward(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0] + 0.5f; //x
        outArray[1] = inArray[1] +0.5f; //y
    }
    
    public void driveBack(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0] - 0.1f;
        outArray[1] = inArray[1];
    }
    
    public void driveLeft(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0];
        outArray[1] = inArray[1] + 0.5f;
    }
    
    public void driveRight(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0];
        outArray[1] = inArray[1] - 0.5f;
    }
    
    public void driveDiagonal(float[] inArray, float[] outArray) {
        outArray[0] = inArray[0] + 1.0f;
        outArray[1] = inArray[1] + 2.0f;
    }
    
    public void rotate(float[] inArray, float[] outArray, int deg){
        outArray[0] = inArray[0] + 1.0f;
        outArray[1] = inArray[1] + 2.0f;
    }
}
