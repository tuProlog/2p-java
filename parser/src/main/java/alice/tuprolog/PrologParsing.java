package alice.tuprolog;

import alice.tuprolog.dynamic.Associativity;
import it.unibo.tuprolog2.Term;
import org.antlr.v4.runtime.*;
import org.javatuples.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import static alice.tuprolog.dynamic.Associativity.*;

public class PrologParsing {
    private static final Map<List<String>, Pair<Integer, Associativity>> STANDARD_OPERATORS =
            Collections.unmodifiableMap(getStandardOperatorsImpl());

    public static void main(String[] args) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String prompt = "term > ";
        System.out.print(prompt);

        StringBuilder buffer = new StringBuilder();

        while (true) {
            try {
                final String line = reader.readLine();
                if (line.length() == 0) {
                    final PrologParser parser = createParser(buffer.toString());
                    parser.addErrorListener(newErrorListener(line));
                    final PrologParser.SingletonExpressionContext expression = parser.singletonExpression();
                    System.out.println("inline > " + String.join(" ", buffer.toString().split("\\s+")));
                    System.out.println("ast > " + expression.toStringTree(parser));
                    Term term = expression.accept(TermVisitor.get());
                    System.out.println("prolog > " + term);
                    System.out.println("java > " + term.toJava(false));
//                    expression.inspect(parser).get();
                    buffer = new StringBuilder();
                    prompt = "term > ";
                } else {
                    buffer.append(line).append(" ");
                    prompt = " ... > ";
                }
            } catch (Exception e) {
                e.printStackTrace();
                buffer = new StringBuilder();
                prompt = "term > ";
            } finally {
                System.out.print(prompt);
            }
        }

    }

    private static Map<List<String>, Pair<Integer, Associativity>> getDefaultOperatorsImpl() {
        final Map<List<String>, Pair<Integer, Associativity>> standard = getStandardOperatorsImpl();
        standard.put(
                Collections.unmodifiableList(Arrays.asList("++", "--")),
                Pair.with(200, YF)
        );

        return standard;
    }

    private static Map<List<String>, Pair<Integer, Associativity>> getStandardOperatorsImpl() {
        final Map<List<String>, Pair<Integer, Associativity>> result = new HashMap<>();
        result.put(
                Collections.unmodifiableList(Arrays.asList(":-")),
                Pair.with(1200, XFX)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList(":-", "?-")),
                Pair.with(1200, FX)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList(";")),
                Pair.with(1100, XFY)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("->")),
                Pair.with(1050, XFY)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList(",")),
                Pair.with(1000, XFY)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("\\+", "not")),
                Pair.with(900, FY)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("<", "=", "=..", "=@=", "=:=", "=<", "==", "=\\=", ">", ">=", "@<", "@=<", "@>", "@>=", "\\=", "\\==", "is")),
                Pair.with(700, XFX)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("+", "-", "/\\", "\\/")),
                Pair.with(500, YFX)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("*", "/", "//", "<<", ">>")),
                Pair.with(400, YFX)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("**")),
                Pair.with(200, XFX)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("^")),
                Pair.with(200, XFY)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("\\")),
                Pair.with(200, FX)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("-", "+")),
                Pair.with(200, FY)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("--", "++")),
                Pair.with(100, YF)
        );
        result.put(
                Collections.unmodifiableList(Arrays.asList("fails", "succeeds")),
                Pair.with(50, XF)
        );
        return result;
    }

    private static ANTLRErrorListener newErrorListener(String whileParsing) {
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new ParsingException(whileParsing,
                                           offendingSymbol instanceof Token ? ((Token) offendingSymbol).getText()
                                                                            : offendingSymbol.toString(), line, charPositionInLine, msg, e);
            }
        };
    }

    public static Map<List<String>, Pair<Integer, Associativity>> getStandardOperators() {
        return STANDARD_OPERATORS;
    }

    public static PrologParser.SingletonExpressionContext parseCanonicalExpression(String string) {
        final PrologParser parser = createCanonicalParser(string);
        parser.addErrorListener(newErrorListener(string));
        return parser.singletonExpression();
    }

    public static PrologParser.SingletonExpressionContext parseExpression(String string) {
        return parseExpression(string, getStandardOperators());
    }

    public static PrologParser.SingletonExpressionContext parseExpression(String string, Map<List<String>, Pair<Integer, Associativity>> withOperators) {
        final PrologParser parser = createParser(string, withOperators);
        parser.addErrorListener(newErrorListener(string));
        return parser.singletonExpression();
    }

    public static PrologParser.SingletonTermContext parseCanonicalTerm(String string) {
        final PrologParser parser = createCanonicalParser(string);
        parser.addErrorListener(newErrorListener(string));
        return parser.singletonTerm();
    }

    public static PrologParser.SingletonTermContext parseTerm(String string) {
        return parseTerm(string, getStandardOperators());
    }

    public static PrologParser.SingletonTermContext parseTerm(String string, Map<List<String>, Pair<Integer, Associativity>> withOperators) {
        final PrologParser parser = createParser(string, withOperators);
        parser.addErrorListener(newErrorListener(string));
        return parser.singletonTerm();
    }

    public static PrologParser createCanonicalParser(String string) {
        final CharStream stream = new ANTLRInputStream(string);
        final PrologLexer lexer = new PrologLexer(stream);
        lexer.removeErrorListeners();
        final TokenStream tokenStream = new BufferedTokenStream(lexer);
        final PrologParser parser = new PrologParser(tokenStream);
        parser.removeErrorListeners();
        return parser;
    }

    public static PrologParser createParser(String string, Map<List<String>, Pair<Integer, Associativity>> operators) {
        return addOperators(createCanonicalParser(string), operators);
    }

    public static PrologParser createParser(String string) {
        return createParser(string, getStandardOperators());
    }

    public static PrologParser addOperators(PrologParser prologParser, Map<List<String>, Pair<Integer, Associativity>> ops) {
        ops.entrySet().forEach(kv -> {
            kv.getKey().forEach(k -> {
                prologParser.addOperator(k, kv.getValue().getValue1(), kv.getValue().getValue0());
            });
        });
        return prologParser;
    }
}
