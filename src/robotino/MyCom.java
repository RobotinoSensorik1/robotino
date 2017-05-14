/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotino;

import java.util.Timer;
import java.util.TimerTask;
import rec.robotino.api2.Com;

/**
 *
 * @author Saskia
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
