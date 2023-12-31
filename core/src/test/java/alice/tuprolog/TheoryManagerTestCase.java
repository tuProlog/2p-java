package alice.tuprolog;

import alice.tuprolog.exceptions.*;
import junit.framework.TestCase;

import java.util.List;

public class TheoryManagerTestCase extends TestCase {

    public void testUnknownDirective() throws InvalidTheoryException {
        String theory = ":- unidentified_directive(unknown_argument).";
        Prolog engine = new Prolog();
        TestWarningListener warningListener = new TestWarningListener();
        engine.addWarningListener(warningListener);
        engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        assertTrue(warningListener.warning.indexOf("unidentified_directive/1") > 0);
        assertTrue(warningListener.warning.indexOf("is unknown") > 0);
    }

    public void testFailedDirective() throws InvalidTheoryException {
        String theory = ":- load_library('UnknownLibrary').";
        Prolog engine = new Prolog();
        TestWarningListener warningListener = new TestWarningListener();
        engine.addWarningListener(warningListener);
        engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        assertTrue(warningListener.warning.indexOf("load_library/1") > 0);
        assertTrue(warningListener.warning.indexOf("InvalidLibraryException") > 0);
    }

    public void testAssertNotBacktrackable() throws PrologException {
        Prolog engine = new Prolog();
        SolveInfo firstSolution = engine.solve("assertz(a(z)).");
        assertTrue(firstSolution.isSuccess());
        assertFalse(firstSolution.hasOpenAlternatives());
    }

    public void testAbolish() throws PrologException {
        Prolog engine = new Prolog();
        String theory = "test(A, B) :- A is 1+2, B is 2+3.";
        engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        TheoryManager manager = engine.getTheoryManager();
        Struct testTerm = Struct.of("test", Struct.atom("a"), Struct.atom("b"));
        List<ClauseInfo> testClauses = manager.find(testTerm);
        assertEquals(1, testClauses.size());
        manager.abolish(Struct.of("/", Struct.atom("test"), Int.of(2)));
        testClauses = manager.find(testTerm);
        // The predicate should also disappear completely from the clause
        // database, i.e. ClauseDatabase#get(f/a) should return null
        assertEquals(0, testClauses.size());
    }

    public void testAbolish2() throws InvalidTheoryException, MalformedGoalException {
        Prolog engine = new Prolog();
        engine.setTheory(Theory.parseLazilyWithStandardOperators("fact(new).\n" +
                                    "fact(other).\n"));

        SolveInfo info = engine.solve("abolish(fact/1).");
        assertTrue(info.isSuccess());
        info = engine.solve("fact(V).");
        assertFalse(info.isSuccess());
    }

    // Based on the bugs 65 and 66 on sourceforge
    public void testRetractall() throws MalformedGoalException, NoSolutionException, NoMoreSolutionException {
        Prolog engine = new Prolog();
        SolveInfo info = engine.solve("assert(takes(s1,c2)), assert(takes(s1,c3)).");
        assertTrue(info.isSuccess());
        info = engine.solve("takes(s1, N).");
        assertTrue(info.isSuccess());
        assertTrue(info.hasOpenAlternatives());
        assertEquals("c2", info.getVarValue("N").toString());
        info = engine.solveNext();
        assertTrue(info.isSuccess());
        assertEquals("c3", info.getVarValue("N").toString());

        info = engine.solve("retractall(takes(s1,c2)).");
        assertTrue(info.isSuccess());
        info = engine.solve("takes(s1, N).");
        assertTrue(info.isSuccess());
        assertFalse(info.hasOpenAlternatives());
        assertEquals("c3", info.getVarValue("N").toString());
    }

    // TODO test retractall: ClauseDatabase#get(f/a) should return an
    // emptyWithStandardOperators list

    public void testRetract() throws InvalidTheoryException, MalformedGoalException {
        Prolog engine = new Prolog();
        TestOutputListener listener = new TestOutputListener();
        engine.addOutputListener(listener);
        engine.setTheory(Theory.parseWithStandardOperators("insect(ant). \n insect(bee)."));
        SolveInfo info = engine.solve("retract(insect(I)), write(I), retract(insect(bee)), fail.");
        assertFalse(info.isSuccess());
        assertEquals("antbee", listener.output);

    }

}
