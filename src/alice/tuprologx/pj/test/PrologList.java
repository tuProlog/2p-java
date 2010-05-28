/*
 * PrologList.java
 *
 * Created on March 30, 2007, 1:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

@PrologClass
public abstract class PrologList {    
        @PrologMethod (                        
            predicate="emptyList(-X)",   
            signature="()->(X)",    
            clauses = {"emptyList([])."}            
        )             
    	abstract public <X extends Term<?>> List<X> nil();
                
        @PrologMethod (                        
            predicate="listAppend(@L,@E,-X)",   
            signature="(L,E)->(X)",    
            clauses = {"listAppend(L,E,X):-append(L,[E],X)."}
        )        
        abstract public <X extends Term<?>> List<X> append(List<X> list, X element);
        
        @PrologMethod (                        
            predicate="listPrepend(@L,@E,-X)",   
            signature="(L,E)->(X)",
            clauses = {"listPrepend(L,E,[E|L])."}
        )        
        abstract public <X extends Term<?>> List<X> prepend(List<X> list, X element);              
        
        
        @PrologMethod (                        
            predicate="listContains(@L,@E)",   
            signature="(L,E)->()",
            clauses = {"listContains([E|_],E).", 
                       "listContains([_|T],E):-listContains(T,E)."},
            types={"List<?>","Term<?>"}
        )                                
        abstract public boolean contains(List<?> list, Term<?> element);
        
        @PrologMethod (                        
            predicate="listSize(@L,-S)",   
            signature="(L)->(S)",
            clauses = {"listSize([],0).",
                       "listSize([_|T],X):-listSize(T,Z),X is 1+Z."}
        )                
        abstract public <X extends Term<?>> Int sizeOf(List<X> list);                
                
	public static void main(String[] s) throws Exception{
		PrologList lists = PJ.newInstance(PrologList.class);
                List<Atom> names = lists.nil();           
                Atom mike = new Atom("Mike");
                Atom john = new Atom("John");
                Atom bob = new Atom("Bob");
                names = lists.append(names,mike);
                names = lists.prepend(names,john);
                System.out.println("Resulting list is: "+names.toJava());
                System.out.println("Does the list hold Mike: "+lists.contains(names,mike));
                System.out.println("Does the list hold John: "+lists.contains(names,john));
                System.out.println("Does the list hold Bob: "+lists.contains(names,bob));
                System.out.println("The size of the list is: "+lists.sizeOf(names).toJava());                              
	}	
}


