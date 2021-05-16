/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.PRNDS;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author Enache
 */
public class Physics_PRNDS extends Thread {

    public static double MAX_POWER = 60; //kW
    public static double MAX_TORQUE = 165; //Nm
    public static INTRVF_PRNDS interfc;
    public static double real_parameters[] = new double[7];
    public HashMap map = new HashMap();
    public long StartTimeStamp = System.currentTimeMillis();
    public long dt;
    public double reflevel_temp_function;
    public static boolean termostate_open_state = false;
    public double currentTorque;
    public double currentPower;
    public HashMap ratedPower = new HashMap();
    public HashMap ratedTorque = new HashMap();
    private long termoopened_timestamp;
    private double x;
    private static long REALTIME_UPDATE = 500;
    private boolean Fan_start_state = false;
    private long fanopened_timestamp;
    private long Fanstopped_timestamp;
    private double dt4;
    private long[] f_timestamp=new long[2];
    private int f=-1;

    Physics_PRNDS(INTRVF_PRNDS inf) {
        Physics_PRNDS.interfc = inf;
        f_timestamp[0]=this.termoopened_timestamp;
        f_timestamp[1]=this.Fanstopped_timestamp;
        ratedPower.put(MAX_POWER, 6300);
        ratedTorque.put(MAX_TORQUE, 4800);

    }

    public void setInitialData(JTextField[] entries) {
        for (int i = 1; i <= 6; i++) {
            real_parameters[i] = Double.valueOf(entries[i].getText());

        }
        System.out.println("TEMP on start " + real_parameters[4]);
        //map.put("rpm", real_parameters[1]);
        //    map.put("speed",real_parameters[2] );
        //     map.put("tps",real_parameters[3]);
        //     map.put("coolant", real_parameters[4]);
        //if (real_parameters[4] > 80) {
        //      termostate_open_state = true;
        //}
        if (real_parameters[4] > 133) {
            Fan_start_state = true;
        }
        reflevel_temp_function = this.calculateX_reflevel(real_parameters[4]);

        //     map.put("airdebit", real_parameters[5]);
        //      map.put("fuel", real_parameters[6]);
    }

