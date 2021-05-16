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
public class changeTEMP extends Thread {

    private eu.hansolo.steelseries.gauges.Radial1Vertical radial33;
    private double x;
    private double currenttemp;
    public int heatcode;

    public changeTEMP(double nx, eu.hansolo.steelseries.gauges.Radial1Vertical s, int heatc) {
        radial33 = s;
        x = nx;
        heatcode = heatc;
        Thread t = new Thread(this);
        t.start();
        currenttemp = radial33.getValue();
    }

    @Override
    public void run() {
        if (heatcode == 0) {
            if (x >= currenttemp) { 
                while (currenttemp < x) {
                    if (currenttemp != 0) {
                        if (currenttemp > ((99.9 / 100) * x)) {
                            break;
                        }
                    }
                    currenttemp = currenttemp + (x - currenttemp) / 100000;
                  
                    radial33.setValue(currenttemp);
                }
            } else {
                double ls = x + 0.01 * x;

                while (currenttemp > x)
                {
                    if (currenttemp != 0) {
                        if (x == 0) {
                            if (currenttemp < 0.1) {
                                break;
                            }
                        } else {
                            if (currenttemp <= ls) {
                                break;
                            }
                        }
                    }

                    currenttemp = currenttemp + ((x - currenttemp)) / 100000;

                    radial33.repaint();
                    radial33.setValue(currenttemp);
                    radial33.repaint();
                }
            }
            
        }
        if (heatcode == 1) {
        }
    }

}
