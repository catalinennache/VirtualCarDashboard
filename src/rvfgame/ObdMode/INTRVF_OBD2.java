/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;

import eu.hansolo.steelseries.gauges.Radial4;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.LedColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import static javax.swing.BoxLayout.Y_AXIS;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.SwingConstants.BOTTOM;
import javax.swing.border.Border;
import rvfgame.PRNDS.Enginesession_prnds;
import rvfgame.Tools.DispatchHelper;
import rvfgame.Tools.Simulator;

/**
 *
 * @author enc
 */
public class INTRVF_OBD2 extends JFrame {

    public JPanel s;
    public Box y_area;
    public JPanel gauges;

    public JPanel Lights;
    public JPanel Brake;
    public JPanel Gas;
    public JPanel Checkengine;
    public JPanel Oil;
    public JPanel Coolant;
    public JPanel Airbag;
    public JPanel BI;
    public JPanel ABS;
    public JPanel ESP;
    public JPanel Battery;
    public JPanel[] tablou;
    public Enginesession_prnds enginesession = null;
    public JPanel mainpanel;
    public Simulator sim;
    public eu.hansolo.steelseries.gauges.Radial3 RPMGauge;
    public eu.hansolo.steelseries.gauges.Radial4 SpeedGauge;
    public eu.hansolo.steelseries.gauges.Radial1Vertical TempGauge;
    //   eu.hansolo.steelseries.gauges.Radial3 radial34;
    public eu.hansolo.steelseries.gauges.DisplaySingle miniMonitor;
    public eu.hansolo.steelseries.gauges.Radial1Vertical FuelGauge;
    public JTextArea Monitor;
    public Kernel kernel;

