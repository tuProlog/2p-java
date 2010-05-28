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
interface PrologVisitor {
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_plus($E expr);
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_minus($E expr);
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_times($E expr);
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_div($E expr);
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_number($E expr);

    
    @PrologMethod (clauses={"visit(plus(L,R),X):-this(Z), Z <- visit_plus(plus(L,R)) returns X.",
                          "visit(minus(L,R),X):-this(Z), Z <- visit_minus(minus(L,R)) returns X.",
                          "visit(times(L,R),X):-this(Z), Z <- visit_times(times(L,R)) returns X.",
                          "visit(div(L,R),X):-this(Z), Z <- visit_div(div(L,R)) returns X.",
                          "visit(N,X):-number(N), this(Z), Z <- visit_number(N) returns X."})
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit($E expr);
}


@PrologClass
public abstract class EvalVisitor implements PrologVisitor {
    
    //@Override
    @PrologMethod (clauses={"visit_plus(plus(L,R),X):-this(V), V <- visit(L) returns X1, V <- visit(R) returns X2, X is X1 + X2."})
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_plus($E expr);

    
    //@Override
    @PrologMethod (clauses={"visit_minus(minus(L,R),X):-this(V), V <- visit(L) returns X1, V <- visit(R) returns X2, X is X1 - X2."})
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_minus($E expr);

    
    //@Override
    @PrologMethod (clauses={"visit_times(times(L,R),X):-this(V), V <- visit(L) returns X1, V <- visit(R) returns X2, X is X1 * X2."})
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_times($E expr);

    
    //@Override
    @PrologMethod (clauses={"visit_div(div(L,R),X):-this(V), V <- visit(L) returns X1, V <- visit(R) returns X2, X is X1 / X2."})
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_div($E expr);

    
    //@Override
    @PrologMethod (clauses={"visit_number(N, N)."})
    public abstract <$E extends Term<?>, $X extends Term<?>> $X visit_number($E expr);
}
