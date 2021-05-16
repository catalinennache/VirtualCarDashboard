/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rvfgame.ObdMode;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import vanilla.java.affinity.AffinityLock;

/**
 *
 * @author Enache
 */
public class OBD_Reader extends Kernel implements Runnable {

    public static int FIRSTLEVEL_CHECK = 1;
    public static int SECONDLEVEL_CHECK = 2;
    public boolean enginestate = false;
    public double[] parameters_read = new double[6];
    public Emitter_Receiver defaultComTool;
    public HashMap command = new HashMap();
    public boolean[] bits = new boolean[32];
    public long RealtimeUpdate_StartTimeStamp;
    private long instantupdate_start;
    public long update_rate;
    private boolean connected;
    public static boolean[] av_commands;

    OBD_Reader() {
        for (int i = 0; i < 6; i++) {
            parameters_read[i] = 0;
        }

        this.defaultComTool = Kernel.emrc;
        command.put("rpm", "01 0C\r");
        command.put("speed", "01 0D\r");
        command.put("fuel_level", "01 2F\r");
        command.put("enginestate", "\r");
        command.put("coolant_temp", "01 05\r");
        command.put("TPS", "\r");
        command.put("air debit", "\r");
        command.put("instant fuel consuption", "\r");
        command.put("protocol auto set", "ATSP0\r");
        command.put("display protocol", "ATDP\r");
        command.put("checkpids 1-20", "0100\r");
        command.put("checkpids 21-40", "0120\r");
        command.put("CheckMIL", "01 01\r");
        command.put("restore_defaults", "ATD\r");
        command.put("disable_echo", "ATE0\r");
        command.put("disable_linefeed", "ATL0\r");
        command.put("disable_headers", "ATH0\r");
        command.put("disable_spaces", "ATS0\r");
        av_commands = new boolean[6];
        for (int i = 1; i < 6; i++) {
            av_commands[i] = true;
        }

    }

