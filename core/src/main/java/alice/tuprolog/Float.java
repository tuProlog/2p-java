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
 * Float class represents the float prolog data type
 */
public class Float extends Number {

    private float value;

    @SuppressWarnings({"deprecated"})
    public static Float of(float value) {
        return new Float(value);
    }

    @SuppressWarnings({"deprecated"})
    public static Float of(java.lang.Float value) {
        return new Float(value);
    }

    @Deprecated
    public Float(float v) {
        value = v;
    }

    /**
     * Returns the value of the Float as int
     */
    final public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value of the Float as float
     */
    final public float floatValue() {
        return value;
    }

    /**
     * Returns the value of the Float as double
     */
    final public double doubleValue() {
        return value;
    }

    /**
     * Returns the value of the Float as long
     */
    final public long longValue() {
        return (long) value;
    }


    /**
     * is this term a prolog integer term?
     */
    final public boolean isInteger() {
        return false;
    }

    /**
     * is this term a prolog real term?
     */
    final public boolean isReal() {
        return true;
    }

    /**
     * is an int Integer number?
     *
     * @deprecated Use <code>instanceof Int</code> instead.
     */
    final public boolean isTypeInt() {
        return false;
    }

    /**
     * is an int Integer number?
     *
     * @deprecated Use <code>instanceof Int</code> instead.
     */
    final public boolean isInt() {
        return false;
    }

    /**
     * is a float Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Float</code> instead.
     */
    final public boolean isTypeFloat() {
        return true;
    }

    /**
     * is a float Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Float</code> instead.
     */
    final public boolean isFloat() {
        return true;
    }

    /**
     * is a double Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Double</code> instead.
     */
    final public boolean isTypeDouble() {
        return false;
    }

    /**
     * is a double Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Double</code> instead.
     */
    final public boolean isDouble() {
        return false;
    }

    /**
     * is a long Integer number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Long</code> instead.
     */
    final public boolean isTypeLong() {
        return false;
    }

    /**
     * is a long Integer number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Long</code> instead.
     */
    final public boolean isLong() {
        return false;
    }

    /**
     * Returns true if this Float term is grater that the term provided.
     * For number term argument, the int value is considered.
     */
    public boolean isGreater(Term t) {
        t = t.getTerm();
        if (t instanceof Number) {
            return value > ((Number) t).floatValue();
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
            return t.unify(vl2, vl1, this, isOccursCheckEnabled);
        } else if (t instanceof Number && ((Number) t).isReal()) {
            return value == ((Number) t).floatValue();
        } else {
            return false;
        }
    }

    public Float copy() {
        return this;
    }

    public String toString() {
        return java.lang.Float.toString(value);
    }

    /**
     * @author Paolo Contessi
     */
    public int compareTo(Number o) {
        return java.lang.Float.compare(value, o.floatValue());
    }

    @Override
    boolean unify(List<Var> varsUnifiedArg1, List<Var> varsUnifiedArg2, Term t) {
        return unify(varsUnifiedArg1, varsUnifiedArg2, t, true);
    }

    @Override
    public <T> T accept(TermVisitor<T> tv) {
        return tv.visit(this);
    }
}