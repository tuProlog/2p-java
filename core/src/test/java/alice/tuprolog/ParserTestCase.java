package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import junit.framework.TestCase;

public class ParserTestCase extends TestCase {

    public void testReadingTerms() throws InvalidTermException {
        Term t = Term.createTerm("hello");
        Struct result = new Struct("hello");
        assertEquals(result, t);
    }

    public void testReadingEOF() throws InvalidTermException {
        try {
            Term.createTerm("");
            fail();
        } catch (InvalidTermException e) {
            assertTrue(true);
        }
    }

//    public void testUnaryPlusOperator() {
//        Term t = Term.createTerm("n(+100)");
//        // SICStus Prolog interprets "n(+100)" as "n(100)"
//        // GNU Prolog interprets "n(+100)" as "n(+(100))"
//        // The ISO Standard says + is not a unary operator
//        try {
//            fail("test not implemented");
//        } catch (InvalidTermException e) {
//        }
//    }

    public void testUnaryPlusOperator() throws InvalidTermException {
        // SICStus Prolog interprets "n(+100)" as "n(100)"
        // GNU Prolog interprets "n(+100)" as "n(+(100))"
        // The ISO Standard says + is not a unary operator
        Term t = Term.createTerm("n(+100)");
        Struct result = new Struct("n", Int.of(100));
        assertEquals(result, t);

        t = Term.createTerm("n(+(100))");

        result = new Struct("n", new Struct("+", Int.of(100)));
        assertEquals(result, t);
    }

    public void testUnaryMinusOperator() throws InvalidTermException {
        Term t = Term.createTerm("n(-100)");
        Struct result = new Struct("n", Int.of(-100));
        assertEquals(result, t);

        t = Term.createTerm("n(-(100))");

        result = new Struct("n", new Struct("-", Int.of(100)));
        assertEquals(result, t);
    }

    public void testBinaryMinusOperator() throws InvalidTermException {
        Term t = Term.createTerm("abs(3-11)");
        Struct result = new Struct("abs", new Struct("-", Int.of(3), Int.of(11)));
        assertEquals(result, t);
    }

    public void testListWithTail() throws InvalidTermException {
        Term t = Term.createTerm("[p|Y]");
        Struct result = new Struct(new Struct("p"), Var.of("Y"));
        result.resolveTerm();
        assertEquals(result.toString(), t.toString());
    }

    public void testBraces() throws InvalidTermException {
        String s = "{a,b,[3,{4,c},5],{a,b}}";
        Term t = Term.createTerm(s);
        assertEquals(s, t.toString());
    }

    public void testUnivOperator() throws InvalidTermException {
        Term t = Term.createTerm("p =.. q");
        Struct result = new Struct("=..", new Struct("p"), new Struct("q"));
        assertEquals(result, t);
    }

    public void testDotOperator() throws InvalidTermException {
        String s = "class('java.lang.Integer').'MAX_VALUE'";
        OperatorManager om = OperatorManager.standardOperatorsPlus(
                Operator.xfx(".", 600)
        );
        Struct result = new Struct(".", new Struct("class", new Struct("java.lang.Integer")),
                                   new Struct("MAX_VALUE"));
        Term t = Term.createTerm(s, om);
        assertEquals(result, t);
    }

    public void testBracketedOperatorAsTerm() throws InvalidTermException {
        String s = "u (b1) b2 (b3)";
        OperatorManager om = OperatorManager.standardOperatorsPlus(
                Operator.fx("u", 200),
                Operator.yfx("b1", 400),
                Operator.yfx("b2", 500),
                Operator.yfx("b3", 300)
        );
        Struct result = new Struct("b2", new Struct("u", new Struct("b1")), new Struct("b3"));
        Term t = Term.createTerm(s, om);
        assertEquals(result, t);
    }

    public void testBracketedOperatorAsTerm2() throws InvalidTermException {
        String s = "(u) b1 (b2) b3 a";
        OperatorManager om = OperatorManager.standardOperatorsPlus(
                Operator.fx("u", 200),
                Operator.yfx("b1", 400),
                Operator.yfx("b2", 500),
                Operator.yfx("b3", 300)
        );
        Struct result = new Struct("b1", new Struct("u"), new Struct("b3", new Struct("b2"), new Struct("a")));
        Term t = Term.createTerm(s, om);
        assertEquals(result, t);
    }

    public void testIntegerBinaryRepresentation() throws InvalidTermException {
        String n = "0b101101";
        Term t = Term.createTerm(n);
        alice.tuprolog.Number result = Int.of(45);
        assertEquals(result, t);
        String invalid = "0b101201";
        try {
            Term.createTerm(invalid);
            fail();
        } catch (InvalidTermException expected) {
            assertTrue(true); // success
        }
    }

    public void testIntegerOctalRepresentation() throws InvalidTermException {
        String n = "0o77351";
        Term t = Term.createTerm(n);
        alice.tuprolog.Number result = Int.of(32489);
        assertEquals(result, t);
        String invalid = "0o78351";
        try {
            Term.createTerm(invalid);
            fail();
        } catch (InvalidTermException expected) {
        }
    }

    public void testIntegerHexadecimalRepresentation() throws InvalidTermException {
        String n = "0xDECAF";
        Term t = Term.createTerm(n);
        alice.tuprolog.Number result = Int.of(912559);
        assertEquals(result, t);
        String invalid = "0xG";
        try {
            Term.createTerm(invalid);
            fail();
        } catch (InvalidTermException expected) {
            assertTrue(true); // success
        }
    }

