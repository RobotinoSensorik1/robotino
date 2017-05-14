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
    protected Command command = new Command();
    protected final ArrayList<Sensor> sensors;
    private ArrayList<Sensor> detectedSensors = new ArrayList<Sensor>();
    private boolean drive = false;

    public Robot() {
        _com = new MyCom();
        _omniDrive = new OmniDrive();
        _bumper = new Bumper();
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
            System.out.println(e.toString());
        }

    }

    public boolean isConnected() {
        return _com.isConnected();
    }

    public void connect(String hostname, boolean block) {
        System.out.println("Connecting...");
        _com.setAddress(hostname);
        _com.connectToServer(block);
    }

    public void disconnect() {
        _com.disconnectFromServer();
    }

    public void drive() throws InterruptedException {
        System.out.println("Driving...");
        float[] startVector = new float[]{0.0f, 0.1f};
        float[] dir = new float[2];
        float a = 0.0f;
        while (_com.isConnected() && !_bumper.value()) {

            a = command.speedUpOfSpeedUp(0.00005f, 0.1f);
            command.driveForward(dir, dir, a);
            _omniDrive.setVelocity(dir[0], dir[1], 0);
            while (isHindernis()) {

                
                if(detectedSensors.size() == 1){
                    //rotate in place
                    //get angle to rotate
                    float deg = command.getAngleToRotate(detectedSensors.get(0));
                    float w = command.getAngularSpeed(deg);
                    a = command.speedUpOfSpeedUp(0.0005f, 0.1f);
                    command.rotateInPlace(dir, w);
                    _omniDrive.setVelocity(a * 0.0f, a * 0.0f, w);
                    Thread.sleep(100);

                    //geradeausfahren
                    a = command.speedUpOfSpeedUp(0.00005f, 0.1f);
                    command.driveForward(dir, dir, a);
                    _omniDrive.setVelocity(dir[0], dir[1], 0);
                    detectedSensors.clear();
                    break;
                }
                else if (detectedSensors.size() == 2) {
                    //check for free side
                    float deg = command.getAngleToRotate(detectedSensors, 2);

                } else if(detectedSensors.size() > 2){
                    float deg = command.getAngleToRotate(detectedSensors, detectedSensors.size());
                }

            }

            Thread.sleep(100);

        }
    }
    //Hier in der Methode werden alle Sensoren getestet. Wenn ein Sensor ein Hindernis
    //erkennt, bei dem der Abstand unter 0.409m liegt, wird der Sensor in "detectedSensors"
    //Variable abgespeichert
      public boolean isHindernis() {
        boolean hindernis = false;
        for (Sensor s : sensors) { //die for-Schleife wird solange durchlaufen, bis alle Sensoren(=grün) die vorhanden sind, getestet sind
            if (s.getDistance() < 0.409) { //der abgespeicherte Sensor in s, wird auf Abstand geprüft, ob er unter 0.409m liegt, wenn ja
                detectedSensors.add(s); //erfasster Sensor wird in "detectedSensors" abgespeichert
                System.out.println(s.getNumberOfSensor()+" stopped by\t" + s.getDistance());//zu wissen bei welchem Sensor und bei welchem Abstand gestoppt wurde
                hindernis = true; // sofern Hindernis erkannt wurde, wird auf true gesetzt

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
