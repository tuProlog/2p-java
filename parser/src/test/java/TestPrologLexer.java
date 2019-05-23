import alice.tuprolog.PrologLexer;
import alice.tuprolog.dynamic.DynamicLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class TestPrologLexer {

    private static PrologLexer lexerForString(String input) {
        return new PrologLexer(new ANTLRInputStream(input));
    }

    private static TokenStream tokenStreamFromLexer(DynamicLexer lexer) {
        return new BufferedTokenStream(lexer);
    }

    private static List<Token> tokenStreamToList(TokenStream stream) {
        final LinkedList<Token> result = new LinkedList<>();

        int i = 0;
        stream.consume();
        do {
            result.add(stream.get(i++));
            stream.consume();
        } while (stream.LA(1) != TokenStream.EOF);
        result.add(stream.get(i));

        return result;
    }

    private static void assertTokenIs(Token token, int type, String text) {
        Assert.assertEquals("'" + text + "'", "'" + token.getText() + "'");
        Assert.assertEquals(PrologLexer.VOCABULARY.getSymbolicName(type), PrologLexer.VOCABULARY.getSymbolicName(token.getType()));
    }

    @Test
    public void testPrologLexerRecognisesAtoms() {
        final PrologLexer lexer = lexerForString("1 + a + \"b\" + 'c'");
        final TokenStream tokenStream = tokenStreamFromLexer(lexer);
        final List<Token> tokens = tokenStreamToList(tokenStream);

        int i = 0;

        assertTokenIs(tokens.get(i++), PrologLexer.INTEGER, "1");
        assertTokenIs(tokens.get(i++), PrologLexer.ATOM, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.ATOM, "a");
        assertTokenIs(tokens.get(i++), PrologLexer.ATOM, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.STRING, "b");
        assertTokenIs(tokens.get(i++), PrologLexer.ATOM, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.STRING, "c");
        Assert.assertEquals(tokens.size(), i);
    }

    @Test
    public void testPrologLexerRecognisesOperators() {
        final PrologLexer lexer = lexerForString("1 + a + \"b\" - 'c' dada a");
        lexer.addOperators("+", "-", "dada");
        final TokenStream tokenStream = tokenStreamFromLexer(lexer);
        final List<Token> tokens = tokenStreamToList(tokenStream);

        int i = 0;

        assertTokenIs(tokens.get(i++), PrologLexer.INTEGER, "1");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.ATOM, "a");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.STRING, "b");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "-");
        assertTokenIs(tokens.get(i++), PrologLexer.STRING, "c");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "dada");
        assertTokenIs(tokens.get(i++), PrologLexer.ATOM, "a");
        Assert.assertEquals(tokens.size(), i);
    }

    @Test
    public void testPrologLexerRecognisesVariables() {
        final PrologLexer lexer = lexerForString("_ + A + _B + _1 + _a + _+");
        lexer.addOperators("+");
        final TokenStream tokenStream = tokenStreamFromLexer(lexer);
        final List<Token> tokens = tokenStreamToList(tokenStream);

        int i = 0;

        assertTokenIs(tokens.get(i++), PrologLexer.VARIABLE, "_");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.VARIABLE, "A");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.VARIABLE, "_B");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.VARIABLE, "_1");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.VARIABLE, "_a");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        assertTokenIs(tokens.get(i++), PrologLexer.VARIABLE, "_");
        assertTokenIs(tokens.get(i++), PrologLexer.OPERATOR, "+");
        Assert.assertEquals(tokens.size(), i);
    }
}
