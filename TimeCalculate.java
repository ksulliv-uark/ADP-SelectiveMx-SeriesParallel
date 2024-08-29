/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adp.sgd;

import java.util.Calendar;
import java.util.Date;
/**
 *
 * @author kahadi
 */
public class TimeCalculate {
    long Start;
    long End;
    String Stamp;

    public TimeCalculate() {
        Start = new Date().getTime();
    }

    public void setStart() {
        Start = new Date().getTime();
    }

    public String getTimeUptoNow() {
        End = new Date().getTime();
        int Minute = (int) (End - Start) / (1000 * 60);
        double Second = ((End - Start - 1000 * 60 * Minute) / 1000.0);
        //System.out.println("Duration = " + Integer.toString(Minute) + ":" + Double.toString(Second) + " minutes");
        Stamp = Integer.toString(Minute) + ":" + Double.toString(Second) + " ";
        return getStamp();
    }

    public int getMinute() {
        Long EndNow = new Date().getTime();
        int Minute = (int) (EndNow - Start) / (1000 * 60);
        double Second = ((EndNow - Start - 1000 * 60 * Minute) / 1000.0);
        return Minute;
    }

    public String getTime() {
        Calendar C = Calendar.getInstance();
        return C.getTime().toString();
    }

    String getNumberUptoNow() {
        return Double.toString((End - Start) / (1000 * 60.0));
    }

    public String getStamp() {
        return Stamp;
    } 
}
