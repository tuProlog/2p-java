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
    public final int line;

    @Deprecated
    public final int pos;

    public InvalidPrologException(String message) {
        this(message, -1, -1);
    }

    public InvalidPrologException(String message, int line, int pos) {
        super(message);
        this.line = line;
        this.pos = pos;
    }

    public InvalidPrologException(final String message, final Throwable cause, final int line, final int pos) {
        super(message, cause);
        this.line = line;
        this.pos = pos;
    }

    public InvalidPrologException(final Throwable cause, final int line, final int pos) {
        super(cause);
        this.line = line;
        this.pos = pos;
    }

    public InvalidPrologException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace, final int line, final int pos) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.line = line;
        this.pos = pos;
    }

    public InvalidPrologException(final int line, final int pos) {
        this.line = line;
        this.pos = pos;
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
}
