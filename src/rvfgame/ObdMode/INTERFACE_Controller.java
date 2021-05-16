/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import rvfgame.PRNDS.Enginesession_prnds;
import rvfgame.Tools.Gtest;
import rvfgame.Tools.lightscontrol;
import rvfgame.Tools.lightscontrol_OBD;
import rvfgame.Tools.lightscontrol_PRNDS;
import vanilla.java.affinity.AffinityLock;

/**
 *
 * @author Enache
 */
public class INTERFACE_Controller extends Kernel implements Runnable {

    public INTRVF_OBD2 interfc = Kernel.interfc;

    public Enginesession_IC engses = null;
    public long updaterate_IC = 0;
    long start = 0;
    static boolean enginesesionAlreadyCreated = false;
    static boolean ICThreadAlive = false;

    @Override
    public void run() {
        engses = new Enginesession_IC(interfc);
        INTERFACE_Controller.current_parameters = new double[6];
        while (!Kernel.forcedstopped) {
            start = System.currentTimeMillis();
            System.arraycopy(OBD_Reader.current_parameters, 1, INTERFACE_Controller.current_parameters, 1, 5);
            engses.update();
            updaterate_IC = ((System.currentTimeMillis() - start) + Kernel.kernel_update_rate) / 2;
            try {
                Thread.sleep(updaterate_IC);
            } catch (InterruptedException ex) {
                Logger.getLogger(INTERFACE_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public void selft() throws InterruptedException {

        // new lightscontrol_OBD(this.interfc).manually("1x00");
        //  Thread.sleep(800);
        new Gtest(this.interfc.TempGauge);
        new Gtest(this.interfc.FuelGauge);
        new Gtest(interfc.SpeedGauge);
        new Gtest(interfc.RPMGauge);
        if (Enginesession_IC.numberofsessions < 2) {
           this.interfc.tablou = new JPanel[9];
            this.interfc.tablou[0] = this.interfc.Oil;
            this.interfc.tablou[1] = this.interfc.Gas;
            this.interfc.tablou[3] = this.interfc.Coolant;
            this.interfc.tablou[2] = this.interfc.Checkengine;
            this.interfc.tablou[4] = this.interfc.Airbag;
           this.interfc.tablou[5] = this.interfc.ABS;
           this.interfc.tablou[6] = this.interfc.ESP;
            this.interfc.tablou[7] = this.interfc.Battery;
           this.interfc.tablou[8] = this.interfc.Brake;
            // Thread.sleep(2200);
        }
        for (int i = 0; i <= 8; i++) {

            this.interfc.tablou[i].setVisible(true);
            //Thread.sleep(200);
        }

        try {
            Thread.sleep(2000);

        } catch (InterruptedException ex) {
        }

        for (int i = 0; i <= 8; i++) {
            if (i != 2 && i != 7 && i != 0) {
                this.interfc.tablou[i].setVisible(false);
            }
            this.interfc.tablou[i].repaint();
            // Thread.sleep(400);
        }

        // tablou[7].setVisible(true);
        Thread.sleep(1000);
       Kernel.ready=true;
    }

}
