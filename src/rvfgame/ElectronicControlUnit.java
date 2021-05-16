/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame;

import rvfgame.Tools.DispatchHelper;
import rvfgame.Tools.changeFuel;
import rvfgame.Tools.lightscontrol;
import rvfgame.Tools.changeRPM;
import rvfgame.Tools.Gtest;
import rvfgame.Tools.changeTEMP;
import rvfgame.Tools.changespeed;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 *
 * @author Enache
 */
public class ElectronicControlUnit implements ActionListener {
    
    public INTRVF interfata_controlata;
   
    
    ElectronicControlUnit(INTRVF intrf)
    {
    interfata_controlata=intrf;

    
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (interfata_controlata.sim.mode == 0) {
            if (!interfata_controlata.sim.entry[1].isEditable()) {
                interfata_controlata.sim.entry[1].setEditable(true);
            }

            double rpm = Double.parseDouble(interfata_controlata.sim.entry[1].getText());
            if (rpm <= 250) {
                rpm = 0;
                interfata_controlata.sim.entry[1].setText("0");
            }
            if (interfata_controlata.enginesession == null) {
                new DispatchHelper(interfata_controlata.enginesession, this);
            } else {
                if (rpm == 0) {
                    try {
                        interfata_controlata.enginesession.update();
                        interfata_controlata.enginesession.logout();
                        interfata_controlata.enginesession.preparefor_nextsession();
                        interfata_controlata.enginesession = null;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(INTRVF.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    interfata_controlata.enginesession.update();
                }

            }
        }

    }

    public JPanel prepare(JPanel p, String s) {
        p = new JPanel();
        int h = 33;
        int w = 60;
        if (s == "esp.jpg") {
            h = 40;
            w = 58;
        }
        System.out.println("Martor instantiat");
        ImageIcon martor = new ImageIcon(s);
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(s));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dimg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);
        JLabel lb = new JLabel();
        lb.setIcon(imageIcon);
        p.add(lb);
        p.setBackground(Color.black);
        p.setVisible(true);
        return p;
    }

    public void selft() throws InterruptedException {
        interfata_controlata.tablou = new JPanel[8];
        interfata_controlata.tablou[0] =  interfata_controlata.Oil;
        interfata_controlata.tablou[1] = interfata_controlata.Gas;
        interfata_controlata.tablou[3] = interfata_controlata.Coolant;
        interfata_controlata.tablou[2] = interfata_controlata.Checkengine;
        interfata_controlata.tablou[4] = interfata_controlata.Airbag;
        interfata_controlata.tablou[5] = interfata_controlata.ABS;
        interfata_controlata.tablou[6] = interfata_controlata.ESP;
        interfata_controlata.tablou[7] = interfata_controlata.Battery;
        Thread.sleep(2200);

        new Gtest(interfata_controlata.SpeedGauge);
        new Gtest(interfata_controlata.RPMGauge);
        // tablou[7].setVisible(true);
        new lightscontrol(interfata_controlata).manually("1x00");

        new Gtest(interfata_controlata.TempGauge);
        new Gtest(interfata_controlata.FuelGauge);
        if (interfata_controlata.sim.mode == 0) {
            interfata_controlata.sim.Start.setText("Done!");
            interfata_controlata.sim.Start.setText("Start Engine");
            Thread.sleep(3000);
            interfata_controlata.sim.Start.setEnabled(true);
        } else {
            interfata_controlata.sim.Tbuttons[6].setText("Done!");
            interfata_controlata.sim.Tbuttons[6].setText("START-STOP");
            Thread.sleep(3000);
            interfata_controlata.sim.Tbuttons[6].setEnabled(true);
        }

    }

    public void changespd(double x) {
        new changespeed(x, interfata_controlata.SpeedGauge);
    }

    public void changerpm(double x) {
        new changeRPM(x, interfata_controlata.RPMGauge);
    }

    public void changetemp(double x) {
        new changeTEMP(x, interfata_controlata.TempGauge, 0);
    }

    public void changefuel(double x) {
        new changeFuel(x, interfata_controlata.FuelGauge, 0);
    }
    
}
