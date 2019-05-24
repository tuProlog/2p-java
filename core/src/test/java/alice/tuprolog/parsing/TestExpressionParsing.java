package alice.tuprolog.parsing;

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
        om.opNew("++", "yf", 100);
        om.opNew("--", "yf", 100);
        om.opNew("fails", "xf", 50);
        om.opNew("succeeds", "xf", 50);
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
                Term expected = new Struct(op.trim(), parseTerm(arguments.get(0)), parseTerm(arguments.get(1)));
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

            Term expected = new Struct(op.trim(), parseTerm(arguments.get(0)), parseTerm(arguments.get(1)));
            for (int i = 2; i < arguments.size(); i++) {
                expected = new Struct(op.trim(), expected, parseTerm(arguments.get(i)));
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

        Term expected = new Struct(operator, parseTerm(argument));

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

        Term expected = new Struct(operator, parseTerm(argument));

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

        return fxOps.stream().flatMap(op ->
                                              arguments.stream().map(arg -> new Object[]{op, arg})
        ).toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getPrefixAssociativeOperatorsParams")
    public void testPrefixAssociativeOperators(String operator, String argument) {

        for (int i = 1; i <= 3; i++) {
            final String toBeParsed = IntStream.range(0, i)
                                               .mapToObj(j -> operator)
                                               .collect(Collectors.joining(" ", "", " " + argument));


            Term expected = new Struct(operator, parseTerm(argument));
            for (int j = 1; j < i; j++) {
                expected = new Struct(operator, expected);
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


            Term expected = new Struct(operator, parseTerm(argument));
            for (int j = 1; j < i; j++) {
                expected = new Struct(operator, expected);
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
            Term expected = new Struct(op.trim(), parseTerm(arguments.get(i - 1)), parseTerm(arguments.get(i)));
            for (i -= 2; i >= 0; i--) {
                expected = new Struct(op.trim(), parseTerm(arguments.get(i)), expected);
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
                Arrays.asList("1", "2"),
                Arrays.asList("1.9", "2.8"),
                Arrays.asList("a", "b"),
                Arrays.asList("A", "B"),
                Arrays.asList("f(x)", "g(y)"),
                Arrays.asList("(1, 2)", "(3, 4)"),
                Arrays.asList("1", "2", "3"),
                Arrays.asList("1.9", "2.8", "3.7"),
                Arrays.asList("a", "b", "c"),
                Arrays.asList("A", "B", "C"),
                Arrays.asList("f(x)", "g(y)", "h(z)"),
                Arrays.asList("(1, 2)", "(3, 4)", "(5, 6)")
        );
    }

    public Object[][] getInfixRightAssociativeOperatorsParams() {
        final java.util.List<String> xfyOps = Arrays.asList(";", "->", ",", "^");
        final java.util.List<java.util.List<String>> arguments = getTermsSequences();

        return xfyOps.stream().flatMap(op ->
                                               arguments.stream().map(args -> new Object[]{op, args})
        ).toArray(Object[][]::new);
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
                        new Struct(":-",
                                   new Struct("a"),
                                   new Int(1))
                ),
                Stream.of(
                        "a; B :- 1",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Var("B")),
                                   new Int(1))
                ),
                Stream.of(
                        "a :- 1; '2'",
                        new Struct(":-",
                                   new Struct("a"),
                                   new Struct(";",
                                              new Int(1),
                                              new Struct("2")))
                ),
                Stream.of(
                        "a; B :- 1; '2'",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Var("B")),
                                   new Struct(";",
                                              new Int(1),
                                              new Struct("2")))
                ),
                Stream.of(
                        "a; B :- 1, 3.1; '2'",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Var("B")),
                                   new Struct(";",
                                              new Struct(",",
                                                         new Int(1),
                                                         new Double(3.1)),
                                              new Struct("2")))
                ),
                Stream.of(
                        "a; B :- 1; '2', 3.1",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Var("B")),
                                   new Struct(";",
                                              new Int(1),
                                              new Struct(",",
                                                         new Struct("2"),
                                                         new Double(3.1))))
                ),
                Stream.of(
                        "a, c(D); B :- 1, 3.1; '2'",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct(",",
                                                         new Struct("a"),
                                                         new Struct("c", new Var("D"))),
                                              new Var("B")),
                                   new Struct(";",
                                              new Struct(",",
                                                         new Int(1),
                                                         new Double(3.1)),
                                              new Struct("2")))
                ),
                Stream.of(
                        "a; B, c(D) :- 1, 3.1; '2'",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Struct(",",
                                                         new Var("B"),
                                                         new Struct("c", new Var("D")))),
                                   new Struct(";",
                                              new Struct(",",
                                                         new Int(1),
                                                         new Double(3.1)),
                                              new Struct("2")))
                ),
                Stream.of(
                        "a, c(D); B :- 1; '2', 3.1",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct(",",
                                                         new Struct("a"),
                                                         new Struct("c", new Var("D"))),
                                              new Var("B")),
                                   new Struct(";",
                                              new Int(1),
                                              new Struct(",",
                                                         new Struct("2"),
                                                         new Double(3.1))))
                ),
                Stream.of(
                        "a; B, c(D) :- 1; '2', 3.1",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Struct(",",
                                                         new Var("B"),
                                                         new Struct("c", new Var("D")))),
                                   new Struct(";",
                                              new Int(1),
                                              new Struct(",",
                                                         new Struct("2"),
                                                         new Double(3.1))))
                ),
                Stream.of(
                        "a; B, c(D) :- 1, \"4\"; '2', 3.1",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Struct(",",
                                                         new Var("B"),
                                                         new Struct("c", new Var("D")))),
                                   new Struct(";",
                                              new Struct(",",
                                                         new Int(1),
                                                         new Struct("4")),
                                              new Struct(",",
                                                         new Struct("2"),
                                                         new Double(3.1))))
                ),
                Stream.of(
                        "a, c(D); B, e(_f, [g]) :- 1; '2', 3.1",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct(",",
                                                         new Struct("a"),
                                                         new Struct("c", new Var("D"))),
                                              new Struct(",",
                                                         new Var("B"),
                                                         new Struct("e",
                                                                    new Var("_f"),
                                                                    new Struct(".",
                                                                               new Struct("g"),
                                                                               new Struct())))),
                                   new Struct(";",
                                              new Int(1),
                                              new Struct(",",
                                                         new Struct("2"),
                                                         new Double(3.1))))
                ),
                Stream.of(
                        "a; b; B, c(D) :- 1, \"4\", 5; '2', 3.1, 6.7",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Struct(";",
                                                         new Struct("b"),
                                                         new Struct(",",
                                                                    new Var("B"),
                                                                    new Struct("c",
                                                                               new Var("D"))))),
                                   new Struct(";",
                                              new Struct(",",
                                                         new Int(1),
                                                         new Struct(",",
                                                                    new Struct("4"),
                                                                    new Int(5))),
                                              new Struct(",",
                                                         new Struct("2"),
                                                         new Struct(",",
                                                                    new Double(3.1),
                                                                    new Double(6.7)))))
                ),
                Stream.of(
                        "a; b; B, c(D) :- 1, \"4\"; '2', 3.1",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("a"),
                                              new Struct(";",
                                                         new Struct("b"),
                                                         new Struct(",",
                                                                    new Var("B"),
                                                                    new Struct("c",
                                                                               new Var("D"))))),
                                   new Struct(";",
                                              new Struct(",",
                                                         new Int(1),
                                                         new Struct("4")),
                                              new Struct(",",
                                                         new Struct("2"),
                                                         new Double(3.1))))
                ),
                Stream.of(
                        "b, 1 -> 2; 3,4 :- a",
                        new Struct(":-",
                                   new Struct(";",
                                              new Struct("->",
                                                         new Struct(",",
                                                                    new Struct("b"),
                                                                    new Int(1)),
                                                         new Int(2)),
                                              new Struct(",",
                                                         new Int(3),
                                                         new Int(4))),
                                   new Struct("a"))
                )
        ).map(s -> s.toArray(Object[]::new))
                     .toArray(Object[][]::new);
    }

    @Test
    @Parameters(method = "getExpressionsAreParsedCorrectlyThrough99Problems")
    public void testExpressionsAreParsedCorrectlyThrough99Problems(String toBeParsed, Term expected) {
        System.out.printf("Parsing\n\t\t%s\n\tequals\n\t\t%s\n\t?", toBeParsed, expected);
        Assert.assertEquals(expected, parseTerm(toBeParsed));
        System.out.println(" yes.");
    }

    public Object[][] getExpressionsAreParsedCorrectlyThrough99Problems() {
        return Stream.of(
                Stream.of(
                        "my_last(X,[X])",
                        new Struct("my_last",
                                   new Var("X"),
                                   new Struct(".", new Var("X"), new Struct()))
                ),
                Stream.of(
                        "my_last(X,[_|L]) :- my_last(X,L)",
                        new Struct(":-",
                                   new Struct("my_last",
                                              new Var("X"),
                                              new Struct(".",
                                                         new Var(),
                                                         new Var("L"))),
                                   new Struct("my_last",
                                              new Var("X"),
                                              new Var("L")))
                ),
                Stream.of(
                        "last_but_one(X,[X,_])",
                        new Struct("last_but_one",
                                   new Var("X"),
                                   new Struct(
                                           new Var("X"),
                                           new Var()))
                ),
                Stream.of(
                        "last_but_one(X,[_,Y|Ys]) :- last_but_one(X,[Y|Ys])",
                        new Struct(":-",
                                   new Struct("last_but_one",
                                              new Var("X"),
                                              new Struct(".",
                                                         new Var(),
                                                         new Var("Y"),
                                                         new Var("Ys"))),
                                   new Struct("last_but_one",
                                              new Var("X"),
                                              new Struct(".",
                                                         new Var("Y"),
                                                         new Var("Ys"))))
                ),
                Stream.of(
                        "element_at(X,[X|_],1)",
                        new Struct("element_at",
                                   new Var("X"),
                                   new Struct(".", new Var("X"), new Var()),
                                   new Int(1))
                ),
                Stream.of(
                        "element_at(X,[_|L],K) :- K > 1, K1 is K - 1, element_at(X,L,K1)",
                        new Struct(":-",
                                   new Struct("element_at",
                                              new Var("X"),
                                              new Struct(".", new Var(), new Var("L")),
                                              new Var("K")),
                                   new Struct(",",
                                              new Struct(">",
                                                         new Var("K"),
                                                         new Int(1)),
                                              new Struct(",",
                                                         new Struct("is",
                                                                    new Var("K1"),
                                                                    new Struct("-",
                                                                               new Var("K"),
                                                                               new Int(1))),
                                                         new Struct("element_at",
                                                                    new Var("X"),
                                                                    new Var("L"),
                                                                    new Var("K1")))))
                ),
                Stream.of(
                        "my_length([],0)",
                        new Struct("my_length",
                                   new Struct(),
                                   new Int(0))
                ),
                Stream.of(
                        "my_length([_|L],N) :- my_length(L,N1), N is N1 + 1",
                        new Struct(":-",
                                   new Struct("my_length",
                                              new Struct(".", new Var(), new Var("L")),
                                              new Var("N")),
                                   new Struct(",",
                                              new Struct("my_length",
                                                         new Var("L"),
                                                         new Var("N1")),
                                              new Struct("is",
                                                         new Var("N"),
                                                         new Struct("+",
                                                                    new Var("N1"),
                                                                    new Int(1)))))
                ),
                Stream.of(
                        "my_reverse(L1,L2) :- my_rev(L1,L2,[])",
                        new Struct(":-",
                                   new Struct("my_reverse",
                                              new Var("L1"),
                                              new Var("L2")),
                                   new Struct("my_rev",
                                              new Var("L1"),
                                              new Var("L2"),
                                              new Struct()))
                ),
                Stream.of(
                        "my_rev([],L2,L2) :- !",
                        new Struct(":-",
                                   new Struct("my_rev",
                                              new Struct(),
                                              new Var("L2"),
                                              new Var("L2")),
                                   new Struct("!"))
                ),
                Stream.of(
                        "my_rev([X|Xs],L2,Acc) :- my_rev(Xs,L2,[X|Acc])",
                        new Struct(":-",
                                   new Struct("my_rev",
                                              new Struct(".", new Var("X"), new Var("Xs")),
                                              new Var("L2"),
                                              new Var("Acc")),
                                   new Struct("my_rev",
                                              new Var("Xs"),
                                              new Var("L2"),
                                              new Struct(".", new Var("X"), new Var("Acc"))))
                ),
                Stream.of(
                        "is_palindrome(L) :- reverse(L,L)",
                        new Struct(":-",
                                   new Struct("is_palindrome",
                                              new Var("L")),
                                   new Struct("reverse",
                                              new Var("L"),
                                              new Var("L")))
                ),
                Stream.of(
                        "my_flatten(X,[X]) :- \\+ is_list(X)",
                        new Struct(":-",
                                   new Struct("my_flatten",
                                              new Var("X"),
                                              new Struct(new Var("X"))),
                                   new Struct("\\+",
                                              new Struct("is_list",
                                                         new Var("X"))))
                ),
                Stream.of(
                        "my_flatten([],[])",
                        new Struct("my_flatten",
                                   new Struct(),
                                   new Struct())
                ),
                Stream.of(
                        "my_flatten([X|Xs],Zs) :- my_flatten(X,Y), my_flatten(Xs,Ys), append(Y,Ys,Zs)",
                        new Struct(":-",
                                   new Struct("my_flatten",
                                              new Struct(".", new Var("X"), new Var("Xs")),
                                              new Var("Zs")),
                                   new Struct(",",
                                              new Struct("my_flatten",
                                                         new Var("X"),
                                                         new Var("Y")),
                                              new Struct(",",
                                                         new Struct("my_flatten",
                                                                    new Var("Xs"),
                                                                    new Var("Ys")),
                                                         new Struct("append",
                                                                    new Var("Y"),
                                                                    new Var("Ys"),
                                                                    new Var("Zs")))))
                ),
                Stream.of(
                        "compress([X,Y|Ys],[X|Zs]) :- X \\= Y, compress([Y|Ys],Zs)",
                        new Struct(":-",
                                   new Struct("compress",
                                              new Struct(".", new Var("X"), new Var("Y"), new Var("Ys")),
                                              new Struct(".", new Var("X"), new Var("Zs"))),
                                   new Struct(",",
                                              new Struct("\\=",
                                                         new Var("X"),
                                                         new Var("Y")),
                                              new Struct("compress",
                                                         new Struct(".", new Var("Y"), new Var("Ys")),
                                                         new Var("Zs"))))
                ),
                Stream.of(
                        "count(X,[],[],N,[N,X]) :- N > 1",
                        new Struct(":-",
                                   new Struct("count",
                                              new Var("X"),
                                              new Struct(),
                                              new Struct(),
                                              new Var("N"),
                                              new Struct(new Var("N"), new Var("X"))),
                                   new Struct(">",
                                              new Var("N"),
                                              new Int(1)))
                ),
                Stream.of(
                        "count(X,[Y|Ys],[Y|Ys],1,X) :- X \\= Y",
                        new Struct(":-",
                                   new Struct("count",
                                              new Var("X"),
                                              new Struct(".", new Var("Y"), new Var("Ys")),
                                              new Struct(".", new Var("Y"), new Var("Ys")),
                                              new Int(1),
                                              new Var("X")),
                                   new Struct("\\=",
                                              new Var("X"),
                                              new Var("Y")))
                ),
                Stream.of(
                        "count(X,[Y|Ys],[Y|Ys],N,[N,X]) :- N > 1, X \\= Y",
                        new Struct(":-",
                                   new Struct("count",
                                              new Var("X"),
                                              new Struct(".", new Var("Y"), new Var("Ys")),
                                              new Struct(".", new Var("Y"), new Var("Ys")),
                                              new Var("N"),
                                              new Struct(new Var("N"), new Var("X"))),
                                   new Struct(",",
                                              new Struct(">",
                                                         new Var("N"),
                                                         new Int(1)),
                                              new Struct("\\=",
                                                         new Var("X"),
                                                         new Var("Y"))))
                ),
                Stream.of(
                        "count(X,[X|Xs],Ys,K,T) :- K1 is K + 1, count(X,Xs,Ys,K1,T)",
                        new Struct(":-",
                                   new Struct("count",
                                              new Var("X"),
                                              new Struct(".", new Var("X"), new Var("Xs")),
                                              new Var("Ys"),
                                              new Var("K"),
                                              new Var("T")),
                                   new Struct(",",
                                              new Struct("is",
                                                         new Var("K1"),
                                                         new Struct("+",
                                                                    new Var("K"),
                                                                    new Int(1))),
                                              new Struct("count",
                                                         new Var("X"),
                                                         new Var("Xs"),
                                                         new Var("Ys"),
                                                         new Var("K1"),
                                                         new Var("T"))))
                ),
                Stream.of(
                        "map_upper_bound(XMax, YMax) :- map_size(XSize, YSize), XMax is XSize - 1, YMax is YSize - 1",
                        new Struct(":-",
                                   new Struct("map_upper_bound",
                                              new Var("XMax"),
                                              new Var("YMax")),
                                   new Struct(",",
                                              new Struct("map_size",
                                                         new Var("XSize"),
                                                         new Var("YSize")),
                                              new Struct(",",
                                                         new Struct("is",
                                                                    new Var("XMax"),
                                                                    new Struct("-",
                                                                               new Var("XSize"),
                                                                               new Int(1))),
                                                         new Struct("is",
                                                                    new Var("YMax"),
                                                                    new Struct("-",
                                                                               new Var("YSize"),
                                                                               new Int(1))))))
                ),
                Stream.of(
                        "in_map(X, Y) :- X >= 0, Y >= 0, map_size(XSize, YSize), X < XSize, Y < YSize",
                        new Struct(":-",
                                   new Struct("in_map",
                                              new Var("X"),
                                              new Var("Y")),
                                   new Struct(",",
                                              new Struct(">=",
                                                         new Var("X"),
                                                         new Int(0)),
                                              new Struct(",",
                                                         new Struct(">=",
                                                                    new Var("Y"),
                                                                    new Int(0)),
                                                         new Struct(",",
                                                                    new Struct("map_size",
                                                                               new Var("XSize"),
                                                                               new Var("YSize")),
                                                                    new Struct(",",
                                                                               new Struct("<",
                                                                                          new Var("X"),
                                                                                          new Var("XSize")),
                                                                               new Struct("<",
                                                                                          new Var("Y"),
                                                                                          new Var("YSize")))))))
                ),
                Stream.of(
                        "tile(wall, X, Y) :- \\+ in_map(X, Y)",
                        new Struct(":-",
                                   new Struct("tile",
                                              new Struct("wall"),
                                              new Var("X"),
                                              new Var("Y")),
                                   new Struct("\\+",
                                              new Struct("in_map",
                                                         new Var("X"),
                                                         new Var("Y"))))
                ),
                Stream.of(
                        "draw_char(X, Y) :- tty_size(_, XSize), X >= XSize, NY is Y + 1, draw_char(0, NY)",
                        new Struct(":-",
                                   new Struct("draw_char",
                                              new Var("X"),
                                              new Var("Y")),
                                   new Struct(",",
                                              new Struct("tty_size",
                                                         new Var(),
                                                         new Var("XSize")),
                                              new Struct(",",
                                                         new Struct(">=",
                                                                    new Var("X"),
                                                                    new Var("XSize")),
                                                         new Struct(",",
                                                                    new Struct("is",
                                                                               new Var("NY"),
                                                                               new Struct("+",
                                                                                          new Var("Y"),
                                                                                          new Int(1))),
                                                                    new Struct("draw_char",
                                                                               new Int(0),
                                                                               new Var("NY"))))))
                ),
                Stream.of(
                        "Y < YMsgs -> write(' ') ; display_offset(XOff, YOff), XMap is X + XOff, YMap is Y + YOff, get_character(XMap, YMap, C), format('~s', [C])",
                        new Struct(";",
                                   new Struct("->",
                                              new Struct("<",
                                                         new Var("Y"),
                                                         new Var("YMsgs")),
                                              new Struct("write",
                                                         new Struct(" "))),
                                   new Struct(",",
                                              new Struct("display_offset",
                                                         new Var("XOff"),
                                                         new Var("YOff")),
                                              new Struct(",",
                                                         new Struct("is",
                                                                    new Var("XMap"),
                                                                    new Struct("+",
                                                                               new Var("X"),
                                                                               new Var("XOff"))),
                                                         new Struct(",",
                                                                    new Struct("is",
                                                                               new Var("YMap"),
                                                                               new Struct("+",
                                                                                          new Var("Y"),
                                                                                          new Var("YOff"))),
                                                                    new Struct(",",
                                                                               new Struct("get_character",
                                                                                          new Var("XMap"),
                                                                                          new Var("YMap"),
                                                                                          new Var("C")),
                                                                               new Struct("format",
                                                                                          new Struct("~s"),
                                                                                          new Struct(new Var("C"))))))))
                ),
                Stream.of(
                        "display_offset(X, Y) :- player(XPos, YPos), tty_size(YSize, XSize), message_lines(YMsgs), X is XPos - floor(XSize / 2), Y is YPos - floor((YSize - YMsgs) / 2)",
                        new Struct(":-",
                                   new Struct("display_offset",
                                              new Var("X"),
                                              new Var("Y")),
                                   new Struct(",",
                                              new Struct("player",
                                                         new Var("XPos"),
                                                         new Var("YPos")),
                                              new Struct(",",
                                                         new Struct("tty_size",
                                                                    new Var("YSize"),
                                                                    new Var("XSize")),
                                                         new Struct(",",
                                                                    new Struct("message_lines",
                                                                               new Var("YMsgs")),
                                                                    new Struct(",",
                                                                               new Struct("is",
                                                                                          new Var("X"),
                                                                                          new Struct("-",
                                                                                                     new Var("XPos"),
                                                                                                     new Struct("floor",
                                                                                                                new Struct("/",
                                                                                                                           new Var("XSize"),
                                                                                                                           new Int(2))))),
                                                                               new Struct("is",
                                                                                          new Var("Y"),
                                                                                          new Struct("-",
                                                                                                     new Var("YPos"),
                                                                                                     new Struct("floor",
                                                                                                                new Struct("/",
                                                                                                                           new Struct("-",
                                                                                                                                      new Var("YSize"),
                                                                                                                                      new Var("YMsgs")),
                                                                                                                           new Int(2))))))))))
                )
        ).map(s -> s.toArray(Object[]::new))
                     .toArray(Object[][]::new);
    }
}

