/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.agent;

import java.util.*;

import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class PJBot2 {

    public Maze maze;

    @TRACE
    @PrologMethod (

    clauses={
       "explore(X):-this(Z), Z.maze <- get(M), M.'currentSite' <- get(N), explore_1(N, X).",
       "explore_1(N, X):-this(Z), Z.maze <- get(M), M.exit<-get(N), !.",
       "explore_1(N, X):-!, this(Z), Z.maze <- get(M), M <- reachable_sites returns D, not visited(D), M.currentSite <- set(D), Z<-printPosition(D), add_rule(visited(D)), explore_1(D,X)."})
        public abstract <$X extends Atom> $X explore();

    public void printPosition(Object o) {
        System.out.println("[current node="+o+"]");
    }

    @PrologMethod (
    clauses={
        "visited_nodes(X):-visited(X)."}
    )
    public abstract <$X extends Atom> Iterable<$X> visited_nodes();

    public static void main(String[] args) throws Exception {
        String topology = "porta(a,g).\n" +
                          "porta(b,a).\n"+
                          "porta(a,d).\n"+
                          "porta(e,b).\n"+
                          "porta(g,h).\n"+
                          "porta(e,f).\n"+
                          "porta(f,i).";
        Maze m = PJ.newInstance(Maze.class,new Theory(topology));
        PJBot2 b = PJ.newInstance(PJBot2.class, new Theory(""));
        b.maze = m;
       System.out.println(b.explore());
       for (Atom a : b.visited_nodes()) {
           System.out.println("[visited node = " + a.toJava() + " ]");
       }
    }
}
