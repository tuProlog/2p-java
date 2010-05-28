/*
 * PrologListSmart.java
 *
 * Created on May 10, 2007, 10:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package alice.tuprologx.pj.test;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

@PrologClass
public abstract class PrologListSmart {    
        @PrologMethod (                                    
            clauses = {"nil([])."}            
        )             
    	abstract public <$X> $X nil();
        
        @TRACE
        @PrologMethod (                                    
            clauses = {"appendElement(L,E,X):-append(L,[E],X)."}
        )        
        abstract public <$X,$Y,$Z> $Z appendElement($X list, $Y element);
        
        @PrologMethod (                        
            clauses = {"prependElement(L,E,[E|L])."}
        )        
        abstract public <$X,$Y,$Z> $Z prependElement($X list, $Y element);
        
        @PrologMethod (                        
            clauses = {"containsElement([],E,false).",
                       "containsElement([E|_],E,true).", 
                       "containsElement([_|T],E,X):-containsElement(T,E,X)."}
        )                
        abstract public <$X,$Y,$Z extends Term<?>> $Z containsElement($X list, $Y element);
        
        @PrologMethod (                        
            clauses = {"sizeOf([],0).",
                       "sizeOf([_|T],X):-sizeOf(T,Z),X is 1+Z."}
        )                
        abstract public <$X,$Y extends Term<?>> $Y sizeOf($X list);       
        
        @PrologMethod (
      clauses = {"concat([],L,L).",
                 "concat([H|T],L,[H|E]):-concat(T,L,E)."}
   )
   abstract public <$X extends Term<? extends List<?>>,
                    $Y extends Term<? extends List<?>>,
                    $Z extends Term<? extends List<?>>> 
                Iterable<Compound2<$X,$Y>> concat($X l1, $Y l2, $Z l_out);

                
	public static void main(String[] s) throws Exception{
		PrologListSmart lists = PJ.newInstance(PrologListSmart.class);
                List<Atom> names = lists.nil();           
                Atom mike = new Atom("Mike");
                Atom john = new Atom("John");
                Atom bob = new Atom("Bob");
                names = lists.appendElement(names,mike);
                names = lists.prependElement(names,john);
                System.out.println("Resulting list is: "+names.toJava());
                System.out.println("Does the list hold Mike: "+lists.containsElement(names,mike).toJava());
                System.out.println("Does the list hold John: "+lists.containsElement(names,john).toJava());
                System.out.println("Does the list hold Bob: "+lists.containsElement(names,bob).toJava());
                System.out.println("The size of the list is: "+lists.sizeOf(names).toJava());                                              
                Var<List<Int>> l1 = new Var<List<Int>>("X");
                Var<List<Int>> l2 = new Var<List<Int>>("Y");
                java.util.Collection<Integer> ci = java.util.Arrays.asList(new Integer[] {1,2,3,4,5});
                List<Int> l = new List<Int>(ci);
                for (Compound2<Var<List<Int>>,Var<List<Int>>> c: lists.concat(l1,l2,l)){
                    System.out.println("First list is: "+c.get0());
                    System.out.println("Second list is: "+c.get1());
           // Results: ([],[1,2,3,4,5]),([1],[2,3,4,5]),..,([1,2,3,4],[5]),([1,2,3,4,5],[])
                }
	}	
}
