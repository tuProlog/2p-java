/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package alice.tuprologx.pj.test.expr;

import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class ExprParserVal {
  @TRACE
  @PrologMethod (clauses={"parse_expr(E,L):-phrase(expr(E),L).",
                          "expr(E) --> term(T), expr2(T,E).",
                          "expr2(T,E) --> ['+'],term(T2),expr2(plus(T,T2),E).",
                          "expr2(T,E) --> ['-'],term(T2),expr2(minus(T,T2),E).",
                          "expr2(T,T) --> [].",
                          "term(T) --> fact(F), term2(F,T).",
                          "term2(F,T) --> ['*'],fact(F2),term2(times(F,F2),T).",
                          "term2(F,T) --> ['/'],fact(F2),term2(div(F,F2),T).",
                          "term2(F,F) --> [].",
                          "fact(E) --> ['('],expr(E),[')'].",
                          "fact(X) --> [X],{number(X)}."})
  public abstract <$L extends Term<?>, $E extends List<?>> $L parse_expr($E expr);

  @TRACE
  @PrologMethod (clauses={"eval_expr(plus(L,R),X):-eval_expr(L, X1), eval_expr(R, X2), X is X1 + X2.",  
                          "eval_expr(minus(L,R),X):-eval_expr(L, X1), eval_expr(R, X2), X is X1 - X2.",
                          "eval_expr(times(L,R),X):-eval_expr(L, X1), eval_expr(R, X2), X is X1 * X2.",
                          "eval_expr(div(L,R),X):-eval_expr(L, X1), eval_expr(R, X2), X is X1 / X2.",
                          "eval_expr(X,X):-number(X)."})
  public abstract <$E extends Term<?>, $X extends Int> $X eval_expr($E expr);
  
  public static void main(String[] args) throws Exception {
    ExprParserVal ep = PJ.newInstance(ExprParserVal.class);
    java.util.List<Object> s1 = java.util.Arrays.asList(new Object[] {1,"+",2, "-", 3, "*", 5, "+", "(", 5, "/", 2, ")"});
    Term<?> expr = ep.parse_expr(new List(s1));
    System.out.println(ep.eval_expr(expr).toJava());
  }
}