    @Override
    synchronized public void run() {
        while (true) {

            //  this.speed_phx();
            //     this.rpm_phx();
            this.modify_temp();
            this.modify_fuel();
            System.out.println("PHX UPDATED: " + real_parameters[4]);
            try {
                Thread.sleep(REALTIME_UPDATE);
            } catch (InterruptedException ex) {
                Logger.getLogger(Physics_PRNDS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    synchronized private void modify_temp() {
        if (interfc.ECU.ESS != null) {
            if (real_parameters[4] < 80 && this.termostate_open_state == true) {
                System.out.println("REF LVL inainte de  INCHIDERE TERMOSTAT: " + reflevel_temp_function + " " + real_parameters[4] + " dt: " + dt);

                closeTermostate();
                
                StartTimeStamp = System.currentTimeMillis();
                dt = System.currentTimeMillis() - StartTimeStamp;
                reflevel_temp_function = this.calculateX_reflevel(real_parameters[4]);
                System.out.println("REF LVL DUPA INCHIDERE TERMOSTAT: " + reflevel_temp_function + " " + real_parameters[4] + " dt: " + dt);
                //x = (reflevel_temp_function + dt) * 0.001;
            }

            if (this.termostate_open_state == false && real_parameters[4] < 92) {
                dt = System.currentTimeMillis() - StartTimeStamp;
                reflevel_temp_function = this.calculateX_reflevel(real_parameters[4]);
                x = (reflevel_temp_function + dt) * 0.001;
                real_parameters[4] = (real_parameters[1] / (45) + friction_compensation(real_parameters[1])) * Math.log(x + 1);
            } else {
                if (this.termostate_open_state == false && real_parameters[4] > 92) {
                    this.openTermostate();
                     if(f%2!=0)f++;
                    ///SUGESTIE , CALCULAREA LUI REFLEVEL TREBUIE BAGATA IN FUNCTIILE OPENTERMOSTAT SI CLOSE TERMOSTAT
                    f_timestamp[0] = System.currentTimeMillis();
                }

                if ((System.currentTimeMillis() - f_timestamp[0]) * 0.001 < 50) {
                    double dt2 = (System.currentTimeMillis() - f_timestamp[0]) * 0.001;
                    real_parameters[4] = (-0.20 + Math.pow(10, (-5)) * real_parameters[1]) * dt2 + 92;
                } else {
                    if ((System.currentTimeMillis() - f_timestamp[0]) * 0.001 > 50 && (System.currentTimeMillis() - termoopened_timestamp) * 0.001 < 50) {
                        reflevel_temp_function = this.calculateX_reflevel(real_parameters[4]);
                    }
                    double dt3 = (System.currentTimeMillis() - f_timestamp[f%2]) * 0.001 - 50; //50 secunde
                    // x = this.reflevel_temp_function * 0.001 + dt3;
                    x = this.reflevel_temp_function * 0.001 + dt3;
                    
                    if (real_parameters[4] > 105 && Fan_start_state == false) {
                        startFan();
                          fanopened_timestamp=System.currentTimeMillis();
                          if(f%2!=1)f++;
                    }
                    if (Fan_start_state == true) {
                        if (real_parameters[4] < 92) {
                            stopFan();
                            f_timestamp[1]=System.currentTimeMillis();
                            this.reflevel_temp_function=this.calculateX_reflevel(real_parameters[4]);
                          
                        } else {
                            System.out.println(real_parameters[4] + " ");
                            dt4 = (System.currentTimeMillis() - fanopened_timestamp) * 0.001 ; //50 secunde
                            // x = this.reflevel_temp_function * 0.001 + dt3;
                         //   x = this.reflevel_temp_function * 0.001 + dt3;

                            real_parameters[4] = (-0.80 + Math.pow(10, (-5)) * real_parameters[1]) * dt4 + 105;

                            // real_parameters[4] = 18 * Math.log(x + 1) + this.heat_fromEngineLoad() + this.speed_coolingeffect(120, x);
                            System.out.println(real_parameters[4] + " ");
                            
                        }
                    } else { 
                        System.out.println("REACHED TERMOSTATOFF SECOND LOG WITH TEMP: " + real_parameters[4]);
                        real_parameters[4] = 18 * Math.log(x + 113) + this.heat_fromEngineLoad() + this.speed_coolingeffect(real_parameters[2], x) -2;
                        // temperatura trebuie sa varieze in functie de timp turatie/(sarcina motorului) si viteza(efectul de racire al radiatorului) 
                        System.out.println("PASSED TERMOSTATOFF SECOND LOG WITH TEMP: " + real_parameters[4]);

                    }
                }

            }
        }

    }

    private void modify_fuel() {

    }

    private void speed_phx() {
        if (interfc.sim.sliders[1].getValue() == 20) {
            real_parameters[2] = real_parameters[2] - (this.calculateFrictionForce(real_parameters[2], real_parameters[1])) / (1800) * REALTIME_UPDATE * 0.001;
        }
    }

    private void rpm_phx() {
        if (interfc.sim.sliders[1].getValue() == 20 || interfc.sim.sliders[1].getValue() == 40) {
            real_parameters[1] = real_parameters[1] - 500 * REALTIME_UPDATE * 0.001;
        } else if (interfc.sim.sliders[1].getValue() != 40) {
            real_parameters[1] = real_parameters[1] - 200 * REALTIME_UPDATE * 0.001;
        }
    }

    private void startFan() {
        Fan_start_state = true;

    }

    private void openTermostate() {
        termostate_open_state = true;
    }

    private double calculateX_reflevel(double y) {
        if (Physics_PRNDS.termostate_open_state == false) {
            return Math.pow(Math.E, (y / 45) + this.friction_compensation(y)) + 1;
        } else {
            return Math.pow(Math.E, (y / 45) + this.friction_compensation(y)) + 113+20;
                    //Math.pow(Math.E, (y -(this.speed_coolingeffect(real_parameters[2], x) -2) / 18))  + 113;
                                                                                                 //+ this.friction_compensation(y))
            //Math.pow(Math.E, (y / 18) + 113); // INCOMPLETA, TREBUIE SA RETURNEZE INVERSA FUNCTIEI DE LA LINIA 79;
        }
    }

    private double friction_compensation(double rpm) {
        return 0.00000000277 * rpm;
    }

    private void closeTermostate() {
        termostate_open_state = false;

    }

    private double speed_coolingeffect(double speed, double current_time) {
        speed = (speed * 1000 / 3600);
        return (-0.072) * speed * current_time;
    }

    private double heat_fromEngineLoad() {
        return 0;

    }

    private double calculateFrictionForce(double real_parameter, double speed) {
        double rpm = real_parameter;

        double currentPower = (MAX_POWER * rpm) / ((double) ratedPower.get(MAX_POWER));
        speed = (speed * 1000 / 3600);
        return MAX_POWER / speed + 458 + 500;
    }

    private void stopFan() {
        Fan_start_state = false;
    }
}
