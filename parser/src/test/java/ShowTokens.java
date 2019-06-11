import alice.tuprolog.parser.PrologLexer;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.io.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ShowTokens {

    private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        Stream<Token> tokens;

        while (true) {

            if (args.length > 0) {
                tokens = tokenStream(new StringReader(String.join(" ", args)));
            } else {
                System.out.print("> ");
                tokens = tokenStream(stdin.readLine());
            }

            tokens.forEach(it -> {
                System.out.printf("%s: «%s» %d:%d\n",
                                  PrologLexer.VOCABULARY.getSymbolicName(it.getType()),
                                  it.getText(),
                                  it.getLine(),
                                  it.getCharPositionInLine()
                );
            });
        }
    }

    private static Stream<Token> tokenStream(String reader) throws IOException {
        return tokenStream(new StringReader(reader));
    }

    private static Stream<Token> tokenStream(Reader reader) throws IOException {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        tokenIterator(reader),
                        Spliterator.ORDERED
                ),
            false
        );
    }

    private static Iterator<Token> tokenIterator(Reader reader) throws IOException {
        PrologLexer lexer = new PrologLexer(CharStreams.fromReader(reader));
        TokenStream stream = new BufferedTokenStream(lexer);

        return new Iterator<Token>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return stream.LA(1) != TokenStream.EOF;
            }

            @Override
            public Token next() {
                Token t = stream.get(i++);
                stream.consume();
                return t;
            }
        };
    }
}

