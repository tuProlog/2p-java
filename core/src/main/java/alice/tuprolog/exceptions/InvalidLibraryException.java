/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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

import alice.tuprolog.Library;

/**
 * This exception means that a not valid tuProlog library has been specified.
 *
 * @see Library
 */
public class InvalidLibraryException extends InvalidPrologException {

    private final String libraryName;

    public InvalidLibraryException() {
        this(null, 0, 0);
    }

    public InvalidLibraryException(String libName, int line, int pos) {
        libraryName = libName;
        setLine(line);
        setPositionInLine(pos);
    }

    public InvalidLibraryException(final String message, final String libraryName, final int line, final int pos) {
        super(message);
        this.libraryName = libraryName;
        setLine(line);
        setPositionInLine(pos);
    }

    public InvalidLibraryException(final String message, final Throwable cause, final String libraryName, final int line, final int pos) {
        super(message, cause);
        this.libraryName = libraryName;
        setLine(line);
        setPositionInLine(pos);
    }

    public InvalidLibraryException(final Throwable cause, final String libraryName, final int line, final int pos) {
        super(cause);
        this.libraryName = libraryName;
        setLine(line);
        setPositionInLine(pos);
    }

    public InvalidLibraryException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace, final String libraryName, final int line, final int pos) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.libraryName = libraryName;
        setLine(line);
        setPositionInLine(pos);
    }

    public InvalidLibraryException(String libName, int line, int pos, Throwable cause) {
        super(cause);
        libraryName = libName;
        setLine(line);
        setPositionInLine(pos);
    }

    public String getLibraryName() {
        return libraryName;
    }

    @Deprecated
    public int getPos() {
        return pos;
    }

    public String getMessage() {
        return toString();
    }

    public String toString() {
        return "InvalidLibraryException: " + libraryName + " at " + getLine() + ":" + getPositionInLine();
    }

}