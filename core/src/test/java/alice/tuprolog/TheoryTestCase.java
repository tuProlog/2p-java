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
        Term[] clauseList = new Term[]{new Struct("p"), new Struct("q"), new Struct("r")};
        Term[] otherClauseList = new Term[]{new Struct("a"), new Struct("b"), new Struct("c")};
        Theory theory = Theory.fromPrologList(new Struct(clauseList));
        theory.append(Theory.fromPrologList(new Struct(otherClauseList)));
        Prolog engine = new Prolog();
        engine.setTheory(theory);
        assertTrue((engine.solve("p.")).isSuccess());
        assertTrue((engine.solve("b.")).isSuccess());
    }

}
