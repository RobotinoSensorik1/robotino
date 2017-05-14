/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotino;

import rec.robotino.api2.Com;
import rec.robotino.api2.DistanceSensor;

/**
 *
 * @author Saskia
 */
public class Sensor extends Thread {

    private final long numberOfSensor;
    private final Com _com;
    private final DistanceSensor _distanceSensor;
    private float distance;

    public Sensor(long number, Com c) {
        super("Sensor " + number);
        this.numberOfSensor = number;
        _com = c;
        _distanceSensor = new DistanceSensor();
        _distanceSensor.setSensorNumber(number);
        _distanceSensor.setComId(_com.id());

    }

    public void run() {
        while (true) {
            try {
                distance = _distanceSensor.distance();

                this.sleep(100);
            } catch (Exception e) {
            }
        }
    }

    public long getNumberOfSensor() {
        return numberOfSensor;
    }

    public float getDistance() {
        return distance;
    }

}
