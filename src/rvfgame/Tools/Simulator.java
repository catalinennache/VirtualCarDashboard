/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import rvfgame.ObdMode.BTConnection;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import static javax.swing.BoxLayout.Y_AXIS;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/**
 *
 * @author enc
 */
public class Simulator extends JFrame implements ActionListener {

    public JPanel data_entry;
    public JPanel[] tablou;
    public JLabel[] Param;
    public JTextField[] entry;
    public JButton Start;
    public JButton set;
    public JPanel mainpanel;
    public int mode;
    public JToggleButton[] Tbuttons;
    public JSlider[] sliders;
    public BTConnection BCon;
    public InputStream is;
    public OutputStream os;
    public JTextArea log;
    public JTextField Ch;
    public JButton enter;
    private DispatchHelper d;
    public Object lock;
    public JTextField info_ecu;
    public JButton mon;
    public JTextField[] updateTimes;
    public JLabel[] updateTimes_Label;

    public Simulator(int mode) throws InterruptedException, IOException {
        super("Simulator");
        this.mode = mode;
        Param = new JLabel[7];
        entry = new JTextField[7];
        if (mode == 0) {
            JPanel area1 = new JPanel(new FlowLayout());

            JPanel area2 = new JPanel(new FlowLayout());
            Box area3 = new Box(Y_AXIS);
            area3.add(area1);
            area3.add(area2);
            Param[1] = new JLabel("Engine RPM:");
            Param[2] = new JLabel("Speed:");
            Param[3] = new JLabel("ACC.Degrees:");
            Param[4] = new JLabel("Coolant T:");
            Param[5] = new JLabel("A-Debit:");
            Param[6] = new JLabel("Fuel : ");
            area1.add(Param[1]);
            entry[1] = new JTextField(5);
            entry[1].setText("800");
            entry[1].setEditable(false);
            area1.add(entry[1]);
            area1.add(Param[2]);
            entry[2] = new JTextField(5);
            entry[2].setText("0");
            area1.add(entry[2]);
            area1.add(Param[3]);
            entry[3] = new JTextField(6);
            entry[3].setText("0");
            area1.add(entry[3]);
            area2.add(Param[4]);
            entry[4] = new JTextField(5);
            entry[4].setText("24");
            area2.add(entry[4]);
            area2.add(Param[5]);
            entry[5] = new JTextField(5);
            entry[5].setText("0");
            area2.add(entry[5]);
            area2.add(Param[6]);
            entry[6] = new JTextField(5);
            entry[6].setText("10");
            area2.add(entry[6]);
            // mainpanel.add(area3);
            Start = new JButton("self-t on pwr");
            Start.setEnabled(false);
            set = new JButton("Set");
            set.setEnabled(false);
            area2.add(Start);
            area2.add(set);
            this.add(area3);
        }
        if (mode == 1) {
            JPanel area1 = new JPanel(new FlowLayout());
            JPanel area2 = new JPanel(new FlowLayout());
            JPanel area3 = new JPanel(new FlowLayout());
            JPanel area4 = new JPanel(new FlowLayout());
            JPanel area5 = new JPanel(new FlowLayout());
            JPanel area6 = new JPanel(new FlowLayout());
            Box vertical_pos = new Box(Y_AXIS);
            vertical_pos.add(area1);
            vertical_pos.add(area2);
            vertical_pos.add(area3);
            vertical_pos.add(area4);
            vertical_pos.add(area5);
            vertical_pos.add(area6);

            Param[1] = new JLabel("Engine RPM:");
            Param[2] = new JLabel("Speed:");
            Param[3] = new JLabel("ACC.Degrees:");
            Param[4] = new JLabel("Coolant T:");
            Param[5] = new JLabel("A-Debit:");
            Param[6] = new JLabel("Fuel : ");
            area1.add(Param[1]);
            entry[1] = new JTextField(5);
            entry[1].setText("800");
            entry[1].setEditable(true);
            area1.add(entry[1]);
            area1.add(Param[2]);
            entry[2] = new JTextField(5);
            entry[2].setText("0");
            area1.add(entry[2]);
            area1.add(Param[3]);
            entry[3] = new JTextField(6);
            entry[3].setText("0");
            area1.add(entry[3]);
            area2.add(Param[4]);
            entry[4] = new JTextField(5);
            entry[4].setText("24");
            area2.add(entry[4]);
            area2.add(Param[5]);
            entry[5] = new JTextField(5);
            entry[5].setText("0");
            area2.add(entry[5]);
            area2.add(Param[6]);
            entry[6] = new JTextField(5);
            entry[6].setText("10");
            area2.add(entry[6]);

            Tbuttons = new JToggleButton[7];
            Tbuttons[1] = new JToggleButton("ASC", true);
            Tbuttons[2] = new JToggleButton("E-BRAKE", true);
            Tbuttons[3] = new JToggleButton("LAUNCH-CONTROL", false);
             mon = new JButton("Set Start Values");
            area3.add(Tbuttons[1]);
            area3.add(Tbuttons[2]);
            area3.add(Tbuttons[3]);
            area3.add(mon);
            Tbuttons[4] = new JToggleButton("THROTTLE", false);
            Tbuttons[5] = new JToggleButton("BRAKE", false);
            Tbuttons[6] = new JToggleButton("START-STOP", false);
            Tbuttons[6].setEnabled(true);
            area4.add(Tbuttons[4]);
            area4.add(Tbuttons[5]);
            area4.add(Tbuttons[6]);

            sliders = new JSlider[4];
            sliders[1] = new JSlider(JSlider.VERTICAL, 0, 40, 40);
            sliders[1].setMajorTickSpacing(10);
            //sliders[1].setMinorTickSpacing(0);
            sliders[1].setPaintTicks(true);
            sliders[1].setPaintLabels(false);
            //Create the label table
            Hashtable labelTable = new Hashtable();
            labelTable.put(new Integer(0), new JLabel("Sport"));
            labelTable.put(new Integer(40), new JLabel("Park"));
            labelTable.put(new Integer(30), new JLabel("Reverse"));
            labelTable.put(new Integer(20), new JLabel("Neutral"));
            labelTable.put(new Integer(10), new JLabel("Drive"));

            sliders[1].setLabelTable(labelTable);

            sliders[1].setPaintLabels(true);
            sliders[1].setSnapToTicks(true);
            JLabel transmission = new JLabel("PRND/S: ");
            sliders[2] = new JSlider(JSlider.VERTICAL, 0, 50, 0);
            sliders[2].setMajorTickSpacing(10);
            sliders[2].setMinorTickSpacing(1);
            sliders[2].setPaintTicks(true);
            sliders[2].setPaintLabels(true);
            JLabel brake = new JLabel("Brake sns: ");
            sliders[3] = new JSlider(JSlider.VERTICAL, 0, 50, 0);
            sliders[3].setMajorTickSpacing(10);
            sliders[3].setMinorTickSpacing(1);
            sliders[3].setPaintTicks(true);
            sliders[3].setPaintLabels(true);
            
            JLabel Throttle = new JLabel("Throttle sns: ");
            area5.add(Throttle);
            area5.add(sliders[3]);
            area5.add(brake);
            area5.add(sliders[2]);

            area5.add(transmission);
            area5.add(sliders[1]);
            JLabel info_ECU=new JLabel("ECU's TIME RESPONSE:");
             info_ecu=new JTextField("ENGINE NOT IGNITED"); 
            info_ecu.setEditable(false);
            
            area6.add(info_ECU);
            area6.add(info_ecu);
            PRNDS_prepare_setup();
            this.add(vertical_pos);
        }
        if (mode == 2) {
            JPanel area1 = new JPanel(new FlowLayout());
            JPanel area2 = new JPanel(new FlowLayout());
            JPanel area3 = new JPanel(new FlowLayout());
            JPanel area4 = new JPanel(new FlowLayout());
            JPanel area5 = new JPanel(new FlowLayout());
            Box vertical_pos = new Box(Y_AXIS);
            vertical_pos.add(area1);
            vertical_pos.add(area2);
            vertical_pos.add(area3);
            vertical_pos.add(area4);
            vertical_pos.add(area5);
            Param[1] = new JLabel("Engine RPM:");
            Param[2] = new JLabel("Speed:");
            Param[3] = new JLabel("ACC.Degrees:");
            Param[4] = new JLabel("Coolant T:");
            Param[5] = new JLabel("A-Debit:");
            Param[6] = new JLabel("Fuel : ");
            area1.add(Param[1]);
            entry[1] = new JTextField(5);
            entry[1].setText("0");
            entry[1].setEditable(false);
            area1.add(entry[1]);
            area1.add(Param[2]);
            entry[2] = new JTextField(5);
            entry[2].setText("0");
            area1.add(entry[2]);
            area1.add(Param[3]);
            entry[3] = new JTextField(6);
            entry[3].setText("0");
            area1.add(entry[3]);
            area2.add(Param[4]);
            entry[4] = new JTextField(5);
            entry[4].setText("0");
            area2.add(entry[4]);
            area2.add(Param[5]);
            entry[5] = new JTextField(5);
            entry[5].setText("0");
            area2.add(entry[5]);
            area2.add(Param[6]);
            entry[6] = new JTextField(5);
            entry[6].setText("0");
            area2.add(entry[6]);
            for (int i = 1; i <= 6; i++) {
                entry[i].setEditable(false);
            }
            JLabel chl = new JLabel("Command:");
            Ch = new JTextField(30);
            Ch.setEditable(false);
            Ch.setText("");
            enter = new JButton("Submit");
            enter.setEnabled(false);
            area3.setLayout(new FlowLayout());
            area3.add(chl);
            area3.add(Ch);
            area3.add(enter);
            log = new JTextArea(20, 50);
            log.setEditable(false);
            log.setWrapStyleWord(true);
            log.setLineWrap(true);
            log.setFont(log.getFont().deriveFont(18f));

            JScrollPane sc = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            // sc.setViewportView(log);
            sc.setWheelScrollingEnabled(true);
            sc.setAutoscrolls(true);
            area4.setLayout(new FlowLayout());
            // area4.add(log);
            area4.add(sc);
            this.add(vertical_pos);
            enter.addActionListener(this);
            this.establish_connection();
           
        }

    }

