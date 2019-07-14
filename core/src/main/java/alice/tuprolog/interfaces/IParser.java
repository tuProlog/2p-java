package alice.tuprolog.interfaces;

import alice.tuprolog.Term;
import alice.tuprolog.exceptions.InvalidTermException;

public interface IParser {

    /**
     * Parses next term from the stream built on string.
     *
     * @param endNeeded <code>true</code> if it is required to parseWithStandardOperators the end token (a period), <code>false</code> otherwise.
     * @throws InvalidTermException if a syntax error is found.
     */
    Term nextTerm(boolean endNeeded) throws Exception;

    /**
     * @return the current line number
     */
    int getCurrentLine();

    /**
     * @return the current offset
     */
    int getCurrentOffset();

    /**
     * @return the line correspondent to the current offset
     */
    int[] offsetToRowColumn(int offset);

}
