package alice.tuprolog.parser;

import alice.tuprolog.exceptions.InvalidTermException;
import alice.tuprolog.exceptions.InvalidTheoryException;
import org.antlr.v4.runtime.Token;

public class ParsingException extends RuntimeException {
    private Object input;
    private int line;
    private int column;
    private String offendingSymbol;

    public ParsingException(Object input, String offendingSymbol, int line, int column, String message, Throwable throwable) {
        super(message, throwable);
        this.input = input;
        this.line = line;
        this.column = column;
        this.offendingSymbol = offendingSymbol;
    }

    public ParsingException(Object input, Token token, String message, Throwable throwable) {
        this(input, token.getText(), token.getLine(), token.getCharPositionInLine(), message, throwable);
    }

    public ParsingException(Token token, String message, Throwable throwable) {
        this(null, token.getText(), token.getLine(), token.getCharPositionInLine(), message, throwable);
    }

    public ParsingException(Token token, String message) {
        this(null, token.getText(), token.getLine(), token.getCharPositionInLine(), message, null);
    }

    public ParsingException(Token token, Throwable throwable) {
        this(null, token.getText(), token.getLine(), token.getCharPositionInLine(), "", throwable);
    }

    public Object getInput() {
        return input;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getOffendingSymbol() {
        return offendingSymbol;
    }

    public void setInput(final Object input) {
        this.input = input;
    }

    public void setLine(final int line) {
        this.line = line;
    }

    public void setColumn(final int column) {
        this.column = column;
    }

    public void setOffendingSymbol(final String offendingSymbol) {
        this.offendingSymbol = offendingSymbol;
    }

    @Override
    public String toString() {
        return "ParsingException{" +
               "message='" + getMessage().replace("\\n", "\\\\n") + '\'' +
               ", line=" + line +
               ", column=" + column +
               ", offendingSymbol='" + offendingSymbol + '\'' +
               '}';
    }

    public InvalidTermException toInvalidTermException() {
        return new InvalidTermException(getMessage(), this)
                .setOffendingSymbol(getOffendingSymbol())
                .setInput(getInput() != null ? getInput().toString() : null)
                .setLine(getLine())
                .setPositionInLine(getColumn());
    }

    public InvalidTheoryException toInvalidTheoryException(int clause) {
        return new InvalidTheoryException(getMessage(), this)
                .setOffendingSymbol(getOffendingSymbol())
                .setInput(getInput().toString())
                .setLine(getLine())
                .setPositionInLine(getColumn())
                .setClause(clause);
    }
}
