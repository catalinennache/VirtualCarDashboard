/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

/**
 *
 * @author enc
 */
public class changespeed extends Thread {

    private eu.hansolo.steelseries.gauges.Radial4 radial32;
    private double x;
    private double currentspeed;

    public changespeed(double x, eu.hansolo.steelseries.gauges.Radial4 radial32) {
        currentspeed = radial32.getValue();
        this.x = x;
        this.radial32 = radial32;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        if (x >= currentspeed) {
            while (currentspeed < x) 
            {
                if (currentspeed != 0) {
                    if (currentspeed > ((99.9 / 100) * x)) {
                        break;
                    }
                } //ALGORITMUL FACE CA ACUL VITEZOMETRULUI SA NU SARA BRUSC LA VALOAREA DORITA CI VITEZA LUI SA SE DIMINUEZE PE MASURA CE SE APROPIE LA LIMITA (99.9%) DE VALOAREA DORITA
                currentspeed = currentspeed + (x - currentspeed) / 100000;
                radial32.setValue(currentspeed);
            }
        } else {

            double ls = x + 0.01 * x;

            while (currentspeed > x)
            {
                if (currentspeed != 0) {
                    if (x == 0) {
                        if (currentspeed < 0.1) {
                            break;
                        }
                    } else { if (currentspeed <= ls) {
                            break;
                        }
                    }
                }
                
                currentspeed = currentspeed + ((x - currentspeed)) / 100000;
               
                radial32.repaint();
                radial32.setValue(currentspeed); 
                radial32.repaint();
            }
        }
    }
}