    @Override
    public void run() {

        performHandShake();
        RealtimeUpdate_StartTimeStamp = System.currentTimeMillis();
        if (!Kernel.forcedstopped) {
            processRAWDATA(this.defaultComTool.WriteAndRead((String) command.get("rpm")), "rpm");
            while (Kernel.ready == false) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(OBD_Reader.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        while (!Kernel.forcedstopped) {
            instantupdate_start = System.currentTimeMillis();
            processRAWDATA(this.defaultComTool.WriteAndRead((String) command.get("rpm")), "rpm");
            processRAWDATA(this.defaultComTool.WriteAndRead((String) command.get("speed")), "speed");
            processRAWDATA(this.defaultComTool.WriteAndRead((String) command.get("coolant_temp")), "coolant_temp");
            if (av_commands[4]) {
                processRAWDATA(this.defaultComTool.WriteAndRead((String) command.get("fuel_level")), "fuel_level");
            }
            if (av_commands[5]) {
                processRAWDATA(this.defaultComTool.WriteAndRead((String) command.get("CheckMIL")), "CheckMIL");
            }
            update_rate = System.currentTimeMillis() - instantupdate_start;

        }

    }

    private void processRAWDATA(byte[] rawresponse, String givencommand) {
        if (givencommand == "rpm") {

            this.parameters_read[1] = this.performConversiontoNumber(rawresponse, givencommand) / 4;
        } else if (givencommand == "speed") {
            this.parameters_read[2] = this.performConversiontoNumber(rawresponse, givencommand);
        } else if (givencommand == "coolant_temp") {
            this.parameters_read[3] = this.performConversiontoNumber(rawresponse, givencommand) - 40;
        } else if (givencommand == "fuel_level") {
            this.parameters_read[4] = (100.0 / 255.0) * this.performConversiontoNumber(rawresponse, givencommand);
        } else if (givencommand == "CheckMIL") {
            boolean[] bits = this.performConversiontoBits(rawresponse, givencommand);
            if (bits[0] == true) {
                this.parameters_read[5] = 1;
            } else {
                this.parameters_read[5] = 0;
            }
        }

        //byte s=(byte 0xA1;
        //hex[] hexresp=new hex[8];
    }

    private void performHandShake() {
        /*ATD
ATZ
AT E0
AT L0
AT S0
AT H0
AT SP 0
         command.put("restore_defaults", "AT D");
        command.put("disable_echo", "AT E0");
        command.put("disable_linefeed", "AT L0");
        command.put("disable_headers", "AT H0\n");
        command.put("disable_spaces", "AT S0\n");*/
        try {
            boolean OK = false;
            //   String rsp= new String( this.defaultComTool.WriteAndRead((String) command.get("restore_defaults")));
            //   while(!OK)  {  }
            this.defaultComTool.WriteAndRead((String) command.get("disable_echo"));
            this.defaultComTool.WriteAndRead((String) command.get("disable_linefeed"));
            this.defaultComTool.WriteAndRead((String) command.get("disable_headers"));
            this.defaultComTool.WriteAndRead((String) command.get("disable_spaces"));
            this.defaultComTool.WriteAndRead((String) command.get("protocol auto set"));
            Thread.sleep(3000);

            this.CheckRequiredCommands_Availability(FIRSTLEVEL_CHECK);
            //Thread.sleep(2000);

            this.CheckRequiredCommands_Availability(SECONDLEVEL_CHECK);
            for (int i = 1; i < 6; i++) {
                switch (i) {
                    case 1:
                        if (av_commands[i] == false) {
                            Kernel.interfc.sim.log.append("WARNING: RPM Reading is NOT SUPPORTED\n APP forcestopped!!\n");
                            synchronized (this) {
                                Kernel.forcedstopped = true;

                            }

                        }
                        break;
                    case 2:
                        if (av_commands[i] == false) {
                            Kernel.interfc.sim.log.append("WARNING: SPEED Reading is NOT SUPPORTED\n APP forcestopped!!\n");
                            synchronized (this) {
                                Kernel.forcedstopped = true;

                            }

                        }
                        break;
                    case 3:
                        if (av_commands[i] == false) {
                            Kernel.interfc.sim.log.append("WARNING: COOLANT TEMP Reading is NOT SUPPORTED\n APP forcestopped!!\n");
                            synchronized (this) {
                                Kernel.forcedstopped = true;

                            }

                        }
                        break;
                    case 4:
                        if (av_commands[i] == false) {
                            int arbFuel;
                            Random rnd = new Random();
                            arbFuel = rnd.nextInt(100) + 1;
                            Kernel.interfc.sim.log.append("WARNING: FUEL LVL Reading is NOT SUPPORTED\n Arbitrary value (" + arbFuel + ") set!! \n");

                        }
                        break;
                    case 5:
                        if (av_commands[i] == false) {
                            int arbCheck;
                            Random rnd = new Random();
                            arbCheck = rnd.nextInt(2);
                            Kernel.interfc.sim.log.append("WARNING: CheckEngine Light Reading is NOT SUPPORTED\n Arbitrary value (" + arbCheck + ") set!! \n");

                        }
                        break;

                }
            }
            {
                Kernel.interfc.sim.log.append("HANDSHAKE PERFORMED...Awaiting for SelfTest\n");

            }

        } catch (Exception e) {
            System.out.println("PERFORM HANDSHAKE:" + e.getMessage() + " " + e.getLocalizedMessage());
        }
    }

    private void CheckRequiredCommands_Availability(int mode) {
        byte[] response = new byte[32];
        if (mode == 1) {
            response = defaultComTool.WriteAndRead((String) command.get("checkpids 1-20"));
        } else {
            response = defaultComTool.WriteAndRead((String) command.get("checkpids 21-40"));
        }
        String givencommand;
        if (mode == 1) {
            givencommand = "01 00\r";
        } else {
            givencommand = "01 20\r";
        }
        boolean[] bits = new boolean[32];
        bits = this.performConversiontoBits(response, givencommand);
        //  this.afiseaza(bits);

        if (mode == 1) {
            //01 05 12 13 
            if (bits[0] != true) {
                OBD_Reader.av_commands[5] = false; //CheckEngine Light
            }
            if (bits[4] != true) {
                av_commands[1] = false;  // RPM 
            }
            if (bits[11] != true) {
                av_commands[2] = false; //Speed
            }
            if (bits[12] != true) {
                av_commands[3] = false; //Temp
            }

        } else {
            if (bits[14] != true) {
                av_commands[4] = false; //Fuel LVL
            }
        }

    }

    private double performConversiontoNumber(byte[] rawresponse, String givencommand) {
        String command_sent = (String) command.get(givencommand);
        String preprocessed_response = new String(rawresponse);
        /* int PARSE_STARTPOINT = command_sent.length() - 2;
        int PARSE_ENDPOINT = preprocessed_response.length() - 2;
        System.out.println("PERFORM CONV TO NUMBER: RAWRESPONSE= " + (new String(rawresponse)) + " and commandset= " + command_sent);
        String trimed_res = "";
        if (!preprocessed_response.contains("\n")) {
            PARSE_ENDPOINT--;
        }
        for (int i = PARSE_STARTPOINT; i < PARSE_ENDPOINT; i++) { /// REVINO LA INTELEGEREA SITUATIEI DE CE E NEVOIE DE MINUS 2
            trimed_res = trimed_res + preprocessed_response.charAt(i);
        }*/
        //  if(preprocessed_response.contains("SEARCHING")) preprocessed_response="41XX0000>";
        String finaltrimed_res = this.clean(preprocessed_response, command_sent);
        finaltrimed_res.trim();

        //      System.out.println("PERFORM CONV TO NUMBER: finaltrimed_res=" + finaltrimed_res+" with length: "+finaltrimed_res.length());
        //String[] res = finaltrimed_res.split(" ");
        String[] res = null;
        if (finaltrimed_res.contains(" ")) {
            res = finaltrimed_res.split(" ");
        } else if (finaltrimed_res.length() > 2) {
            String scopy = finaltrimed_res;
            scopy.trim();
            int modifiedtimes = -1;
            for (int i = 0; i < scopy.length(); i = i + 2) {
                if (i % 2 == 0) {
                    if (modifiedtimes >= 1) {
                        finaltrimed_res = insertspace(finaltrimed_res, i - 1 + modifiedtimes);
                        modifiedtimes++;
                        scopy.trim();
                    } else {
                        finaltrimed_res = insertspace(finaltrimed_res, i - 1);
                        modifiedtimes++;
                        scopy.trim();
                    }
                }
            }
            finaltrimed_res = finaltrimed_res.trim();

            res = finaltrimed_res.split(" ");
        } else {
            res = new String[1];
            res[0] = finaltrimed_res;
        }
        int[] respons = new int[res.length];
        for (int i = 0; i < res.length; i++) {
            //   System.out.println(res[i] + " from string to hex : " + Integer.parseInt(res[i], 16));
            respons[i] = Integer.parseInt(res[i], 16);
        }
        byte[] bas = new byte[respons.length];
        for (int i = 0; i < bas.length; i++) {
            bas[i] = (byte) respons[i];
            //        System.out.println(respons[i] + " => " + (int) bas[i]);
        }
        boolean[] bits = new boolean[bas.length * 8];
        boolean[] temp = new boolean[8];

        for (int i = 0; i < bas.length; i++) {
            
            //     System.out.println("working on number : " + (int) bas[i] + " from " + respons[i]);

            temp = toBits(bas[i]);

            //       System.out.println("Started copying to array <bits> start index " + (i * 8) + " to max index " + (i * 8 + 8 - 1) + " and last index of temp is " + (i * 8 + 8 - 1) % 8);
            for (int k = i * 8; k < i * 8 + 8; k++) {
                bits[k] = (boolean) temp[k % 8];
            }
        }

        double processednr = 0;
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                processednr = processednr + Math.pow(2, bits.length - 1 - i);
            }

        }
        //  System.out.println("CONVERSION TO NUMBER: REZULT OF " + (new String(rawresponse) + " IS " + processednr));
        return processednr;

    }

