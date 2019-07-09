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
                asList("a", Struct.atom("a")),
                asList("a(1)", Struct.of("a", Int.of(1))),
                asList("a(1, b(2), c(d(3), e))", Struct.of("a", Int.of(1), Struct.of("b", Int.of(2)), Struct.of("c", Struct.of("d", Int.of(3)), Struct.atom("e")))),
                asList("X", Var.of("X")),
                asList("_", Var.underscore()),
                asList("[]", Struct.emptyList()),
                asList("[1]", Struct.cons(Int.of(1), Struct.emptyList())),
                asList("[2]", Struct.list(Int.of(2))),
                asList("[3]", Struct.list(Int.of(3))),
                asList("[4]", Struct.cons(Int.of(4), Struct.emptyList())),
                asList("[1, a, 2, b]", Struct.list(Int.of(1), Struct.atom("a"), Int.of(2), Struct.atom("b"))),
                asList("[H | T]", Struct.cons(Var.of("H"), Var.of("T"))),
                asList("[H | T]", Struct.cons(Var.of("H"), Var.of("T"))),
                asList("1", Int.of(1))
        );
    }

    public List<List<Object>> getSpaceSensibleTerms() {
        return asList(
                asList(java.lang.Long.valueOf(java.lang.Long.MAX_VALUE).toString(), Long.of(java.lang.Long.MAX_VALUE)),
                asList("1.1", Double.of(1.1)),
                asList("true", Struct.atom("true")),
                asList("fail", Struct.atom("fail"))
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
