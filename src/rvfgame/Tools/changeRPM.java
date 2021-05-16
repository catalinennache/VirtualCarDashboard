/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import eu.hansolo.steelseries.tools.LedColor;

/**
 *
 * @author enc
 */
public class changeRPM extends Thread {

    private eu.hansolo.steelseries.gauges.Radial3 radial32;
    private double x;
    private double currentENGspeed;

    public changeRPM(double x, eu.hansolo.steelseries.gauges.Radial3 radial32) {
        currentENGspeed = radial32.getValue();
        this.x = x;
        this.radial32 = radial32;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        if (x >= currentENGspeed) {
            while (currentENGspeed < x) {
                if (currentENGspeed != 0) {
                    if (currentENGspeed > ((99.9 / 100) * x)) {
                        break;
                    }
                }
                currentENGspeed = currentENGspeed + (x - currentENGspeed) / 100000;
                /*  {  if(currentENGspeed>=6000) {radial32.setLedColor(LedColor.RED_LED);radial32.setLedBlinking(true);}
            else
        {radial32.setLedBlinking(false); radial32.setLedColor(LedColor.GREEN_LED);}}
                 */
                radial32.setValue(currentENGspeed);
            }
        } else {

            double ls = x + 0.01 * x;

            while (currentENGspeed > x)//&& currentspeed>((99.9/100)*x))
            {
                if (currentENGspeed != 0) {
                    if (x == 0) {
                        if (currentENGspeed < 0.1) {
                            break;
                        }
                    } else {
                        if (currentENGspeed <= ls) {
                            break;
                        }
                    }
                }

                currentENGspeed = currentENGspeed + ((x - currentENGspeed)) / 100000;

                radial32.repaint();
                radial32.setValue(currentENGspeed);
                radial32.repaint();
            }
        }
    }
}
