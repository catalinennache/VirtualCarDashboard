/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTextField;
import rvfgame.PRNDS.ElectronicControlUnit_PRNDS;
import rvfgame.PRNDS.INTRVF_PRNDS;
import rvfgame.Tools.DispatchHelper;
import rvfgame.Tools.ParameterListener;
import rvfgame.Tools.lightscontrol_OBD;
import rvfgame.Tools.lightscontrol_PRNDS;

/**
 *
 * @author Enache
 */
public class Enginesession_IC {public INTRVF_PRNDS intrf;
    public static ParameterListener RPMListener;
   public static ParameterListener SpeedListener;
   public static ParameterListener TempListener;
   public static ParameterListener FuelListener;
    public double speed;
    public double rpm;
    public double acc;
    public double coolant;
    public double fuel;
    public double checkMIL;
    public double[] parameters;
    
    public lightscontrol_OBD boardcontrol;
   
    public Object lock;
    public static int numberofsessions = 0;
    public INTRVF_OBD2 intrfc;

    public Enginesession_IC  (INTRVF_OBD2 intrf)  {
        System.out.println("EngineSession Created on "+Thread.currentThread().getName());
       
        lock=new Object();
        
        
        intrfc = intrf;
   
        numberofsessions++;
        intrf.Monitor.setText("");
        //intrf.Monitor.append("ENGINE SESSION #"+numberofsessions+" STARTED"+"\n");
        new DispatchHelper(intrf.Monitor, "ENGINE SESSION #" + numberofsessions + " STARTED");
    try {
        // intrf.Monitor.setFont();
        Kernel.interface_controller.selft();

        // DispatchHelper d=new DispatchHelper(boardcontrol);
    } catch (InterruptedException ex) {
        Logger.getLogger(Enginesession_IC.class.getName()).log(Level.SEVERE, null, ex);
    }
       

        rpm = Kernel.current_parameters[1];
        speed = Kernel.current_parameters[2];
      //  acc = Kernel.current_parameters[3];
        coolant = Kernel.current_parameters[3];
        fuel = Kernel.current_parameters[4];
        checkMIL = Kernel.current_parameters[5];

        parameters = new double[7];
        parameters[1] = 0;
        parameters[2] = 0;
        parameters[3] = 0;
        parameters[4] = 0;
        parameters[6] = 0;
        parameters[5] = 0;
        RPMListener=new ParameterListener(this.intrfc.RPMGauge);RPMListener.setTarget(parameters[1]);
        SpeedListener= new ParameterListener(this.intrfc.SpeedGauge);SpeedListener.setTarget(parameters[2]);
        TempListener= new ParameterListener(this.intrfc.TempGauge);TempListener.setTarget(parameters[3]);
        FuelListener= new ParameterListener(this.intrfc.FuelGauge);FuelListener.setTarget(parameters[4]);
        
     
          
         
        
        boardcontrol = new lightscontrol_OBD(intrf);
        synchronized (boardcontrol) {
            boardcontrol.manually("0x00");
            boardcontrol.updateinfo(parameters);
        }
       
     
      
       
    }
    
    public void update(){
      RPMListener.UPDATE(INTERFACE_Controller.current_parameters[1]);
      SpeedListener.UPDATE(INTERFACE_Controller.current_parameters[2]);
      TempListener.UPDATE(INTERFACE_Controller.current_parameters[3]);
      this.intrfc.miniMonitor.setValue(INTERFACE_Controller.current_parameters[3]);
    //  FuelListener.UPDATE(INTERFACE_Controller.current_parameters[4]);
      boardcontrol.updateinfo(INTERFACE_Controller.current_parameters);
      
    
    
    }
    
}
