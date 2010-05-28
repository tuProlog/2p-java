/*
 * MazePlain2.java
 *
 * Created on May 4, 2007, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;

import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
public class MazePlain2 {
    
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

    static final String theory2 =   "percorso(X,Y,Str,Acc) :- da_p(X,Z,_),(not member(Z,Acc),(percorso(Z,Y,Str,[Z|Acc]))).\n"+
                                    "percorso(X,X,Str,Str).\n"+
                                    "strada(X,Y,Str) :-	percorso(X,Y,Str,[X]).\n"+
                                    "salva(Str,N) :-retract(minimo(A,B)),(not minimo(A,B),(assert(minimo(Str,N)),(!))).\n"+
                                    "porta_p(a,b,2).\n"+
                                    "porta_p(a,d,1).\n"+
                                    "porta_p(b,e,5).\n"+
                                    "porta_p(d,g,1).\n"+
                                    "porta_p(g,h,1).\n"+
                                    "porta_p(e,f,8).\n"+
                                    "porta_p(f,i,9).\n"+
                                    "porta_p(e,h,1).\n"+
                                    "calcola_peso([H|[H1|T]],N,Acc) :-da_p(H,H1,P),	(Acc2 is Acc + P,(calcola_peso([H1|T],N,Acc2))).\n"+
                                    "calcola_peso([_],N,N).\n"+
                                    "da_p(X,Y,W) :-	porta_p(X,Y,W).\n"+            
                                    "da_p(X,Y,W) :-	porta_p(Y,X,W).\n"+
                                    "percorso_min(X,Y) :-	strada(X,Y,Str),(calcola_peso(Str,N,0),	(minimo(Str1,N1),(N < N1,(salva(Str,N),	(fail))))).\n"+
                                    "strada_min_p(X,Y,Z,P) :-assert(minimo('[]',999)),percorso_min(X,Y).\n"+
                                    "strada_min_p(X,Y,Z,P):- minimo(Z,P),retract(minimo(Z,P)),!.";

static final String theory3 =   "remove([X|Xs],X,Xs).\n"+
                                "remove([X|Xs],E,[X|Ys]):-remove(Xs,E,Ys).\n"+
                                "permutation([],[]).\n"+
                                "permutation(Xs, [X | Ys]) :- remove(Xs,X,Zs), permutation(Zs, Ys).";
    
    
    /** Creates a new instance of MazePlain */
    public static void main (String args[]) throws Exception {
        Term[] vars = new Term[] {new Atom("a"),new Var("X"),new Var("P")};//,new Var("Z")};        
        Cons<?,?> goal = Cons.make("strada",vars);
        PJProlog p = new PJProlog();        
        p.setTheory(new Theory(theory));        
        for (int i=0;i<10;i++) {
        for (PrologSolution<?,?> ps : p.solveAll(goal)) {        
            System.out.println(ps.getTerm("P").toJava());            
        }        
        }
       java.util. Collection<Integer> c;
c = java.util.Arrays.<Integer>asList(new Integer[]{1,2,3});
Compound2<List<Int>,Var<List<Int>>> g;
List<Int> l = new List<Int>(c);
Var<List<Int>> v = new Var<List<Int>>("X");
g = Cons.make("permutation",new Term<?>[]{l,v});
p.setTheory(new Theory(theory3));
//setup the PJ engine
for (PrologSolution<?,?> s : p.solveAll(g)) {
   java.util.Collection<Integer> l2 = s.getTerm("X").toJava();
   int max = java.util.Collections.max(l2);
   l2.remove(max);
   System.out.println(l2);
}

    }
}
