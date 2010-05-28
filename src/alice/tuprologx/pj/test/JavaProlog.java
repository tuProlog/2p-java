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
@PrologClass (clauses = {"remove([X|Xs],X,Xs).",
			 "remove([X|Xs],E,[X|Ys]):-remove(Xs,E,Ys).",
			 "permutation([],[]).",
			 "permutation(Xs, [X | Ys]) :- remove(Xs,X,Zs), permutation(Zs, Ys)."})
public abstract class JavaProlog {           
        /*
        @PrologMethod (        
            predicate="remove(@A,@B,-!C)",
            types={"List<Int>","Int","List<Int>"},    
            signature="(A,B)->(C)")           
    	public abstract List<Int> remove(List<Int> c1, Int i);
        */            
        @PrologMethod(
            predicate="remove(@A,@B,-!C)",
            types={"List<Int>","Int","List<Int>"}
        )
    	public abstract <A extends List<Int>,B extends Int, C extends List<Int>> C remove(A c1, B i);
        
         @PrologMethod (        
            predicate="permutation(@X,?!Y)",
            types={"List<Int>","List<Int>"},    
            signature="(X,Y)->{X,Y}",
            keepSubstitutions=true)             
	public abstract Iterable<Compound2<List<Int>,Term<List<Int>>>> permutations(List<Int> c1,Term<List<Int>> c2);            
        
        @PrologMethod (        
            predicate="permutation(?X,-!Y)",
            types={"List<Int>","List<Int>"},    
            signature="(X,Y)->{Y}")                       
	public abstract Iterable<List<Int>> permutations2(Term<? extends List<? extends Term<Int>>> c1,Var<List<Int>> c2);        
        
        @PrologMethod (
            predicate="permutation(+X,-!Y)",
            types={"List<Int>","List<Int>"},    
            signature="(X)->{Y}")                       
	public abstract Iterable<List<Int>> permutations3(List<? extends Term<Int>> c1);
                  
        
        @PrologMethod (
            predicate="permutation(?X,?!Y)",
            types={"List<Int>","List<Int>"},    
            signature="(X,Y)->{}",
            exceptionOnFailure=false) 
	public abstract Iterable<Boolean> permutations4(Term<? extends List<? extends Term<Int>>> c1, Term<List<Int>> c2) throws NoSolutionException;
        
        @PrologMethod (
            predicate="permutation(?X,-!Y)",
            types={"List<?>","List<?>"},    
            signature="(X)->{Y}")                       
	public abstract Iterable<List<?>> permutations5(Term<? extends List<? extends Term<?>>> c1) throws NoSolutionException;
                
        @PrologMethod (
            predicate="permutation(@X,-!Y)",
            types={"List<JavaObject<alice.tuprologx.pj.test.Counter>>","List<JavaObject<alice.tuprologx.pj.test.Counter>>"},    
            signature="(X)->{Y}")                       
	public abstract Iterable<List<JavaObject<Counter>>> permutations6(List<JavaObject<Counter>> c1);
        
        @TRACE
        @PrologMethod (            
            clauses={"init_counter(X):-X <- setValue(42)."})
	public abstract <$Counter extends JavaObject<Counter>> $Counter init_counter($Counter c);
        
	public static void main(String[] s) throws Exception{
		JavaProlog pu = PJ.newInstance(JavaProlog.class);				
		java.util.Collection<Integer> l=new java.util.LinkedList<Integer>();
        	l.add(1);
        	l.add(2);
       		l.add(3);
                
		Var<List<Int>> c = new Var<List<Int>>("X");
       		//perform some operations on the results
		//for (List<Int> li : pu.permutations(new List<Int>(l),c)) { OR
                
                for (Compound2<List<Int>,Term<List<Int>>> li : pu.permutations(Term.<List<Int>>fromJava(l),c)) {
        	  System.out.println(li);          		
                  java.util.Collection<Integer> ci = (java.util.Collection<Integer>)li.get1().toJava();
                  System.out.println(ci);          		
                }                
                
                for (List<Int> li : pu.permutations2(Term.<List<Int>>fromJava(l),c)) {
        	  System.out.println(li);          		
                  java.util.Collection<Integer> ci = li.toJava();
                  System.out.println(ci);          		
                }
                
                for (List<Int> li : pu.permutations3(Term.<List<Int>>fromJava(l))) {
        	  System.out.println(li);          		
                  java.util.Collection<Integer> ci = li.toJava();
                  System.out.println(ci);      
                  List<Int> l2 = pu.remove(li,new Int(1));
                  System.out.println(l2.toJava());
                }
                
                for (Boolean b : pu.permutations4(Term.<List<Int>>fromJava(l),c)) {
        	  System.out.println(b);          		
                  Boolean jb = b;
                  System.out.println(jb);          		
                } 
        
                
                /*
                java.util.Collection<?> co1 = (java.util.Collection<?>)l;
                java.util.Collection<Object> co = (java.util.Collection<Object>)co1;
                co.add(new Counter());
                for (List<?> b: pu.permutations5(Term.<List<?>>fromJava(co))) {
        	  System.out.println(b);          		
                  Object jb = b.toJava();                  
                }
                 */
                
                
                java.util.Collection<Counter> co1 = new java.util.ArrayList<Counter>();                
                co1.add(new Counter(1));
                co1.add(new Counter(2));
                co1.add(new Counter(3));
                for (List<?> b: pu.permutations6(Term.<List<JavaObject<Counter>>>fromJava(co1))) {
        	  System.out.println(b);          		
                  Object jb = b.toJava();                  
                }
                
                Counter cx = new Counter(); //value is 0;
		assert(cx.getValue() == 0);
                System.out.println(cx);
                pu.init_counter(new JavaObject(cx)); //now value is 42
		assert(cx.getValue() == 42);
                System.out.println(cx);
                
                
	}	
}

