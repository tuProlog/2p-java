/*
 * Graph.java
 *
 * Created on October 24, 2007, 11:32 AM
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
    clauses={"arc(a,b).",
             "arc(a,d).",
             "arc(b,e).",
             "arc(d,g).",
             "arc(g,h).",
             "arc(e,f).",
             "arc(f,i).",
             "arc(e,h)."})
public abstract class Graph {         
    @PrologMethod (
       clauses = {"path(X,X,[X]).",
                  "path(X,Y,[X,Q]) :-arc(X,Z),path(Z,Y,Q)."})                   
    public abstract <$X,$Y,$P> Iterable<$P> path($X from, $Y to);        

    public static void main(String[] s) throws Exception {
        Graph graph = PJ.newInstance(Graph.class);                  
        System.out.println(graph.path(new Atom("a"), new Atom("x")));        
        for (Object solution : graph.path(new Atom("a"), new Var<Atom>("X"))) {
            System.out.println(solution);                    
        }            
    }	
}