    public void testEmptyDCGAction() throws InvalidTermException {
        String s = "{}";
        Struct result = new Struct("{}");
        Term t = Term.createTerm(s);
        assertEquals(result, t);
    }

    public void testSingleDCGAction() throws InvalidTermException {
        String s = "{hello}";
        Struct result = new Struct("{}", new Struct("hello"));
        Term t = Term.createTerm(s);
        assertEquals(result, t);
    }

    public void testMultipleDCGAction() throws InvalidTermException {
        String s = "{a, b, c}";
        Struct result = new Struct("{}",
                                   new Struct(",", new Struct("a"),
                                              new Struct(",", new Struct("b"), new Struct("c"))));
        Term t = Term.createTerm(s);
        assertEquals(result, t);
    }

//     This is an error both in 2.0.1 and in 2.1... don't know why, though.
	public void testDCGActionWithOperators() throws Exception {
        String input = "{A =.. B, hotel, 2}";
        Struct result = new Struct("{}",
                            new Struct(",", new Struct("=..", Var.of("A"), Var.of("B")),
                                new Struct(",", new Struct("hotel"), Int.of(2))));
        result.resolveTerm();
        Term t = Term.createTerm(input);
        assertEquals(result, t);
	}

    public void testMissingDCGActionElement() {
        String s = "{1, 2, , 4}";
        try {
            Term.createTerm(s);
            fail();
        } catch (InvalidTermException expected) {
            assertTrue(true); // success
        }
    }

    public void testDCGActionCommaAsAnotherSymbol() {
        String s = "{1 @ 2 @ 4}";
        try {
            Term.createTerm(s);
            fail();
        } catch (InvalidTermException expected) {
            assertTrue(true); // success
        }
    }

    public void testUncompleteDCGAction() {
        String s = "{1, 2,}";
        try {
            Term.createTerm(s);
            fail();
        } catch (InvalidTermException expected) {
            assertTrue(true); // success
        }

        s = "{1, 2";
        try {
            Term.createTerm(s);
            fail();
        } catch (InvalidTermException expected) {
            assertTrue(true); // success
        }
    }

    public void testMultilineComments() throws InvalidTermException {
        String text = "t1." + "\n" +
                        "/*" + "\n" +
                        "t2" + "\n" +
                        "*/" + "\n" +
                        "t3." + "\n";
        Theory theory = Theory.parseWithStandardOperators(text);
        Theory expected = Theory.of(new Struct("t1"), new Struct("t3"));
        assertEquals(expected, theory);
    }

    public void testSingleQuotedTermWithInvalidLineBreaks() {
        String s = "out('" +
                   "can_do(X).\n" +
                   "can_do(Y).\n" +
                   "').";
        try {
            Term.createTerm(s);
            fail();
        } catch (InvalidTermException expected) {
            assertTrue(true); // success
        }
    }

    public void testSingleQuotedEscaping() {
        String input = "'a''b'";
        Struct result = new Struct("a'b");
        Term t = Term.createTerm(input);
        assertEquals(result, t);

        input = "'a\"\"b'";
        result = new Struct("a\"\"b");
        t = Term.createTerm(input);
        assertEquals(result, t);
    }

    public void testDoubleQuotedEscaping() {
        String input = "\"a\"\"b\"";
        Struct result = new Struct("a\"b");
        Term t = Term.createTerm(input);
        assertEquals(result, t);

        input = "\"a''b\"";
        result = new Struct("a''b");
        t = Term.createTerm(input);
        assertEquals(result, t);
    }

    public void testMultilineSingleQuotedStrings() throws InvalidTermException {
        String input = "'a \\\n b'";
        Struct result = new Struct("a  b");
        Term t = Term.createTerm(input);
        assertEquals(result, t);

        input = "'a \\\r\n b'";
        t = Term.createTerm(input);
        assertEquals(result, t);
    }

    public void testEscapeHexChar() throws InvalidTermException {
        char c = 'G';

        String input = "'ab\\x" + Integer.toString(c, 16) + "\\c'";
        Struct result = new Struct("ab" + c +"c");
        Term t = Term.createTerm(input);
        assertEquals(result, t);

        input = "'ab\\X" + Integer.toString(c, 16) + "\\c'";
        t = Term.createTerm(input);
        assertEquals(result, t);

        input = "\"ab\\x" + Integer.toString(c, 16) + "\\c\"";
        t = Term.createTerm(input);
        assertEquals(result, t);

        input = "\"ab\\X" + Integer.toString(c, 16) + "\\c\"";
        t = Term.createTerm(input);
        assertEquals(result, t);
    }

    public void testEscapeOctChar() throws InvalidTermException {
        char c = 'G';

        String input = "'ab\\" + Integer.toString(c, 8) + "\\c'";
        Struct result = new Struct("ab" + c +"c");
        Term t = Term.createTerm(input);
        assertEquals(result, t);

        input = "\"ab\\" + Integer.toString(c, 8) + "\\c\"";
        t = Term.createTerm(input);
        assertEquals(result, t);

    }

    public void testStringEscaping() throws InvalidTermException {
        String input = "'\\a\\b\\f\\n\\r\\t\\v\\\\\\'\\\"\\`'";
        Struct result = new Struct("\u0007\b\f\n\r\t\u000b\\'\"`");
        Term t = Term.createTerm(input);
        assertEquals(result, t);

    }
}
