/*
 * PrologTest.java
 *
 * Created on April 3, 2007, 5:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;

import alice.tuprolog.*;
import java.util.Vector;

/**
 *
 * @author maurizio
 */
public class PrologTest {
    
    static String maze1 ="p1(X):-p2(X).\n"+
                         "p2(Y):-Y is 2.";
      
    
    public static void main(String args[]) throws Exception {        
        Prolog p = new Prolog();
        Theory t1 = new Theory(maze1);        
        p.setTheory(t1);        
        SolveInfo si = p.solve("p1(X).");        
        while (si.isSuccess() && si.hasOpenAlternatives()) {
            System.out.println(si.getSolution());
            si = p.solveNext();
        }                
    }    
}
