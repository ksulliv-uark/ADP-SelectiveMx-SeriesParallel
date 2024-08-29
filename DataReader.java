/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adp.sgd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author kahadi
 */
public class DataReader {
    int iCounter; //Number of Subsystem
    int jCounter; //Number of Resources
    //int Time; //Time Horizon
    int [] com; // Number of components at each subsystem
    int [] inS; // Initial State
    double [] r; // Reliability of each subsystem
    double [][] u; //Resource usage at each susbsystem
    double [] B; //resource available 
    double [] w; //weight of each subsystem
    double epsilon;
    int iter;
    int TimeLimit;
    public void setTime(int TimLim) {
        TimeLimit = TimLim;
    }
    
     public int getTimeLimit() {
        return TimeLimit;
    }
    
    public DataReader(String Name)throws FileNotFoundException, IOException {
      System.out.println("Input File: \t" + Name);
       BufferedReader Input = new BufferedReader(new FileReader(Name));
        iCounter = Integer.parseInt(Input.readLine());
        jCounter = Integer.parseInt(Input.readLine());
        iter = Integer.parseInt(Input.readLine());
        epsilon = Double.parseDouble(Input.readLine());
       // Time = Integer.parseInt(Input.readLine());
       
        inS= new int [iCounter];
        for (int i = 0; i < iCounter; i++){
            inS[i] = Integer.parseInt(Input.readLine());            
        }
        
        com = new int [iCounter];
        for (int i = 0; i < iCounter; i++){
            com[i] = Integer.parseInt(Input.readLine());            
        }
        
        
        r = new double[iCounter];
        for (int i = 0; i < iCounter; i++) {
            r[i] = Double.parseDouble(Input.readLine());
        }
        
        u = new double[jCounter][iCounter];
        for (int j = 0; j < jCounter; j++) {            
            String R = Input.readLine();
            String[] NewLine = R.split("!");
            for (int i = 0; i < iCounter; i++) {
                u[j][i] = Double.parseDouble(NewLine[i]);
            }
        }
        
        B = new double[jCounter];
        for (int j = 0; j < jCounter; j++) { 
          B[j] = Double.parseDouble(Input.readLine());   
        }
        
//        w = new double[iCounter];
//        for (int i=0; i <iCounter; i++) {
//          w[i] = Double.parseDouble(Input.readLine());
//        }
    }
    
}
