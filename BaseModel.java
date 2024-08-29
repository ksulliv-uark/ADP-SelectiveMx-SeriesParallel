/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adp.sgd;

/**
 *
 * @author kahadi
 * 
 */
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import ilog.concert.IloException;
import java.lang.*;


public final class BaseModel {
protected IloCplex master;
protected IloIntVar[] a;
String Out = "";
IloCplex Sub;
DataReader Data;
int [] State;
double [] Alpha;
double [] Beta;
double [] Gamma;
double Theta1;
 
  public BaseModel(DataReader Data1, int[] S, double [] Alp, double [] Bet, double [] Gam) throws IloException, FileNotFoundException, IOException {
        Data = Data1;
        for (int i = 0; i < Data.iCounter; i++) {
         State[i]= S[i];
         Alpha[i]= Alp [i];
         Beta[i]= Bet[i];
         Gamma[i] = Gam[i];        
        }
         //Theta1= Thet1;
        Modeling();
    }
 
  public void Modeling() throws IloException {
    master = new IloCplex();
    a = new IloIntVar[Data.iCounter];
    for (int i = 0; i < Data.iCounter; i++) {
        a[i] = master.intVar(0, Data.com[i], "a_" + Integer.toString(i + 1));
    }
    IloLinearNumExpr exprMaster = master.linearNumExpr();
    master.addMaximize(exprMaster);
    exprMaster.clear();  
    for (int i = 0; i < Data.iCounter; i++) {
        exprMaster.addTerm(Data.w[i]*Alpha[i] + Data.w[i]* Theta1*Beta[i], a[i]);
    } 
    //Constraint1
       Out += "End Obj\t" + Calendar.getInstance().getTime() + "\n";
           for (int j = 0; j < Data.jCounter; j++){
           for  (int i = 0; i < Data.iCounter; i++){
                exprMaster.addTerm(Data.u[j][i], a[i]);
                master.addLe(exprMaster, Data.B[i],"Constraint #1");                
            }
           exprMaster.clear();
        }  
       Out += "End Const 1\t" + Calendar.getInstance().getTime() + "\n";
      // Constraint 2
        for (int i = 0; i < Data.iCounter; i++) {
          master.addLe(a[i], State[i],"Constraint #2");        
          exprMaster.clear();  
        }
      
       Out += "End Const 2\t" + Calendar.getInstance().getTime() + "\n";
      //Constraint 3
       for (int i = 0; i < Data.iCounter; i++) {
         exprMaster.addTerm(-1* Alpha[i], a[i]);
         master.addLe(exprMaster, -Gamma[i]+Alpha[i]*Data.com[i]-Alpha[i]*State[i]- Data.epsilon, "Constraint #3");     
         exprMaster.clear(); 
       }
       Out += "End Const 3\t" + Calendar.getInstance().getTime() + "\n"; 
      //Constraint 4
       for (int i = 0; i < Data.iCounter; i++) {
           exprMaster.addTerm(-1* Beta[i], a[i]);
         master.addLe(exprMaster, -Gamma[i]+Beta[i]*Data.com[i]-Beta[i]*State[i]-Data.epsilon, "Constraint #4");
         exprMaster.clear(); 
       }
       Out += "End Const 4\t" + Calendar.getInstance().getTime() + "\n"; 
      
  }
 
    void MasterEnd() {
        master.end();
    }
 
   void WriteTheModel() throws IloException {
        master.exportModel("Final.lp");
    }
   
    public boolean solve(String R) throws IloException {
        if (master.solve()) {
            if (master.getStatus() == IloCplex.Status.Optimal) {
                System.out.println("Solved!");
                System.out.println(master.getObjValue());
                master.writeSolution("Solution-" + R + "-.sol");
                return true;
            }
        }
        System.out.println(master.getStatus());
        return false;
    }

    public double solve() throws IloException {
        if (master.solve()) {
            if (master.getStatus() == IloCplex.Status.Optimal) {
                return master.getObjValue();
            }
        }
        return -1.0;
    }

    public double getObj() throws IloException {
        return master.getObjValue();
    }
    IloNumVar[] SubObj;
    public double[] getSubObj() throws IloException {
        return Sub.getValues(SubObj);
    }
    
    double [] geta() throws IloException {                 
         double [] X = master.getValues(a);
   
         return X;
    }
 
     void setTimeLimit(int i) throws IloException {
        master.setParam(IloCplex.DoubleParam.TiLim, i * 3600);
    }
}
