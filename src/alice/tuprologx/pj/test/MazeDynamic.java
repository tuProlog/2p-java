/*
 * MazeDynamic.java
 *
 * Created on April 3, 2007, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.engine.*;
/**
 *
 * @author maurizio
 */
@PrologClass   
public abstract class MazeDynamic {       
    
    
        
    
    @PrologMethod (            
            predicate="strada(+From,+To,-Path)",    
            signature="(From,To)->{Path}",            
            clauses = {"da(X,Y):-porta(X,Y).",
                       "da(X,Y):-porta(Y,X).",
                       "strada(X,Y,Str):-percorso(X,Y,Str,[X]).",
                       "percorso(X,X,Str,Str).",
                       "percorso(X,Y,Str,Acc):-da(X,Z),not member(Z,Acc),append(Acc,[Z],R),percorso(Z,Y,Str,R)."}
    )                   
    public abstract Iterable<List<Atom>> strada(Term<Atom> from, Term<Atom> to);
        
    @PrologMethod(
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
    
    public static void main(String[] s) throws Exception{
       String topology = "porta(b,a).\n"+
                         "porta(a,d).\n"+
                         "porta(e,b).\n"+
                         "porta(g,h).\n"+                                         
                         "porta(e,f).\n"+
                         "porta(f,i).";
        MazeDynamic maze = PJ.newInstance(MazeDynamic.class,new Theory(topology));
        maze.addDoor(new Atom("e"),new Atom("h"));
        for (List<Atom> solution : maze.strada(new Atom("e"), new Var<Atom>("X"))) {
            System.out.println(solution.toJava());            
        }    
        System.out.println("*******************************************");            
        maze.addDoor(new Atom("b"), new Atom("z"));
        maze.removeDoor(new Atom("e"), new Atom("f"));        
        for (List<Atom> solution : maze.strada(new Atom("e"), new Var<Atom>("X"))) {
            System.out.println(solution.toJava());            
        }
        System.out.println("*******************************************");
        maze.removeDoors(new Atom("b"));
        for (List<Atom> solution : maze.strada(new Atom("e"), new Var<Atom>("X"))) {
            System.out.println(solution.toJava());
        }
    }    
}
