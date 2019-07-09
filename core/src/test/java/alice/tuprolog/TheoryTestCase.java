package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.tuprolog.exceptions.MalformedGoalException;
import junit.framework.TestCase;

public class TheoryTestCase extends TestCase {

    public void testToStringWithParenthesis() throws InvalidTheoryException {
        String before = "a :- b, (d ; e).";
        Theory theory = Theory.parseLazilyWithStandardOperators(before);
        String after = theory.toString();
        assertEquals(theory.toString(), Theory.parseLazilyWithStandardOperators(after).toString());
    }

    public void testAppendClauseLists() throws InvalidTheoryException, MalformedGoalException {
        Term[] clauseList = new Term[]{Struct.of("p"), Struct.of("q"), Struct.of("r")};
        Term[] otherClauseList = new Term[]{Struct.of("a"), Struct.of("b"), Struct.of("c")};
        Theory theory = Theory.fromPrologList(Struct.list(clauseList));
        theory.append(Theory.fromPrologList(Struct.list(otherClauseList)));
        Prolog engine = new Prolog();
        engine.setTheory(theory);
        assertTrue((engine.solve("p.")).isSuccess());
        assertTrue((engine.solve("b.")).isSuccess());
    }

}
