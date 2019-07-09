package alice.tuprolog.parser;

import alice.tuprolog.Double;
import alice.tuprolog.*;
import alice.tuprolog.exceptions.InvalidTermException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class TestExpressionParsing extends BaseTestPrologParsing {

    @Override
    protected OperatorManager getOperatorManager() {
        OperatorManager om = OperatorManager.standardOperators();
        om.add("++", "yf", 100);
        om.add("--", "yf", 100);
        om.add("fails", "xf", 50);
        om.add("succeeds", "xf", 50);
        return om;
    }

    @Test
    @Parameters(method = "getInfixNonAssociativeOperatorsParams")
    public void testInfixNonAssociativeOperators(String operator, java.util.List<String> arguments) {
        if (operator.matches("[A-Za-z]+")) {
            operator = " " + operator + " ";
        }

        for (String op : Arrays.asList(operator, String.format(" %s ", operator))) {
            final String toBeParsed = String.join(op, arguments);

            if (arguments.size() == 2) {
                Term expected = Struct.of(op.trim(), parseTerm(arguments.get(0)), parseTerm(arguments.get(1)));
                System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

                assertEquals(expected, parseTerm(toBeParsed));
                System.out.println(" yes.");
            } else {
                int failingColumn = String.join(op, arguments.subList(0, 2)).length();
                System.out.printf("Parsing\n\t\t%s\n\tmust fail at or after column %d", toBeParsed, failingColumn);

                try {
                    Term parsed = parseTerm(toBeParsed);
                    Assert.fail(String.format("%s is parsed as %s", toBeParsed, parsed));
                } catch (InvalidTermException e) {
                    assertEquals(1, e.getLine());
                    assertEquals(toBeParsed, e.getInput());
                    assertEquals(operator.trim(), e.getOffendingSymbol());
                    assertTrue(e.getPositionInLine() >= failingColumn);
                    assertTrue(e.getPositionInLine() <= toBeParsed.length());

                    System.out.printf(": %s\n", e.toString());

                }
            }

        }

    }

    public Object[][] getInfixNonAssociativeOperatorsParams() {
        final java.util.List<String> xfxOps = Arrays.asList(":-", "<", "=", "=..", "=@=", "=:=", "=<", "==", "=\\=", ">", ">=", "@<", "@=<", "@>", "@>=", "\\=", "\\==", "is", "**");
        final java.util.List<java.util.List<String>> arguments = getTermsSequences();

        return xfxOps.stream().flatMap(op ->
                                               arguments.stream().map(args -> new Object[]{op, args})
        ).toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getInfixLeftAssociativeOperatorsParams")
    public void testInfixLeftAssociativeOperators(String operator, java.util.List<String> arguments) {

        for (String op : Arrays.asList(operator, String.format(" %s ", operator))) {
            final String toBeParsed = String.join(op, arguments);

            Term expected = Struct.of(op.trim(), parseTerm(arguments.get(0)), parseTerm(arguments.get(1)));
            for (int i = 2; i < arguments.size(); i++) {
                expected = Struct.of(op.trim(), expected, parseTerm(arguments.get(i)));
            }

            System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

            assertEquals(expected, parseTerm(toBeParsed));
            System.out.println(" yes.");
        }

    }

    public Object[][] getInfixLeftAssociativeOperatorsParams() {
        final java.util.List<String> yfxOps = Arrays.asList("+", "-", "/\\", "\\/", "*", "/", "//", "<<", ">>");
        final java.util.List<java.util.List<String>> arguments = getTermsSequences();

        return yfxOps.stream().flatMap(op ->
                                               arguments.stream().map(args -> new Object[]{op, args})
        ).toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getPostfixNonAssociativeOperatorsParams")
    public void testPostfixNonAssociativeOperators(String operator, String argument) {
        String toBeParsed = argument + " " + operator;

        Term expected = Struct.of(operator, parseTerm(argument));

        System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

        assertEquals(expected, parseTerm(toBeParsed));
        System.out.println(" yes.");

        int failingColumn = toBeParsed.length() + 1;
        toBeParsed += " " + operator;
        System.out.printf("Parsing\n\t\t%s\n\tmust fail at or after column %d", toBeParsed, failingColumn);

        try {
            Term parsed = parseTerm(toBeParsed);
            Assert.fail(String.format("%s is parsed as %s", toBeParsed, parsed));
        } catch (InvalidTermException e) {
            assertEquals(1, e.getLine());
            assertEquals(toBeParsed, e.getInput());
            assertEquals(operator.trim(), e.getOffendingSymbol());
            assertTrue(e.getPositionInLine() >= failingColumn);
            assertTrue(e.getPositionInLine() <= toBeParsed.length());

            System.out.printf(": %s\n", e.toString());

        }
    }

    public Object[][] getPostfixNonAssociativeOperatorsParams() {
        final java.util.List<String> xfOps = Arrays.asList("succeeds", "fails");
        final java.util.List<String> arguments = getATermsSequence();

        return xfOps.stream().flatMap(op ->
                                              arguments.stream().map(arg -> new Object[]{op, arg})
        ).toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getPrefixNonAssociativeOperatorsParams")
    public void testPrefixNonAssociativeOperators(String operator, String argument) {
        String toBeParsed = operator + " " + argument;

        Term expected = Struct.of(operator, parseTerm(argument));

        System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

        assertEquals(expected, parseTerm(toBeParsed));
        System.out.println(" yes.");

        toBeParsed = operator + " " + toBeParsed;
        int failingColumn = operator.length() + 1;
        System.out.printf("Parsing\n\t\t%s\n\tmust fail at or after column %d", toBeParsed, failingColumn);

        try {
            Term parsed = parseTerm(toBeParsed);
            Assert.fail(String.format("%s is parsed as %s", toBeParsed, parsed));
        } catch (InvalidTermException e) {
            assertEquals(1, e.getLine());
            assertEquals(toBeParsed, e.getInput());
            assertEquals(operator.trim(), e.getOffendingSymbol());
            assertTrue(e.getPositionInLine() >= failingColumn);
            assertTrue(e.getPositionInLine() <= toBeParsed.length());

            System.out.printf(": %s\n", e.toString());

        }
    }

    public Object[][] getPrefixNonAssociativeOperatorsParams() {
        final java.util.List<String> fxOps = Arrays.asList(":-", "?-", "\\");
        final java.util.List<String> arguments = getATermsSequence();

        return fxOps.stream()
                    .flatMap(op -> arguments.stream().map(arg -> new Object[]{op, arg}))
                    .toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getPrefixAssociativeOperatorsParams")
    public void testPrefixAssociativeOperators(String operator, String argument) {

        for (int i = 1; i <= 3; i++) {
            final String toBeParsed = IntStream.range(0, i)
                                               .mapToObj(j -> operator)
                                               .collect(Collectors.joining(" ", "", " " + argument));


            Term expected = Struct.of(operator, parseTerm(argument));
            for (int j = 1; j < i; j++) {
                expected = Struct.of(operator, expected);
            }

            System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

            assertEquals(expected, parseTerm(toBeParsed));
            System.out.println(" yes.");
        }
    }

    public Object[][] getPrefixAssociativeOperatorsParams() {
        final java.util.List<String> fyOps = Arrays.asList("not", "\\+", "+", "-");
        final java.util.List<String> arguments = getATermsSequence();

        return fyOps.stream().flatMap(op ->
                                              arguments.stream().map(arg -> new Object[]{op, arg})
        ).toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getPostfixAssociativeOperatorsParams")
    public void testPostfixAssociativeOperators(String operator, String argument) {

        for (int i = 1; i <= 3; i++) {
            final String toBeParsed = IntStream.range(0, i)
                                               .mapToObj(j -> operator)
                                               .collect(Collectors.joining(" ", argument + " ", ""));


            Term expected = Struct.of(operator, parseTerm(argument));
            for (int j = 1; j < i; j++) {
                expected = Struct.of(operator, expected);
            }

            System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

            assertEquals(expected, parseTerm(toBeParsed));
            System.out.println(" yes.");
        }
    }

    public Object[][] getPostfixAssociativeOperatorsParams() {
        final java.util.List<String> yfOps = Arrays.asList("++", "--");
        final java.util.List<String> arguments = getATermsSequence();

        return yfOps.stream().flatMap(op ->
                                              arguments.stream().map(arg -> new Object[]{op, arg})
        ).toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getInfixRightAssociativeOperatorsParams")
    public void testInfixRightAssociativeOperators(String operator, java.util.List<String> arguments) {

        for (String op : Arrays.asList(operator, String.format(" %s ", operator))) {
            final String toBeParsed = String.join(op, arguments);

            int i = arguments.size() - 1;
            Term expected = Struct.of(op.trim(), parseTerm(arguments.get(i - 1)), parseTerm(arguments.get(i)));
            for (i -= 2; i >= 0; i--) {
                expected = Struct.of(op.trim(), parseTerm(arguments.get(i)), expected);
            }

            System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

            assertEquals(expected, parseTerm(toBeParsed));
            System.out.println(" yes.");
        }

    }

    private java.util.List<String> getATermsSequence() {
        return getTermsSequences().stream().flatMap(java.util.List::stream).collect(Collectors.toList());
    }

    private java.util.List<java.util.List<String>> getTermsSequences() {
        return Arrays.asList(
                Arrays.asList("(1)", "(2)"),
                Arrays.asList("(1.9)", "(2.8)"),
                Arrays.asList("a", "b"),
                Arrays.asList("A", "B"),
                Arrays.asList("f(x)", "g(y)"),
                Arrays.asList("(1, 2)", "(3, 4)"),
                Arrays.asList("(1)", "(2)", "(3)"),
                Arrays.asList("(1.9)", "(2.8)", "(3.7)"),
                Arrays.asList("a", "b", "c"),
                Arrays.asList("A", "B", "C"),
                Arrays.asList("f(x)", "g(y)", "h(z)"),
                Arrays.asList("(1, 2)", "(3, 4)", "(5, 6)")
        );
    }

    public Object[][] getInfixRightAssociativeOperatorsParams() {
        final java.util.List<String> xfyOps = Arrays.asList(";", "->", ",", "^");
        final java.util.List<java.util.List<String>> arguments = getTermsSequences();

        return xfyOps.stream()
                     .flatMap(op -> arguments.stream().map(args -> new Object[]{op, args}))
                     .toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getExpressionsAreParsedCorrectly")
    public void testExpressionsAreParsedCorrectly(String toBeParsed, Term expected) {
        System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);

        Assert.assertEquals(expected, parseTerm(toBeParsed));
        System.out.println(" yes.");
    }

    public Object[][] getExpressionsAreParsedCorrectly() {
        return Stream.of(
                Stream.of(
                        "a :- 1",
                        Struct.of(":-",
                                   Struct.atom("a"),
                                   Int.of(1))
                ),
                Stream.of(
                        "a; B :- 1",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Var.of("B")),
                                   Int.of(1))
                ),
                Stream.of(
                        "a :- 1; '2'",
                        Struct.of(":-",
                                   Struct.atom("a"),
                                   Struct.of(";",
                                              Int.of(1),
                                              Struct.atom("2")))
                ),
                Stream.of(
                        "a; B :- 1; '2'",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Var.of("B")),
                                   Struct.of(";",
                                              Int.of(1),
                                              Struct.atom("2")))
                ),
                Stream.of(
                        "a; B :- 1, 3.1; '2'",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Var.of("B")),
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Int.of(1),
                                                         Double.of(3.1)),
                                              Struct.atom("2")))
                ),
                Stream.of(
                        "a; B :- 1; '2', 3.1",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Var.of("B")),
                                   Struct.of(";",
                                              Int.of(1),
                                              Struct.of(",",
                                                         Struct.atom("2"),
                                                         Double.of(3.1))))
                ),
                Stream.of(
                        "a, c(D); B :- 1, 3.1; '2'",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Struct.atom("a"),
                                                         Struct.of("c", Var.of("D"))),
                                              Var.of("B")),
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Int.of(1),
                                                         Double.of(3.1)),
                                              Struct.atom("2")))
                ),
                Stream.of(
                        "a; B, c(D) :- 1, 3.1; '2'",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Struct.of(",",
                                                         Var.of("B"),
                                                         Struct.of("c", Var.of("D")))),
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Int.of(1),
                                                         Double.of(3.1)),
                                              Struct.atom("2")))
                ),
                Stream.of(
                        "a, c(D); B :- 1; '2', 3.1",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Struct.atom("a"),
                                                         Struct.of("c", Var.of("D"))),
                                              Var.of("B")),
                                   Struct.of(";",
                                              Int.of(1),
                                              Struct.of(",",
                                                         Struct.atom("2"),
                                                         Double.of(3.1))))
                ),
                Stream.of(
                        "a; B, c(D) :- 1; '2', 3.1",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Struct.of(",",
                                                         Var.of("B"),
                                                         Struct.of("c", Var.of("D")))),
                                   Struct.of(";",
                                              Int.of(1),
                                              Struct.of(",",
                                                         Struct.atom("2"),
                                                         Double.of(3.1))))
                ),
                Stream.of(
                        "a; B, c(D) :- 1, \"4\"; '2', 3.1",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Struct.of(",",
                                                         Var.of("B"),
                                                         Struct.of("c", Var.of("D")))),
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Int.of(1),
                                                         Struct.atom("4")),
                                              Struct.of(",",
                                                         Struct.atom("2"),
                                                         Double.of(3.1))))
                ),
                Stream.of(
                        "a, c(D); B, e(_f, [g]) :- 1; '2', 3.1",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Struct.atom("a"),
                                                         Struct.of("c", Var.of("D"))),
                                              Struct.of(",",
                                                         Var.of("B"),
                                                         Struct.of("e",
                                                                    Var.of("_f"),
                                                                    Struct.of(".",
                                                                               Struct.atom("g"),
                                                                               Struct.emptyList())))),
                                   Struct.of(";",
                                              Int.of(1),
                                              Struct.of(",",
                                                         Struct.atom("2"),
                                                         Double.of(3.1))))
                ),
                Stream.of(
                        "a; b; B, c(D) :- 1, \"4\", 5; '2', 3.1, 6.7",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Struct.of(";",
                                                         Struct.atom("b"),
                                                         Struct.of(",",
                                                                    Var.of("B"),
                                                                    Struct.of("c",
                                                                               Var.of("D"))))),
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Int.of(1),
                                                         Struct.of(",",
                                                                    Struct.atom("4"),
                                                                    Int.of(5))),
                                              Struct.of(",",
                                                         Struct.atom("2"),
                                                         Struct.of(",",
                                                                    Double.of(3.1),
                                                                    Double.of(6.7)))))
                ),
                Stream.of(
                        "a; b; B, c(D) :- 1, \"4\"; '2', 3.1",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.atom("a"),
                                              Struct.of(";",
                                                         Struct.atom("b"),
                                                         Struct.of(",",
                                                                    Var.of("B"),
                                                                    Struct.of("c",
                                                                               Var.of("D"))))),
                                   Struct.of(";",
                                              Struct.of(",",
                                                         Int.of(1),
                                                         Struct.atom("4")),
                                              Struct.of(",",
                                                         Struct.atom("2"),
                                                         Double.of(3.1))))
                ),
                Stream.of(
                        "b, 1 -> 2; 3,4 :- a",
                        Struct.of(":-",
                                   Struct.of(";",
                                              Struct.of("->",
                                                         Struct.of(",",
                                                                    Struct.atom("b"),
                                                                    Int.of(1)),
                                                         Int.of(2)),
                                              Struct.of(",",
                                                         Int.of(3),
                                                         Int.of(4))),
                                   Struct.atom("a"))
                )
        ).map(s -> s.toArray(Object[]::new))
                     .toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getExpressionsAreParsedCorrectlyThrough99Problems")
    public void testExpressionsAreParsedCorrectlyThrough99Problems(String toBeParsed, Term expected) {
        System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);
        assertEquals(expected, parseTerm(toBeParsed));
        System.out.println(" yes.");
    }

    protected void assertEquals(Term t1, Term t2) {
        Assert.assertTrue(t1 + " is not matching " + t2, t1.match(t2));
    }

    public Object[][] getExpressionsAreParsedCorrectlyThrough99Problems() {
        return Stream.of(
                Stream.of(
                        "my_last(X,[X])",
                        Struct.of("my_last",
                                   Var.of("X"),
                                   Struct.of(".", Var.of("X"), Struct.emptyList()))
                ),
                Stream.of(
                        "my_last(X,[_|L]) :- my_last(X,L)",
                        Struct.of(":-",
                                   Struct.of("my_last",
                                              Var.of("X"),
                                              Struct.of(".",
                                                         Var.underscore(),
                                                         Var.of("L"))),
                                   Struct.of("my_last",
                                              Var.of("X"),
                                              Var.of("L")))
                ),
                Stream.of(
                        "last_but_one(X,[X,_])",
                        Struct.of("last_but_one",
                                   Var.of("X"),
                                   Struct.list(
                                           Var.of("X"),
                                           Var.underscore()))
                ),
                Stream.of(
                        "last_but_one(X,[_,Y|Ys]) :- last_but_one(X,[Y|Ys])",
                        Struct.of(":-",
                                   Struct.of("last_but_one",
                                              Var.of("X"),
                                              Struct.of(".",
                                                         Var.underscore(),
                                                      Struct.of(".",
                                                              Var.of("Y"),
                                                              Var.of("Ys")))),
                                   Struct.of("last_but_one",
                                              Var.of("X"),
                                              Struct.of(".",
                                                         Var.of("Y"),
                                                         Var.of("Ys"))))
                ),
                Stream.of(
                        "element_at(X,[X|_],1)",
                        Struct.of("element_at",
                                   Var.of("X"),
                                   Struct.of(".", Var.of("X"), Var.underscore()),
                                   Int.of(1))
                ),
                Stream.of(
                        "element_at(X,[_|L],K) :- K > 1, K1 is K - 1, element_at(X,L,K1)",
                        Struct.of(":-",
                                   Struct.of("element_at",
                                              Var.of("X"),
                                              Struct.of(".", Var.underscore(), Var.of("L")),
                                              Var.of("K")),
                                   Struct.of(",",
                                              Struct.of(">",
                                                         Var.of("K"),
                                                         Int.of(1)),
                                              Struct.of(",",
                                                         Struct.of("is",
                                                                    Var.of("K1"),
                                                                    Struct.of("-",
                                                                               Var.of("K"),
                                                                               Int.of(1))),
                                                         Struct.of("element_at",
                                                                    Var.of("X"),
                                                                    Var.of("L"),
                                                                    Var.of("K1")))))
                ),
                Stream.of(
                        "my_length([],0)",
                        Struct.of("my_length",
                                   Struct.emptyList(),
                                   Int.of(0))
                ),
                Stream.of(
                        "my_length([_|L],N) :- my_length(L,N1), N is N1 + 1",
                        Struct.of(":-",
                                   Struct.of("my_length",
                                              Struct.of(".", Var.underscore(), Var.of("L")),
                                              Var.of("N")),
                                   Struct.of(",",
                                              Struct.of("my_length",
                                                         Var.of("L"),
                                                         Var.of("N1")),
                                              Struct.of("is",
                                                         Var.of("N"),
                                                         Struct.of("+",
                                                                    Var.of("N1"),
                                                                    Int.of(1)))))
                ),
                Stream.of(
                        "my_reverse(L1,L2) :- my_rev(L1,L2,[])",
                        Struct.of(":-",
                                   Struct.of("my_reverse",
                                              Var.of("L1"),
                                              Var.of("L2")),
                                   Struct.of("my_rev",
                                              Var.of("L1"),
                                              Var.of("L2"),
                                              Struct.emptyList()))
                ),
                Stream.of(
                        "my_rev([],L2,L2) :- !",
                        Struct.of(":-",
                                   Struct.of("my_rev",
                                              Struct.emptyList(),
                                              Var.of("L2"),
                                              Var.of("L2")),
                                   Struct.atom("!"))
                ),
                Stream.of(
                        "my_rev([X|Xs],L2,Acc) :- my_rev(Xs,L2,[X|Acc])",
                        Struct.of(":-",
                                   Struct.of("my_rev",
                                              Struct.of(".", Var.of("X"), Var.of("Xs")),
                                              Var.of("L2"),
                                              Var.of("Acc")),
                                   Struct.of("my_rev",
                                              Var.of("Xs"),
                                              Var.of("L2"),
                                              Struct.of(".", Var.of("X"), Var.of("Acc"))))
                ),
                Stream.of(
                        "is_palindrome(L) :- reverse(L,L)",
                        Struct.of(":-",
                                   Struct.of("is_palindrome",
                                              Var.of("L")),
                                   Struct.of("reverse",
                                              Var.of("L"),
                                              Var.of("L")))
                ),
                Stream.of(
                        "my_flatten(X,[X]) :- \\+ is_list(X)",
                        Struct.of(":-",
                                   Struct.of("my_flatten",
                                              Var.of("X"),
                                              Struct.list(Var.of("X"))),
                                   Struct.of("\\+",
                                              Struct.of("is_list",
                                                         Var.of("X"))))
                ),
                Stream.of(
                        "my_flatten([],[])",
                        Struct.of("my_flatten",
                                   Struct.emptyList(),
                                   Struct.emptyList())
                ),
                Stream.of(
                        "my_flatten([X|Xs],Zs) :- my_flatten(X,Y), my_flatten(Xs,Ys), append(Y,Ys,Zs)",
                        Struct.of(":-",
                                   Struct.of("my_flatten",
                                              Struct.of(".", Var.of("X"), Var.of("Xs")),
                                              Var.of("Zs")),
                                   Struct.of(",",
                                              Struct.of("my_flatten",
                                                         Var.of("X"),
                                                         Var.of("Y")),
                                              Struct.of(",",
                                                         Struct.of("my_flatten",
                                                                    Var.of("Xs"),
                                                                    Var.of("Ys")),
                                                         Struct.of("append",
                                                                    Var.of("Y"),
                                                                    Var.of("Ys"),
                                                                    Var.of("Zs")))))
                ),
                Stream.of(
                        "compress([X,Y|Ys],[X|Zs]) :- X \\= Y, compress([Y|Ys],Zs)",
                        Struct.of(":-",
                                   Struct.of("compress",
                                           Struct.of(".", Var.of("X"), Struct.of(".", Var.of("Y"), Var.of("Ys"))),
                                              Struct.of(".", Var.of("X"), Var.of("Zs"))),
                                   Struct.of(",",
                                              Struct.of("\\=",
                                                         Var.of("X"),
                                                         Var.of("Y")),
                                              Struct.of("compress",
                                                         Struct.of(".", Var.of("Y"), Var.of("Ys")),
                                                         Var.of("Zs"))))
                ),
                Stream.of(
                        "count(X,[],[],N,[N,X]) :- N > 1",
                        Struct.of(":-",
                                   Struct.of("count",
                                              Var.of("X"),
                                              Struct.emptyList(),
                                              Struct.emptyList(),
                                              Var.of("N"),
                                              Struct.list(Var.of("N"), Var.of("X"))),
                                   Struct.of(">",
                                              Var.of("N"),
                                              Int.of(1)))
                ),
                Stream.of(
                        "count(X,[Y|Ys],[Y|Ys],1,X) :- X \\= Y",
                        Struct.of(":-",
                                   Struct.of("count",
                                              Var.of("X"),
                                              Struct.of(".", Var.of("Y"), Var.of("Ys")),
                                              Struct.of(".", Var.of("Y"), Var.of("Ys")),
                                              Int.of(1),
                                              Var.of("X")),
                                   Struct.of("\\=",
                                              Var.of("X"),
                                              Var.of("Y")))
                ),
                Stream.of(
                        "count(X,[Y|Ys],[Y|Ys],N,[N,X]) :- N > 1, X \\= Y",
                        Struct.of(":-",
                                   Struct.of("count",
                                              Var.of("X"),
                                              Struct.of(".", Var.of("Y"), Var.of("Ys")),
                                              Struct.of(".", Var.of("Y"), Var.of("Ys")),
                                              Var.of("N"),
                                              Struct.list(Var.of("N"), Var.of("X"))),
                                   Struct.of(",",
                                              Struct.of(">",
                                                         Var.of("N"),
                                                         Int.of(1)),
                                              Struct.of("\\=",
                                                         Var.of("X"),
                                                         Var.of("Y"))))
                ),
                Stream.of(
                        "count(X,[X|Xs],Ys,K,T) :- K1 is K + 1, count(X,Xs,Ys,K1,T)",
                        Struct.of(":-",
                                   Struct.of("count",
                                              Var.of("X"),
                                              Struct.of(".", Var.of("X"), Var.of("Xs")),
                                              Var.of("Ys"),
                                              Var.of("K"),
                                              Var.of("T")),
                                   Struct.of(",",
                                              Struct.of("is",
                                                         Var.of("K1"),
                                                         Struct.of("+",
                                                                    Var.of("K"),
                                                                    Int.of(1))),
                                              Struct.of("count",
                                                         Var.of("X"),
                                                         Var.of("Xs"),
                                                         Var.of("Ys"),
                                                         Var.of("K1"),
                                                         Var.of("T"))))
                ),
                Stream.of(
                        "map_upper_bound(XMax, YMax) :- map_size(XSize, YSize), XMax is XSize - 1, YMax is YSize - 1",
                        Struct.of(":-",
                                   Struct.of("map_upper_bound",
                                              Var.of("XMax"),
                                              Var.of("YMax")),
                                   Struct.of(",",
                                              Struct.of("map_size",
                                                         Var.of("XSize"),
                                                         Var.of("YSize")),
                                              Struct.of(",",
                                                         Struct.of("is",
                                                                    Var.of("XMax"),
                                                                    Struct.of("-",
                                                                               Var.of("XSize"),
                                                                               Int.of(1))),
                                                         Struct.of("is",
                                                                    Var.of("YMax"),
                                                                    Struct.of("-",
                                                                               Var.of("YSize"),
                                                                               Int.of(1))))))
                ),
                Stream.of(
                        "in_map(X, Y) :- X >= 0, Y >= 0, map_size(XSize, YSize), X < XSize, Y < YSize",
                        Struct.of(":-",
                                   Struct.of("in_map",
                                              Var.of("X"),
                                              Var.of("Y")),
                                   Struct.of(",",
                                              Struct.of(">=",
                                                         Var.of("X"),
                                                         Int.of(0)),
                                              Struct.of(",",
                                                         Struct.of(">=",
                                                                    Var.of("Y"),
                                                                    Int.of(0)),
                                                         Struct.of(",",
                                                                    Struct.of("map_size",
                                                                               Var.of("XSize"),
                                                                               Var.of("YSize")),
                                                                    Struct.of(",",
                                                                               Struct.of("<",
                                                                                          Var.of("X"),
                                                                                          Var.of("XSize")),
                                                                               Struct.of("<",
                                                                                          Var.of("Y"),
                                                                                          Var.of("YSize")))))))
                ),
                Stream.of(
                        "tile(wall, X, Y) :- \\+ in_map(X, Y)",
                        Struct.of(":-",
                                   Struct.of("tile",
                                              Struct.atom("wall"),
                                              Var.of("X"),
                                              Var.of("Y")),
                                   Struct.of("\\+",
                                              Struct.of("in_map",
                                                         Var.of("X"),
                                                         Var.of("Y"))))
                ),
                Stream.of(
                        "draw_char(X, Y) :- tty_size(_, XSize), X >= XSize, NY is Y + 1, draw_char(0, NY)",
                        Struct.of(":-",
                                   Struct.of("draw_char",
                                              Var.of("X"),
                                              Var.of("Y")),
                                   Struct.of(",",
                                              Struct.of("tty_size",
                                                         Var.underscore(),
                                                         Var.of("XSize")),
                                              Struct.of(",",
                                                         Struct.of(">=",
                                                                    Var.of("X"),
                                                                    Var.of("XSize")),
                                                         Struct.of(",",
                                                                    Struct.of("is",
                                                                               Var.of("NY"),
                                                                               Struct.of("+",
                                                                                          Var.of("Y"),
                                                                                          Int.of(1))),
                                                                    Struct.of("draw_char",
                                                                               Int.of(0),
                                                                               Var.of("NY"))))))
                ),
                Stream.of(
                        "Y < YMsgs -> write(' ') ; display_offset(XOff, YOff), XMap is X + XOff, YMap is Y + YOff, get_character(XMap, YMap, C), format('~s', [C])",
                        Struct.of(";",
                                   Struct.of("->",
                                              Struct.of("<",
                                                         Var.of("Y"),
                                                         Var.of("YMsgs")),
                                              Struct.of("write",
                                                         Struct.atom(" "))),
                                   Struct.of(",",
                                              Struct.of("display_offset",
                                                         Var.of("XOff"),
                                                         Var.of("YOff")),
                                              Struct.of(",",
                                                         Struct.of("is",
                                                                    Var.of("XMap"),
                                                                    Struct.of("+",
                                                                               Var.of("X"),
                                                                               Var.of("XOff"))),
                                                         Struct.of(",",
                                                                    Struct.of("is",
                                                                               Var.of("YMap"),
                                                                               Struct.of("+",
                                                                                          Var.of("Y"),
                                                                                          Var.of("YOff"))),
                                                                    Struct.of(",",
                                                                               Struct.of("get_character",
                                                                                          Var.of("XMap"),
                                                                                          Var.of("YMap"),
                                                                                          Var.of("C")),
                                                                               Struct.of("format",
                                                                                          Struct.atom("~s"),
                                                                                          Struct.list(Var.of("C"))))))))
                ),
                Stream.of(
                        "display_offset(X, Y) :- player(XPos, YPos), tty_size(YSize, XSize), message_lines(YMsgs), X is XPos - floor(XSize / 2), Y is YPos - floor((YSize - YMsgs) / 2)",
                        Struct.of(":-",
                                   Struct.of("display_offset",
                                              Var.of("X"),
                                              Var.of("Y")),
                                   Struct.of(",",
                                              Struct.of("player",
                                                         Var.of("XPos"),
                                                         Var.of("YPos")),
                                              Struct.of(",",
                                                         Struct.of("tty_size",
                                                                    Var.of("YSize"),
                                                                    Var.of("XSize")),
                                                         Struct.of(",",
                                                                    Struct.of("message_lines",
                                                                               Var.of("YMsgs")),
                                                                    Struct.of(",",
                                                                               Struct.of("is",
                                                                                          Var.of("X"),
                                                                                          Struct.of("-",
                                                                                                     Var.of("XPos"),
                                                                                                     Struct.of("floor",
                                                                                                                Struct.of("/",
                                                                                                                           Var.of("XSize"),
                                                                                                                           Int.of(2))))),
                                                                               Struct.of("is",
                                                                                          Var.of("Y"),
                                                                                          Struct.of("-",
                                                                                                     Var.of("YPos"),
                                                                                                     Struct.of("floor",
                                                                                                                Struct.of("/",
                                                                                                                           Struct.of("-",
                                                                                                                                      Var.of("YSize"),
                                                                                                                                      Var.of("YMsgs")),
                                                                                                                           Int.of(2))))))))))
                )
        ).map(s -> s.toArray(Object[]::new))
         .toArray(Object[][]::new);
    }
}

