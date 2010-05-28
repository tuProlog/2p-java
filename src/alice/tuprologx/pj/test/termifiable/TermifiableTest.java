/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.termifiable;

import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
@PrologClass (clauses = {"remove([X|Xs],X,Xs).",
                         "remove([X|Xs],E,[X|Ys]):-remove(Xs,E,Ys)."})
@WithTermifiable({"alice.tuprologx.pj.test.termifiable.Counter"})
public abstract class TermifiableTest {
  @TRACE
  @PrologMethod (clauses={"permutations([],[]).",
                          "permutations(Xs,[X|Ys]):-remove(Xs,X,Zs), permutations(Zs,Ys)."})
  public abstract <$X extends List<JavaTerm<Counter>>,
          $Y extends List<JavaTerm<Counter>>> Iterable<$Y> permutations($X list);

  @TRACE
  @PrologMethod (clauses={"inc(counter(X), Y):-Y is X + 1."})
  public abstract <$X extends JavaTerm<Counter>,
          $Y extends Int> $Y inc($X counter);

  public static void main(String[] args) throws Exception{
    TermifiableTest tt = PJ.newInstance(TermifiableTest.class);
    Counter c1 = new Counter(1);
    Counter c2 = new Counter(2);
    Counter c3 = new Counter(3);
    java.util.Collection<Counter> l=java.util.Arrays.<Counter>asList(new Counter[]{c1, c2, c3});
    for (List<JavaTerm<Counter>> p : tt.permutations(Term.<List<JavaTerm<Counter>>>fromJava(List.fromJava(l))))  {
      System.out.println(p.toJava());
    }
    System.out.println(tt.inc(new JavaTerm(c1)));
  }
}
