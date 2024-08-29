/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adp.sgd;

import ilog.concert.IloException;
import java.io.IOException;
import java.lang.*;

/**
 *
 * @author kahadi
 */
public final class ADPSGD {
    static double Budget;
    static int N;
    static double [][] Theta1; 
    static double [][][] Theta2;
    static double v1;
    static double [] v2;
    static int [][][] S;
    static int [][][] A;
    static int [] State;
    static int [] Action;
    static int [][][]BestAction;
    static double [][]BestValue1;
    static double [][]BestValue2;
    static int [] working; //number of working components 
    static double [] alpha;
    static double [][] Vbarr;
    static double [] Max2;
    static double [] Alph;
    static double [] Bet;

    DataReader Data;
 
    /**
     *
     * @param Data1
     * @param TimeHorizon
     * @throws IOException
     * @throws IloException
     */
    public ADPSGD (DataReader Data1, int TimeHorizon) throws IOException, IloException {
        Data = Data1;
        N = 500;

        S = new int[Data.iCounter][TimeHorizon][N];
        State = new int[Data.iCounter];
        v2 = new double[Data.iCounter];
        Theta1 = new double[TimeHorizon][N];
        Theta2 = new double[Data.iCounter][TimeHorizon][N];
        alpha = new double[N];
        Alph = new double [Data.iCounter];
        Bet = new double [Data.iCounter];
        BestAction = new int [Data.iCounter][TimeHorizon][N];
        BestValue1 = new double [TimeHorizon][N];   
        Vbarr = new double [TimeHorizon][N];
        working = new int [Data.iCounter];

        for (int t = 0; t < TimeHorizon; t++){
           if (t!= TimeHorizon-1){ 
           Theta1[t][0]= 0; 
           for (int i= 0; i < Data.iCounter; i++){  
                Theta2[i][t][0] = 1-Data.r[i];  
              } 
           }else{ 
           Theta1[TimeHorizon-1][0] = 0; 
           for (int i= 0; i < Data.iCounter; i++){  
                Theta2[i][TimeHorizon-1][0] = 1-Data.r[i] ;  
              } 
           }    
        }  
        for (int i =0 ; i < Data.iCounter; i++){
         Alph[i]= -1* java.lang.Math.log(1-Data.r[i]);        
       }
for (int n = 0; n < N; n++){
         for (int i = 0; i <Data.iCounter; i++) {
             S[i][0][n] = Data.inS[i];
         }
   
    for (int t = 0; t < TimeHorizon; t++){
            for (int i = 0; i < Data.iCounter; i++){
                Bet [i] = java.lang.Math.log(1-Theta2[i][t][n]);
            }   
        v1 = Theta1[t][n];
        for (int i = 0; i <Data.iCounter; i++) {
             State[i]= S[i][t][n];             
             v2[i] = Theta2[i][t][n];
        } 
//            double Max1;
//            Max1 = 0;      
//            ArrayList<int[]> AllAction = ActionGenerator(State, Budget);             
//            for (int[] Action1 : AllAction) {  
//               if (Max1 < ValueFunction(State, Action1, v1,v2)+ RCalculator(State,Action1)){ 
//                  Max1  =  ValueFunction(State, Action1, v1,v2)+ RCalculator(State,Action1);
//                  for (int i =0; i < Data.iCounter; i++ ){
//                  BestAction[i][t][n]= Action1[i];                
//                  } 
               
                double[] OptimalAction=  MWO(State, Alph, Bet);
               
                for (int i =0; i < Data.iCounter; i++){
                    BestAction[i][t][n]= (int) Math.round(OptimalAction[i]);
                }
                BestValue1[t][n]= ValueFunction(State, BestAction[t][n], v1,v2)+ RCalculator(State,BestAction[t][n]);
//                Vbarr[t][n] = ValueFunction(State, Action1, v1,v2);
                
        for (int i =0; i < Data.iCounter; i++)  {
           working [i]= Data.com[i]-S[i][t][n]+ BestAction[i][t][n]; 
           if (t != TimeHorizon-1){
              S[i][t+1][n]= S[i][t][n] - BestAction[i][t][n]+ getBinomial(working,Data.r)[i];
           }   
        }
        if ( t != 0 && n != N-1 ){
          double V = 1;  
          for (int j=0; j < Data.iCounter; j++){ 
            V = V* (1- Math.pow(Theta2[j][t-1][n],(Data.com[j]- S[j][t-1][n] + BestAction[j][t-1][n])));    
          }              
         Theta1[t-1][n+1] = Theta1[t-1][n]-0.1*(Theta1[t-1][n]* V- BestValue1[t][n])* V;  
         for (int j=0; j < Data.iCounter; j++){
         Theta2[j][t-1][n+1]= Theta2[j][t-1][n]-0.1*(Theta1[t-1][n]* V- BestValue1[t][n])*Theta1[t-1][n]*(-Data.com[j]+ S[j][t-1][n] - BestAction[j][t-1][n])* Math.pow(Theta2[j][t-1][n],(Data.com[j]- S[j][t-1][n] + BestAction[j][t-1][n]-1))* (V/(1- Math.pow(Theta2[j][t-1][n],(Data.com[j]- S[j][t-1][n] + BestAction[j][t-1][n]))));      
         }  
         if (t == TimeHorizon-1){
           Theta1[TimeHorizon-1][0] = 0; 
           for (int i= 0; i < Data.iCounter; i++){  
                Theta2[i][TimeHorizon-1][0] = 0 ;  
              }   
         }
             
    }
}    
        
      
} 
         
       

    for (int t=0; t < TimeHorizon; t++){
            System.out.println("*********************");
            System.out.println("T = " + t);
            System.out.println("N = " + N);
            System.out.println("ValueFunction= " + BestValue1[t][N-1]);
            System.out.println("Vbar= " + Vbarr [t][N-1]);
            System.out.println("Theta1=" + Theta1 [t][N-1]);
            System.out.println("Best Action");
            for (int i = 0; i < Data.iCounter; i++) {
                System.out.print(BestAction[i][t][N-1] + "\t");
                
            }
            System.out.println();
            System.out.println("State");
            for (int i = 0; i < Data.iCounter; i++) {
                System.out.print(S[i][t][N-1] + "\t");
            }    
            System.out.println();
            
        }    

    } 
    
    
     public double [] MWO (int [] State, double [] Alpha, double[] Beta) throws IOException, IloException {
//     private  int Iteration;
//    private TimeCalculate GATime;
//    private final  boolean timeRun;
//    private final  int TimeLimit;
  //  private DataReader Data;
//    private  double [][] a;
//    private  double [][] Rmin;
//    private  double [] Gamma;
//    private double Delta;
//    private double [] R;
//    private double [][]RS;
//    private  double RStar;
//    private  double [] BestAction;
//        timeRun = runtime;
//        TimeLimit = timeMinute;        
       // Data = new DataReader(Name);       
        int Iteration = 100; 
        double [][] a = new double [Data.iCounter][Iteration];
        double [][] Rmin = new double [Data.iCounter][Iteration];
        double [] R = new double [Iteration];
        double [][] RS = new double [Data.iCounter][Iteration];
        double[] Gamma= new double [Data.iCounter];
        double [] BestAction = new double [Data.iCounter];
        double RStar = 0;
        double Delta = 0.00001;
        
        for(int i =0; i < Data.iCounter; i++){
              a[i][0] = 0;
          }
          R[0]  = 1.0;   
          for (int i = 0; i < Data.iCounter; i++){            
             R [0]= R[0] * (1- Math.pow((1-Data.r[i]),Data.com[i]- State[i] + a[i][0]));    //Calculate reliability of system 
          } 
          RStar = R[0];
          for(int i = 0; i < Data.iCounter; i++){
           Rmin[i][0] = 0;//1- Math.pow((1-Data.r[i]),Data.com[i]- State[i] + a[i][0]);              
          }
    for (int q = 0; q < Iteration;q++){
         for(int i = 0; i < Data.iCounter; i++){
         Gamma[i] = java.lang.Math.log(1-Rmin[i][q]);    
         }         
         BaseModel Master= new BaseModel (Data, State, Alpha, Beta, Gamma); 
         double [] AOptimal = Master.geta(); 
         for (int i = 0; i < Data.iCounter; i++){
          a[i][q] = AOptimal[i];       
         } 
         
        R[q]  = 1.0;          
        for (int i = 0; i < Data.iCounter; i++){            
             R[q] = R[q] * (1- Math.pow((1-Data.r[i]),Data.com[i]- State[i] + a[i][q]));    //Calculate reliability of system 
             RS[i][q]= (1- Math.pow((1-Data.r[i]),Data.com[i]- State[i] + a[i][q]));
        }
        double min = 1;
        int k = 0;
        for (int i = 0; i < Data.iCounter; i++){
            if( min >= RS[i][q]){
            min = RS[i][q];
            k = q;
            }
        }
       Rmin[k][q] = RS[k][q]+ Delta;
       
//        if (RStar <= R[q]){
//           RStar = R[q]; 
//           for (int i =0 ; i < Data.iCounter; i ++){
//           Rmin[i][q] = 1- Math.pow((1-Data.r[i]),Data.com[i]- State[i] + a[i][q]);
//           Gamma[i] = java.lang.Math.log(1-Rmin[i][q]);
//        }
//        }else{
//        for (int i = 0; i < Data.iCounter; i++){
//           BestAction[i]=  a[i][q-1]; 
//           break;       
//          }
//            
//        }
                          
}
    double max = 0;
    int v =0;
    for (int q = 0; q < Iteration;q++){
      if( max <= R[q]){ 
          RStar= R[q];
          v = q;
      }  
     }
      for (int i = 0; i < Data.iCounter; i++){
           BestAction[i]=  a[i][v];       
     }  
      

        return BestAction;
    
    
    }  
           
