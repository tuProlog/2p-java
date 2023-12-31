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

import alice.tuprolog.exceptions.InvalidTermException;

import java.util.EnumSet;
import java.util.Map;

/**
 * Number abstract class represents numbers prolog data type
 *
 * @see Int
 * @see Long
 * @see Float
 * @see Double
 */
public abstract class Number extends Term implements Comparable<Number> {

    public static Number createNumber(String s) {
        Term t = Term.createTerm(s);
        if (t instanceof Number) {
            return (Number) t;
        }
        throw new InvalidTermException("Term " + t + " is not a number.");
    }

    public static Number of(java.lang.Number value) {
        if (java.lang.Double.class.equals(value.getClass())) {
            return Double.of(value.doubleValue());
        } else if (java.lang.Float.class.equals(value.getClass())) {
            return Float.of(value.floatValue());
        } else if (java.lang.Long.class.equals(value.getClass())) {
            return Long.of(value.longValue());
        } else {
            return Int.of(value.intValue());
        }
    }

    /**
     * Returns the value of the number as int
     */
    public abstract int intValue();

    /**
     * Returns the value of the number as float
     */
    public abstract float floatValue();

    /**
     * Returns the value of the number as long
     */
    public abstract long longValue();

    /**
     * Returns the value of the number as double
     */
    public abstract double doubleValue();

    /**
     * is this term a prolog integer term?
     */
    public abstract boolean isInteger();

    /**
     * is this term a prolog real term?
     */
    public abstract boolean isReal();

    /**
     * is an int Integer number?
     *
     * @deprecated Use <code>instanceof Int</code> instead.
     */
    public abstract boolean isTypeInt();

    /**
     * is an int Integer number?
     *
     * @deprecated Use <code>instanceof Int</code> instead.
     */
    public abstract boolean isInt();

    /**
     * is a float Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Float</code> instead.
     */
    public abstract boolean isTypeFloat();

    /**
     * is a float Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Float</code> instead.
     */
    public abstract boolean isFloat();

    /**
     * is a double Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Double</code> instead.
     */
    public abstract boolean isTypeDouble();

    /**
     * is a double Real number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Double</code> instead.
     */
    public abstract boolean isDouble();

    /**
     * is a long Integer number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Long</code> instead.
     */
    public abstract boolean isTypeLong();

    /**
     * is a long Integer number?
     *
     * @deprecated Use <code>instanceof alice.tuprolog.Long</code> instead.
     */
    public abstract boolean isLong();

    /**
     * Gets the actual term referred by this Term.
     */
    public Term getTerm() {
        return this;
    }

    /**
     * is this term a prolog numeric term?
     */
    @Override
    @Deprecated
    final public boolean isNumber() {
        return true;
    }

    /**
     * is this term a struct
     */
    @Override
    @Deprecated
    final public boolean isStruct() {
        return false;
    }

    /**
     * is this term a variable
     */
    @Override
    @Deprecated
    final public boolean isVar() {
        return false;
    }

    final public boolean isEmptyList() {
        return false;
    }

    /**
     * is this term a constant prolog term?
     */
    final public boolean isAtomic() {
        return true;
    }

    /**
     * is this term a prolog compound term?
     */
    final public boolean isCompound() {
        return false;
    }

    /**
     * is this term a prolog (alphanumeric) atom?
     */
    final public boolean isAtom() {
        return false;
    }

    /**
     * is this term a prolog list?
     */
    final public boolean isList() {
        return false;
    }

    /**
     * is this term a ground term?
     */
    final public boolean isGround() {
        return true;
    }

    /**
     * gets a copy of this term.
     */
    public Term copy(int idExecCtx) {
        return this;
    }

    @Override //Alberto
    public Term copyAndRetainFreeVar(Map<Var, Var> vMap, int idExecCtx) {
        return this;
    }

    /**
     * gets a copy (with renamed variables) of the term.
     * <p>
     * the list argument passed contains the list of variables to be renamed
     * (if emptyWithStandardOperators list then no renaming)
     */
    Term copy(Map<Var, Var> vMap, int idExecCtx) {
        return this;
    }

    Term copyClone(Map<Var, Var> vMap, int idExecCtx) {
        return this;
    }

    /**
     * gets a copy of the term.
     */
    Term copy(Map<Var, Var> vMap, Map<Term, Var> substMap) {
        return this;
    }

    @Override
    public Number copy() {
        return this;
    }

    long resolveTerm(long count) {
        return count;
    }

    public void free() {
    }

    void restoreVariables() {
    }

    @Override
    public boolean equals(Object t) {
        if (super.equals(t)) return true;
        if (!(t instanceof Term) || !(((Term) t).getTerm() instanceof Number)) return false;

        final Number other = (Number) ((Term) t).getTerm();

        if (isInteger() && other.isInteger()) {
            return longValue() == other.longValue();
        } else if (isReal() && other.isReal()) {
            return java.lang.Double.compare(doubleValue(), other.doubleValue()) == 0;
        }

        return false;
    }

    @Override
    public boolean equals(Term other, EnumSet<Comparison> comparison) {
        if (comparison.contains(Comparison.NUMBERS_BY_TYPE)) {
            if (!(other.getTerm() instanceof Number)) return false;

            final Number otherNumber = (Number) other.getTerm();

            if (isInteger() && otherNumber.isInteger()) {
                return longValue() == otherNumber.longValue();
            } else if (isReal() && otherNumber.isReal()) {
                return java.lang.Double.compare(doubleValue(), otherNumber.doubleValue()) == 0;
            }
        } else if (comparison.contains(Comparison.NUMBERS_BY_VALUE)) {
            if (!(other.getTerm() instanceof Number)) return false;

            final Number otherNumber = (Number) other.getTerm();

            if (isInteger() && otherNumber.isInteger()) {
                return longValue() == otherNumber.longValue();
            } else if (isReal() || otherNumber.isReal()) {
                return java.lang.Double.compare(doubleValue(), otherNumber.doubleValue()) == 0;
            }
        } else if (comparison.contains(Comparison.CONSTANTS_BY_REPRESENTED_VALUE)) {
            if (other.getTerm().isAtom()) {
                final Term parsed = Term.createTerm(other.getTerm().toString());

                if (parsed instanceof Number) {
                    final Number otherNumber = (Number) other.getTerm();

                    if (isInteger() && otherNumber.isInteger()) {
                        return longValue() == otherNumber.longValue();
                    } else if (isReal() || otherNumber.isReal()) {
                        return java.lang.Double.compare(doubleValue(), otherNumber.doubleValue()) == 0;
                    }
                }
            } else if (other.getTerm() instanceof Number) {
                final Number otherNumber = (Number) other.getTerm();

                if (isInteger() && otherNumber.isInteger()) {
                    return longValue() == otherNumber.longValue();
                } else if (isReal() || otherNumber.isReal()) {
                    return java.lang.Double.compare(doubleValue(), otherNumber.doubleValue()) == 0;
                }
            }
        } else {
            return this == other;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return isInteger() ? java.lang.Long.hashCode(longValue()) : java.lang.Double.hashCode(doubleValue());
    }

    @Override
    public <T> T accept(TermVisitor<T> tv) {
        return tv.visit(this);
    }
}