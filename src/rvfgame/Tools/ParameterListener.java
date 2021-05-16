/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.Tools;

import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.SwingConstants.BOTTOM;
import rvfgame.ObdMode.INTERFACE_Controller;
import rvfgame.ObdMode.INTRVF_OBD2;
import rvfgame.ObdMode.Kernel;

/**
 *
 * @author Enache
 */
public class ParameterListener extends Thread {

    public eu.hansolo.steelseries.gauges.Radial3 RPMGauge;
    public eu.hansolo.steelseries.gauges.Radial4 SpeedGauge;
    public eu.hansolo.steelseries.gauges.Radial1Vertical Temp_Fuel;
    //   eu.hansolo.steelseries.gauges.Radial3 radial34;
    public eu.hansolo.steelseries.gauges.DisplaySingle miniMonitor;
    public eu.hansolo.steelseries.gauges.Radial1Vertical FuelGauge;
    public int Gaugeid;
    public double target_parameter;
    private boolean stopped;
    private INTRVF_OBD2 intf = Kernel.interfc;

    public ParameterListener(Object gauge) {
        if (gauge instanceof eu.hansolo.steelseries.gauges.Radial3) {
            RPMGauge = (eu.hansolo.steelseries.gauges.Radial3) gauge;
            Gaugeid = 1;
        } else if (gauge instanceof eu.hansolo.steelseries.gauges.Radial4) {
            SpeedGauge = (eu.hansolo.steelseries.gauges.Radial4) gauge;
            Gaugeid = 2;
        } else if (gauge instanceof eu.hansolo.steelseries.gauges.Radial1Vertical) {
            Temp_Fuel = (eu.hansolo.steelseries.gauges.Radial1Vertical) gauge;
            Gaugeid = 3;
        }
    }

    @Override
    public void run() {
        double localtarget = target_parameter;
        if (Gaugeid == 1) {
            while (!Kernel.forcedstopped) {
                if (localtarget != target_parameter) {
                    RPMGauge.setValue(target_parameter);
                    try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParameterListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                    if (RPMGauge.getValue() != target_parameter) {
                        this.repairComponent(Gaugeid);
                        RPMGauge.setValue(target_parameter);
                    }

                    localtarget = target_parameter;

                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParameterListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (Gaugeid == 2) {
            while (!stopped) {
                if (localtarget != target_parameter) {
                    SpeedGauge.setValue(target_parameter);
                    //this.intf.miniMonitor.setValue(target_parameter);
                    try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParameterListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                    if (SpeedGauge.getValue() != target_parameter) {
                        this.repairComponent(Gaugeid);
                        SpeedGauge.setValue(target_parameter);
                    }
                    localtarget = target_parameter;

                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParameterListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else {
            while (!stopped) {
                if (localtarget != target_parameter) {
                    Temp_Fuel.setValue(target_parameter);
                    try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParameterListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                    if (Temp_Fuel.getValue() != target_parameter) {
                        this.repairComponent(Gaugeid);
                        Temp_Fuel.setValue(target_parameter);
                    }
                    localtarget = target_parameter;

                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ParameterListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    public void setTarget(double target) {
        target_parameter = target;
        this.start();

    }

    public void UPDATE(double newtarget) {
        target_parameter = newtarget;

    }

    private void repairComponent(int id) {

        switch (id) {
            case 1: {
                this.intf.RPMGauge = new eu.hansolo.steelseries.gauges.Radial3();
                this.intf.RPMGauge.setSize(250, 250);
                this.intf.RPMGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
                this.intf.RPMGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
                this.intf.RPMGauge.setLedBlinking(false);
                this.intf.RPMGauge.setMaxValue(7000.0);

                this.intf.RPMGauge.setName("Rot/min"); // NOI18N
                this.intf.RPMGauge.setTitle("ENG-spd");
                this.intf.RPMGauge.setUnitString("Rot/min");
                this.intf.RPMGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
                this.intf.RPMGauge.setTickLabelPeriod(1000);
                RPMGauge=this.intf.RPMGauge;
            }
            break;
            case 2: {
                this.intf.SpeedGauge = new eu.hansolo.steelseries.gauges.Radial4();
                this.intf.SpeedGauge.setSize(250, 250);
                this.intf.SpeedGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
                this.intf.SpeedGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
                this.intf.SpeedGauge.setLedBlinking(false);
                this.intf.SpeedGauge.setMaxValue(260.0);
                this.intf.SpeedGauge.setName("speed"); // NOI18N
                this.intf.SpeedGauge.setTitle("Speed");
                this.intf.SpeedGauge.setUnitString("Km/h");
                this.intf.SpeedGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
                this.intf.SpeedGauge.setTickLabelPeriod(20);
                SpeedGauge=this.intf.SpeedGauge;
            }
            break;
            case 3:
                if (Temp_Fuel.getMinValue() > 0) {
                    this.intf.TempGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
                    this.intf.TempGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
                    this.intf.TempGauge.setLedBlinking(false);
                    this.intf.TempGauge.setAlignmentY(BOTTOM);
                    this.intf.TempGauge.setMaxValue(130);
                    this.intf.TempGauge.setMinValue(50);
                    this.intf.TempGauge.setTitle("Temp");
                    this.intf.TempGauge.setUnitString("*C");
                    this.intf.TempGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
                    this.intf.TempGauge.setTickLabelPeriod(80);
                    this.Temp_Fuel=this.intf.TempGauge;
                } else {
                    this.intf.FuelGauge = new eu.hansolo.steelseries.gauges.Radial1Vertical();
                    this.intf.FuelGauge.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.BLACK);
                    this.intf.FuelGauge.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.SHINY_METAL);
                    this.intf.FuelGauge.setLedBlinking(false);
                    this.intf.FuelGauge.setMaxValue(100);
                    this.intf.FuelGauge.setMinValue(0);
                    this.intf.FuelGauge.setTitle("Fuel");
                    this.intf.FuelGauge.setUnitString("%");
                    this.intf.FuelGauge.setPointerColor(eu.hansolo.steelseries.tools.PointerColor.BLUE);
                    this.intf.FuelGauge.setTickLabelPeriod(25);
                    this.Temp_Fuel=this.intf.FuelGauge;
                }
                break;

        }

    }

}
