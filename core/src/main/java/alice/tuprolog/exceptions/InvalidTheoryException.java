/*
 * tuProlog - Copyright (C) 2001-2007 aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuprolog.exceptions;

import alice.tuprolog.Theory;

/**
 * This exceptions means that a not valid tuProlog theory has been specified
 *
 * @see Theory
 */
public class InvalidTheoryException extends InvalidPrologException {

    @Deprecated
    public int clause = -1;

    public InvalidTheoryException() {
    }

    public InvalidTheoryException(String message) {
        super(message);
    }

    public InvalidTheoryException(String message, int clause, int line, int positionInLine) {
        super(message);
        setClause(clause);
        setLine(line);
        setPositionInLine(positionInLine);
    }

    public InvalidTheoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTheoryException(Throwable cause) {
        super(cause);
    }

    public InvalidTheoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getClause() {
        return clause;
    }

    public InvalidTheoryException setClause(int clause) {
        this.clause = clause;
        return this;
    }

    @Override
    public InvalidTheoryException setLine(int line) {
        return (InvalidTheoryException) super.setLine(line);
    }

    @Override
    public InvalidTheoryException setPositionInLine(int pos) {
        return (InvalidTheoryException) super.setPositionInLine(pos);
    }

    @Override
    public InvalidTheoryException setOffendingSymbol(String offendingSymbol) {
        return (InvalidTheoryException) super.setOffendingSymbol(offendingSymbol);
    }

    @Override
    public InvalidTheoryException setInput(String input) {
        return (InvalidTheoryException) super.setInput(input);
    }
}