/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.pjlibrary;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;


/**
 * Nota: 'alice.tuprologx.pj.test.pjlibrary.TestIterable' e' un'altra classe Prolog (astratta)!
 * @author maurizio
 */
@PrologClass
public abstract class TestNewPJClass {

    @TRACE
    @PrologMethod (
        clauses={"test(X):-new_object('alice.tuprologx.pj.test.pjlibrary.TestIterable', [], Z), Z <- elements([1,2,3]) returns X."})
    public abstract <$X extends Int> Iterable<$X> test();

    public static void main(String[] args) throws Exception {
        TestNewPJClass jl = PJ.newInstance(TestNewPJClass.class);
        for (Term<?> t : jl.test()) {
            System.out.println(t);
        }
    }
}
