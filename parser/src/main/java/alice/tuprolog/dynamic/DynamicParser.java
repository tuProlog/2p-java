package alice.tuprolog.dynamic;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import java.util.*;
import java.util.function.IntBinaryOperator;
import java.util.stream.Stream;

public abstract class DynamicParser extends Parser {

    private final Map<String, Map<Associativity, Integer>> operators = new HashMap<>();
    private DynamicLexer lexer;

    public DynamicParser(TokenStream input) {
        super(input);
        if (input.getTokenSource() instanceof DynamicLexer) {
            this.lexer = (DynamicLexer) input.getTokenSource();
        } else {
            throw new IllegalStateException("No lexer was provided");
        }
    }

    public DynamicParser(TokenStream input, DynamicLexer lexer) {
        super(input);
        this.lexer = lexer;
    }

    protected static String[] except(String... strings) {
        return strings;
    }

    public DynamicLexer getLexer() {
        return lexer;
    }

    public void setLexer(DynamicLexer lexer) {
        this.lexer = lexer;
    }

    public int getOperatorPriority(String operator, Associativity associativity) {
        return Optional.of(operators.get(operator))
                       .map(m -> m.get(associativity))
                       .orElse(Integer.MAX_VALUE);
    }

    public int getOperatorPriority(Token operator, Associativity associativity) {
        return getOperatorPriority(operator.getText(), associativity);
    }

    public boolean isOperator(Token token) {
        return lexer.isOperator(token);
    }

    public boolean isOperator(String functor) {
        return lexer.isOperator(functor);
    }

    public void addOperator(String functor, Associativity associativity, int priority) {
        lexer.addOperators(functor);
        final Map<Associativity, Integer> item = Collections.singletonMap(associativity, priority);

        operators.merge(functor, item, (m1, m2) -> {
            final Map<Associativity, Integer> m3 = new HashMap<>(m1);
            m3.putAll(m2);
            return m3;
        });
    }

    public void removeOperator(String functor) {
        lexer.removeOperators(functor);
        operators.remove(functor);
    }

    public boolean isOperatorAssociativity(Token token, Associativity associativity) {
        return isOperatorAssociativity(token.getText(), associativity);
    }

    public boolean isOperatorAssociativity(String functor, Associativity a) {
        return operators.containsKey(functor)
               && operators.get(functor).containsKey(a);
    }

    protected OptionalInt lookahead(IntBinaryOperator f, Associativity associativity, int priority, String... except) {
        final Token lookahead = getTokenStream().LT(1);
        if (Stream.of(except).filter(this::isOperator).anyMatch(o -> lookahead.getText().equals(o))) {
            return OptionalInt.empty();
        }
        if (!isOperator(lookahead)) {
            return OptionalInt.empty();
        }
        if (!isOperatorAssociativity(lookahead, associativity)) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(f.applyAsInt(getOperatorPriority(lookahead, associativity), priority));
    }

    protected boolean lookaheadGt(Associativity associativity, int priority, String... except) {
        return lookahead(Integer::compare, associativity, priority, except).orElse(-1) > 0;
    }

    protected boolean lookaheadEq(Associativity associativity, int priority, String... except) {
        return lookahead(Integer::compare, associativity, priority, except).orElse(-1) == 0;
    }

    protected boolean lookaheadNeq(Associativity associativity, int priority, String... except) {
        return lookahead(Integer::compare, associativity, priority, except).orElse(0) != 0;
    }

    protected boolean lookaheadGeq(Associativity associativity, int priority, String... except) {
        return lookahead(Integer::compare, associativity, priority, except).orElse(-1) >= 0;
    }

    protected boolean lookaheadLeq(Associativity associativity, int priority, String... except) {
        return lookahead(Integer::compare, associativity, priority, except).orElse(1) <= 0;
    }

    protected boolean lookaheadLt(Associativity associativity, int priority, String... except) {
        return lookahead(Integer::compare, associativity, priority, except).orElse(1) < 0;
    }

    protected boolean lookahead(Associativity associativity, int top, int bottom, String... except) {
        return lookahead(EnumSet.of(associativity), top, bottom, except);
    }

    protected boolean lookahead(EnumSet<Associativity> associativities, int top, int bottom, String... except) {
        final Token lookahead = getTokenStream().LT(1);
        if (Stream.of(except).filter(this::isOperator).anyMatch(o -> lookahead.getText().equals(o))) {
            return false;
        }
        if (!isOperator(lookahead)) {
            return false;
        }

        return associativities.stream().anyMatch(associativity -> {
            if (!isOperatorAssociativity(lookahead, associativity)) {
                return false;
            }

            final int priority = getOperatorPriority(lookahead, associativity);

            return priority <= top && priority >= bottom;
        });
    }
}
