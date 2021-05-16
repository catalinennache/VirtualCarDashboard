/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import rvfgame.Tools.Simulator;

/**
 *
 * @author Enache
 */
public class Emitter_Receiver extends Thread implements ActionListener {

    private InputStream is;
    private OutputStream os;
    protected Socket s;
    BufferedReader br;
    byte[] rawdata_input = null;
    byte[] osb;
    private boolean connected = true;
    int stream_state;
    JTextArea Monitor;
    JTextField choice;
    Simulator sim;
    String DEV_CONNECTED;
    public long rawdata_TimeSignature = 0;
    public static Object synchronizer = new Object();
    private byte[] raw_final;
    private int realL;
    private boolean Request_Made = false;
    private long comand_sent;

    Emitter_Receiver(InputStream is, OutputStream os, Simulator sim) throws IOException {

        synchronized (synchronizer) {
            this.is = is;
            this.os = os;
            Monitor = sim.log;
            choice = sim.Ch;
            this.sim = sim;
            this.sim.enter.removeActionListener(sim);
            this.sim.enter.addActionListener(this);
            this.sim.enter.setEnabled(false);
           this.sim.Ch.setEditable(false);
             this.sim.Ch.setVisible(true);
             this.sim.enter.setVisible(true);

            DEV_CONNECTED = sim.BCon.BT.rd.getFriendlyName(true);
            new Thread(this).start();
            System.out.println("EMITOR RECEIVER CREATED");
            synchronizer.notifyAll();
        }

    }

    @Override
    public void run() {
        try {
            while (connected) {
                rawdata_input = new byte[32];
                stream_state = is.read(rawdata_input);

            //    System.out.println("RECEIVER: " + new String(rawdata_input));
                String raw = new String(rawdata_input);
                realL = raw.length();
           //     System.out.println("Raw  length: " + raw.length());
                for (int i = rawdata_input.length - 1; i >= 0; i--) {
                    if ((int) rawdata_input[i] == 0) {
                        realL--;
                    }
                }

             //   System.out.println("Real raw length: " + realL);
             realL=stream_state;
                raw_final = new byte[realL];
                for (int i = 0; i < realL; i++) {
                    raw_final[i] = rawdata_input[i];
                }
                if (stream_state > 0) {
                    String r = new String(raw_final);
                    if (!(r.contains(">")) && !(r.contains("SEARCHING")) && !(r.contains("."))) {
                        raw_final = AWT_for_CONSTandPROMPT(raw_final);
                        realL = raw_final.length;
                    }
                    rawdata_TimeSignature = System.nanoTime();
               //     Monitor.append(DEV_CONNECTED + ": " + new String(raw_final) + "\n");
                    synchronized (synchronizer) {
                        synchronizer.wait();
                    }
                    try {
                        Thread.sleep(2);
                    } catch (Exception e) {
                    }
                    rawdata_input = null;
                    raw_final = null;
                } else {
                    connected = false;
                    System.out.println("Connection Terminated (The program will exit in 5 seconds)");
                    Thread.sleep(5000);
                    System.exit(0);
                }

            }
        } catch (Exception e) {
            System.out.println("Connection Terminated err.code 0x01");
            e.printStackTrace();
        }

    }

