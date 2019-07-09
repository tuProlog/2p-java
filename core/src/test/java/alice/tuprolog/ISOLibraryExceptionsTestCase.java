package alice.tuprolog;

import junit.framework.TestCase;

/**
 * @author Matteo Iuliani
 * <p>
 * Test del funzionamento delle eccezioni lanciate dai predicati della ISOLibrary
 */
public class ISOLibraryExceptionsTestCase extends TestCase {

    // verifico che atom_length(X, Y) lancia un errore di instanziazione
    public void test_atom_length_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(atom_length(X, Y), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("atom_length", Var.of("X"), Var.of("Y"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che atom_length(1, Y) lancia un errore di tipo
    public void test_atom_length_2_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(atom_length(1, Y), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("atom_length", Int.of(1), Var.of("Y"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.atom("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che atom_chars(1, X) lancia un errore di tipo
    public void test_atom_chars_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(atom_chars(1, X), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("atom_chars", Int.of(1), Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.atom("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che atom_chars(X, a) lancia un errore di tipo
    public void test_atom_chars_2_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(atom_chars(X, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("atom_chars", Var.of("X"), Struct.atom("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.atom("list")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.atom("a")));
    }

    // verifico che char_code(ab, X) lancia un errore di tipo
    public void test_char_code_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(char_code(ab, X), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("char_code", Struct.atom("ab"), Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.atom("character")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.atom("ab")));
    }

    // verifico che char_code(X, a) lancia un errore di tipo
    public void test_char_code_2_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(char_code(X, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("char_code", Var.of("X"), Struct.atom("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.atom("integer")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.atom("a")));
    }

    // verifico che sub_atom(1, B, C, D, E) lancia un errore di tipo
    public void test_sub_atom_5_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(sub_atom(1, B, C, D, E), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("sub_atom_guard", Int.of(1), Var.of("B"), Var.of("C"), Var.of("D"), Var.of("E"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.atom("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

}