/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

import alice.tuprolog.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author michael
 */
public class RunEngine {
    private static int n=1;     // iterations
    private static double[] times;

    public static void main(String[] args) throws Exception{

//  THEORY

    //File file=new File("/home/michael/Desktop/theory/th100.pl");
    //File file=new File("/home/michael/Desktop/theory/exam.pl");
    File file=new File("/home/michael/Desktop/theory/tail.pl");
    //File file=new File("/home/michael/Desktop/theory/asserting2.pl");
    Prolog engine=new Prolog();
    engine.setTheory(new Theory(new FileInputStream(file)));

    times=new double[n];

    for (int i=0;i<n;i++){
        StaticStopwatch.start();

//  GOAL
    //SolveInfo info=engine.solve("program(P),engine(P, 10, [val(i1,10),val(i2,10),val(t1,0),val(o1,0)], O).");
    //SolveInfo info=engine.solve("asserter(60).");
    SolveInfo info=engine.solve("tcount(7,B).");
    //SolveInfo info=engine.solve("p(50,X).");
    //SolveInfo info=engine.solve("asserter(1000).");


    while (info.isSuccess()){
       log("solution: "+info.getSolution()+" - bindings: "+info);
        if (engine.hasOpenAlternatives()){
            info=engine.solveNext();
        } else
            break;

        }
    StaticStopwatch.stop();
    times[i]=StaticStopwatch.getTime();
    }

    // Calculating average time
    double s=0;
    for (int i=0;i<n;i++){
        s=s+times[i];
    }

    s=s/n;
    log("\nAverage time: "+s+", "+n+" iteration(s)");

    StaticCounter.printAll();
    

  }

    private static void log(String s){
        System.out.println(s);
    }
    private static void log(long s){
        System.out.println(s);
    }
}
