/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.expr;

import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;

/**
 *
 * @author maurizio
 */
@PrologClass
abstract class PrologEvaluator {
    @TRACE
    @PrologMethod (clauses={"eval(plus(L,R),X):-eval(L, L1), eval(R, R1), X is L1 + R1.",
    "eval(minus(L,R),X):-eval(L, L1), eval(R, R1), X is L1 - R1.",
    "eval(times(L,R),X):-eval(L, L1), eval(R, R1), X is L1 * R1.",
    "eval(div(L,R),X):-eval(L, L1), eval(R, R1), X is L1 / R1.",
    "eval(num(X),X)."})
    public abstract <$E extends JavaTerm<?>, $X extends Term<?>> $X eval($E expr);
}