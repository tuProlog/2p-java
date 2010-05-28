package alice.tuprologx.pj.test;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class JavaLibrary {
    
    public int count = 45;
    
    @TRACE
    @PrologMethod (            
        clauses={"init_counter(X):-this(Z), Z.count <- get(V), X <- setValue(V)."})
	public abstract <$Counter extends JavaObject<Counter>> $Counter init_counter($Counter c);
        
    public static void main(String[] args) throws Exception {
        JavaLibrary jl = PJ.newInstance(JavaLibrary.class);
        Counter cx = new Counter(); //value is 0;
		assert(cx.getValue() == 0);
        System.out.println(cx);
        jl.init_counter(new JavaObject<Counter>(cx)); //now value is 45		
        System.out.println(cx);
        jl.count = 90;
        jl.init_counter(new JavaObject<Counter>(cx)); //now value is 90
        System.out.println(cx);
    }
}
