/*
 * MazeDynamic.java
 *
 * Created on April 3, 2007, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.agent;

import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.engine.*;
/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class Maze {

    @PrologField(init="a")
    public Atom currentSite;

    @PrologField(init="i")
    public Atom exit;

    @PrologMethod (
            clauses = {"da(X,Y):-porta(X,Y).",
                       "da(X,Y):-porta(Y,X).",
                       "reachable_sites(X):-this(Z), Z.currentSite <- get(C), da(C, X)."}
    )
    public abstract <$X extends Atom> Iterable<$X> reachable_sites();

    
    @PrologMethod (
            clauses = {"is_exit(true):-this(Z), Z.exit <- get(E), Z.currentSite <- get(E), !.",
                       "is_exit(false)."}
    )
    public abstract <$X extends Bool> $X is_exit();

    /*@PrologMethod(
        clauses = {"addDoor(X,Y):-add_rule(porta(X,Y))."}
    )
    public abstract <$X extends Atom,$Y extends Atom> Boolean addDoor($X x,$Y y);

    @PrologMethod(
        clauses = {"removeDoor(X,Y):-remove_rule(porta(X,Y))."}
    )
    public abstract <$X extends Atom,$Y extends Atom> Boolean removeDoor($X x,$Y y);

    @PrologMethod(
        clauses = {"removeDoors(Z):-remove_rules(porta(Z,_))."}
    )
    public abstract <$Z extends Atom> Boolean removeDoors($Z z);
    */

    public void moveTo(String name) {
        currentSite = new Atom(name);
    }

    public static void main(String[] s) throws Exception{
       String topology = "porta(a,g).\n" +
                          "porta(b,a).\n"+
                         "porta(a,d).\n"+
                         "porta(e,b).\n"+
                         "porta(g,h).\n"+
                         "porta(e,f).\n"+
                         "porta(f,i).";
        Maze maze = PJ.newInstance(Maze.class,new Theory(topology));
        //maze.moveTo("e");
        System.out.println(maze.is_exit());
        for (Atom solution : maze.reachable_sites()) {
            System.out.println(solution.toJava());
        }
        maze.moveTo("i");
        System.out.println(maze.is_exit());
    }
}
