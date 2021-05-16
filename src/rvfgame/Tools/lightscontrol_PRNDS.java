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
import rvfgame.PRNDS.INTRVF_PRNDS;

/**
 *
 * @author enc INCA IN DEZVOLTARE VA DEFINI ENTITATEA CE SE VA OCUPA DE
 * CONTROLUL MARTORILOR PENTRU O MASINA CU TRANSMISIE AUTOMATA
 */
public class lightscontrol_PRNDS implements Runnable {

    private String codemodule = "0x00";
    private JPanel[] tablou;
    private INTRVF_PRNDS intrf;
    private double[] preparameters;
    private double[] postparameters;
    private int parameter_invoked;
    private double parameter_measuredvalue;
    private boolean checked_and_displayed[]=new boolean[7];

   public lightscontrol_PRNDS(INTRVF_PRNDS intrf) {
        System.out.println("LightsControl Created on " + Thread.currentThread().getName());
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
        for(int i=01;i<=6;i++)
        { checked_and_displayed[i]=false;
        
        }

    }

    @Override
  synchronized  public void run() {

        double[] postp = new double[7];
        double[] prep = new double[7];
        for (int i = 1; i <= 6; i++) {
            postp[i] = postparameters[i];
            prep[i] = preparameters[i];
            preparameters[i] = postp[i];

        }
         
            for (int i = 1; i <= 6; i++) {
                if (prep[i] != postp[i] ) {
                    System.out.println("prep" + i + "= " + prep[i] + " was not equal with postp" + i + "= " + postp[i]);
                    this.led_control(postp[i], i, postp);
                }
               
            }
        
        
    

    /*   
        
        if (codemodule.compareTo("1x00")==0)
    {for(int i=0;i<8;i++)
                    { try {
                        tablou[i].setVisible(true);Thread.sleep(400);
        } catch (InterruptedException ex) {
            Logger.getLogger(lightscontrol.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
}       try {
    Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(lightscontrol.class.getName()).log(Level.SEVERE, null, ex);
        }
   
        for(int i=0;i<8;i++)
{  if(i!=2&&i!=7&&i!=0)tablou[i].setVisible(false); tablou[i].repaint();}

    
    }
      if (codemodule.compareTo("0x00")==0)
          for(int i=0;i<8;i++)
                    { try {
                        tablou[i].setVisible(false);
                        tablou[i].repaint(); Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(lightscontrol.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    
      
      if(codemodule.compareTo("1x01")==0) {tablou[2].setVisible(true);tablou[0].setVisible(true);tablou[7].setVisible(true);}*/
}

public synchronized void updateinfo(double[] measuredvalues) {
    //  while(true){  
        for (int i = 1; i <= 6; i++) {
            postparameters[i] = measuredvalues[i];
        }

        (new Thread(this)).start();
       
      //}
    }

    public void manually(String s) {
        try {
            if (s.compareTo("1x00") == 0) {
                try {
                    for (int i = 0; i <= 8; i++) {

                        tablou[i].setVisible(true);
                        //Thread.sleep(200);
                    }

                    Thread.sleep(2000);
                

} catch (InterruptedException ex) {
                    Logger.getLogger(lightscontrol.class
.getName()).log(Level.SEVERE, null, ex);
                }
                 
                for (int i = 0; i <=8; i++) {
                    if (i != 2 && i != 7 && i != 0) {
                        tablou[i].setVisible(false);
                    }
                    tablou[i].repaint();
                    // Thread.sleep(400);
                }

            }
            if (s.compareTo("0x00") == 0) {Thread.sleep(500);
                for (int i = 0; i <= 8; i++) {
                    tablou[i].setVisible(false);
                    tablou[i].repaint();
                 //   Thread.sleep(300);
                }
            }

            if (s.compareTo("1x01") == 0) {
                tablou[2].setVisible(true);

                tablou[0].setVisible(true);

                tablou[7].setVisible(true);
            }

        } catch (Exception e) {
        }
    }

   synchronized private void led_control(double y, int index, double[] postp) {
        switch (index) {
            case 1:
                if (y >= 6300) {
                    intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                    intrf.RPMGauge.setLedBlinking(true);
                } else if (postp[4] <= 50) {
                    if ( y <= 3500) {
                        intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (y > 3500 && y < 4100) {
                        intrf.RPMGauge.setLedColor(LedColor.YELLOW_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (y < 6300) {
                        intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    }
                } else if (y < 6300) {
                    intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                    intrf.RPMGauge.setLedBlinking(false);
                }
                
                break;            
            
            case 4:
                if (y > 60) { if(y <80){intrf.TempGauge.setLedColor(LedColor.YELLOW_LED); intrf.TempGauge.setLedBlinking(false);}
                                    else
                                        if(y<100){intrf.TempGauge.setLedColor(LedColor.GREEN_LED);intrf.TempGauge.setLedBlinking(false);}
                                            else
                                           if(y<103){ intrf.TempGauge.setLedColor(LedColor.RED_LED);intrf.TempGauge.setLedBlinking(false);}
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
                    } else if (postp[1] > 3500 && y < 4100) {
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
                if (y <= 5) {
                    intrf.FuelGauge.setLedColor(LedColor.RED_LED);
                    intrf.FuelGauge.setLedBlinking(true);
                    intrf.Gas.setVisible(true);
                    System.out.println("Reahed low gas because lvlf:"+ postp[6] + " "+postp[4]);
                    new DispatchHelper(intrf.Monitor,"CAUTION: Fuel level is LOW!");
                    checked_and_displayed[index]=true; 
                    
                } else {
                    intrf.FuelGauge.setLedColor(LedColor.GREEN_LED);
                    intrf.FuelGauge.setLedBlinking(false);
                    intrf.Gas.setVisible(false);
                    checked_and_displayed[index]=false; 
                }
                
               
            
        }
    }
}
