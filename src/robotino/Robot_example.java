/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotino;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import rec.robotino.api2.Bumper;
import rec.robotino.api2.Com;
import rec.robotino.api2.DistanceSensor;
import rec.robotino.api2.OmniDrive;

/**
 *
 * @author Saskia
 */
public class Robot_example {

    protected final Com _com;
    protected final OmniDrive _omniDrive;
    protected final Bumper _bumper;
    protected Command command = new Command();
    protected Timer timer;
    protected final List<DistanceSensor> _distanceSensors;

    protected final float SLOW_VELOCITY = 0.08f;
    protected final float MEDIUM_VELOCITY = 0.16f;
    protected final float VELOCITY = 0.24f;
    protected final float FAST_VELOCITY = 0.32f;
    protected final float ANGULARVELOCITY = 0.02f;

    public Robot_example() {

        _com = new MyCom();
        _omniDrive = new OmniDrive();
        _bumper = new Bumper();
        _distanceSensors = new ArrayList<DistanceSensor>();

        _omniDrive.setComId(_com.id());
        _bumper.setComId(_com.id());

        for (int i = 0; i < 9; ++i) {
            DistanceSensor s = new DistanceSensor();
            s.setSensorNumber(i);
            s.setComId(_com.id());
            _distanceSensors.add(s);
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

    public void setVelocity(float vx, float vy, float omega) {
        _omniDrive.setVelocity(vx, vy, omega);
    }

    public void rotate(float[] inArray, float[] outArray, float deg) {
        float rad = 2 * (float) Math.PI / 360.0f * deg;
        outArray[0] = (float) Math.cos(rad) * inArray[0] - (float) Math.sin(rad) * inArray[1];
        outArray[1] = (float) Math.sin(rad) * inArray[0] + (float) Math.cos(rad) * inArray[1];
    }

    public void drive() throws InterruptedException {
        System.out.println("Driving...");
        float[] startVector = new float[]{0.0f, 0.1f};
        float[] dir = new float[2];
        float a = 0.0f;
        int i = 0;
        while (_com.isConnected() && false == _bumper.value() && i < 5) {
            //rotate 360degrees in 5s
//            rotate(startVector, dir, a);
//            a = 360.0f * _com.msecsElapsed() / 5000;

            command.driveForward(dir, dir,0.05f);

            _omniDrive.setVelocity(dir[0], dir[1], 0);
            i++;
            Thread.sleep(100);

        }
    }
    
    void addScaledVector(float[] srcDest, float[] uv, float scale)
    {
        srcDest[0] += uv[0] * scale;
        srcDest[1] += uv[1] * scale;
    }
    
    void normalizeVector(float[] v)
    {
        float len = (float)Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        v[0] /= len;
        v[1] /= len;
    }

    void rotateInPlace(float[] v, float deg)
    {
        float rad = 2 * (float)Math.PI / 360.0f * deg;
        float tmp = v[0];
        v[0] = (float)Math.cos(rad) * v[0] - (float)Math.sin(rad) * v[1];
        v[1] = (float)Math.sin(rad) * tmp + (float)Math.cos(rad) * v[1];
    }
    
    public void followWalls() throws InterruptedException {
        float[] ev = new float[]{1.0f, 0.0f};
        // escape vector for distance sensor
        float[][] escapeVector = new float[][]{
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f}
        };

        for (int i = 0; i < 9; i++) {
            rotate(ev, escapeVector[i], 40.0f * i);
        }
        final float ESCAPE_DISTANCE = 0.10f;
        final float WALL_LOST_DISTANCE = 0.35f;
        final float WALL_FOUND_DISTANCE = 0.30f;
        final float WALL_FOLLOW_DISTANCE = 0.15f;
        final float NEW_WALL_FOUND_DISTANCE = 0.12f;
        float[] escape = new float[]{0.0f, 0.0f};
        int curWallSensor = -1;
        float[] dir = new float[]{1.0f, 0.0f};
        float velocity = VELOCITY;
        float rotVelocity = ANGULARVELOCITY;

        while (_com.isConnected() && false == _bumper.value()) {
            velocity = VELOCITY;
            int minIndex = 0;
            float minDistance = 0.40f;
            int numEscape = 0;
            escape[0] = escape[1] = 0.0f;

            StringBuilder values = new StringBuilder();
            for (int i = 0; i < _distanceSensors.size(); ++i) {
                float v = (float) _distanceSensors.get(i).distance();
                values.append(v + " ");
                if (v < minDistance) {
                    minDistance = v;
                    minIndex = i;
                }
                if (v < ESCAPE_DISTANCE) {
                    ++numEscape;
                    addScaledVector(escape, escapeVector[i], v);
                }
            }
            System.out.println(values.toString());

            if (numEscape >= 2) {
                // close to walls with more than one sensor, try to escape
                normalizeVector(escape);
                rotate(escape, dir, 180);
                velocity = SLOW_VELOCITY;
            } else {
                if (curWallSensor != -1
                        && _distanceSensors.get(curWallSensor).distance() > WALL_LOST_DISTANCE) {
                    curWallSensor = -1;
                    rotateInPlace(dir, -20);
                    rotVelocity = -0.20f;
                }
                if (curWallSensor == -1) {
                    // wall not found yet
                    if (minDistance < WALL_FOUND_DISTANCE) {
                        curWallSensor = minIndex;
                        velocity = SLOW_VELOCITY;
                    }
                }
                if (curWallSensor != -1) {
                    float wallDist = (float) _distanceSensors.get(curWallSensor).distance();

                    // check for global new wall
                    if (minIndex != curWallSensor && minDistance < NEW_WALL_FOUND_DISTANCE) {
                        // new wall found, drive along this wall
                        curWallSensor = minIndex;
                        wallDist = (float) _distanceSensors.get(curWallSensor).distance();
                        velocity = SLOW_VELOCITY;
                    } else {
                        if (_distanceSensors.get((curWallSensor + 1) % 9).distance() < wallDist) {
                            // switch walls
                            curWallSensor = (curWallSensor + 1) % 9;
                            velocity = MEDIUM_VELOCITY;
                        }
                        // check for new wall in direction
                        for (int i = 0; i < 2; ++i) {
                            int tmpId = (curWallSensor + 2 + i) % 9;
                            if (_distanceSensors.get(tmpId).distance() < WALL_FOUND_DISTANCE) {
                                curWallSensor = tmpId;
                                wallDist = (float) _distanceSensors.get(tmpId).distance();
                                velocity = SLOW_VELOCITY;
                                break;
                            }
                        }
                    }
                    // try to keep neighbor distance sensors in balance
                    float vr = (float) _distanceSensors.get((curWallSensor + 1) % 9).distance();
                    float vl = (float) _distanceSensors.get((curWallSensor + 8) % 9).distance();

                    rotVelocity = (vr - vl);
                    float followAngle = 95;
                    if (Math.abs(rotVelocity) > 0.30) {
                        velocity = SLOW_VELOCITY;
                        followAngle = rotVelocity >= 0.0 ? 140 : 80;
                    } else if (Math.abs(rotVelocity) > 0.40) {
                        velocity = MEDIUM_VELOCITY;
                        followAngle = rotVelocity >= 0.0 ? 120 : 85;
                    }
                    rotVelocity *= 8 * ANGULARVELOCITY;
                    // follow the wall to the left
                    rotate(escapeVector[curWallSensor], dir, followAngle);
                    // keep distance to wall steady
                    float scale = wallDist - WALL_FOLLOW_DISTANCE;

                    scale *= 10f;

                    addScaledVector(dir, escapeVector[curWallSensor], scale);
                    normalizeVector(dir);
                }
            }
            if (minDistance > 0.20f) {
                velocity = FAST_VELOCITY;
            }
            _omniDrive.setVelocity(velocity * (float) dir[0], velocity * (float) dir[1], rotVelocity);
            Thread.sleep(100);
        }
    }

    /**
     * The class MyCom derives from rec.robotino.api2.Com and implements some of
     * the virtual event handling methods. This is the standard approach for
     * handling these Events.
     */
    class MyCom extends Com {

        Timer _timer;

        public MyCom() {
            _timer = new Timer();
            _timer.scheduleAtFixedRate(new OnTimeOut(), 0, 20);
        }

        class OnTimeOut extends TimerTask {

            public void run() {
                processEvents();
            }
        }

        @Override
        public void connectedEvent() {
            System.out.println("Connected");
        }

        @Override
        public void errorEvent(String errorStr) {
            System.err.println("Error: " + errorStr);
        }

        @Override
        public void connectionClosedEvent() {
            System.out.println("Disconnected");
        }
    }

    public static void main(String[] args) {
        //String hostname = "127.0.0.1";
        String hostname = "172.26.1.1";
        if (args.length == 1) {
            hostname = args[0].toString();
        }

        Robot_example robotino = new Robot_example();

        try {
            robotino.connect(hostname, true);
            robotino.followWalls();
            robotino.disconnect();
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
