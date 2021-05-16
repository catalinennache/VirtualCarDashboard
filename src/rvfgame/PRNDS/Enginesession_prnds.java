/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.PRNDS;

import rvfgame.Tools.DispatchHelper;
import rvfgame.Tools.lightscontrol_PRNDS;
import eu.hansolo.steelseries.tools.LedColor;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author enc
 */
public class Enginesession_prnds extends Thread  {

    public INTRVF_PRNDS intrf;
    public JPanel[] tablou;
    public double speed;
    public double rpm;
    public double acc;
    public double coolant;
    public double fuel;
    public double airdebit;
    public double[] parameters;
    public JTextField[] entry;
    public lightscontrol_PRNDS boardcontrol;
    public ElectronicControlUnit_PRNDS ECU;
    public Object lock;
    public static int numberofsessions = 0;

    public Enginesession_prnds  (ElectronicControlUnit_PRNDS acu, JPanel[] p) throws InterruptedException {
        System.out.println("EngineSession Created on "+Thread.currentThread().getName());
        ECU=acu;
        lock=new Object();
        
        
        intrf = ECU.interfata_controlata;
        this.tablou = p;
        numberofsessions++;
        intrf.Monitor.setText("");
        //intrf.Monitor.append("ENGINE SESSION #"+numberofsessions+" STARTED"+"\n");
        new DispatchHelper(intrf.Monitor, "ENGINE SESSION #" + numberofsessions + " STARTED");
        // intrf.Monitor.setFont();
       

        // DispatchHelper d=new DispatchHelper(boardcontrol);
       

        rpm = ECU.parameters[1];
        speed = ECU.parameters[2];
        acc = ECU.parameters[3];
        coolant = ECU.parameters[4];
        fuel = ECU.parameters[6];
        airdebit = ECU.parameters[5];

        parameters = new double[7];
        parameters[1] = rpm;
        parameters[2] = speed;
        parameters[3] = acc;
        parameters[4] = coolant;
        parameters[6] = fuel;
        parameters[5] = airdebit;
        entry = new JTextField[7];
        entry[1] = intrf.sim.entry[1];
        entry[2] = intrf.sim.entry[2];
        entry[3] = intrf.sim.entry[3];
        entry[4] = intrf.sim.entry[4];
        entry[5] = intrf.sim.entry[5];
        entry[6] = intrf.sim.entry[6];
     
            ECU.selft(); 
         
        ECU.changerpm_PRNDS(rpm);
       ECU.changefuel_PRNDS(fuel);
         ECU.changespd_PRNDS(speed);
        ECU.changetemp_PRNDS(coolant);
        boardcontrol = new lightscontrol_PRNDS(intrf);
        synchronized (boardcontrol) {
            boardcontrol.manually("0x00");
            boardcontrol.updateinfo(parameters);
        }
       
     
        //ECU.realtime_update=new Thread(ECU);
       // ECU.realtime_update.start();
        ElectronicControlUnit_PRNDS.enginestate=true;
        synchronized(ECU){ECU.notify();}
    }
    


    

    public void update() {
        for (int i = 1; i < 7; i++) {
            if (parameters[i] != this.ECU.parameters[i]) {
                switch (i) {
                    case 1: {
                        parameters[1] = this.ECU.parameters[1];
                        ECU.changerpm_PRNDS(parameters[1]);
                    }
                    break;
                    case 2: {
                        ECU.changespd_PRNDS(Double.parseDouble(entry[2].getText()));
                    }
                    break;
                    case 3:
                        break;

                    case 4: {
                        parameters[4] = this.ECU.parameters[4];
                        ECU.changetemp_PRNDS(parameters[4]);
                    }
                    break;
                    case 5:
                        break;
                    case 6: {
                        parameters[6] = Double.parseDouble(entry[6].getText());
                        ECU.changefuel_PRNDS(parameters[6]);
                    }
                    break;
                }
            }
        }
        boardcontrol.updateinfo(parameters);
    }

    public void preparefor_nextsession()  {ElectronicControlUnit_PRNDS.enginestate=false;
        ECU.interfata_controlata.RPMGauge.setLedBlinking(false);
        ECU.interfata_controlata.SpeedGauge.setLedBlinking(false);
        ECU.interfata_controlata.TempGauge.setLedBlinking(false);
        ECU.interfata_controlata.FuelGauge.setLedBlinking(false);
        ECU.interfata_controlata.RPMGauge.setLedColor(LedColor.RED_LED);
        ECU.interfata_controlata.SpeedGauge.setLedColor(LedColor.RED_LED);
        ECU.interfata_controlata.TempGauge.setLedColor(LedColor.RED_LED);
        ECU.interfata_controlata.FuelGauge.setLedColor(LedColor.RED_LED);
        ECU.changefuel_PRNDS(0);
        ECU.changerpm_PRNDS(0);
        ECU.changespd_PRNDS(0);
        ECU.changetemp_PRNDS(24);
         this.boardcontrol.manually("0x00");
         
        
    
    }

    public void logout() {
        System.out.println("EngineSession number :" + numberofsessions + " has ended");
        
    }

    public void focusGained(FocusEvent fe) {
       synchronized(ECU.realtime_update){try {
           ECU.realtime_update.wait();
           } catch (InterruptedException ex) {
               Logger.getLogger(Enginesession_prnds.class.getName()).log(Level.SEVERE, null, ex);
           }
}
    }

    public void focusLost(FocusEvent fe) {
      synchronized (ECU.realtime_update){ECU.realtime_update.notify();}
    }

}
