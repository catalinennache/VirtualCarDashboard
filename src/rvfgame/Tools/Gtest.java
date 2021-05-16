/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import eu.hansolo.steelseries.gauges.Radial1Vertical;
import eu.hansolo.steelseries.gauges.Radial3;
import eu.hansolo.steelseries.gauges.Radial4;
import eu.hansolo.steelseries.tools.LedColor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author enc
 */
public class Gtest extends Thread {

    Object l;
    eu.hansolo.steelseries.gauges.Radial3 radial31;
    eu.hansolo.steelseries.gauges.Radial4 radial32;
    eu.hansolo.steelseries.gauges.Radial1Vertical radial33;
    eu.hansolo.steelseries.gauges.Radial3 radial34;
    eu.hansolo.steelseries.gauges.DisplaySingle displaySingle1;
    private eu.hansolo.steelseries.gauges.Radial1Vertical radial1Vertical1;
    public static Object lock=new Object();
    public Gtest(Object s) {
        l = s;
        Thread t = new Thread(this);
        t.start();

    }

    @Override
    public void run() {
      {
        if (l instanceof eu.hansolo.steelseries.gauges.Radial3) {
            radial31 = (Radial3) l;
            double i = 0;
            Boolean si = true;
//            intrf.radial31.setLedBlinking(true);
            //Thread.sleep(1000);
            // intrf.radial31.setValue(6000);

            radial31.setLedBlinking(true);

            while (si) { synchronized(lock){
                if (i >= 2 * radial31.getMaxValue()) {
                    si = false;
                } else if (i < radial31.getMaxValue()) {
                    i = i + (0.015) * radial31.getMaxValue();
                    radial31.setValue(i);
                } else {
                    i = i + (0.015) * radial31.getMaxValue();
                    radial31.setValue(2 * radial31.getMaxValue() - i);}
                   
                try {
                    lock.wait(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Gtest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                   radial31.repaint();
              
}
            radial31.setLedBlinking(false);
            radial31.setLedColor(LedColor.GREEN_LED);
        } else if (l instanceof eu.hansolo.steelseries.gauges.Radial4) {
            radial32 = (Radial4) l;
            double i = 0;
            Boolean si = true;
//            intrf.radial31.setLedBlinking(true);
            //Thread.sleep(1000);
            // intrf.radial31.setValue(6000);

            radial32.setLedBlinking(true);
             double  ls = radial32.getMaxValue();
            while (si) {synchronized(lock){
                if (i >= 2 * radial32.getMaxValue()) {
                    si = false;
                    radial32.setValue(0);
                } else if (i < radial32.getMaxValue()) {
                    i = i + (0.015) * ls;
                    radial32.setValue(i);
                } else {
                    i = i + (0.015) * ls;
                    radial32.setValue(2 * radial32.getMaxValue() - i);

                } radial32.repaint();
            
                try {
                    lock.wait(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Gtest.class.getName()).log(Level.SEVERE, null, ex);
                }

            } }
            radial32.setLedBlinking(false);
            System.out.println("RPM G Test DONE");
            radial32.setLedColor(LedColor.GREEN_LED);
        } else if (l instanceof eu.hansolo.steelseries.gauges.Radial1Vertical) {
            radial33 = (Radial1Vertical) l;
            double i = radial33.getMinValue();
            Boolean si = true;
       double ls=radial33.getMaxValue();
            radial33.setLedBlinking(true);
             while (si) {synchronized(lock){
                if (i >= 2 * radial33.getMaxValue()) {
                    si = false;
                    radial33.setValue(0);
                } else if (i < radial33.getMaxValue()) {
                    i = i + (0.015) * ls;
                    radial33.setValue(i);
                } else {
                    i = i + (0.015) * ls;
                    radial33.setValue(2 * radial33.getMaxValue() - i);

                } radial33.repaint();
            
                try {
                    lock.wait(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Gtest.class.getName()).log(Level.SEVERE, null, ex);
                }

            } }
            radial33.setLedBlinking(false);
         //   System.out.println("RPM G Test DONE");
            radial33.setLedColor(LedColor.GREEN_LED);
            
            //radial33.setValue(radial33.getMaxValue());
            //radial33.setLedColor(LedColor.GREEN_LED);
           /* try {
                Thread.sleep(1700);
            } catch (InterruptedException ex) {
                Logger.getLogger(Gtest.class.getName()).log(Level.SEVERE, null, ex);
            }
            radial33.setValue(radial33.getMinValue());*/
         //   radial33.setLedBlinking(false);
        }
    }}

}
