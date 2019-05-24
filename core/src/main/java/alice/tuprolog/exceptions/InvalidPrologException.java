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

/**
 * This exception means that a method has been passed an argument
 * containing an invalid Prolog term.
 */
public class InvalidPrologException extends PrologRuntimeException {

    @Deprecated
    public int line = -1;

    @Deprecated
    public int pos = -1;

    private String offendingSymbol;
    private String input;

    public InvalidPrologException() {
    }

    public InvalidPrologException(String message) {
        super(message);
    }

    public InvalidPrologException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPrologException(Throwable cause) {
        super(cause);
    }

    public InvalidPrologException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getInput() {
        return null;
    }

    public String getOffendingSymbol() {
        return null;
    }

    public int getLine() {
        return line;
    }

    public int getPositionInLine() {
        return pos;
    }

    public InvalidPrologException setLine(int line) {
        this.line = line;
        return this;
    }

    public InvalidPrologException setPositionInLine(int pos) {
        this.pos = pos;
        return this;
    }

    public InvalidPrologException setOffendingSymbol(String offendingSymbol) {
        this.offendingSymbol = offendingSymbol;
        return this;
    }

    public InvalidPrologException setInput(String input) {
        this.input = input;
        return this;
    }
}
