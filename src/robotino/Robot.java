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
    private Command command;
    protected final ArrayList<Sensor> sensors;
    private ArrayList<Sensor> detectedSensors = new ArrayList<Sensor>();
    private boolean drive = false;

    public Robot() {
        _com = new MyCom();
        _omniDrive = new OmniDrive();
        _bumper = new Bumper();
        command = new Command();
        sensors = new ArrayList<Sensor>();

        // Sensoren anlegen als eigene Threads und den Thread starten
        for (int i = 0; i < 9; i++) {
            sensors.add(new Sensor(i, this._com));
            sensors.get(i).start();
        }

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

    public void drive() throws InterruptedException {
        System.out.println("Driving...");
        float[] startVector = new float[]{0.0f, 0.1f};
        float[] dir = new float[2];
        float a = 0.0f;
        float deg = 0.0f;
        int counter = 0;
        while (_com.isConnected() && !_bumper.value()) {
          
            while (isHindernis()) {
                
                if(detectedSensors.size() == 1){
                    //rotate in place
                    //get angle to rotate
                    deg = command.getAngleToRotate(detectedSensors.get(0));
                    
                }
                else if (detectedSensors.size() == 2) {
                    //check for free side
                    deg = command.getAngleToRotate(detectedSensors, 2);
                } else if(detectedSensors.size() > 2){
                    deg = command.getAngleToRotate(detectedSensors, detectedSensors.size());
                }
                float w = command.getAngularSpeed(deg);
                    a = command.speedUpOfSpeedUp(0.00f, 0.1f);
                    command.rotateInPlace(dir, w);
                    _omniDrive.setVelocity(a * 0.0f, a * 0.0f, w);
                    Thread.sleep(275);
                    //geradeausfahren
                    a = command.speedUpOfSpeedUp(0.0000005f, 0.1f);
                    command.driveForward(dir, dir, a);
                    _omniDrive.setVelocity(dir[0], dir[1], 0);
                    detectedSensors.clear();
                    break;
            }
            a = command.speedUpOfSpeedUp(0.00005f, 0.1f);
            command.driveForward(dir, dir, a);
            _omniDrive.setVelocity(dir[0], dir[1], 0);
            Thread.sleep(100);
        }
    }
    
    public boolean isHindernis() {
        boolean hindernis = false;
        for (Sensor s : sensors) {
            if (s.getDistance() < 0.409) {
                detectedSensors.add(s);

                System.out.println(s.getNumberOfSensor() + " stopped by\t" + s.getDistance());

                hindernis = true;

            }
        }

        return hindernis;
    }

    private Sensor getNearestSensor() {
        if (detectedSensors != null) {
            Sensor min = detectedSensors.get(0);
            if (detectedSensors.size() > 1) {
                for (Sensor s : detectedSensors) {
                    if (s.getDistance() < min.getDistance()) {
                        min = s;
                    }
                }
            }
            return min;
        }
        return null;
    }
}
