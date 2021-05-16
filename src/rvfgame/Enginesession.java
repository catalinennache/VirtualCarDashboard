/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame;

import rvfgame.Tools.DispatchHelper;
import rvfgame.Tools.lightscontrol;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author enc
 */
public class Enginesession {

    public INTRVF intrf;
    public JPanel[] tablou;
    public double speed;
    public double rpm;
    public double acc;
    public double coolant;
    public double fuel;
    public double airdebit;
    public double[] parameters;
    public JTextField[] entry;
    public lightscontrol boardcontrol;
    public ElectronicControlUnit ACU;
    private static int numberofsessions = 0;

    public Enginesession(ElectronicControlUnit acu, JPanel[] p) {
        ACU=acu;
        
        intrf = ACU.interfata_controlata;
        this.tablou = p;
        numberofsessions++;
        intrf.Monitor.setText("");
        //intrf.Monitor.append("ENGINE SESSION #"+numberofsessions+" STARTED"+"\n");
        new DispatchHelper(intrf.Monitor, "ENGINE SESSION #" + numberofsessions + " STARTED");
        // intrf.Monitor.setFont();
        boardcontrol = new lightscontrol(intrf);

        // DispatchHelper d=new DispatchHelper(boardcontrol);
      
        

        rpm = Double.parseDouble(intrf.sim.entry[1].getText());
        speed = Double.parseDouble(intrf.sim.entry[2].getText());
        acc = Double.parseDouble(intrf.sim.entry[3].getText());
        coolant = Double.parseDouble(intrf.sim.entry[4].getText());
        fuel = Double.parseDouble(intrf.sim.entry[6].getText());
        airdebit = Double.parseDouble(intrf.sim.entry[5].getText());

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
        ACU.changerpm(rpm);
        ACU.changefuel(fuel);
        ACU.changespd(speed);
        ACU.changetemp(coolant);

        synchronized (boardcontrol) {
            boardcontrol.manually("0x00");
            boardcontrol.updateinfo(parameters);
        }

    }

    public void update() {
        for (int i = 1; i < 7; i++) {
            if (parameters[i] != Double.parseDouble(entry[i].getText())) {
                switch (i) {
                    case 1: {
                        parameters[1] = Double.parseDouble(entry[1].getText());
                        ACU.changerpm(parameters[1]);
                    }
                    break;
                    case 2: {
                        ACU.changespd(Double.parseDouble(entry[2].getText()));
                    }
                    break;
                    case 3:
                        break;

                    case 4: {
                        parameters[4] = Double.parseDouble(entry[4].getText());
                        ACU.changetemp(parameters[4]);
                    }
                    break;
                    case 5:
                        break;
                    case 6: {
                        parameters[6] = Double.parseDouble(entry[6].getText());
                        ACU.changefuel(parameters[6]);
                    }
                    break;
                }
            }
        }
        boardcontrol.updateinfo(parameters);
    }

    public void preparefor_nextsession() throws InterruptedException {
        this.boardcontrol.manually("1x01");
        intrf.sim.Start.setEnabled(true);
        intrf.sim.set.setEnabled(false);
        intrf.sim.entry[1].setText("800");
        intrf.sim.entry[1].setEditable(false);
    }

    public void logout() {
        System.out.println("EngineSession number :" + numberofsessions + " has ended");
    }

}
