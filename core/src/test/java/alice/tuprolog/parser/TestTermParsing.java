package alice.tuprolog.parser;

import alice.tuprolog.Double;
import alice.tuprolog.Long;
import alice.tuprolog.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@RunWith(JUnitParamsRunner.class)
public class TestTermParsing extends BaseTestPrologParsing {

    public Object[][] getTerms() {
        return Stream.concat(
                getSpaceInvariantTerms().stream().map(s -> Stream.concat(Stream.of(true), s.stream())),
                getSpaceSensibleTerms().stream().map(s -> Stream.concat(Stream.of(false), s.stream()))
        ).map(s -> s.toArray(Object[]::new)).toArray(Object[][]::new);

    }

    public List<List<Object>> getSpaceInvariantTerms() {
        return asList(
                asList("a", new Struct("a")),
                asList("a(1)", new Struct("a", new Int(1))),
                asList("a(1, b(2), c(d(3), e))", new Struct("a", new Int(1), new Struct("b", new Int(2)), new Struct("c", new Struct("d", new Int(3)), new Struct("e")))),
                asList("X", new Var("X")),
                asList("_", new Var()),
                asList("[]", new Struct()),
                asList("[1]", new Struct(new Int(1), new Struct())),
                asList("[2]", new Struct(new Int(2))),
                asList("[3]", new Struct(new Int(3))),
                asList("[4]", new Struct(new Int(4), new Struct())),
                asList("[1, a, 2, b]", new Struct(new Int(1), new Struct("a"), new Int(2), new Struct("b"))),
                asList("[H | T]", new Struct(new Var("H"), new Var("T"))),
                asList("[H | T]", new Struct(new Var("H"), new Var("T"))),
                asList("1", new Int(1))
        );
    }

    public List<List<Object>> getSpaceSensibleTerms() {
        return asList(
                asList(java.lang.Long.valueOf(java.lang.Long.MAX_VALUE).toString(), new Long(java.lang.Long.MAX_VALUE)),
                asList("1.1", new Double(1.1)),
                asList("true", new Struct("true")),
                asList("fail", new Struct("fail"))
        );
    }

    @Test
    @Parameters(method = "getTerms")
    public void testTermIsParsedCorrectly(boolean testWithSpaces, String toBeParsed, Term expected) {
        System.out.printf("Parsing %s equals %s?", toBeParsed, expected);
        assertEquals(expected, parseTerm(toBeParsed));
        System.out.println(" yes.");

        final String withoutSpaces = toBeParsed.replace("\\s+", "");
        System.out.printf("Parsing %s equals %s?", withoutSpaces, expected);
        assertEquals(expected, parseTerm(withoutSpaces));
        System.out.println(" yes.");

        if (testWithSpaces) {
            final String withSpaces = IntStream.range(0, toBeParsed.length())
                                               .mapToObj(i -> toBeParsed.charAt(i) + "")
                                               .filter(it -> !it.isEmpty())
                                               .collect(Collectors.joining("    ", "    ", "    "));

            System.out.printf("Parsing %s equals %s?", withSpaces, expected);
            assertEquals(expected, parseTerm(withSpaces));
            System.out.println(" yes.");
        }
    }
}
