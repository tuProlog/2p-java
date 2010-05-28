/*
 * AnotherList.java
 *
 * Created on May 18, 2007, 8:47 AM
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
@PrologClass 
public abstract class AnotherList {
   @PrologMethod (
      clauses = {"nil([])."}
   )
   abstract public <X> X nil();

   @PrologMethod (
      clauses = {"add(E,L,[E|L])."}
   )
   abstract public <X extends Term<?>,Y extends List<X>,Z extends List<X>> Z add(X element, Y list);

   @PrologMethod (
      clauses = {"contains([E|_],E).",
                 "contains([_|T],E):-contains(T,E)."}
   )
   abstract public <X extends List<?>,Y> boolean contains(X list, Y element);

   @PrologMethod (
      clauses = {"concat([],L,L).",
                 "concat([E|T1], L, [E|T2]) :- concat(T1, L, T2)."}
   )
   abstract public <X extends Term<? extends List<?>>,Y extends Term<? extends List<?>>,Z extends Term<? extends List<?>>>
                Iterable<Compound3<X,Y,Z>> concat(X l1, Y l2, Z l_out);

   public static void main(String[] s) throws Exception{
      AnotherList lists = PJ.newInstance(AnotherList.class);
      List<Int> l=lists.nil();          // Creating an empty list []
      for (int i=10;i>0;i--){
          l=lists.add(new Int(i),l);    // Adding elements...[1,2,3,4,5,6,7,8,9,10]
      }
      Var<List<Int>> l1 = new Var<List<Int>>("X");
      Var<List<Int>> l2 = new Var<List<Int>>("Y");
      // Executing goal concat(X,Y,[1,2,3,4,5,6,7,8,9,10]).
      for (Compound3<Var<List<Int>>,Var<List<Int>>,List<Int>> c: lists.concat(l1,l2,l)){
           System.out.println("First list is: "+c.getHead().toJava());
           System.out.println("Second list is: "+c.getRest().getHead().toJava());
           // Results: ([],[1,2,3,4,5,6,7,8,9,10]),([1],[2,3,4,5,6,7,8,9,10]),..
           // ..([1,2,3,4,5,6,7,8,9],[10]),([1,2,3,4,5,6,7,8,9,10],[])
      }
    }
}
