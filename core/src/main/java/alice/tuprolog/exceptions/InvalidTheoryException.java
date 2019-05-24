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
    public final int clause;

    public InvalidTheoryException() {
        this(null, -1, -1, -1);
    }

    public InvalidTheoryException(int line, int pos) {
        this(null, -1, line, pos);
    }

    public InvalidTheoryException(String message) {
        this(message, -1, -1, -1);
    }

    public InvalidTheoryException(String message, int clause, int line, int pos) {
        super(message, line, pos);
        this.clause = clause;
    }


    public int getClause() {
        return clause;
    }
}