    public void run() {
    }
    public void PRNDS_prepare_setup(){
        for (int i = 1; i <= 6; i++) {
    sliders[i%3+1].setEnabled(false);
            Tbuttons[i].setEnabled(false);}
    }
    public void PRNDS_disable_setup() {
        for (int i = 1; i <= 6; i++) {
            entry[i].setEditable(false);
            sliders[i%3+1].setEnabled(true);
            Tbuttons[i].setEnabled(true);
        }
        
        
    }

    private void establish_connection() throws InterruptedException, IOException {
        lock = new Object();
        d = new DispatchHelper(BCon, this);
     //   BCon = new BTConnection(this);
        /*  synchronized (lock) {
            lock.wait();
            System.out.println("INTERFACE AWAKEN");
            BCon = d.getBTSession();
        }*/
    }

    ;

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.enter) {
            if (BCon == null) {
                BCon = d.getBTSession();
            }
            this.enter.setEnabled(false);
            this.Ch.setEditable(false);
            if (BCon != null) {
                BCon.ch = Integer.parseInt(this.Ch.getText());
            } else {
                System.out.println("BCON IS NULL");
            }
            this.Ch.setText("");
            synchronized (BCon.BT.lock) {
                BCon.BT.lock.notify();

            }
        }
    }

}
