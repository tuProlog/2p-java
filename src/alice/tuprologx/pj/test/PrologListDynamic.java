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
public abstract class PrologListDynamic {        
        
        @PrologField (init="[]", predicate="l")
        public List<?> list;
        
        
        @PrologMethod (                        
            predicate="listAppend(+E,-L)",   
            signature="(E)->(L)",    
            clauses = {"listAppend(E,X):-l(L),listAppend1(L,E,X),l := X.",
                       "listAppend1(L,E,X):-append(L,[E],X)."}
        )
                @TRACE
        abstract public <X extends Term<?>> List<X> append(X element);        
        
        @PrologMethod (                        
            predicate="listPrepend(@E,-L)",   
            signature="(E)->(L)",    
            clauses = {"listPrepend(E,X):-l(L),listPrepend1(L,E,X), l := X.",
                       "listPrepend1(L,E,[E|L])."}
        )        
        abstract public <X  extends Term<?>> List<X> prepend(X element);
                
        @PrologMethod (                        
            predicate="listContains(@E,-R)",   
            signature="(E)->(R)",
            clauses = {"listContains(E,R):-l(L), listContains1(L,E,R).",
                       "listContains1([],E,false).",
                       "listContains1([E|_],E,true).", 
                       "listContains1([_|T],E,X):-listContains1(T,E,X)."}
        )                
        abstract public <X extends Term<?>> Bool contains(X element);
        
        @PrologMethod (                        
            predicate="listSize(S)",   
            signature="()->(S)",    
            clauses = {"listSize(S):-l(L),listSize1(L,S).",
                       "listSize1([],0).",
                       "listSize1([_|T],X):-listSize1(T,Z),X is 1+Z."}
        )                
        abstract public <X extends Term<?>> Int sizeOf();
        
        @PrologMethod (                        
            predicate="l(-L)",
            signature="()->(L)"            
        )                
        abstract public <X extends Term<?>> List<X> getList();
                
	public static void main(String[] s) throws Exception{                
        PrologListDynamic list = PJ.newInstance(PrologListDynamic.class);                
        Atom mike = new Atom("Mike");                
        Atom john = new Atom("John");                
        Atom michael = new Atom("Michael");
        list.append(mike);                
        list.prepend(john);                
        System.out.println(list.getList());
        System.out.println(list.contains(mike));                
        System.out.println(list.contains(john));
        System.out.println(list.contains(michael));
        System.out.println(list.sizeOf());                
	}	
}