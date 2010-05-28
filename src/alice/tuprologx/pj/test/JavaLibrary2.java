package alice.tuprologx.pj.test;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class JavaLibrary2 {
    
    @PrologField(init="0")
    public Int count;
    
    @PrologMethod (            
        clauses={"inc(X):-this(Z), Z.count <- get(V1), V2 is V1 + X, Z.count <- set(V2)."})
	public abstract <$I extends Int> Boolean inc($I i);    
    
    @PrologMethod (            
        clauses={"init_counter(X):-this(Z), Z <- inc(5), Z.count <- get(V1), X <- setValue(V1)."})
	public abstract <$Counter extends JavaObject<Counter>> $Counter init_counter($Counter c);
        
    public static void main(String[] args) throws Exception {
        JavaLibrary2 jl = PJ.newInstance(JavaLibrary2.class);
        Counter cx = new Counter(); //value is 0;
		assert(cx.getValue() == 0);
        System.out.println(cx);
        jl.init_counter(new JavaObject<Counter>(cx)); //now value is 45		
        System.out.println(cx);
        //jl.count = new Int(90);
        jl.init_counter(new JavaObject<Counter>(cx)); //now value is 90
        System.out.println(cx);
    }
}
