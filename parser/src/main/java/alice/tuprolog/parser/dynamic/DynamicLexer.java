package alice.tuprolog.parser.dynamic;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class DynamicLexer extends org.antlr.v4.runtime.Lexer {

    private final Set<String> operators = new HashSet<>();

    public DynamicLexer() {
    }

    public DynamicLexer(CharStream input) {
        super(input);
    }

    public boolean isOperator(Token token) {
        return this.isOperator(token.getText());
    }

    public boolean isOperator(String functor) {
        return operators.contains(functor);
    }

    public void addOperators(String operator, String... operators) {
        this.operators.add(operator);
        this.operators.addAll(Arrays.asList(operators));
    }

    public void removeOperators(String operator, String... operators) {
        this.operators.remove(operator);
        this.operators.removeAll(Arrays.asList(operators));
    }

    public void clearOperators() {
        operators.clear();
    }

    public Set<String> getOperators() {
        return new HashSet<>(operators);
    }

    protected final String substring(String string, int start, int end) {
        if (end >= 0) {
            return string.substring(start, end);
        } else {
            return string.substring(start, string.length() + end);
        }
    }
}
