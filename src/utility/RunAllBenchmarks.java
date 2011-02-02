/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

import alice.tuprolog.*;
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author michael
 */
public class RunAllBenchmarks {
    private static double[] times;
    private static String[] benchs={"crypt","qsort", "queens", "query", "tak"};
    public static void main(String[] args) throws Exception{
 
    for (int i=0;i<benchs.length;i++){
        Prolog engine = new Prolog();
        engine.setTheory(new Theory(new FileInputStream(new File("/home/michael/Desktop/theory/"+benchs[i]+".pl"))));
        SolveInfo info=engine.solve("data(A),benchmark(A,B).");

        while (info.isSuccess()){
           log("solution: "+info.getSolution()+" - bindings: "+info);
            if (engine.hasOpenAlternatives()){
                info=engine.solveNext();
            } else
                break;

            }
    }
  }

    private static void log(String s){
        System.out.println(s);
    }
}