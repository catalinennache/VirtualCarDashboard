/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import eu.hansolo.steelseries.tools.LedColor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import rvfgame.INTRVF;

/**
 *
 * @author enc
 * UN OBIECT CREAT DUPA ACEASTA CLASA ESTE CONSIDERAT O ENTITATE CARE DECIDA CE MARTORI(ATAT MARTORII CONSACRATI CAT SI LEDURILE BORDULUI VIRTUAL) SA DECLANSEZE
 * ARE UN MOD AUTOMAT CE SE EXECUTA PE UN THREAD SEPARAT IN CARE DECIZIILE SE IAU DUPA O LOGICA IMPLEMENTATA, DAR CARE SE AFLA INCA IN DEZVOLTARE CA SI CONCEPT
 * DE ASEMENEA ARE UN MOD MANUAL CARE POATE FI ACCESAT ORICAND FOLOSIND METODA "manually( cod care defineste o anumita operatie : 1xXX inseamna ca este o activitate 
 * care va porni anumiti martori(poate chiar pe toti in functie de a doua parte a codului) exemplu 1x01 va aprinde martorii care semnaleaza ca motorul este oprit dar 
 * contactul este pus si este gata de o noua pornire exemplu 0x00 va stinge fortat toti martorii aprinsi) 
 * 
 */
public class lightscontrol implements Runnable {

    private String codemodule = "0x00";
    private JPanel[] tablou;
    private INTRVF intrf;
    private double[] preparameters;
    private double[] postparameters;
    private int parameter_invoked;
    private double parameter_measuredvalue;

   public lightscontrol(INTRVF intrf) {
        this.intrf = intrf;
        tablou = intrf.tablou;
        
        double rpm = Double.parseDouble(intrf.sim.entry[1].getText());
        double speed = Double.parseDouble(intrf.sim.entry[2].getText());
        double acc = Double.parseDouble(intrf.sim.entry[3].getText());
        double coolant = Double.parseDouble(intrf.sim.entry[4].getText());
        double fuel = Double.parseDouble(intrf.sim.entry[6].getText());
        double airdebit = Double.parseDouble(intrf.sim.entry[5].getText());
        preparameters = new double[7];
        preparameters[1] = rpm;
        preparameters[2] = speed;
        preparameters[3] = acc;
        preparameters[4] = coolant;
        preparameters[6] = fuel;
        preparameters[5] = airdebit;
        postparameters = new double[7];
        postparameters[1] = rpm;
        postparameters[2] = speed;
        postparameters[3] = acc;
        postparameters[4] = coolant;
        postparameters[6] = fuel;
        postparameters[5] = airdebit;
        
    }
    
    @Override
    public void run() {
        double[] postp = new double[7];
        double[] prep = new double[7];
        for (int i = 1; i <= 6; i++) {            
            postp[i] = postparameters[i];
            prep[i] = preparameters[i];
        }
        for (int i = 1; i <= 6; i++) { 
            this.led_control(postp[i], i, postp);
        }

    }
    
    public synchronized void updateinfo(double[] measuredvalues) {
        for (int i = 1; i <= 6; i++) {
            postparameters[i] = measuredvalues[i];
        }
        
        (new Thread(this)).start();
        
        
    }

    public void manually(String s)  {try{
        if (s.compareTo("1x00") == 0) {
          try {  for (int i = 0; i < 8; i++) {
                
                    tablou[i].setVisible(true);
                    Thread.sleep(400);
                } 
                
            
            
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(lightscontrol.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            for (int i = 0; i < 8; i++) {
                if (i != 2 && i != 7 && i != 0) 
                    {
                        tablou[i].setVisible(false);
                    }
                tablou[i].repaint();
               // Thread.sleep(400);
            }
            
        }
        if (s.compareTo("0x00") == 0) {
            for (int i = 0; i < 8; i++) {
                tablou[i].setVisible(false);
                tablou[i].repaint();
                Thread.sleep(300);
            }
        }
        
        if (s.compareTo("1x01") == 0) {
            tablou[2].setVisible(true);
            
            tablou[0].setVisible(true);
            
            tablou[7].setVisible(true);
        }
        
    }
    catch(Exception e){}
    }
    
    private void led_control(double x, int index, double[] postp) {
        switch (index) {
            case 1:
                if (x >= 6300) {
                    intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                    intrf.RPMGauge.setLedBlinking(true);
                } else if (postp[4] <= 50) {
                    if (x <= 3500) {
                        intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (x > 3500 && x < 4100) {
                        intrf.RPMGauge.setLedColor(LedColor.YELLOW_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (x < 6300) {
                        intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    }
                } else if (x < 6300) {
                    intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                    intrf.RPMGauge.setLedBlinking(false);
                }
                
                break;            
            
            case 4:
                if (x > 50) { if(x <80){intrf.TempGauge.setLedColor(LedColor.YELLOW_LED); intrf.TempGauge.setLedBlinking(false);}
                                    else
                                        if(x<120){intrf.TempGauge.setLedColor(LedColor.GREEN_LED);intrf.TempGauge.setLedBlinking(false);}
                                            else
                                           if(x<125){ intrf.TempGauge.setLedColor(LedColor.RED_LED);intrf.TempGauge.setLedBlinking(false);}
                                                else
                                           {intrf.TempGauge.setLedColor(LedColor.RED_LED);  intrf.TempGauge.setLedBlinking(true); }
                    if (postp[1] > 6300) {
                        intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                        intrf.RPMGauge.setLedBlinking(true);
                    } else if (postp[1] < 6300) {
                        intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (postp[1] <= 3500) {
                        intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (postp[1] > 3500 && x < 4100) {
                        intrf.RPMGauge.setLedColor(LedColor.YELLOW_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (postp[1] < 6300) {
                        intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    }
                }
                else
                {intrf.TempGauge.setLedColor(LedColor.BLUE_LED);intrf.TempGauge.setLedBlinking(false);}
            
            case 6:
                if (x <= 5) {
                    intrf.FuelGauge.setLedColor(LedColor.RED_LED);
                    intrf.FuelGauge.setLedBlinking(true);
                    intrf.Gas.setVisible(true);
                    new DispatchHelper(intrf.Monitor,"CAUTION: Fuel level is LOW!");
                    
                } else {
                    intrf.FuelGauge.setLedColor(LedColor.GREEN_LED);
                    intrf.FuelGauge.setLedBlinking(false);
                    intrf.Gas.setVisible(false);
                }
            
        }
    }
}
