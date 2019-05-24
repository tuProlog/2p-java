package alice.tuprolog.parser;

import alice.tuprolog.OperatorManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.stream.Stream;

public interface ParserFactory {

    static ParserFactory getInstance() {
        return ParserFactoryImpl.INSTANCE;
    }

    PrologParser.SingletonExpressionContext parseExpression(String string);

    PrologParser.SingletonExpressionContext parseExpression(String string, OperatorManager withOperators);

    PrologParser.SingletonExpressionContext parseExpressionWithStandardOperators(String string);

    PrologParser.SingletonExpressionContext parseExpression(Reader string) throws IOException;

    PrologParser.SingletonExpressionContext parseExpression(Reader string, OperatorManager withOperators) throws IOException;

    PrologParser.SingletonExpressionContext parseExpressionWithStandardOperators(Reader string) throws IOException;

    PrologParser.SingletonExpressionContext parseExpression(InputStream string) throws IOException;

    PrologParser.SingletonExpressionContext parseExpression(InputStream string, OperatorManager withOperators) throws IOException;

    PrologParser.SingletonExpressionContext parseExpressionWithStandardOperators(InputStream string) throws IOException;

    PrologParser.SingletonTermContext parseTerm(String string);

    PrologParser.SingletonTermContext parseTerm(String string, OperatorManager withOperators);

    PrologParser.SingletonTermContext parseTermWithStandardOperators(String string);

    PrologParser.SingletonTermContext parseTerm(Reader string) throws IOException;

    PrologParser.SingletonTermContext parseTerm(Reader string, OperatorManager withOperators) throws IOException;

    PrologParser.SingletonTermContext parseTermWithStandardOperators(Reader string) throws IOException;

    PrologParser.SingletonTermContext parseTerm(InputStream string) throws IOException;

    PrologParser.SingletonTermContext parseTerm(InputStream string, OperatorManager withOperators) throws IOException;

    PrologParser.SingletonTermContext parseTermWithStandardOperators(InputStream string) throws IOException;

    Stream<PrologParser.ClauseContext> parseClauses(String source, OperatorManager withOperators);

    Stream<PrologParser.ClauseContext> parseClauses(Reader source, OperatorManager withOperators) throws IOException;

    Stream<PrologParser.ClauseContext> parseClauses(InputStream source, OperatorManager withOperators) throws IOException;

    Stream<PrologParser.ClauseContext> parseClauses(String source);

    Stream<PrologParser.ClauseContext> parseClauses(Reader source) throws IOException;

    Stream<PrologParser.ClauseContext> parseClauses(InputStream source) throws IOException;

    Stream<PrologParser.ClauseContext> parseClausesWithStandardOperators(String source);

    Stream<PrologParser.ClauseContext> parseClausesWithStandardOperators(Reader source) throws IOException;

    Stream<PrologParser.ClauseContext> parseClausesWithStandardOperators(InputStream source) throws IOException;

    PrologParser createParser(String string);

    PrologParser createParser(Reader source) throws IOException;

    PrologParser createParser(InputStream source) throws IOException;

    PrologParser createParser(String source, OperatorManager operators);

    PrologParser createParser(Reader source, OperatorManager operators) throws IOException;

    PrologParser createParser(InputStream source, OperatorManager operators) throws IOException;
}