    private boolean[] performConversiontoBits(byte[] rawresponse, String givencommand) {
        String command_sent = givencommand;
        String preprocessed_response = new String(rawresponse);
        //    System.out.println("PERFORMING CONVERSION TO BITS WITH RAWDATA: " + preprocessed_response);
       // String trimed_res = "";
       
        String finaltrimed_res = this.clean(preprocessed_response, command_sent);
        finaltrimed_res.trim();

        String[] res = null;
        if (finaltrimed_res.contains(" ")) {
            res = finaltrimed_res.split(" ");
        } else if (finaltrimed_res.length() > 2) {
            String scopy = finaltrimed_res;
            int modifiedtimes = -1;
            for (int i = 0; i < scopy.length(); i = i + 2) {
                if (i % 2 == 0) {
                    if (modifiedtimes >= 1) {
                        finaltrimed_res = insertspace(finaltrimed_res, i - 1 + modifiedtimes);
                        modifiedtimes++;
                    } else {
                        finaltrimed_res = insertspace(finaltrimed_res, i - 1);
                        modifiedtimes++;
                    }
                }
            }
            finaltrimed_res = finaltrimed_res.trim();

            res = finaltrimed_res.split(" ");
        }

        int[] respons = new int[res.length];
        for (int i = 0; i < res.length; i++) {
            //    System.out.println(res[i] + " from string to hex : " + Integer.parseInt(res[i], 16));
            respons[i] = Integer.parseInt(res[i], 16);
        }
        byte[] bas = new byte[respons.length];
        for (int i = 0; i < bas.length; i++) {
            bas[i] = (byte) respons[i];
            //     System.out.println(respons[i] + " => " + (int) bas[i]);
        }
        boolean[] bits = new boolean[bas.length * 8];
        boolean[] temp = new boolean[8];

        for (int i = 0; i < bas.length; i++) {
            int xcopy1 = i;
            //       System.out.println("working on number : " + (int) bas[i] + " from " + respons[i]);

            temp = toBits(bas[i]);

            //    System.out.println("Started copying to array <bits> start index " + (i * 8) + " to max index " + (i * 8 + 8 - 1) + " and last index of temp is " + (i * 8 + 8 - 1) % 8);
            for (int k = i * 8; k < i * 8 + 8; k++) {
                bits[k] = temp[k % 8];
            }
        }
        this.afiseaza(bits);
        return bits;

    }

