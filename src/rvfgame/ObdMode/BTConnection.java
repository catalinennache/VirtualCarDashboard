/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.swing.JTextArea;
import rvfgame.Tools.Simulator;

/**
 *
 * @author Enache
 */
public class BTConnection extends Thread {

    public static InputStream is;
    public static OutputStream os;
    public JTextArea Monitor;
    public Simulator sim;
    public BTservice BT;
    public static Emitter_Receiver ER;
    public int ch;
    public Thread exec;

    /**
     * @param args the command line arguments
     * @throws javax.bluetooth.BluetoothStateException
     * @throws java.lang.InterruptedException
     *
     * CONECTIVITATE BLUETOOTH DATORITA BIBIOTECII BLUECOVE
     *
     */
   public  BTConnection(Simulator sim) throws InterruptedException, IOException {
        this.sim = sim;
        ch = 0;
        exec = new Thread(this);
  

    }

    public void main() throws BluetoothStateException, InterruptedException, IOException {

        BT = new BTservice(sim, this);
        BT.mainexec();
        is = BT.getInputStream();

        os = BT.getOutputStream();
        if (is != null && os != null) {
            ER = new Emitter_Receiver(is, os, sim);  
        } else {
            System.out.println("Something went wrong, streams are null");
        }

    }

    @Override
    public void run() {

        try {
            this.main();
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(BTConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
