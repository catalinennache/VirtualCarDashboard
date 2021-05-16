/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.swing.JButton;
import javax.swing.JTextArea;
import rvfgame.Tools.Simulator;
import rvfgame.Tools.Wizard;

/**
 *
 * @author Enache
 */

public class BTservice implements DiscoveryListener {

    public Vector vectoraparate;
    public LocalDevice localDevice;
    public DiscoveryAgent agent;
    public Object lock;
    public Scanner sc;
    public UUID[] uuid;
    public RemoteDevice rd;
    public String[] conURL;
    public String selectedURL;
    public OutputStream os;
    public InputStream is;
    public JTextArea Monitor;
    public BTConnection bcon;
    public Simulator sim;
    private int ch;

    BTservice(Simulator sim, BTConnection b) throws BluetoothStateException, InterruptedException, IOException {
        Monitor = sim.log;
        this.bcon = b;
        this.sim = sim;

    }

    public void mainexec() throws BluetoothStateException, InterruptedException, IOException {
        Monitor.append("BT service started :)\n");
        lock = new Object();
        if (LocalDevice.isPowerOn()) {
            localDevice = LocalDevice.getLocalDevice();
            vectoraparate = new Vector();
            agent = localDevice.getDiscoveryAgent();
            Monitor.append("Scanning for devices.\n");
            agent.startInquiry(DiscoveryAgent.GIAC, this);// metodele device detected si inquirycompleted vor fi apelate dintr-un alt thread de aceea threadul prinipal este fortat sa astepte notificare
            synchronized (lock) {
                lock.wait();;
                sim.Ch.setEditable(true);
                sim.enter.setEnabled(true);

                Monitor.append("Chose index for the device to communicate with:");
                lock.wait();
                ch = bcon.ch;
                Monitor.append(ch + "\n#############################\n");
                rd = (RemoteDevice) vectoraparate.elementAt(ch - 1);
                uuid = new UUID[1];
                uuid[0] = new UUID("1101", true); //UUID specific pentru a putea face request de SPP service 
                Monitor.append("Interogating for Serial Com service..\n");
                agent.searchServices(null, uuid, rd, this); //la fel ca la scanarea device-urilor thread-urile trebuie sincronizate
            }
            synchronized (lock) {
                lock.wait();
            }
            if (conURL != null) {
                synchronized (lock) {
                    Monitor.append("Chose index for selected URL:");
                    sim.enter.setEnabled(true);
                    sim.Ch.setEditable(true);
                    lock.wait();
                    ch = bcon.ch;
                    Monitor.append(ch + "\n#############################\n");
                }

                selectedURL = conURL[ch - 1];
                StreamConnection con = (StreamConnection) Connector.open(selectedURL);
                os = con.openDataOutputStream();
                is = con.openDataInputStream();
             //   sim.enter.setVisible(false);
              //  sim.Ch.setVisible(false);
                Monitor.append("Connection Established\nOpening GUI\n");
                Wizard.starttriggered=true;

            }
        } else {
            Monitor.append("BT is not powered on\n");
        }
    }

    @Override
    public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
        if (!vectoraparate.contains(rd)) {
            vectoraparate.add(rd);
        }
    }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] srs) {
        if (srs != null && srs.length > 0) {
            conURL = new String[srs.length];
            for (int k = 0; k < srs.length; k++) {
                conURL[k] = srs[k].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            }
        }
    }

    @Override
    public void serviceSearchCompleted(int i, int i1) {
        Monitor.append("Interogation complete.\n#############################\nListing available URL Connections:\n");
        try {
            {
                for (int k = 0; k < conURL.length; k++) {
                    Monitor.append((k + 1) + ". " + conURL[k] + "\n");
                }
            }
        } catch (NullPointerException e) {
            Monitor.append("No Serial Port services were available.\nClose the application and check if:\n# Another device has already connected to the OBD adapter.\n# The OBD adapter is powered on.\n# The OBD adapter is paired with your device.\n# The Virtual necessary Com ports are created or listed in bluetooth control panel.");
        }
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void inquiryCompleted(int i) {
        synchronized (lock) {
            Monitor.append("Scan complete.\n#############################\nListing devices.\n");
            for (int k = 0; k < vectoraparate.size(); k++) {
                RemoteDevice rd = (RemoteDevice) vectoraparate.elementAt(k);

                try {
                    String frd = rd.getFriendlyName(true);

                    Monitor.append((k + 1) + ". " + "(" + frd + ")" + vectoraparate.elementAt(k) + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(BTservice.class.getName()).log(Level.SEVERE, null, ex);
                }
                lock.notify();
            }

        }
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public InputStream getInputStream() {
        return is;
    }

}
