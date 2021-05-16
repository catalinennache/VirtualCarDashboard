/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import rvfgame.ObdMode.BTConnection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import rvfgame.ElectronicControlUnit;
import rvfgame.PRNDS.ElectronicControlUnit_PRNDS;
import rvfgame.Enginesession;
import rvfgame.PRNDS.Enginesession_prnds;
import rvfgame.INTRVF;
import rvfgame.ObdMode.Emitter_Receiver;
import rvfgame.ObdMode.INTRVF_OBD2;
import rvfgame.ObdMode.Kernel;
import rvfgame.PRNDS.INTRVF_PRNDS;

/**
 *
 * @author enc
 */
public class DispatchHelper implements Runnable {

    public Enginesession target;
    public lightscontrol lgt;
    public INTRVF intrf;
    public INTRVF_PRNDS intrf_prnds;
    public JTextArea Monitor;
    public static ArrayList<String> info=null;
    public double mode;
    private String s;
    public BTConnection bc;
    public Simulator sim;
    public static Object lockd;
    public ElectronicControlUnit ACU;
    public ElectronicControlUnit_PRNDS ACU_PRNDS;
    public String temp_info;
    public static HashMap map;
    private long CurrentTimeStamp;
    private long remainingtime;
    private static Thread clock=null;
    private INTRVF_OBD2 intrfobd;
    private Emitter_Receiver emrc;
    
   

    public DispatchHelper(Enginesession eng, ElectronicControlUnit acu) {
        ACU = acu;
        this.intrf = acu.interfata_controlata;
        mode = 0;
        s = "engine";
        (new Thread(this)).start();

    }

    public DispatchHelper(Enginesession_prnds eng, ElectronicControlUnit_PRNDS acu) {
        ACU_PRNDS = acu;
        this.intrf_prnds = ACU_PRNDS.interfata_controlata;
        mode = 0.1;
        s = "Engine_PRNDS";
        (new Thread(this)).start();

    }

    public DispatchHelper(lightscontrol lgt, INTRVF intrf) {
        this.intrf = intrf;

        mode = 1;
        s = "lightscontrol";
        (new Thread(this)).start();

    }

    public DispatchHelper(JTextArea monitor, String info) {
        Monitor = monitor;
        if(DispatchHelper.info==null) {DispatchHelper.info=new ArrayList<String>(); map=new HashMap(); remainingtime=5000;}
        if(!DispatchHelper.info.contains(info))
        { 
        CurrentTimeStamp=System.currentTimeMillis();
          this.info.add(info);
          map.put(this.info.get(this.info.lastIndexOf(info)), CurrentTimeStamp);
          
         // System.out.println(this.info.get(this.info.lastIndexOf(info))+" ADDED IN QUEUE WITH TIMESTAMP: "+  map.get(this.info.get(this.info.lastIndexOf(info))));
          
        s = "Monitor";
        mode = 2;
        temp_info=info;
        
        if(clock==null) (clock=new Thread(this)).start();
       }

    }

    public DispatchHelper(BTConnection bc, Simulator sim) {
        this.sim = sim;
        this.bc = bc;
        lockd = new Object();
        s = "BTConnection";
        mode = 3;
        (new Thread(this)).start();

    }

    public DispatchHelper(ElectronicControlUnit_PRNDS acu) {
        s = "Logout Engine";
        ACU_PRNDS = acu;
        intrf_prnds = ACU_PRNDS.interfata_controlata;
        mode = 4;
        (new Thread(this)).start();

    }
     public DispatchHelper(INTRVF_PRNDS intrf, ElectronicControlUnit_PRNDS acu) {
        intrf_prnds = intrf;
       // ACU_PRNDS = acu;
        s = "ACU_PRNDS";
        mode = 5;
        
        (new Thread(this)).start();

    }
     public DispatchHelper(INTRVF_OBD2 intrf, Emitter_Receiver emrc)
     { this.intrfobd=intrf;
       this.emrc=emrc;
       s=" KERNEL ";
       mode=6;
       (new Thread(this)).start();
     
     }

    @Override
    public void run() {
        System.out.println("Dispatching  session " + s);
        if (mode == 0) {
            intrf.enginesession = new Enginesession(ACU, intrf.tablou);

        }
        if (mode == 0.1) {
            try {
                intrf_prnds.ECU.ESS = new Enginesession_prnds(ACU_PRNDS, intrf_prnds.tablou);
                 
                intrf_prnds.sim.Tbuttons[6].setEnabled(true);
             

            } catch (InterruptedException ex) {
                System.out.println("INTERRUPTED EXCEPTION DETECTED IN DISPATCH HELPER LINE 89");
            }

        }
        if (mode == 1) {
            intrf.enginesession.boardcontrol = new lightscontrol(intrf);
            System.out.println("Dispatched lightscontrol session");
        }
        if (mode == 2) {
            try {{long StartTimeStamp=System.currentTimeMillis(); 
                { while(remainingtime>0){  
                    Thread.sleep(50);
                    if((System.currentTimeMillis()-(long)map.get(info.get(0)))>5000)
                        Monitor.setText("");
                    refresh(Monitor);
                     for(int i=0;i<info.size();i++)
                        Monitor.append(info.get(i)+"\n");
    
                    //System.out.println("Last item added : "+map.get(info.get(info.size()-1)).toString());
                   if(info.size()>0)
                   {//System.out.println("size is "+info.size());
                   remainingtime=System.currentTimeMillis()-(long)map.get(info.get(info.size()-1));}
                            else
                   {//System.out.println("size is 0"); 
                   remainingtime=0;}
                       }
                  //  System.out.println("Disp." + s + " called with info:<< " + temp_info + " >>");
                //    Monitor.append(temp_info + "\n");
                    
                     info=null;
                     clock=null;
                    Monitor.setText("");}
                
            } } catch (InterruptedException ex) {
            }
        }

        if (mode == 3) {
            try {
                sim.BCon = new BTConnection(sim);
                synchronized (sim.lock) {
                    sim.lock.notify();
                }
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(DispatchHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Dispatched BTSession session");

        }

        System.out.println("Dispatcher " + s + " terminated");

        if (mode == 4) { intrf_prnds.sim.Tbuttons[6].setEnabled(false);
            intrf_prnds.ECU.ESS.boardcontrol.manually("1x01");
            intrf_prnds.ECU.ESS.logout();
            intrf_prnds.ECU.ESS.preparefor_nextsession();
            intrf_prnds.ECU.realtime_update=null;
            intrf_prnds.ECU.ESS = null;
            intrf_prnds.sim.Tbuttons[6].setEnabled(true);

        }
        if (mode == 5) {
            intrf_prnds.ECU = new ElectronicControlUnit_PRNDS(intrf_prnds);
            synchronized (intrf_prnds.lock) {
                intrf_prnds.lock.notify();
            }

        }
        if(mode == 6){
          try{ this.intrfobd.kernel=new Kernel(this.intrfobd,this.emrc); synchronized(this.intrfobd){this.intrfobd.notify();}} catch(Exception e){}
          
          
        }
        

    }
    
    public void refresh (JTextArea monitor)
    {monitor.setText("");
        
    for(int i=0;i<map.size();i++)
      { if(System.currentTimeMillis()-5000 > (long) map.get(info.get(i))) {map.remove(info.get(i)); info.remove(i);}
      
      }
    
   
    }

    public BTConnection getBTSession() {

        // System.out.println("Returning BCON");
        if (bc == null) {
            System.out.println("BCON IS NULL IN DISPATCH HELPER");
        }

        return bc;
    }
}
