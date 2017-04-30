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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import rec.robotino.api2.Bumper;
import rec.robotino.api2.Com;
import rec.robotino.api2.OmniDrive;

/**
 *
 * @author Saskia
 */
public class Robot extends JFrame implements ActionListener {

    JButton weiter = new JButton("weiter");
    JButton stop = new JButton("stop");

    protected final Com _com;
    protected final OmniDrive _omniDrive;
    protected final Bumper _bumper;
    protected Command command = new Command();

    public Robot() {
        weiter.addActionListener(this);
        stop.addActionListener(this);
        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(weiter, FlowLayout.LEFT);
        this.getContentPane().add(stop, FlowLayout.LEFT);
        this.setSize(300, 200);
        this.setBounds(400, 400, 402, 402);
        this.pack();
        this.setVisible(true);

        _com = new MyCom();
        _omniDrive = new OmniDrive();
        _bumper = new Bumper();

        _omniDrive.setComId(_com.id());
        _bumper.setComId(_com.id());
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
        int i= 0;
        while (_com.isConnected() && false == _bumper.value() && i < 10) {
            //rotate 360degrees in 5s
//            rotate(startVector, dir, a);
//            a = 360.0f * _com.msecsElapsed() / 5000;
            
                command.driveForward(dir, dir);
                
                _omniDrive.setVelocity(dir[0], dir[1], 0);
                i++;
                Thread.sleep(100);
            
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("weiter")) {
            try {
                drive();
            } catch (InterruptedException ex) {
                Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (e.getActionCommand().equals("stop")) {
            System.exit(0);

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
        String hostname = "127.0.0.1";
        if (args.length == 1) {
            hostname = args[0].toString();
        }

        Robot robotino = new Robot();

        try {
            robotino.connect(hostname, true);
            robotino.drive();
            robotino.disconnect();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
