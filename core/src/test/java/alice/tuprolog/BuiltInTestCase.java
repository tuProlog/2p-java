package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.tuprolog.exceptions.MalformedGoalException;
import junit.framework.TestCase;

public class BuiltInTestCase extends TestCase {

    public void testConvertTermToGoal() throws InvalidTermException {
        Term t = Var.of("T");
        Struct result = new Struct("call", t);
        assertEquals(result, BuiltIn.convertTermToGoal(t));
        assertEquals(result, BuiltIn.convertTermToGoal(new Struct("call", t)));

        t = Int.of(2);
        assertNull(BuiltIn.convertTermToGoal(t));

        t = new Struct("p", new Struct("a"), Var.of("B"), new Struct("c"));
        result = (Struct) t;
        assertEquals(result, BuiltIn.convertTermToGoal(t));

        Var linked = Var.of("X");
        linked.setLink(new Struct("!"));
        Term[] arguments = new Term[]{linked, Var.of("Y")};
        Term[] results = new Term[]{new Struct("!"), new Struct("call", Var.of("Y"))};
        assertEquals((new Struct(";", results)).toString(), BuiltIn.convertTermToGoal(new Struct(";", arguments))
                                                                   .toString());
        assertEquals((new Struct(",", results)).toString(), BuiltIn.convertTermToGoal(new Struct(",", arguments))
                                                                   .toString());
        assertEquals((new Struct("->", results)).toString(), BuiltIn.convertTermToGoal(new Struct("->", arguments))
                                                                    .toString());
    }

    //Based on the bug #59 Grouping conjunctions in () changes result on sourceforge
    public void testGroupingConjunctions() throws InvalidTheoryException, MalformedGoalException {
        Prolog engine = new Prolog();
        engine.setTheory(Theory.parseLazilyWithStandardOperators("g1. \n g2."));
        SolveInfo info = engine.solve("(g1, g2), (g3, g4).");
        assertFalse(info.isSuccess());
        engine.setTheory(Theory.parseLazilyWithStandardOperators("g1. \n g2. \n g3. \n g4."));
        info = engine.solve("(g1, g2), (g3, g4).");
        assertTrue(info.isSuccess());
    }

}
