/*
 * To change this template, choose Tools | Templates
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
public abstract class PairTest {
    @TRACE
@PrologMethod(
   clauses={"swap(pair(X,Y), pair(Y,X))."})
public abstract <$X extends JavaTerm<Pair>, $Y extends JavaTerm<Pair>> $Y swap($X x);

public static void main(String[] args) throws Exception {
    Pair p = new Pair(
                  new Int(4),
                  new Atom("Hello!"));
    JavaTerm<Pair> vp = new JavaTerm<Pair>(p);
    PairTest pt = PJ.newInstance(PairTest.class);
    System.out.println(pt.swap(vp).toJava());
}
}
