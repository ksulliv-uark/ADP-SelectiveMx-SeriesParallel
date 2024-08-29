/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adp.sgd;
import ilog.concert.IloException;
import java.io.IOException;
/**
 *
 * @author kahadi
 */
public class Run {
    
     public static void main(String[] args) throws IOException, IloException {
         
        DataReader Data = new DataReader(args[0]);
        ADPSGD ADP = new ADPSGD(Data, Integer.parseInt(args[1]));
        TimeCalculate Cplex = new TimeCalculate();
        Cplex.getTimeUptoNow();
         
     }
    
    
}