    public double RCalculator( int[] State, int[] Action) {
            double R;    
            int W;            
            R  = 1.0;
           for (int i=0; i < Data.iCounter; i++){            
             W = Data.com[i]- State[i] + Action[i]; // Number of working components
             R = R * (1- Math.pow((1-Data.r[i]),W));    //Calculate reliability of system 
          }       
          return R;
       } 
   public double ValueFunction( int[] State, int[] Action, double v1, double[] v2) {
       
          double  Vbar1 = 1;
          for (int j=0 ; j < Data.iCounter; j++){
             Vbar1 = Vbar1 * (1- Math.pow(v2[j],(Data.com[j]- State[j] + Action[j]))); 
          }  
          double Vbar = Vbar1 * v1;
          return Vbar;
       } 
             
    public int[] getBinomial(int [] n, double [] p) {
    int [] Z = new int [Data.iCounter];
     for (int i = 0; i <Data.iCounter; i++){
        for(int j = 0; j < n[i]; j++) {
           if(Math.random() < (1-p[i]))
            Z[i]++;
        }         
     }       
     return Z;
   }        
              

//     public ArrayList<int[]> ActionGenerator(int[] State, double Budget, int Location, int[] Action) {
//        ArrayList<int[]> Ans = new ArrayList<>();
//        if (State.length - 1 == Location) {
//            for (int i = 0; i <= State[Location]; i++) {
//                Action[Location] = i;
//                if (isBudgetViolated(Action, Budget)) {
//                    Ans.add(Action.clone());
//                }
//            }
//        } else {
//            for (int i = 0; i <= State[Location]; i++) {
//                Action[Location] = i;
//                Ans.addAll(ActionGenerator(State, Budget, Location + 1, Action));
//            }
//        }
//        return Ans;
//    }
//
//    public ArrayList<int[]> ActionGenerator(int[] State, double Budget) {
//        return ActionGenerator(State, Budget, 0, new int[State.length]);
//    }
//
//    public  boolean isBudgetViolated(int[] Action, double Budget) {
//        boolean sum = false; 
//       
//        double [] K = new double [Data.jCounter];
//        for (int j = 0; j < Data.jCounter; j++){
//           for  (int i = 0; i < Data.iCounter; i++){
//               K[j] += Data.u[j][i] * Action[i];
//           }   
//           if (K[j] <= Data.B[j]){
//               sum = true;               
//           }else{
//               sum= false; 
//               break;
//           }
//        }
//        return sum; 
//
//    }     
   
}
    
