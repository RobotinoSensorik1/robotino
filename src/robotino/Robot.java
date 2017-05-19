/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotino;

import java.util.ArrayList;
import rec.robotino.api2.Bumper;
import rec.robotino.api2.Com;
import rec.robotino.api2.OmniDrive;

public class Robot {

    protected final Com _com;
    protected final OmniDrive _omniDrive;
    protected final Bumper _bumper;

    public Robot() {
        _com = new MyCom();
        _omniDrive = new OmniDrive();
        _bumper = new Bumper();

    }

    public static void main(String[] args) {

        String hostname = "127.0.0.1";
        //String hostname = "172.26.1.1";
        if (args.length == 1) {
            hostname = args[0].toString();
        }

        Robot robotino = new Robot();

        try {
            robotino.connect(hostname, true);
            robotino.drive();
            robotino.disconnect();
            System.exit(0);
        } catch (Exception e) {
        }
    }

    public void connect(String hostname, boolean block) {
        System.out.println("Connecting...");
        _com.setAddress(hostname);
        _com.connectToServer(block);
    }

    public void disconnect() {
        _com.disconnectFromServer();
        System.out.println("Disconnecting...");
    }

    public boolean isConnected() {
        return _com.isConnected();
    }
    
    public void drive(){
        System.out.println("Driving...");
        float[] startVector = new float[]{0.0f,  0.1f};
        float[] dir = new float[2];
        float a = 0.0f; //speed Up
        
        while(_com.isConnected() && !_bumper.value()){
            
        }
    }
}
