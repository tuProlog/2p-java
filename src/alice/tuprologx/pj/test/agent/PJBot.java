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
public abstract class PJBot {

    @PrologField(init="a")
    public Atom start;

    

@PrologMethod (
clauses={
   "traverse_graph(N,N).",
   "traverse_graph(N,_):-reached(N),!,fail.",
   "traverse_graph(F,X):-assert(reached(F)),path(F,T), traverse_graph(T,X).",
   "reachable(X) :-this(Z), Z.start <- get(From), traverse_graph(From,X)."}
    )


    public abstract <$X extends Atom> Iterable<$X> reachable();

    
    @PrologMethod (
            clauses = {"add_path(X,Y):-add_rule(path(X,Y))."}
    )
    public abstract <$X extends Atom, $Y extends Atom> Boolean add_path($X a, $Y b);

    
    @PrologMethod (
            clauses = {"add_visited(X):-add_rule(old(X))."}
    )
    public abstract <$X extends Atom> Boolean add_visited($X a);

    @PrologMethod (
            clauses = {"is_visited(X):-old(X)."}
    )
    public abstract <$X extends Atom> Boolean is_visited($X a);

    public Maze maze;

    

    boolean explore(String dest) {
        if (dest != null) {
            System.out.println("[Moving to: " + dest + "]");
            maze.moveTo(dest);
            add_visited(new Atom(dest));
        }
        if (maze.is_exit().toJava())
            return true;
        for (Atom a : maze.reachable_sites()) {
            System.out.println("[Alternative: " + a + "]");
            add_path(maze.currentSite, a);
        }
        
            for (Atom a : reachable()) {
                //System.out.println("[External Node: " + a);
                if (!is_visited(a) && explore(a.<String>toJava()))
                    return true;
            }
        
        return false;
    }

    public static void main(String[] args) throws Exception {
        String topology = "porta(a,g).\n" +
                          "porta(b,a).\n"+
                         "porta(a,d).\n"+
                         "porta(e,b).\n"+
                         "porta(g,h).\n"+
                         "porta(e,f).\n"+
                         "porta(f,i).";
        Maze m = PJ.newInstance(Maze.class,new Theory(topology));
        PJBot b = PJ.newInstance(PJBot.class, new Theory(""));
        b.maze = m;
        b.add_visited(new Atom("a"));
        
       System.out.println(b.explore(null));
    }
}
