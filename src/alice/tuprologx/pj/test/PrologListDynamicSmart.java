/*
 * PrologListDynamicSmart.java
 *
 * Created on May 3, 2007, 4:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

@PrologClass (
    clauses={"getThis(X):-thisList(X).",
             "setThis(V):-getThis(Z), retract(thisList(Z)), assert(thisList(V))."}
)
public abstract class PrologListDynamicSmart {        
        @PrologField(
            //template={"thisList(X):-ground(X),islist(X)."}
        )
        public Theory t;
        
        @PrologMethod (                                    
            clauses = {"append(E,X):-getThis(L),listAppend1(L,E,X),setThis(X).",
                       "listAppend1(L,E,X):-append(L,[E],X)."}
        )
        abstract public <$X,$Y> $Y append($X element);        
        
        @PrologMethod (                                                
            clauses = {"prepend(E,X):-getThis(L),listPrepend1(L,E,X),setThis(X).",
                       "listPrepend1(L,E,[E|L])."}
        )        
        abstract public <$X,$Y> $Y prepend($X element);
                
        @PrologMethod (                                    
            clauses = {"contains(E,R):-getThis(L), listContains1(L,E,R).",
                       "listContains1([],E,false).",
                       "listContains1([E|_],E,true).", 
                       "listContains1([_|T],E,X):-listContains1(T,E,X)."}
        )                
        abstract public <$X,$Y> $Y contains($X element);
        
        @PrologMethod (                        
            clauses = {"sizeOf(S):-getThis(L),listSize1(L,S).",
                       "listSize1([],0).",
                       "listSize1([_|T],X):-listSize1(T,Z),X is 1+Z."}
        )                
        abstract public <$X> $X sizeOf();
        
        @PrologMethod 
        abstract public <$X> $X thisList();        
                
	public static void main(String[] s) throws Exception{
                Theory init = new Theory("thisList(['Taylor','Adam']).");
		PrologListDynamicSmart list = PJ.newInstance(PrologListDynamicSmart.class,init);                
                Atom mike = new Atom("Mike");                
                Atom john = new Atom("John");                
                Atom michael = new Atom("Michael");
                list.append(mike);                
                list.t=new Theory("thisList([]).");
                list.prepend(john);                
                System.out.println(list.thisList());
                System.out.println(list.contains(mike));                
                System.out.println(list.contains(john));
                System.out.println(list.contains(michael));
                System.out.println(list.sizeOf());                
                //questo stampa la clausola associata al metodo Prolog #1
                System.out.println(((PrologObject)list).getMetaPrologClass().getPrologMethods()[1].getClauses()[0]);               
               
	}	
}