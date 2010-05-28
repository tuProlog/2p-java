/*
 * ExprParserToken.java
 *
 * Created on July 24, 2007, 5:13 PM
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
@PrologClass(clauses={"any([X|Xs],X,Xs).",
                     "any([X|Xs],E,[X|Ys]):-any(Xs,E,Ys)."})
public abstract class ExprParserToken {    
  
  @PrologMethod (clauses={"expr(L,R):-term(L,R).",
                          "expr(L,R):-term(L,['+'|R2]), expr(R2,R).",
                          "expr(L,R):-term(L,['-'|R2]), expr(R2,R)."})                          
  public abstract <$E extends List<?>, $R extends List<?>> Boolean expr($E expr, $R rest);                          
  
  
  @PrologMethod (clauses={"term(L,R):-fact(L,R).",
                          "term(L,R):-fact(L,['*'|R2]), term(R2,R).",
                          "term(L,R):-fact(L,['/'|R2]), term(R2,R)."})
  public abstract <$T extends List<?>, $R extends List<?>> Boolean term($T term, $R rest);                            
  
  
  @PrologMethod (clauses={"fact(L,R):-num(L,R).",
                          "fact(['(' | E],R):-expr(E,[')'|R])."})
  public abstract <$F extends List<?>, $R extends List<?>> Boolean fact($F fact, $R rest);                          
  
  
  @PrologMethod (clauses={"num([L|R],R):-num_atom(_,L)."})                          
  public abstract <$N extends List<?>, $R extends List<?>> Boolean num($N num, $R rest);                            
  
  @TRACE
  @PrologMethod (clauses={"scanOp([H|T],[H],T):-any(['(',')','+','-','*','/'],H,_),!.",
                          "scanOp(L,[],L).",
                          "scanNum([H|T],L,R):-num_atom(_,H),!,scanNum(T,L2,R),append([H],L2,L).",
                          "scanNum(L,[],L).",
                          "scanOne(L,[H|T],R):- scanOp(L,[H|T],R); scanNum(L,[H|T],R).",
                          "scan([],[]).",
                          "scan(L,[Token|T2]):- scanOne(L,List,L2),atom_chars(Token,List),scan(L2,T2)."})
  public abstract <$L extends List<Atom>, $X extends List<?>> $X scan($L s);                            
  

  public static void main(String[] args) throws Exception {
    ExprParserToken ep = PJ.newInstance(ExprParserToken.class);        
    System.out.println(ep.expr(ep.scan(new Atom("12*(5+3)").toCharList()),List.NIL));    
  }  
}
