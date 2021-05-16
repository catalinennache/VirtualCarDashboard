/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.PRNDS;

import rvfgame.Tools.DispatchHelper;
import rvfgame.Tools.changeFuel;
import rvfgame.Tools.changeRPM;
import rvfgame.Tools.Gtest;
import rvfgame.Tools.changeTEMP;
import rvfgame.Tools.lightscontrol_PRNDS;
import rvfgame.Tools.changespeed;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class ElectronicControlUnit_PRNDS extends Thread implements ActionListener, FocusListener, KeyListener {

    public static long RESPONSE_TIME = 500; //Electronic Control Unit's response time [milisec] to changes;
    public INTRVF_PRNDS interfata_controlata;
    public Thread realtime_update;
    public boolean realtime_paused = false;
    public static boolean enginestate = false;
    public Enginesession_prnds ESS; //enginesession
    public lightscontrol_PRNDS LGC; //lightscontrol
    public double[] parameters = new double[7];

  public  ElectronicControlUnit_PRNDS(INTRVF_PRNDS intrf) {
        this.interfata_controlata = intrf;
        System.out.println("ECU Created on " + Thread.currentThread().getName());

    }

    @Override
    public void actionPerformed(ActionEvent ae) {

    }

    public void prepare_for_shutdown() {
        new DispatchHelper(this);
    }

    private void preparedata() {
        for (int i = 1; i <= 6; i++) {
            this.parameters[i] = Physics_PRNDS.real_parameters[i];
        }
    }

    @Override
    public void run() {
        this.preparedata();
        new DispatchHelper(interfata_controlata.ECU.ESS, this);
        this.interfata_controlata.sim.info_ecu.setText(String.valueOf(RESPONSE_TIME));
        this.interfata_controlata.sim.info_ecu.setColumns(String.valueOf(RESPONSE_TIME).length());
        
        while (true) {
            try { // this is where the ECU does the magic
                this.preparedata();
                if (this.ESS == null) {
                    synchronized (this) {
                        this.wait();
                        this.ESS.update();
                    }
                }
                else {
                    this.ESS.update();
                }
                //System.out.println("ECU: UPDATE COMPLETE");
                Thread.sleep(RESPONSE_TIME);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void selft() throws InterruptedException {
        interfata_controlata.sim.Tbuttons[6].setEnabled(false);
        this.interfata_controlata.sim.Tbuttons[6].setText("SELF-T on pwr!");

        if (Enginesession_prnds.numberofsessions < 2) {
            interfata_controlata.tablou = new JPanel[9];
            interfata_controlata.tablou[0] = interfata_controlata.Oil;
            interfata_controlata.tablou[1] = interfata_controlata.Gas;
            interfata_controlata.tablou[3] = interfata_controlata.Coolant;
            interfata_controlata.tablou[2] = interfata_controlata.Checkengine;
            interfata_controlata.tablou[4] = interfata_controlata.Airbag;
            interfata_controlata.tablou[5] = interfata_controlata.ABS;
            interfata_controlata.tablou[6] = interfata_controlata.ESP;
            interfata_controlata.tablou[7] = interfata_controlata.Battery;
            interfata_controlata.tablou[8] = interfata_controlata.Brake;
            // Thread.sleep(2200);
        }
        new lightscontrol_PRNDS(this.interfata_controlata).manually("1x00");

        //  Thread.sleep(800);
        new Gtest(this.interfata_controlata.TempGauge);
        new Gtest(this.interfata_controlata.FuelGauge);
        new Gtest(interfata_controlata.SpeedGauge);
        new Gtest(interfata_controlata.RPMGauge);
        // tablou[7].setVisible(true);
        Thread.sleep(700);

        this.interfata_controlata.sim.Tbuttons[6].setText("Done!");
        this.interfata_controlata.sim.Tbuttons[6].setText("START-STOP");
        Thread.sleep(2000);

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

    public void changespd_PRNDS(double x) {
        new changespeed(x, this.interfata_controlata.SpeedGauge);
    }

    public void changerpm_PRNDS(double x) {
        new changeRPM(x, this.interfata_controlata.RPMGauge);
    }

     public void changetemp_PRNDS(double x) {
        new changeTEMP(x, this.interfata_controlata.TempGauge, 0);
    }
     
    public void changefuel_PRNDS(double x) {
        new changeFuel(x, this.interfata_controlata.FuelGauge, 0);
    }

    @Override
    public void focusGained(FocusEvent fe) {
        realtime_paused = true;
    }

    @Override
    public void focusLost(FocusEvent fe) {
        realtime_paused = false;

    }

    @Override
    public void keyTyped(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            interfata_controlata.sim.sliders[0].requestFocus();
        }

    }

    @Override
    public void keyPressed(KeyEvent ke) {

    }

    @Override
    public void keyReleased(KeyEvent ke) {
        //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
