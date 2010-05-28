/*
 * MazePlain.java
 *
 * Created on May 4, 2007, 3:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;

import alice.tuprolog.*;

/**
 *
 * @author maurizio
 */
public class MazePlain {
    
    static final String theory = "porta(a,b).\n"+
                          "porta(a,d).\n"+
                          "porta(b,e).\n"+
                          "porta(d,g).\n"+
                          "porta(g,h).\n"+
                          "porta(e,f).\n"+
                          "porta(f,i).\n"+
                          "porta(e,h).\n"+
                          "da(X,Y):-porta(X,Y).\n"+
                          "da(X,Y):-porta(Y,X).\n"+
                          "strada(X,Y,Str):-percorso(X,Y,Str,[X]).\n"+
                          "percorso(X,X,Str,Str).\n"+
                          "percorso(X,Y,Str,Acc):-da(X,Z),not member(Z,Acc),append(Acc,[Z],R),percorso(Z,Y,Str,R).\n";

    
    /** Creates a new instance of MazePlain */
    public static void main (String args[]) throws Exception {
        
        Term[] vars = new Term[] {new Struct("a"),new Var("X"),new Var("P")};        
        Struct goal = new Struct("strada",vars);        
        Prolog p = new Prolog();
        for (int i=0;i<10;i++) {
            p.setTheory(new Theory(theory));
            SolveInfo s = p.solve(goal);
            while (s.hasOpenAlternatives()) {
                Struct path = (Struct)s.getTerm("P");
                java.util.Iterator<?> li = path.listIterator();
                java.util.Collection<String> res = new java.util.Vector<String>();
                while (li.hasNext()) {
                    res.add(((Struct)li.next()).getName());
                }            
                System.out.println(res);
                s = p.solveNext();
            }        
        }
    }
}
