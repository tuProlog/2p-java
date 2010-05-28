package alice.tuprologx.pj.test.pjlibrary;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class TestIterable {    

    @TRACE
    @PrologMethod (
        clauses={"elements([H|_], H).",
                 "elements([_|T], R):-elements(T, R)."})
	public abstract <$L extends List<Int>, $E extends Int> Iterable<$E> elements($L l);

    @TRACE
    @PrologMethod (
        clauses={"test(X):-this(Z), Z <- elements([1,2,3]) returns X."})
    public abstract <$X extends Int> Iterable<$X> test();


    public static void main(String[] args) throws Exception {
        TestIterable jl = PJ.newInstance(TestIterable.class);
        for (Term<?> t : jl.test()) {
            System.out.println(t);
        }
    }
}
