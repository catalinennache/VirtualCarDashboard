/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;

import rvfgame.ObdMode.INTRVF_OBD2;
import rvfgame.ObdMode.OBD_Reader;
import rvfgame.ObdMode.Emitter_Receiver;
import rvfgame.ObdMode.INTERFACE_Controller;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enache
 */
public class Kernel extends Thread {

    public OBD_Reader o_reader;
    public static INTERFACE_Controller interface_controller;
    public static INTRVF_OBD2 interfc;
    public Thread reader_thread;
    public Thread icontroller_thread;
    public static Emitter_Receiver emrc;
    public static double current_parameters[];
    public long real_parameters_updaterate;
    public static long kernel_update_rate=1; // 1ms in nano
    public static boolean forcedstopped=false;
    public static boolean ready=false;
    public Kernel(INTRVF_OBD2 intrf, Emitter_Receiver emrc) throws InterruptedException {
       synchronized(intrf){
        Kernel.emrc = emrc;
        interfc = intrf;
        o_reader = new OBD_Reader();
        reader_thread = new Thread(o_reader);
        current_parameters = new double[6];
        interface_controller = new INTERFACE_Controller();
        icontroller_thread = new Thread(interface_controller);
    //    this.interface_controller.selft();
        
        
        this.reader_thread.start();
       // this.start();
        this.start();
       }

    }

   public  Kernel() {
   }

    @Override
    public void run() { try {Thread.sleep(3000);
       // Kernel.interface_controller.selft();
        } catch (InterruptedException ex) {
            Logger.getLogger(Kernel.class.getName()).log(Level.SEVERE, null, ex);
        }
    while(true){ parameters_acces(this);
          if(Kernel.current_parameters[1]!=0 && (!INTERFACE_Controller.ICThreadAlive && !INTERFACE_Controller.enginesesionAlreadyCreated)){
           icontroller_thread = new Thread(interface_controller);
           icontroller_thread.start();
           INTERFACE_Controller.ICThreadAlive=true;
           INTERFACE_Controller.enginesesionAlreadyCreated=true;
          
          }
        Kernel.interfc.sim.entry[1].setText(String.valueOf(OBD_Reader.current_parameters[1]));
        Kernel.interfc.sim.entry[1].setText(String.valueOf(OBD_Reader.current_parameters[1]));
        Kernel.interfc.sim.entry[1].setText(String.valueOf(OBD_Reader.current_parameters[1]));
        Kernel.interfc.sim.entry[1].setText(String.valueOf(OBD_Reader.current_parameters[1]));
        kernel_update_rate=(kernel_update_rate+o_reader.update_rate)/2;
        try {
            Thread.sleep(kernel_update_rate);
            
        } catch (InterruptedException ex) {System.out.println("EXCEPTIE IN KERNEL SECTIUNEA RUN");}
    }

    }

    public  void parameters_acces(Object obj) {
        if (obj instanceof INTERFACE_Controller) {
           System.arraycopy(this.current_parameters, 1, this.interface_controller.current_parameters, 1, 5);
        } 
        if (obj instanceof Kernel) {
            setNewData();
        }

        
    }

    private void setNewData() {

        System.arraycopy(this.o_reader.parameters_read, 0, Kernel.current_parameters, 0, 6);

    }

}