    public byte[] WriteAndRead(String command) {
        try {
            {

                if (connected) {
                    synchronized (synchronizer) {
                        synchronizer.notify();
                    }
                  //  Thread.sleep(50);
                    long RegisteredRawData_TimeSignature = (long) rawdata_TimeSignature;
                    //  long comand_sent=System.currentTimeMillis();
                    if (command != null) {
                        os.write(command.getBytes());

                        os.flush(); comand_sent=System.currentTimeMillis();
                        this.waitforReply(RegisteredRawData_TimeSignature);
                        String reply = new String(raw_final);
                        while (reply.contains("SEARCHING") || reply.contains(".")) {
                            RegisteredRawData_TimeSignature = (long) rawdata_TimeSignature;
                            synchronized (synchronizer) {
                                synchronizer.notify();
                            }
                            comand_sent=System.currentTimeMillis();
                            this.waitforReply(RegisteredRawData_TimeSignature);
                            reply = new String(raw_final);
                        }
                        if (reply.contains("NO DATA")) {
                            raw_final = "410000000000".getBytes();
                        }
                        byte[] saved_data = new byte[realL];
                        System.arraycopy(this.raw_final, 0, saved_data, 0, raw_final.length);
                        synchronized (synchronizer) {
                            synchronizer.notify();
                        }
                        return saved_data;
                    } else {
                        System.out.println("command string is NULLL");
                    }
                } else {
                    choice.setText("ADAPTER not connected. ");
                    return "410000000000".getBytes();
                }

            }
        } catch (IOException e) {
            connected = false;
            System.out.println(e.getMessage() + " EXCEPTIE IN EMITTER RECEIVER I/O structure " + e.getLocalizedMessage());
        } catch (Exception ex) {
            Logger.getLogger(Emitter_Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "410000000000".getBytes();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String em = choice.getText();
        Monitor.append(" " + em + "\n");
        choice.setText("");
        Monitor.append(new String(this.WriteAndRead(em + '\r')));
    }

  

    private void waitforReply(long RegisteredRawData_TimeSignature) throws IOException {
        while (RegisteredRawData_TimeSignature == rawdata_TimeSignature) {
             if(System.currentTimeMillis()-comand_sent>9000)
                            {os.write("\r".getBytes()); os.flush();comand_sent=System.currentTimeMillis();}
             
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                System.out.println("exceptie prinsa cand se astepta raspunsul");
            }
        }

    }

    /*
    private void ListenOnRequest() throws InterruptedException, IOException
    {  rawdata_input = new byte[32];
                stream_state = is.read(rawdata_input);
                System.out.println("RECEIVER: " + new String(rawdata_input));
                String raw = new String(rawdata_input);
                 realL = raw.length();
                System.out.println("Raw  length: " + raw.length());
                for (int i = rawdata_input.length - 1; i >= 0; i--) {
                    if ((int) rawdata_input[i] == 0) {
                        realL--;
                    }
                }

                System.out.println("Real raw length: " + realL);
                 raw_final = new byte[realL];
                for (int i = 0; i < realL; i++) {
                    raw_final[i] = rawdata_input[i];
                } 
                if (stream_state > 0) {
                    rawdata_TimeSignature = System.nanoTime();
                     Monitor.append(DEV_CONNECTED + ": " + new String(raw_final) + "\n");
                    synchronized (synchronizer) {
                        synchronizer.wait();
                    }
                    try {
                        Thread.sleep(2);
                    } catch (Exception e) {
                    }
                    rawdata_input = null;
                    raw_final=null;
                } else {
                    connected = false;
                    System.out.println("Connection Terminated (The program will exit in 5 seconds)");
                    Thread.sleep(5000);
                    System.exit(0);
                }
    
    
    }
    private void ListenFree() throws IOException, InterruptedException{
         rawdata_input = new byte[32];
                stream_state = is.read(rawdata_input);
                System.out.println("RECEIVER: " + new String(rawdata_input));
                String raw = new String(rawdata_input);
                 realL = raw.length();
                System.out.println("Raw  length: " + raw.length());
                for (int i = rawdata_input.length - 1; i >= 0; i--) {
                    if ((int) rawdata_input[i] == 0) {
                        realL--;
                    }
                }

                System.out.println("Real raw length: " + realL);
                 raw_final = new byte[realL];
                for (int i = 0; i < realL; i++) {
                    raw_final[i] = rawdata_input[i];
                } 
                if (stream_state > 0) {
                   if(this.Request_Made==false) rawdata_TimeSignature = System.nanoTime();
                     Monitor.append(DEV_CONNECTED + ": " + new String(raw_final) + "\n");
                    synchronized (synchronizer) {
                        synchronizer.wait(48);
                    }
                    try {
                        Thread.sleep(2);
                    } catch (Exception e) {
                    }
                    rawdata_input = null;
                    raw_final=null;
                } else {
                    connected = false;
                    System.out.println("Connection Terminated (The program will exit in 5 seconds)");
                    Thread.sleep(5000);
                    System.exit(0);
                }
    }
     */
    private byte[] AWT_for_CONSTandPROMPT(byte[] raw_final) throws IOException {
      //..  System.out.println("AWAITING FOR CONSTRUCTION OR PROMPT");
        byte[] first_piece = raw_final;
        int first_piece_l = first_piece.length;
        byte[] rawdata_input_AWT = new byte[32];
        is.read(rawdata_input_AWT);

      //  System.out.println("RECEIVER-> AWT_F_Constr. : " + new String(rawdata_input_AWT));
        String raw = new String(rawdata_input_AWT);
        int realL_AWT = raw.length();
        // System.out.println("Raw  length: " + raw.length());
        for (int i = rawdata_input_AWT.length - 1; i >= 0; i--) {
            if ((int) rawdata_input_AWT[i] == 0) {
                realL_AWT--;
            }
        }

        // System.out.println("Real raw length: " + realL);
        byte[] second_piece = new byte[realL_AWT];
        for (int i = 0; i < realL_AWT; i++) {
            second_piece[i] = rawdata_input_AWT[i];
        }
        byte[] complete_reply;
        /* if (!(new String(second_piece).contains(">"))) { System.out.println("AWT: "+new String(second_piece)+" doesn't cont. >");
            byte[] trailbytes=new byte[8];
            int trailcode=is.read(trailbytes);
           
            complete_reply = new byte[first_piece_l + realL_AWT + trailcode];
            for (int i = 0; i < complete_reply.length-trailcode; i++) {
                if (i < first_piece_l) {
                    complete_reply[i] = first_piece[i];
                } else {
                    complete_reply[i] = second_piece[i - first_piece_l];
                }
            }
            
            for(int i=complete_reply.length-trailcode+1;i<complete_reply.length;i++)
                complete_reply[i] = trailbytes[i];
        
            
        }
else */
        {
         //   System.out.println("AWT: " + new String(second_piece) + "  cont. >");
            complete_reply = new byte[first_piece_l + realL_AWT];
            for (int i = 0; i < complete_reply.length; i++) {
                if (i < first_piece_l) {
                    complete_reply[i] = first_piece[i];
                } else {
                    complete_reply[i] = second_piece[i - first_piece_l];
                }
            }
        }
        if(!(new String(complete_reply).contains(">")))
        complete_reply=this.AWT_for_CONSTandPROMPT(complete_reply);
         
       // System.out.println("AWT: " + new String(complete_reply));
        return complete_reply;
    }

}
