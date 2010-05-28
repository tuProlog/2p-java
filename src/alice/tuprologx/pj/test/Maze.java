/*
 * JavaProlog.java
 *
 * Created on March 8, 2007, 5:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */

@PrologClass (
    clauses={"porta(a,b).",
             "porta(a,d).",
             "porta(b,e).",
             "porta(d,g).",
             "porta(g,h).",
             "porta(e,f).",
             "porta(f,i).",
             "porta(e,h)."}
)

public abstract class Maze {         
        @PrologMethod (            
            predicate="strada(?From,?To,-!Path)",    
            signature="(From,To)->{Path}",            
            types={"Atom","Atom","List<Atom>"},    
            clauses = {"da(X,Y):-porta(X,Y).",
                       "da(X,Y):-porta(Y,X).",
                       "strada(X,Y,Str):-percorso(X,Y,Str,[X]).",
                       "percorso(X,X,Str,Str).",
                       "percorso(X,Y,Str,Acc):-da(X,Z),not member(Z,Acc),append(Acc,[Z],R),percorso(Z,Y,Str,R)."}
            /*clauses = {"path(X,X,[X]).",
                       "path(X,Y,P) :-porta(X,Z),path(Z,Y,Q),append([X],Q,P)."}*/)                   
    	public abstract Iterable<List<Atom>> strada(Term<Atom> from, Term<Atom> to);
        
        
	public static void main(String[] s) throws Exception{
		Maze maze = PJ.newInstance(Maze.class);  
                for (int i=0;i<1;i++) {
                for (List<Atom> solution : maze.strada(new Atom("a"), new Var<Atom>("X"))) {
                    System.out.println(solution.toJava());                    
                }                       
            }
	}	
}