    public INTRVF_OBD2(Simulator sim) throws InterruptedException {
        super("RVFGame");
        System.out.println("Interface Created on Thread " + Thread.currentThread().getName());
Thread.sleep(10000);
//synchronized(Emitter_Receiver.synchronizer){Emitter_Receiver.synchronizer.wait();}

        this.sim = sim;
        this.setBackground(Color.black);

        mainpanel = new JPanel(new FlowLayout());
        this.setLayout(new FlowLayout());

        Box area3 = new Box(Y_AXIS);
        Box monitor_struct = new Box(Y_AXIS);
        gauges = new JPanel(new FlowLayout());
        area3.add(gauges);
        TempGauge = new eu.hansolo.steelseries.gauges.Radial1Vertical();

        TempGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
        TempGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        TempGauge.setLedBlinking(true);
        TempGauge.setAlignmentY(BOTTOM);
        TempGauge.setMaxValue(130);
        TempGauge.setMinValue(50);
        TempGauge.setTitle("Temp");
        TempGauge.setUnitString("*C");
        TempGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
        TempGauge.setTickLabelPeriod(80);
        gauges.add(TempGauge);
        RPMGauge = new eu.hansolo.steelseries.gauges.Radial3();
        RPMGauge.setSize(250, 250);
        RPMGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
        RPMGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        RPMGauge.setLedBlinking(true);
        RPMGauge.setMaxValue(7000.0);
        
        RPMGauge.setName("Rot/min"); // NOI18N
        RPMGauge.setTitle("ENG-spd");
        RPMGauge.setUnitString("Rot/min");
        RPMGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
        RPMGauge.setTickLabelPeriod(1000);
        gauges.add(RPMGauge);
        JPanel monitor_pannel = new JPanel(new FlowLayout());
        Monitor = new JTextArea(9, 15);
        JScrollPane monitor_scroll = new JScrollPane(Monitor);
        monitor_scroll.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        monitor_pannel.add(Monitor);
        // monitor_pannel.add(monitor_scroll);
        Monitor.setEditable(false);
        Monitor.setLineWrap(true);
        Monitor.setWrapStyleWord(true);
        Monitor.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0)));
        Monitor.setCaretPosition(Monitor.getDocument().getLength());
        monitor_struct.add(Monitor);

        miniMonitor = new eu.hansolo.steelseries.gauges.DisplaySingle();
        miniMonitor.setSize(70, 100);
        miniMonitor.setLcdDecimals(1);
        miniMonitor.setUnitString("Current TMP");
        miniMonitor.setLcdColor(LcdColor.RED_LCD);
        monitor_struct.add(miniMonitor);
        gauges.add(monitor_struct);
        SpeedGauge = new eu.hansolo.steelseries.gauges.Radial4();
        SpeedGauge.setSize(250, 250);
        SpeedGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
        SpeedGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        SpeedGauge.setLedBlinking(true);
        SpeedGauge.setMaxValue(260.0);
        SpeedGauge.setName("speed"); // NOI18N
        SpeedGauge.setTitle("Speed");
        SpeedGauge.setUnitString("Km/h");
        SpeedGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
        SpeedGauge.setTickLabelPeriod(20);

        gauges.add(SpeedGauge);
        FuelGauge = new eu.hansolo.steelseries.gauges.Radial1Vertical();
        FuelGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
        FuelGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
        FuelGauge.setLedBlinking(true);
        FuelGauge.setMaxValue(100);
        FuelGauge.setMinValue(0);
        FuelGauge.setTitle("Fuel");
        FuelGauge.setUnitString("%");
        FuelGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
        FuelGauge.setTickLabelPeriod(25);
        gauges.add(FuelGauge);
        gauges.setBackground(Color.black);
        Lights = new JPanel();
        // Lights.setBorder(BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0), 4));
        Lights.setBackground(Color.BLACK);
        Lights.setSize(gauges.getWidth(), 40);
        Gas = prepare(Gas, "gas.jpg");
        Gas.setVisible(false);
        Checkengine = prepare(Checkengine, "check.jpg");
        Checkengine.setVisible(false);
        Oil = prepare(Oil, "oil.jpg");
        Oil.setVisible(false);
        Coolant = prepare(Coolant, "coolant.jpg");
        Coolant.setVisible(false);
        Airbag = prepare(Airbag, "airbag.jpg");
        Airbag.setVisible(false);
        ABS = prepare(ABS, "abs.jpg");
        ABS.setVisible(false);
        ESP = prepare(ESP, "esp.jpg");
        ESP.setVisible(false);
        Battery = prepare(Battery, "battery.jpg");
        Battery.setVisible(false);
        Brake = prepare(Brake, "brake.jpg");
        Brake.setVisible(false);

        Lights.add(Oil);
        Lights.add(Gas);
        Lights.add(Checkengine);
        Lights.add(Coolant);
        Lights.add(Airbag);
//  Lights.add(BI);
        Lights.add(ABS);
        Lights.add(ESP);
        Lights.add(Battery);
        Lights.add(Brake);
        area3.add(Lights);
        area3.setBackground(new Color(0, 0, 0));
        mainpanel.add(area3);
        mainpanel.setBackground(Color.black);
        this.add(mainpanel);
      
if(BTConnection.ER==null) System.out.println("EMITOR RECEPTOR NULL");
        synchronized(this){
       // kernel = new Kernel(this, BTConnection.ER);
       new DispatchHelper(this,BTConnection.ER);
        
        }
        
        /* for(int i=0;i<this.sim.entry.length;i++)
        {  this.sim.entry[i].add }}
       for(int i=1;i<this.sim.entry.length;i++)
       {this.sim.entry[i].addFocusListener(ACU);
           this.sim.entry[i].addKeyListener(ACU);}}
        catch(Exception e){e.printStackTrace();}*/
        // sim.setAlwaysOnTop(true);
    }

    public JPanel prepare(JPanel p, String s) {
        p = new JPanel();
        int h = 33;
        int w = 60;
        if (s == "esp.jpg") {
            h = 40;
            w = 58;
        }
        System.out.println("Martor instantiat");
        ImageIcon martor = new ImageIcon(s);
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(s));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dimg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(dimg);
        JLabel lb = new JLabel();
        lb.setIcon(imageIcon);
        p.add(lb);
        p.setBackground(Color.black);
        p.setVisible(true);
        return p;
    }

}
