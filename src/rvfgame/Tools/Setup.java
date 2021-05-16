/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author enc
 * 
 * CLASA INFIINTATA IN IDEEA DE A-TI CREEA PROPRIUL PROFIL AL MASINII (CAPACITATE CILINDRICA, TRANSMISIE, COMBUSTIBIL, SI ALTE SPECIFICATII TEHNICE) DE ASEMENEA
 * SA PERMITA SI SALVAREA SI INCARCAREA UNUI OBIECT("MASINA")(IN CARE VOR FI INCAPSULATE INFORMATIILE) INTR-UN SI DINTR-UN FISIER.
 */
public class Setup extends JFrame {
    
    private JComboBox config_car;
    public String carmodel;
    public JLabel Engine_Capacity;
    public JTextField enginec_entry;
    public double ecc;
    public JLabel Fueltank;
    public JTextField fueltank;
    public double Fuel;
    public JCheckBox Turbocharged;
    public JLabel Transmission;
    public JRadioButton manual;
    public JRadioButton DSG;
    public JRadioButton Secvential;
    public JRadioButton AutomticClassic;
    public JLabel Horsepwr;
    public JTextField horse_entry;
    public double hp;
    public JRadioButton Petrol;
    public JRadioButton Diesel;
    public JPanel panel1;
    public JPanel panel2;
    public JPanel panel3;
    public JPanel panel4;
    public Box box;    
    
   public Setup()
   {
   
   
   }
    
            
    
    
    
}
