/*
 * ExprParser.java
 *
 * Created on July 19, 2007, 4:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;

import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;
import alice.tuprologx.pj.test.expr.*;

/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class MathlExprParser {    
  @TRACE
  @PrologMethod (clauses={"parse_expr(E,L):-phrase(expr(E),L).",
                          "expr(E) --> term(T), expr2(T,E).",
                          //"expr2(T,E) --> ['+'],term(T2),expr2(plus(T,T2),E).",
                          "expr2(T,E) --> ['+'], term(T2), {new_object('alice.tuprologx.pj.test.expr.Plus', [T, T2], SUM)}, expr2(SUM, E).",
                          //"expr2(T,E) --> ['+'],term(T2),expr2(P,E).",
                          //"expr2(T,E) --> ['-'],term(T2),expr2(minus(T,T2),E).",
                          "expr2(T,E) --> ['-'],term(T2),{new_object('alice.tuprologx.pj.test.expr.Minus', [T, T2], DIFF)}, expr2(DIFF, E).",
                          "expr2(T,T) --> [].",
                          "term(T) --> fact(F), term2(F,T).",
                          //"term2(F,T) --> ['*'],fact(F2),term2(times(F,F2),T).",
                          "term2(F,T) --> ['*'],fact(F2),{new_object('alice.tuprologx.pj.test.expr.Multiply', [F, F2], MUL)}, term2(MUL,T).",
                          //"term2(F,T) --> ['/'],fact(F2),term2(div(F,F2),T).",
                          "term2(F,T) --> ['/'],fact(F2), {new_object('alice.tuprologx.pj.test.expr.Div', [F, F2], DIV)}, term2(DIV,T).",
                          "term2(F,F) --> [].",
                          "fact(E) --> ['('],expr(E),[')'].",
                          "fact(X) --> [X],{number(X)}."})
  public abstract <$L extends JavaObject<? extends IExpr>, $E extends List<?>> $L parse_expr($E expr);

  @TRACE
  //@PrologMethod (clauses={"somma(T,E, SUM):-new_object('java.lang.Integer', [T], I1), new_object('java.lang.Integer', [E], I2), new_object('alice.tuprologx.pj.test.expr.Plus', [I1,I2], SUM)."})
  @PrologMethod (clauses={"somma(T,E, SUM):-new_object('alice.tuprologx.pj.test.expr.Plus', [T,E], SUM)."})
  public abstract <$T extends Term<?>, $E extends Term<?>, $SUM extends JavaObject<?>> $SUM somma($T t1, $E t2);

  public static void main(String[] args) throws Exception {
    MathlExprParser ep = PJ.newInstance(MathlExprParser.class);    
    java.util.List<Object> s1 = java.util.Arrays.asList(new Object[] {1,"+",2, "-", 3, "*", 5, "+", "(", 5, "/", 2, ")"});
    IExpr expr = ep.parse_expr(new List(s1)).toJava(); //prints: Compound:'plus'(Int(1),Int(2))
    System.out.println(expr);
    System.out.println(expr.eval());
    //System.out.println(ep.somma(new Int(1), new Int(2)).toJava());
  }  
}



