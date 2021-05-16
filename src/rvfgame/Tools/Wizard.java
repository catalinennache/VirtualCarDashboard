/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import static javax.swing.BoxLayout.Y_AXIS;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static rvfgame.Tools.RVFGame.sim;

/**
 *
 * @author enc
 * 
 * UN MINI SET-UP PENTRU A STII CE TIP DE SIMULATOR SA SE CONSTRUIASCA
 * 
 */
public class Wizard extends JFrame implements ActionListener {

    public Simulator sim;
    public JPanel modearea;
    public JPanel[] preview;
    public String[] previewsrc;
    public JComboBox modec;
    public JButton Start;
    public JPanel Controls;
    public int selectedmode;
    public Object lock;
    public static boolean starttriggered = false;
    public JLabel smode;
    public JPanel previewarea;

    public Wizard(Object lock) {
        this.lock = lock;
        previewarea = new JPanel(new FlowLayout());
        previewsrc = new String[3];
        previewsrc[0] = "mode0.jpg";
        previewsrc[1] = "mode1.jpg";
        previewsrc[2] = "mode2.jpg";
        modearea = new JPanel(new FlowLayout());
        preview = new JPanel[3];
        this.prepare(preview, previewsrc);
        Controls = new JPanel(new FlowLayout());
        Box levels = new Box(Y_AXIS);
        Start = new JButton("Start");
        Controls.add(Start);
        levels.add(modearea);
        levels.add(previewarea);
        levels.add(Controls);
        String[] mode = {"Manual", "P R N D/S", "Advanced"};
        modec = new JComboBox(mode);
        smode = new JLabel("Selected mode:");
        modearea.add(smode);
        modearea.add(modec);

        for (int i = 0; i < 3; i++) {
            previewarea.add(preview[i]);
        }

        this.add(levels);
        selectedmode = 0;
        preview[selectedmode].setVisible(true);

        Start.addActionListener(this);
        modec.addActionListener(this);

    }

    private static void prepare(JPanel[] p, String[] s) {
        for (int i = 0; i < 3; i++) {
            p[i] = new JPanel();
        }
        int h = 250;
        int w = 650;

        for (int i = 0; i <= 2; i++) {
            try {
                ImageIcon preview = new ImageIcon(s[i]);
                BufferedImage img = null;
                System.out.println("trying to acces :" + s[i] + " on index:" + i);
                img = ImageIO.read(new File(s[i]));

                Image dimg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                ImageIcon imageIcon = new ImageIcon(dimg);
                JLabel lb = new JLabel();
                lb.setIcon(imageIcon);
                p[i].add(lb);
                p[i].setBackground(Color.black);
                p[i].setVisible(false);
                System.out.println("image found");
            } catch (IOException e) {
                System.out.println("image not found");
                JLabel lb = new JLabel("MODE NOT SUPPORTED YET");

                p[i].add(lb);
                lb.repaint();
                //p.setBackground(Color.white);
                p[i].setEnabled(false);
                p[i].setVisible(false);

            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(Start)) {
            this.setVisible(false);
            try {
                sim = new Simulator(selectedmode);
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(Wizard.class.getName()).log(Level.SEVERE, null, ex);
            }
            sim.setVisible(true);
            sim.pack();
            //sim.setSize(500, 200);
            sim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            if(sim.mode==2)
                sim.BCon.exec.start();
               else   
            starttriggered = true;
        } else if (e.getSource().equals(modec)) {
            preview[selectedmode].setVisible(false);
            System.out.print("changing from mode :" + selectedmode);
            selectedmode = modec.getSelectedIndex();
            String s;
            System.out.println(" to mode: " + selectedmode);
            s = "mode" + Integer.toString(selectedmode) + ".jpg";
            System.out.println("image tryed to be accesed: " + s);
            preview[selectedmode].setVisible(true);

            if (!preview[selectedmode].isEnabled()) {
                Start.setEnabled(false);
            } else {
                Start.setEnabled(true);
            }
        }

    }
}
