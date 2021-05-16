/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import rvfgame.ObdMode.INTRVF_OBD2;
import eu.hansolo.steelseries.tools.LedColor;
import javax.swing.Box;
import static javax.swing.BoxLayout.Y_AXIS;

import javax.swing.JFrame;
import rvfgame.INTRVF;
import rvfgame.PRNDS.INTRVF_PRNDS;
//import javax.bluetooth.*;

/**
 *
 * @author Enache Catalin T.
 *
 *
 * ATAT TOATE CLASELE TIP X_PRNDS (DEOARECE SUNT AFLATE SUB DEZVOLTARE MASIVA)
 * CAT SI DEZORDINEA HAOTICA A CODULUI, NU AR TREBUI LUATE IN CONSIDERARE 
 * DEOARECE IN URMATORUL UPDATE ARHITECTURA PROGRAMULUI VA FI SCHIMBATA RADICAL
 * SI TOATA DEZORDINEA ELIMINATA PENTRU INFORMATII ADITIONALE DESPRE
 * PROGRAM/CLASE/STRUCTURA CONSULTATI JAVADOC DAR SI ANTETUL ANUMITOR
 * CLASE;
 *
 *
 *
 */
public class RVFGame {

    public static Simulator sim;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        //Box box = new Box(Y_AXIS);

        Object lock = new Object();

        synchronized (lock) {
            Wizard wiz = new Wizard(lock);
            wiz.setVisible(true);
            wiz.pack();
            wiz.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            while (!wiz.starttriggered) {
                lock.wait(100); //verifica la fiecare 100 de milisecunde daca set-up-ul a fost facut ca sa poata deschide interfata bazata pe informatiile rezultate din set-up
            }
            RVFGame.sim = wiz.sim;
        }
        //DIN CAUZA COLECTIEI DE BIBLIOTECI STEELSERIES(CEASURILE DE BORD) SE DECLANSEAZA(RANDOM DOAR LA CREAREA GUI-ULUI) O EXCEPTIE(ARRAY OUT OF BOUND) A CAREI CAUZE NU TINE DE DEVELOPER-UL IN CAUZA(DE MINE)
        //DE ACEEA AM DECIS SA FAC URMATOAREA STRUCTURA CARE SE ASIGURA CA EXCEPTIA NU MAI INCHIDE PROGRAMUL SI ASIGURA IN ACELASI TIMP PORNIREA CORECTA
        //CHIAR DACA JAVA VA ANUNTA O EXCEPTIE PROGRAMUL VA REINCERCA SA PORNEASCA INTERFATA (SI O VA PORNI)
        boolean program_triggered_correctly = false;
        while (!program_triggered_correctly) {
            try {
                if (sim.mode == 0) {
                    INTRVF intrf = new INTRVF(sim);
                    intrf.setVisible(true);
                    intrf.pack();
                    intrf.setSize(intrf.getWidth(), intrf.getHeight() + 45);
                    //intrf.setSize(740, 300);
                    intrf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    Thread.sleep(1000);
                    intrf.ACU.selft();
                    program_triggered_correctly = true;
                }
                if (sim.mode == 1) {
                    INTRVF_PRNDS intrf = new INTRVF_PRNDS(sim);
                    intrf.setVisible(true);
                    intrf.pack();
                    intrf.setSize(intrf.getWidth(), intrf.getHeight() + 45);
                    //intrf.setSize(740, 300);
                    intrf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    
                    program_triggered_correctly = true;

                }
                
                if(sim.mode==2){INTRVF_OBD2 intrf = new INTRVF_OBD2(sim);
                    intrf.setVisible(true);
                    intrf.pack();
                    intrf.setSize(intrf.getWidth(), intrf.getHeight() + 45);
                    //intrf.setSize(740, 300);
                    intrf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    
                    program_triggered_correctly = true;
                
                
                }
            } catch (Exception e) {
                program_triggered_correctly = false;
                System.out.println("trying to execute + FAULT: "); e.printStackTrace();
            }
        }

    }
}
