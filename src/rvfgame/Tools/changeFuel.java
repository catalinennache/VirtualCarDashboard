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
public class changeFuel extends Thread {

    private eu.hansolo.steelseries.gauges.Radial1Vertical radial33;
    private double x;
    private double currentfuel;
    public int fuelcode;

    public changeFuel(double nx, eu.hansolo.steelseries.gauges.Radial1Vertical s, int heatc) {
        radial33 = s;
        x = nx;
        fuelcode = heatc;
        Thread t = new Thread(this);
        t.start();
        currentfuel = radial33.getValue();
    }

    @Override
    public void run() {
        if (fuelcode == 0) {
            if (x >= currentfuel) {
                while (currentfuel < x) {
                    if (currentfuel != 0) {
                        if (currentfuel > ((99.9 / 100) * x)) {
                            break;
                        }
                    }
                    currentfuel = currentfuel + (x - currentfuel) / 100000;
                    /*  {  if(currentENGspeed>=6000) {radial32.setLedColor(LedColor.RED_LED);radial32.setLedBlinking(true);}
            else
        {radial32.setLedBlinking(false); radial32.setLedColor(LedColor.GREEN_LED);}}
                     */
                    radial33.setValue(currentfuel);
                }
            } else {
                // System.out.println("testing set-valuer.."+currenttemp);

                double ls = x + 0.01 * x;

                while (currentfuel > x)//&& currentspeed>((99.9/100)*x))
                {
                    if (currentfuel != 0) {
                        if (x == 0) {
                            if (currentfuel < 0.1) {
                                break;
                            }
                        } else {
                            if (currentfuel <= ls) {
                                break;
                            }
                        }
                    }

                    currentfuel = currentfuel + ((x - currentfuel)) / 100000;

                    radial33.repaint();
                    radial33.setValue(currentfuel);
                    radial33.repaint();
                }
            }

        }
        if (fuelcode == 1) {
        }
    }

}
