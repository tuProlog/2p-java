package alice.tuprolog;

import junit.framework.TestCase;

public class SolveInfoTestCase extends TestCase {

    public void testGetSubsequentQuery() {
        Prolog engine = new Prolog();
        Term query = new Struct("is", Var.of("X"), new Struct("+", Int.of(1), Int.of(2)));
        SolveInfo result = engine.solve(query);
        assertTrue(result.isSuccess());
        assertEquals(query, result.getQuery());
        query = new Struct("functor", new Struct("p"), Var.of("Name"), Var.of("Arity"));
        result = engine.solve(query);
        assertTrue(result.isSuccess());
        assertEquals(query, result.getQuery());
    }

}
