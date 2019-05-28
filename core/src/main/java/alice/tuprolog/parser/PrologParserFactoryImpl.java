package alice.tuprolog.parser;


import alice.tuprolog.Operator;
import alice.tuprolog.OperatorManager;
import alice.tuprolog.parser.PrologParser.ClauseContext;
import alice.tuprolog.parser.PrologParser.OptClauseContext;
import alice.tuprolog.parser.PrologParser.SingletonExpressionContext;
import alice.tuprolog.parser.PrologParser.SingletonTermContext;
import alice.util.ExceptionalFunction;
import com.codepoetics.protonpack.StreamUtils;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class PrologParserFactoryImpl implements PrologParserFactory {

    public static PrologParserFactoryImpl INSTANCE = new PrologParserFactoryImpl();

    private static ANTLRErrorListener newErrorListener(Object whileParsing) {
        return new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new ParsingException(
                        whileParsing,
                        offendingSymbol instanceof Token
                            ? PrologLexer.VOCABULARY.getSymbolicName(((Token) offendingSymbol).getType())
                            : offendingSymbol.toString(),
                        line,
                        charPositionInLine + 1,
                        msg,
                        e);
            }
        };

    }

    private PrologParserFactoryImpl() {

    }

    @Override
    public SingletonExpressionContext parseExpression(String string) {
        return parseExpression(string, OperatorManager.empty());
    }

    @Override
    public SingletonExpressionContext parseExpression(String string, OperatorManager withOperators) {
        final PrologParser parser = createParser(string, withOperators);
        return parseExpression(parser, string);
    }

    @Override
    public SingletonExpressionContext parseExpressionWithStandardOperators(String string) {
        return parseExpression(string, OperatorManager.standardOperators());
    }

    @Override
    public SingletonExpressionContext parseExpression(Reader string) throws IOException {
        return parseExpression(string, OperatorManager.empty());
    }

    @Override
    public SingletonExpressionContext parseExpression(Reader string, OperatorManager withOperators) throws IOException {
        final PrologParser parser = createParser(string, withOperators);
        return parseExpression(parser, string);
    }

    @Override
    public SingletonExpressionContext parseExpressionWithStandardOperators(Reader string) throws IOException {
        return parseExpression(string, OperatorManager.standardOperators());
    }

    @Override
    public SingletonExpressionContext parseExpression(InputStream string) throws IOException {
        return parseExpression(string, OperatorManager.empty());
    }

    @Override
    public SingletonExpressionContext parseExpression(InputStream string, OperatorManager withOperators) throws IOException {
        final PrologParser parser = createParser(string, withOperators);
        return parseExpression(parser, string);
    }

    @Override
    public SingletonExpressionContext parseExpressionWithStandardOperators(InputStream string) throws IOException {
        return parseExpression(string, OperatorManager.standardOperators());
    }

    private SingletonExpressionContext parseExpression(PrologParser parser, Object source) {
        try {
            return parser.singletonExpression();
        } catch (ParseCancellationException ex) {
            if (parser.getInterpreter().getPredictionMode() == PredictionMode.SLL) {
                parser.getTokenStream().seek(0);
                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                parser.setErrorHandler(new DefaultErrorStrategy());
                parser.addErrorListener(newErrorListener(source));
                return parseExpression(parser, source);
            } else if (ex.getCause() instanceof RecognitionException) {
                throw (RecognitionException) ex.getCause();
            } else {
                throw ex;
            }
        }
    }

    @Override
    public SingletonTermContext parseTerm(String string) {
        return parseTerm(string, OperatorManager.empty());
    }

    @Override
    public SingletonTermContext parseTerm(String string, OperatorManager withOperators) {
        final PrologParser parser = createParser(string, withOperators);
        return parseTerm(parser, string);
    }

    @Override
    public SingletonTermContext parseTermWithStandardOperators(String string) {
        return parseTerm(string, OperatorManager.standardOperators());
    }

    @Override
    public SingletonTermContext parseTerm(Reader string) throws IOException {
        return parseTerm(string, OperatorManager.empty());
    }

    @Override
    public SingletonTermContext parseTerm(Reader string, OperatorManager withOperators) throws IOException {
        final PrologParser parser = createParser(string, withOperators);
        return parseTerm(parser, string);
    }

    @Override
    public SingletonTermContext parseTermWithStandardOperators(Reader string) throws IOException {
        return parseTerm(string, OperatorManager.standardOperators());
    }

    @Override
    public SingletonTermContext parseTerm(InputStream string) throws IOException {
        return parseTerm(string, OperatorManager.empty());
    }

    @Override
    public SingletonTermContext parseTerm(InputStream string, OperatorManager withOperators) throws IOException {
        final PrologParser parser = createParser(string, withOperators);
        return parseTerm(parser, string);
    }

    @Override
    public SingletonTermContext parseTermWithStandardOperators(InputStream string) throws IOException {
        return parseTerm(string, OperatorManager.standardOperators());
    }

    private SingletonTermContext parseTerm(PrologParser parser, Object source) {
        try {
            return parser.singletonTerm();
        } catch (ParseCancellationException ex) {
            if (parser.getInterpreter().getPredictionMode() == PredictionMode.SLL) {
                parser.getTokenStream().seek(0);
                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                parser.setErrorHandler(new DefaultErrorStrategy());
                parser.addErrorListener(newErrorListener(source));
                return parseTerm(parser, source);
            } else if (ex.getCause() instanceof RecognitionException) {
                throw (RecognitionException) ex.getCause();
            } else {
                throw ex;
            }
        }
    }

    private OptClauseContext parseClause(PrologParser parser, Object input) {
        int mark = -1, index = -1;

        try {
            mark = parser.getTokenStream().mark();
            index = Math.max(parser.getTokenStream().index(), 0);

            return parser.optClause();

        } catch (ParseCancellationException ex) {
            if (parser.getInterpreter().getPredictionMode() == PredictionMode.SLL) {
                parser.getTokenStream().seek(index);
                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                parser.setErrorHandler(new DefaultErrorStrategy());
                parser.addErrorListener(newErrorListener(input));

                return parser.optClause();

            } else if (ex.getCause() instanceof RecognitionException) {
                throw (RecognitionException) ex.getCause();
            } else {
                throw ex;
            }
        } finally {
            parser.getTokenStream().release(mark);
        }
    }

    private Stream<ClauseContext> parseClauses(PrologParser parser, Object source) {
        Stream<OptClauseContext> optClauses = IntStream.iterate(0, i -> i + 1)
                 .mapToObj(i -> {
                     try {
                         return parseClause(parser, source);
                     } catch (ParsingException e) {
                         throw e.toInvalidTheoryException(i);
                     }
                 });
        return StreamUtils.takeUntil(optClauses, it -> it.isOver)
                .map(OptClauseContext::clause);
    }

    @Override
    public Stream<ClauseContext> parseClauses(String source, OperatorManager withOperators) {
        final PrologParser parser = createParser(source, withOperators);
        return parseClauses(parser, source);
    }

    @Override
    public Stream<ClauseContext> parseClauses(Reader source, OperatorManager withOperators) throws IOException {
        final PrologParser parser = createParser(source, withOperators);
        return parseClauses(parser, source);
    }

    @Override
    public Stream<ClauseContext> parseClauses(InputStream source, OperatorManager withOperators) throws IOException {
        final PrologParser parser = createParser(source, withOperators);
        return parseClauses(parser, source);
    }

    @Override
    public Stream<ClauseContext> parseClauses(String source) {
        return parseClauses(source, OperatorManager.empty());
    }

    @Override
    public Stream<ClauseContext> parseClauses(Reader source) throws IOException {
        return parseClauses(source, OperatorManager.empty());
    }

    @Override
    public Stream<ClauseContext> parseClauses(InputStream source) throws IOException {
        return parseClauses(source, OperatorManager.empty());
    }


    @Override
    public Stream<ClauseContext> parseClausesWithStandardOperators(String source) {
        return parseClauses(source, OperatorManager.standardOperators());
    }

    @Override
    public Stream<ClauseContext> parseClausesWithStandardOperators(Reader source) throws IOException {
        return parseClauses(source, OperatorManager.standardOperators());
    }

    @Override
    public Stream<ClauseContext> parseClausesWithStandardOperators(InputStream source) throws IOException {
        return parseClauses(source, OperatorManager.standardOperators());
    }

    private <T> PrologParser createParser(T source, Function<T, CharStream> charStreamGenerator) {
        final CharStream stream = charStreamGenerator.apply(source);
        final PrologLexer lexer = new PrologLexer(stream);
        lexer.removeErrorListeners();
        final TokenStream tokenStream = new BufferedTokenStream(lexer);
        final PrologParser parser = new PrologParser(tokenStream);
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        return parser;
    }

    @Override
    public PrologParser createParser(String string) {
        return createParser(string, CharStreams::fromString);
    }

    @Override
    public PrologParser createParser(Reader source) throws IOException {
        try {
            return createParser(source, ExceptionalFunction.wrap(CharStreams::fromReader));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public PrologParser createParser(InputStream source) throws IOException {
        try {
            return createParser(source, ExceptionalFunction.wrap(CharStreams::fromStream));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public PrologParser createParser(String source, OperatorManager operators) {
        return addOperators(createParser(source), operators);
    }

    @Override
    public PrologParser createParser(Reader source, OperatorManager operators) throws IOException {
        return addOperators(createParser(source), operators);
    }

    @Override
    public PrologParser createParser(InputStream source, OperatorManager operators) throws IOException {
        return addOperators(createParser(source), operators);
    }

    private PrologParser addOperators(PrologParser prologParser, OperatorManager operators) {
        for (Operator op : operators.getOperators()) {
            prologParser.addOperator(op.getName(), op.getAssociativity(), op.getPriority());
        }
        prologParser.addParseListener(DynamicOpListener.of(prologParser, operators::add));
        return prologParser;
    }
}
