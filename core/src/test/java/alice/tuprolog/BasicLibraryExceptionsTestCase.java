package alice.tuprolog;

import junit.framework.TestCase;

/**
 * @author Matteo Iuliani
 * <p>
 * Test del funzionamento delle eccezioni lanciate dai predicati della
 * BasicLibrary
 */
public class BasicLibraryExceptionsTestCase extends TestCase {

    // verifico che set_theory(X) lancia un errore di instanziazione
    public void test_set_theory_1_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(set_theory(X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        Struct t = Struct.of("set_theory", Var.of("X"));
        assertTrue(g.toString().equals(t.toString()));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che set_theory(1) lancia un errore di tipo
    public void test_set_theory_1_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(set_theory(1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("set_theory", Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che set_theory(a) lancia un errore di sintassi
    public void test_set_theory_1_3() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(set_theory(\"a :-\"), error(syntax_error(Message), syntax_error(Goal, Line, Position, Message)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("set_theory", Struct.of("a :-"))));
        Int line = (Int) info.getTerm("Line");
        assertTrue(line.intValue() == 1);
        Int position = (Int) info.getTerm("Line");
        assertTrue(position.intValue() == 1);
        Struct message = (Struct) info.getTerm("Message");
        assertTrue(message.isEqual(Struct.of("no viable alternative at input ':-'")));
    }

    // verifico che add_theory(X) lancia un errore di instanziazione
    public void test_add_theory_1_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(add_theory(X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("add_theory", Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che add_theory(1) lancia un errore di tipo
    public void test_add_theory_1_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(add_theory(1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("add_theory", Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che add_theory(a) lancia un errore di sintassi
    public void test_add_theory_1_3() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(add_theory('a :-'), error(syntax_error(Message), syntax_error(Goal, Line, Position, Message)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("add_theory", Struct.of("a :-"))));
        Int line = (Int) info.getTerm("Line");
        assertEquals(1, line.intValue());
        Int position = (Int) info.getTerm("Line");
        assertEquals(1, position.intValue());
        Struct message = (Struct) info.getTerm("Message");
        assertEquals(Struct.of("no viable alternative at input ':-'"), message);
    }

    // verifico che agent(X) lancia un errore di instanziazione
    public void test_agent_1_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(prologEngine(X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("prologEngine", Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che agent(1) lancia un errore di tipo
    public void test_agent_1_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(prologEngine(1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("prologEngine", Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che agent(X, a) lancia un errore di instanziazione
    public void test_agent_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(prologEngine(X, a), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g
                           .isEqual(Struct.of("prologEngine", Var.of("X"), Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che agent(a, X) lancia un errore di instanziazione
    public void test_agent_2_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(prologEngine(a, X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g
                           .isEqual(Struct.of("prologEngine", Struct.of("a"), Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che agent(1, a) lancia un errore di tipo
    public void test_agent_2_3() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(prologEngine(1, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("prologEngine", Int.of(1), Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che agent(a, 1) lancia un errore di tipo
    public void test_agent_2_4() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(prologEngine(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("prologEngine", Struct.of("a"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("struct")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che '=:='(X, 1) lancia un errore di instanziazione
    public void test_expression_comparison_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=:='(X, 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Var.of("X"),
                                        Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che '=:='(1, X) lancia un errore di instanziazione
    public void test_expression_comparison_2_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=:='(1, X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che '=:='(a, 1) lancia un errore di tipo
    public void test_expression_comparison_2_3() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=:='(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Struct.of("a"),
                                        Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '=:='(1, a) lancia un errore di tipo
    public void test_expression_comparison_2_4() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=:='(1, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '=\='(X, 1) lancia un errore di instanziazione
    public void test_expression_comparison_2_5() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=\\\\='(X, 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Var.of("X"),
                                        Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che '=\='(1, X) lancia un errore di instanziazione
    public void test_expression_comparison_2_6() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=\\\\='(1, X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che '=\='(a, 1) lancia un errore di tipo
    public void test_expression_comparison_2_7() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=\\\\='(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Struct.of("a"),
                                        Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '=\='(1, a) lancia un errore di tipo
    public void test_expression_comparison_2_8() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=\\\\='(1, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '>'(X, 1) lancia un errore di instanziazione
    public void test_expression_comparison_2_9() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>'(X, 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_than",
                                        Var.of("X"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che '>'(1, X) lancia un errore di instanziazione
    public void test_expression_comparison_2_10() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>'(1, X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_than", Int.of(1),
                                        Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che '>'(a, 1) lancia un errore di tipo
    public void test_expression_comparison_2_11() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>'(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_than", Struct.of(
                "a"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '>'(1, a) lancia un errore di tipo
    public void test_expression_comparison_2_12() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>'(1, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_than", Int.of(1),
                                        Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '<'(X, 1) lancia un errore di instanziazione
    public void test_expression_comparison_2_13() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('<'(X, 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_than", Var.of("X"),
                                        Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che '<'(1, X) lancia un errore di instanziazione
    public void test_expression_comparison_2_14() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('<'(1, X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_than", Int.of(1),
                                        Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che '<'(a, 1) lancia un errore di tipo
    public void test_expression_comparison_2_15() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('<'(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_than",
                                        Struct.of("a"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '<'(1, a) lancia un errore di tipo
    public void test_expression_comparison_2_16() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('<'(1, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_than", Int.of(1),
                                        Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '>='(X, 1) lancia un errore di instanziazione
    public void test_expression_comparison_2_17() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>='(X, 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_or_equal_than",
                                        Var.of("X"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che '>='(1, X) lancia un errore di instanziazione
    public void test_expression_comparison_2_18() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>='(1, X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_or_equal_than",
                                        Int.of(1), Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che '>='(a, 1) lancia un errore di tipo
    public void test_expression_comparison_2_19() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>='(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_or_equal_than",
                                        Struct.of("a"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '>='(1, a) lancia un errore di tipo
    public void test_expression_comparison_2_20() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>='(1, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_or_equal_than",
                                        Int.of(1), Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '=<'(X, 1) lancia un errore di instanziazione
    public void test_expression_comparison_2_21() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=<'(X, 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_or_equal_than",
                                        Var.of("X"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che '=<'(1, X) lancia un errore di instanziazione
    public void test_expression_comparison_2_22() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=<'(1, X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_or_equal_than",
                                        Int.of(1), Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che '=<'(a, 1) lancia un errore di tipo
    public void test_expression_comparison_2_23() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=<'(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_or_equal_than",
                                        Struct.of("a"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '=<'(1, a) lancia un errore di tipo
    public void test_expression_comparison_2_24() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=<'(1, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_or_equal_than",
                                        Int.of(1), Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("evaluable")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che '=:='(1, 1/0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_25() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=:='(1, 1/0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Struct.of("/", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=\='(1, 1/0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_26() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=\\\\='(1, 1/0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Struct.of("/", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '>'(1, 1/0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_27() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>'(1, 1/0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_than", Int.of(1),
                                        Struct.of("/", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '<'(1, 1/0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_28() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('<'(1, 1/0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_than", Int.of(1),
                                        Struct.of("/", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '>='(1, 1/0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_29() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>='(1, 1/0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_or_equal_than",
                                        Int.of(1), Struct.of("/", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=<'(1, 1/0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_30() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=<'(1, 1/0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_or_equal_than",
                                        Int.of(1), Struct.of("/", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=:='(1, 1//0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_31() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=:='(1, 1//0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Struct.of("//", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=\='(1, 1//0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_32() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=\\\\='(1, 1//0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Int.of(1),
                                        Struct.of("//", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '>'(1, 1//0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_33() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>'(1, 1//0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_than", Int.of(1),
                                        Struct.of("//", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '<'(1, 1//0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_34() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('<'(1, 1//0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_than", Int.of(1),
                                        Struct.of("//", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '>='(1, 1//0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_35() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>='(1, 1//0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_or_equal_than",
                                        Int.of(1), Struct.of("//", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=<'(1, 1//0) lancia l'errore di valutazione "zero_divisor"
    public void test_expression_comparison_2_36() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=<'(1, 1//0), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_or_equal_than",
                                        Int.of(1), Struct.of("//", Int.of(1), Int.of(0)))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=:='(1 div 0, 1) lancia l'errore di valutazione
    // "zero_divisor"
    public void test_expression_comparison_2_37() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=:='(1 div 0, 1), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Struct.of(
                "div", Int.of(1), Int.of(0)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=\='(1 div 0, 1) lancia l'errore di valutazione
    // "zero_divisor"
    public void test_expression_comparison_2_38() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=\\\\='(1 div 0, 1), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_equality", Struct.of(
                "div", Int.of(1), Int.of(0)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '>'(1 div 0, 1) lancia l'errore di valutazione
    // "zero_divisor"
    public void test_expression_comparison_2_39() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>'(1 div 0, 1), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_than", Struct.of(
                "div", Int.of(1), Int.of(0)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '<'(1 div 0, 1) lancia l'errore di valutazione
    // "zero_divisor"
    public void test_expression_comparison_2_40() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('<'(1 div 0, 1), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_than", Struct.of(
                "div", Int.of(1), Int.of(0)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '>='(1 div 0, 1) lancia l'errore di valutazione
    // "zero_divisor"
    public void test_expression_comparison_2_41() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('>='(1 div 0, 1), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_greater_or_equal_than",
                                        Struct.of("div", Int.of(1), Int.of(0)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che '=<'(1 div 0, 1) lancia l'errore di valutazione
    // "zero_divisor"
    public void test_expression_comparison_2_42() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch('=<'(1 div 0, 1), error(evaluation_error(Error), evaluation_error(Goal, ArgNo, Error)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("expression_less_or_equal_than",
                                        Struct.of("div", Int.of(1), Int.of(0)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("Error");
        assertTrue(validType.isEqual(Struct.of("zero_divisor")));
    }

    // verifico che text_concat(X, a, b) lancia un errore di instanziazione
    public void test_text_concat_3_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(text_concat(X, a, b), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("text_concat", Var.of("X"),
                                        Struct.of("a"), Struct.of("b"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che text_concat(a, X, b) lancia un errore di instanziazione
    public void test_text_concat_3_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(text_concat(a, X, b), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("text_concat", Struct.of("a"),
                                        Var.of("X"), Struct.of("b"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che text_concat(1, a, b) lancia un errore di tipo
    public void test_text_concat_3_3() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(text_concat(1, a, b), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("text_concat", Int.of(1), Struct.of(
                "a"), Struct.of("b"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che text_concat(a, 1, b) lancia un errore di tipo
    public void test_text_concat_3_4() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(text_concat(a, 1, b), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("text_concat", Struct.of("a"),
                                        Int.of(1), Struct.of("b"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che num_atom(a, X) lancia un errore di tipo
    public void test_num_atom_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(num_atom(a, X), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("num_atom", Struct.of("a"), Var.of(
                "X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("number")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che num_atom(1, 1) lancia un errore di tipo
    public void test_num_atom_2_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(num_atom(1, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("num_atom", Int.of(1), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("atom")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che num_atom(1, a) lancia un errore di dominio
    public void test_num_atom_2_3() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(num_atom(1, a), error(domain_error(ValidDomain, Culprit), domain_error(Goal, ArgNo, ValidDomain, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g
                           .isEqual(Struct.of("num_atom", Int.of(1), Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validDomain = (Struct) info.getTerm("ValidDomain");
        assertTrue(validDomain.isEqual(Struct.of("num_atom")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che arg(X, p(1), 1) lancia un errore di instanziazione
    public void test_arg_3_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(arg(X, p(1), 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("arg_guard", Var.of("X"), Struct.of(
                "p", Int.of(1)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che arg(1, X, 1) lancia un errore di instanziazione
    public void test_arg_3_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(arg(1, X, 1), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("arg_guard", Int.of(1), Var.of("X"),
                                        Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che arg(a, p(1), 1) lancia un errore di tipo
    public void test_arg_3_3() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(arg(a, p(1), 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("arg_guard", Struct.of("a"),
                                        Struct.of("p", Int.of(1)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("integer")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che arg(1, p, 1) lancia un errore di tipo
    public void test_arg_3_4() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(arg(1, p, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("arg_guard", Int.of(1),
                                        Struct.of("p"), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("compound")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("p")));
    }

    // verifico che arg(0, p(0), 1) lancia un errore di dominio
    public void test_arg_3_5() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(arg(0, p(0), 1), error(domain_error(ValidDomain, Culprit), domain_error(Goal, ArgNo, ValidDomain, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("arg_guard", Int.of(0), Struct.of(
                "p", Int.of(0)), Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidDomain");
        assertTrue(validType.isEqual(Struct.of("greater_than_zero")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 0);
    }

    // verifico che clause(X, true) lancia un errore di instanziazione
    public void test_clause_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(clause(X, true), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("clause_guard", Var.of("X"),
                                        Struct.of("true"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che call(X) lancia un errore di instanziazione
    public void test_call_1_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(call(X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("$call_guard", Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che call(1) lancia un errore di tipo
    public void test_call_1_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(call(1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("$call_guard", Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("callable")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che findall(a, X, L) lancia un errore di instanziazione
    public void test_findall_3_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(findall(a, X, L), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        //System.out.println(g);
        assertTrue(g.isEqual(Struct.of("all_solutions_predicates_guard",
                                        Struct.of("a"), Var.of("X"), Var.of("L"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che findall(a, 1, L) lancia un errore di tipo
    public void test_findall_3_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(findall(a, 1, L), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("all_solutions_predicates_guard",
                                        Struct.of("a"), Int.of(1), Var.of("L"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("callable")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che setof(a, X, L) lancia un errore di instanziazione
    public void test_setof_3_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(setof(a, X, L), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("all_solutions_predicates_guard",
                                        Struct.of("a"), Var.of("X"), Var.of("L"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che setof(a, 1, L) lancia un errore di tipo
    public void test_setof_3_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(setof(a, 1, L), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("all_solutions_predicates_guard",
                                        Struct.of("a"), Int.of(1), Var.of("L"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("callable")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che bagof(a, X, L) lancia un errore di instanziazione
    public void test_bagof_3_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(bagof(a, X, L), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("all_solutions_predicates_guard",
                                        Struct.of("a"), Var.of("X"), Var.of("L"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
    }

    // verifico che bagof(a, 1, L) lancia un errore di tipo
    public void test_bagof_3_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(bagof(a, 1, L), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("all_solutions_predicates_guard",
                                        Struct.of("a"), Int.of(1), Var.of("L"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("callable")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che assert(X) lancia un errore di instanziazione
    public void test_assert_1_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(assert(X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("assertz", Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che assert(1) lancia un errore di tipo
    public void test_assert_1_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(assert(1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("assertz", Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("clause")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che retract(X) lancia un errore di instanziazione
    public void test_retract_1_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(retract(X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("$retract", Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che retract(1) lancia un errore di tipo
    public void test_retract_1_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(retract(1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("$retract", Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("clause")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che retractall(X) lancia un errore di instanziazione
    public void test_retractall_1_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(retractall(X), error(instantiation_error, instantiation_error(Goal, ArgNo)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("retract_guard", Var.of("X"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
    }

    // verifico che retractall(1) lancia un errore di tipo
    public void test_retractall_1_2() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(retractall(1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("retract_guard", Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("clause")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che member(a, 1) lancia un errore di tipo
    public void test_member_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(member(a, 1), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("member_guard", Struct.of("a"),
                                        Int.of(1))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("list")));
        Int culprit = (Int) info.getTerm("Culprit");
        assertTrue(culprit.intValue() == 1);
    }

    // verifico che reverse(a, []) lancia un errore di tipo
    public void test_reverse_2_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(reverse(a, []), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("reverse_guard", Struct.of("a"), Struct.emptyList())));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 1);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("list")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che delete(a, a, []) lancia un errore di tipo
    public void test_delete_3_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(delete(a, a, []), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("delete_guard", Struct.of("a"), Struct.of("a"), Struct.emptyList())));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("list")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

    // verifico che element(1, a, a) lancia un errore di tipo
    public void test_element_3_1() throws Exception {
        Prolog engine = new Prolog();
        String goal = "catch(element(1, a, a), error(type_error(ValidType, Culprit), type_error(Goal, ArgNo, ValidType, Culprit)), true).";
        SolveInfo info = engine.solve(goal);
        assertTrue(info.isSuccess());
        Struct g = (Struct) info.getTerm("Goal");
        assertTrue(g.isEqual(Struct.of("element_guard", Int.of(1), Struct.of("a"), Struct.of("a"))));
        Int argNo = (Int) info.getTerm("ArgNo");
        assertTrue(argNo.intValue() == 2);
        Struct validType = (Struct) info.getTerm("ValidType");
        assertTrue(validType.isEqual(Struct.of("list")));
        Struct culprit = (Struct) info.getTerm("Culprit");
        assertTrue(culprit.isEqual(Struct.of("a")));
    }

}