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
@WithTermifiable({"alice.tuprologx.pj.test.expr.Plus",
                  "alice.tuprologx.pj.test.expr.Minus",
                  "alice.tuprologx.pj.test.expr.Multiply",
                  "alice.tuprologx.pj.test.expr.Div",
                  "alice.tuprologx.pj.test.expr.Num"})
public abstract class ExprParserPrologEval {
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
                          "fact(num(X)) --> [X], {number(X)}."})
  public abstract <$L extends JavaTerm<?>, $E extends List<?>> $L parse_expr($E expr);

  public static void main(String[] args) throws Exception {
    ExprParserPrologEval ep = PJ.newInstance(ExprParserPrologEval.class);
    PrologEvaluator eval = PJ.newInstance(PrologEvaluator.class);
    EvalVisitor v = PJ.newInstance(EvalVisitor.class);
    java.util.List<Object> s1 = java.util.Arrays.asList(new Object[] {1,"+",2, "-", 3, "*", 5, "+", "(", 5, "/", 2, ")"});
    JavaTerm<?> expr = ep.parse_expr(new List(s1));
    System.out.println(expr);
    JavaTerm<?> expr_term = new JavaTerm<Object>(expr.toJava());
    System.out.println(eval.eval(expr_term));
  }
}
