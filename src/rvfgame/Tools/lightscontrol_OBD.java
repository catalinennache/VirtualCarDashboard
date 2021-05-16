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
import rvfgame.ObdMode.*;

/**
 *
 * @author Enache
 */
public class lightscontrol_OBD implements Runnable{

  private String codemodule = "0x00";
    public JPanel[] tablou;
    public INTRVF_OBD2 intrf;
    public double[] preparameters;
    public double[] postparameters;
    public int parameter_invoked;
    public double parameter_measuredvalue;
    public boolean checked_and_displayed[]=new boolean[7];
    
    
    public lightscontrol_OBD(INTRVF_OBD2 intf){
    intrf=intf;
    tablou=intrf.tablou;
    postparameters=new double[6];
    preparameters=new double[6];
    
    }

  @Override
   synchronized  public void run() {

        double[] postp = new double[6];
        double[] prep = new double[6];
        for (int i = 1; i < 6; i++) {
            postp[i] = postparameters[i];
            prep[i] = preparameters[i];
            preparameters[i] = postp[i];

        }
         
            for (int i = 1; i < 6; i++) {
                if (prep[i] != postp[i] ) {
                   // System.out.println("prep" + i + "= " + prep[i] + " was not equal with postp" + i + "= " + postp[i]);
                    this.martor_control(postp[i], i, postp);
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
        for (int i = 1; i < 6; i++) {
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
                if (y >= 6000) {
                    intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                    intrf.RPMGauge.setLedBlinking(true);
                } else if (postp[4] <= 70) {
                    if ( y <= 3500) {
                        intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (y > 3500 && y < 4100) {
                        intrf.RPMGauge.setLedColor(LedColor.YELLOW_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (y < 6000) {
                        intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    }
                } else if (y < 6000) {
                    intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                    intrf.RPMGauge.setLedBlinking(false);
                }
                
                break;            
            
            case 4:
                if (y > 70) { if(y <80){intrf.TempGauge.setLedColor(LedColor.YELLOW_LED); intrf.TempGauge.setLedBlinking(false);}
                                    else
                                        if(y<100){intrf.TempGauge.setLedColor(LedColor.GREEN_LED);intrf.TempGauge.setLedBlinking(false);}
                                            else
                                           if(y<103){ intrf.TempGauge.setLedColor(LedColor.RED_LED);intrf.TempGauge.setLedBlinking(false);}
                                                else
                                           {intrf.TempGauge.setLedColor(LedColor.RED_LED);  intrf.TempGauge.setLedBlinking(true); }
                    if (postp[1] > 6000) {
                        intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                        intrf.RPMGauge.setLedBlinking(true);
                    } else if (postp[1] < 6000) {
                        intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (postp[1] <= 3500) {
                        intrf.RPMGauge.setLedColor(LedColor.GREEN_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (postp[1] > 3500 && y < 4100) {
                        intrf.RPMGauge.setLedColor(LedColor.YELLOW_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    } else if (postp[1] < 6000) {
                        intrf.RPMGauge.setLedColor(LedColor.RED_LED);
                        intrf.RPMGauge.setLedBlinking(false);
                    }
                }
                else
                {intrf.TempGauge.setLedColor(LedColor.BLUE_LED);intrf.TempGauge.setLedBlinking(false);}
            
            case 6:
                if (y <= 25) {
                    intrf.FuelGauge.setLedColor(LedColor.RED_LED);
                    intrf.FuelGauge.setLedBlinking(true);
                    intrf.Gas.setVisible(true);                  
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
    
    private void martor_control(double d, int index, double[] postp) {
         switch (index) {
             case 1: 
             {if(d>3000 && postp[3]<80) new DispatchHelper(intrf.Monitor,"Warning: Engine too COLD ("+(int) postp[3]+" *C) for HIGH RPMs!!");}break;
             case 2:
                 break;
             case 3: break;
             
             case 4: if(d<=8.33 && !this.intrf.Gas.isVisible()) this.intrf.Gas.setVisible(true);
                               else
                            if(d>8.33 && this.intrf.Gas.isVisible() ) this.intrf.Gas.setVisible(false);
                 break;
         
             case 5: if(d==1 && !this.intrf.Checkengine.isVisible()) this.intrf.Checkengine.setVisible(true);
                                      else
                                     if(d==0 && this.intrf.Checkengine.isVisible()) this.intrf.Checkengine.setVisible(false);
                 break;
         }
        
        
      }
}
