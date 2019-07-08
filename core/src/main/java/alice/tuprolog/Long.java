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

package alice.tuprolog;

import java.util.List;

/**
 * Long class represents the long prolog data type
 */
public class Long extends Number {

    private long value;

    @SuppressWarnings({"deprecated"})
    public static Long of(long value) {
        return new Long(value);
    }

    @SuppressWarnings({"deprecated"})
    public static Long of(java.lang.Long value) {
        return new Long(value);
    }

    @Deprecated
    public Long(long v) {
        value = v;
    }

    /**
     * Returns the value of the Integer as int
     */
    final public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value of the Integer as float
     */
    final public float floatValue() {
        return (float) value;
    }

    /**
     * Returns the value of the Integer as double
     */
    final public double doubleValue() {
        return (double) value;
    }

    /**
     * Returns the value of the Integer as long
     */
    final public long longValue() {
        return value;
    }


    /**
     * is this term a prolog integer term?
     */
    final public boolean isInteger() {
        return true;
    }

    /**
     * is this term a prolog real term?
     */
    final public boolean isReal() {
        return false;
    }

    /**
     * is an int Integer number?
     *
     * @deprecated Use <tt>instanceof Int</tt> instead.
     */
    final public boolean isTypeInt() {
        return false;
    }

    /**
     * is an int Integer number?
     *
     * @deprecated Use <tt>instanceof Int</tt> instead.
     */
    final public boolean isInt() {
        return false;
    }

    /**
     * is a float Real number?
     *
     * @deprecated Use <tt>instanceof alice.tuprolog.Float</tt> instead.
     */
    final public boolean isTypeFloat() {
        return false;
    }

    /**
     * is a float Real number?
     *
     * @deprecated Use <tt>instanceof alice.tuprolog.Float</tt> instead.
     */
    final public boolean isFloat() {
        return false;
    }

    /**
     * is a double Real number?
     *
     * @deprecated Use <tt>instanceof alice.tuprolog.Double</tt> instead.
     */
    final public boolean isTypeDouble() {
        return false;
    }

    /**
     * is a double Real number?
     *
     * @deprecated Use <tt>instanceof alice.tuprolog.Double</tt> instead.
     */
    final public boolean isDouble() {
        return false;
    }

    /**
     * is a long Integer number?
     *
     * @deprecated Use <tt>instanceof alice.tuprolog.Long</tt> instead.
     */
    final public boolean isTypeLong() {
        return true;
    }

    /**
     * is a long Integer number?
     *
     * @deprecated Use <tt>instanceof alice.tuprolog.Long</tt> instead.
     */
    final public boolean isLong() {
        return true;
    }

    /**
     * Returns true if this integer term is grater that the term provided.
     * For number term argument, the int value is considered.
     */
    public boolean isGreater(Term t) {
        t = t.getTerm();
        if (t instanceof Number) {
            return value > ((Number) t).longValue();
        } else if (t instanceof Struct) {
            return false;
        } else return t instanceof Var;
    }

    /**
     * Tries to unify a term with the provided term argument.
     * This service is to be used in demonstration context.
     */
    boolean unify(List<Var> vl1, List<Var> vl2, Term t, boolean isOccursCheckEnabled) {
        t = t.getTerm();
        if (t instanceof Var) {
            return t.unify(vl1, vl2, this, isOccursCheckEnabled);
        } else if (t instanceof Number && ((Number) t).isInteger()) {
            return value == ((Number) t).longValue();
        } else {
            return false;
        }
    }

    public Long copy() {
        return this;
    }

    public String toString() {
        return java.lang.Long.toString(value);
    }

    /**
     * @author Paolo Contessi
     */
    public int compareTo(Number o) {
        return (new java.lang.Long(value)).compareTo(o.longValue());
    }

    @Override
    boolean unify(List<Var> varsUnifiedArg1, List<Var> varsUnifiedArg2, Term t) {
        return unify(varsUnifiedArg1, varsUnifiedArg2, t, true);
    }
}