    public boolean[] toBits(byte b) {
        boolean[] bitsarray = new boolean[8];
        double value = (double) b;
        //   System.out.println("Converting " + b + " (" + value + ")" + " to bits ");
        if (value < 0) {
            //      System.out.print("Value= " + value + " + " + "256 = ");
            value = value + 256;
            //      System.out.print(value + "\n");
        }

        for (int i = 8; i < 8; i++) {
            bitsarray[i] = false;
        }

        for (int i = 0; i < 8; i++) {
            double cv = Math.pow(2, 7 - i);

            if (value > cv) {
                bitsarray[i] = true;
                value = value % cv;

            } else {
                if (value % Math.pow(2, 7 - i) == 0) {
                    bitsarray[i] = true;
                    break;
                }

            }
        }

        return bitsarray;

    }

    private String insertspace(String s, int i) {
        String poststring = "";
        //     System.out.println("INSERTSPACE: Am primit stringul <" + s + "> sa inserez un spatiu intre pozitiile " + (i + 1) + " si " + (i + 2));
        if ((i + 1) != 0) {
            for (int k = 0; k < i + 1; k++) {
                poststring = poststring + s.charAt(k);
            }
            poststring = poststring + " ";
        } else {
            //      System.out.println("INSERTSPACE: Space hasn't been inserted due to nonsens");
        }
        poststring = poststring + s.substring(i + 1);

        //   System.out.println(s + "=>" + poststring);
        return poststring;
    }

    public void afiseaza(boolean[] bits) {
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == true) {
                //  System.out.print("1 ");
            } else {
                //  System.out.print("0 ");
            }
        }
        //    System.out.println();

    }

    private String clean(String target, String command) {
        //  System.out.println("target: "+target+" with length: " +target.length());
        String payload = target;
        int modifiedtimes = 0;
        for (int i = 0; i < target.length() - modifiedtimes; i++) {
            if (payload.charAt(i) == Character.LINE_SEPARATOR || payload.charAt(i) == '>' || payload.charAt(i) == Character.SPACE_SEPARATOR) {
                payload = removeCh(payload, i);
                i--;
                modifiedtimes++;
                //       System.out.println("Current payload L: "+payload.length()+" with index:"+ i+" and ENDPOINT: "+(target.length()-modifiedtimes));
            }

        }
        //  System.out.println("CLEAN: payload before headercut is "+payload.length());
        String headercut_payload = "";
        int LastHeaderChar_index = command.length() - 1 - 1; // -1 pentru '\n' si -1 pentru ca incepe numaratoarea de la 0
        for (int i = LastHeaderChar_index; i < payload.length(); i++) {
            headercut_payload = headercut_payload + payload.charAt(i);
        }

        //   System.out.println("CLEAN: headercut_payload leght is "+headercut_payload.length()+" before trim");
        headercut_payload.trim();

        return headercut_payload;
    }

    private String removeCh(String payload, int i) {
        String result = "";
        for (int k = 0; k < i; k++) {
            result = result + payload.charAt(k);
        }
        for (int k = i + 1; k < payload.length(); k++) {
            result = result + payload.charAt(k);
        }

        return result;
    }
}
