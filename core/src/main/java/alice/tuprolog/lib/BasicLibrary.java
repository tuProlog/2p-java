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
package alice.tuprolog.lib;

import alice.tuprolog.Number;
import alice.tuprolog.*;
import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.util.Tools;

import java.io.IOException;
import java.util.IdentityHashMap;

/**
 * This class defines a set of basic built-in predicates for the tuProlog engine
 * <p>
 * Library/Theory dependency: none
 */
public class BasicLibrary extends Library {

    private static final long serialVersionUID = 1L;

    public BasicLibrary() {
    }

    //
    // meta-predicates
    //

    /**
     * sets a new theory provided as a text
     *
     * @throws PrologError
     */
    public boolean set_theory_1(Term th) throws PrologError {
        th = th.getTerm();
        if (th instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!th.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", th);
        }
        try {
            Struct theory = (Struct) th;
            getEngine().setTheory(Theory.parseLazilyWithOperators(theory.getName(), getEngine().getOperatorManager()));
            return true;
        } catch (InvalidTheoryException ex) {
            throw PrologError.syntax_error(getEngine().getEngineManager(), ex.getClause(), ex.getLine(), ex.getPositionInLine(), new Struct(ex.getMessage()));
        }
    }

    /**
     * adds a new theory provided as a text
     *
     * @throws PrologError
     */
    public boolean add_theory_1(Term th) throws PrologError {
        th = th.getTerm();
        if (th instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!th.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom",
                                         th);
        }
        try {
            Struct theory = (Struct) th.getTerm();
            getEngine().addTheory(Theory.parseLazilyWithStandardOperators(theory.getName()));
            return true;
        } catch (InvalidTheoryException ex) {
            throw PrologError.syntax_error(getEngine().getEngineManager(), ex.getClause(), ex.getLine(), ex.getPositionInLine(), new Struct(ex.getMessage()));
        }
    }

    /**
     * gets current theory text
     */
    public boolean get_theory_1(Term arg) {
        arg = arg.getTerm();
        try {
            Term theory = new Struct(getEngine().getTheory().toString());
            return (unify(arg, theory));
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Loads a library constructed from a theory.
     *
     * @param th  theory text
     * @param libName name of the library
     * @return true if the library has been succesfully loaded.
     */
    public boolean load_library_from_theory_2(Term th, Term libName) {
        Struct theory = (Struct) th.getTerm();
        Struct libN = (Struct) libName.getTerm();
        try {
            if (!theory.isAtom()) {
                return false;
            }
            if (!libN.isAtom()) {
                return false;
            }
            Theory t = Theory.parseLazilyWithStandardOperators(theory.getName());
            TheoryLibrary thlib = new TheoryLibrary(libN.getName(), t);
            getEngine().loadLibrary(thlib);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean get_operators_list_1(Term argument) {
        Term arg = argument.getTerm();
        Struct list = new Struct();
        java.util.Iterator<Operator> it = getEngine().getCurrentOperatorList().iterator();
        while (it.hasNext()) {
            Operator o = it.next();
            list = new Struct(new Struct("op", new Int(o.getPriority()),
                    new Struct(o.getAssociativity().name().toLowerCase()), new Struct(o.getName())), list);
        }
        return unify(arg, list);
    }

    /**
     * spawns a separate prolog agent providing it a theory text
     *
     * @throws PrologError
     */
    public boolean prologEngine_1(Term th) throws PrologError {
        th = th.getTerm();
        if (th instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!(th.isAtom())) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom",
                                         th);
        }
        Struct theory = (Struct) th;
        try {
            new Prolog(alice.util.Tools.removeApices(theory.toString())).spawn();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * spawns a separate prolog agent providing it a theory text and a goal
     *
     * @throws PrologError
     */
    public boolean prologEngine_2(Term th, Term g) throws PrologError {
        th = th.getTerm();
        g = g.getTerm();
        if (th instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (g instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if (!(th.isAtom())) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom",
                                         th);
        }
        if (!(g instanceof Struct)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                         "struct", g);
        }
        Struct theory = (Struct) th;
        Struct goal = (Struct) g;
        try {
            new Prolog(alice.util.Tools.removeApices(theory.toString()), goal
                                                                                 .toString()
                                                                         + ".").spawn();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean spy_0() {
        getEngine().setSpy(true);
        return true;
    }

    public boolean nospy_0() {
        getEngine().setSpy(false);
        return true;
    }

    /*Castagna 16/09*/
    public boolean trace_0() {
        return this.spy_0();
    }

    public boolean notrace_0() {
        return nospy_0();
    }

    public boolean warning_0() {
        getEngine().setWarning(true);
        return true;
    }

    public boolean nowarning_0() {
        getEngine().setWarning(false);
        return true;
    }

    //
    // term type inspection
    //

    public boolean constant_1(Term t) {
        t = t.getTerm();
        return (t.isAtomic());
    }

    public boolean number_1(Term t) {
        return (t.getTerm() instanceof Number);
    }

    public boolean integer_1(Term t) {
        if (!(t.getTerm() instanceof Number)) {
            return false;
        }
        alice.tuprolog.Number n = (alice.tuprolog.Number) t.getTerm();
        return (n.isInteger());
    }

    public boolean float_1(Term t) {
        if (!(t instanceof Number)) {
            return false;
        }
        alice.tuprolog.Number n = (alice.tuprolog.Number) t.getTerm();
        return (n.isReal());
    }

    public boolean atom_1(Term t) {
        t = t.getTerm();
        return (t.isAtom());
    }

    public boolean compound_1(Term t) {
        t = t.getTerm();
        return t.isCompound();
    }

    public boolean list_1(Term t) {
        t = t.getTerm();
        return (t.isList());
    }

    public boolean var_1(Term t) {
        t = t.getTerm();
        return (t instanceof Var);
    }

    public boolean nonvar_1(Term t) {
        t = t.getTerm();
        return !(t instanceof Var);
    }

    public boolean atomic_1(Term t) {
        t = t.getTerm();
        return t.isAtomic();
    }

    public boolean ground_1(Term t) {
        t = t.getTerm();
        return (t.isGround());
    }

    //
    // term/espression comparison
    //

    private void handleError(Throwable t, int arg) throws PrologError {
        // errore durante la valutazione
        if (t instanceof ArithmeticException) {
            ArithmeticException cause = (ArithmeticException) t;
            if (cause.getMessage().equals("/ by zero")) {
                throw PrologError.evaluation_error(getEngine().getEngineManager(),
                                                   arg, "zero_divisor");
            }
        }
    }

    public boolean expression_equality_2(Term arg0, Term arg1)
            throws PrologError {
        if (arg0.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {
            handleError(e, 1);
        }
        try {
            val1 = evalExpression(arg1);
        } catch (Throwable e) {
            handleError(e, 2);
        }
        if (val0 == null || !(val0 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "evaluable", arg0.getTerm());
        }
        if (val1 == null || !(val1 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                         "evaluable", arg1.getTerm());
        }
        alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
        alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
        if (val0n.isInteger() && val1n.isInteger()) {
            // by ED: note that this would work also with intValue, even with Long args,
            // because in that case both values would be wrong, but 'equally wrong' :)
            // However, it is much better to always operate consistently on long values
            return val0n.longValue() == val1n.longValue();
        } else {
            return val0n.doubleValue() == val1n.doubleValue();
        }
    }

    public boolean expression_greater_than_2(Term arg0, Term arg1)
            throws PrologError {
        if (arg0.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {
            handleError(e, 1);
        }
        try {
            val1 = evalExpression(arg1);
        } catch (Throwable e) {
            handleError(e, 2);
        }
        if (val0 == null || !(val0 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "evaluable", arg0.getTerm());
        }
        if (val1 == null || !(val1 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                         "evaluable", arg1.getTerm());
        }
        return expression_greater_than((alice.tuprolog.Number) val0,
                                       (alice.tuprolog.Number) val1);
    }

    public boolean expression_less_or_equal_than_2(Term arg0, Term arg1)
            throws PrologError {
        if (arg0.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {
            handleError(e, 1);
        }
        try {
            val1 = evalExpression(arg1);
        } catch (Throwable e) {
            handleError(e, 2);
        }
        if (val0 == null || !(val0 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "evaluable", arg0.getTerm());
        }
        if (val1 == null || !(val1 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                         "evaluable", arg1.getTerm());
        }
        return !expression_greater_than((alice.tuprolog.Number) val0,
                                        (alice.tuprolog.Number) val1);
    }

    private boolean expression_greater_than(alice.tuprolog.Number num0,
                                            alice.tuprolog.Number num1) {
        if (num0.isInteger() && num1.isInteger()) {
            return num0.longValue() > num1.longValue();
        } else {
            return num0.doubleValue() > num1.doubleValue();
        }
    }

    public boolean expression_less_than_2(Term arg0, Term arg1)
            throws PrologError {
        if (arg0.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {
            handleError(e, 1);
        }
        try {
            val1 = evalExpression(arg1);
        } catch (Throwable e) {
            handleError(e, 2);
        }
        if (val0 == null || !(val0 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "evaluable", arg0.getTerm());
        }
        if (val1 == null || !(val1 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                         "evaluable", arg1.getTerm());
        }
        return expression_less_than((alice.tuprolog.Number) val0,
                                    (alice.tuprolog.Number) val1);
    }

    public boolean expression_greater_or_equal_than_2(Term arg0, Term arg1)
            throws PrologError {
        if (arg0.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {
            handleError(e, 1);
        }
        try {
            val1 = evalExpression(arg1);
        } catch (Throwable e) {
            handleError(e, 2);
        }
        if (val0 == null || !(val0 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "evaluable", arg0.getTerm());
        }
        if (val1 == null || !(val1 instanceof Number)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                         "evaluable", arg1.getTerm());
        }
        return !expression_less_than((alice.tuprolog.Number) val0,
                                     (alice.tuprolog.Number) val1);
    }

    private boolean expression_less_than(alice.tuprolog.Number num0,
                                         alice.tuprolog.Number num1) {
        if (num0.isInteger() && num1.isInteger()) {
            return num0.longValue() < num1.longValue();
        } else {
            return num0.doubleValue() < num1.doubleValue();
        }
    }

    public boolean term_equality_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        return arg0.isEqual(arg1);
    }

    public boolean term_greater_than_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        return arg0.isGreater(arg1);
    }

    public boolean term_less_than_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        return !(arg0.isGreater(arg1) || arg0.isEqual(arg1));
    }

    public Term expression_plus_1(Term arg0) {
        Term val0 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {

        }
        if (val0 != null && val0 instanceof Number) {
            return val0;
        } else {
            return null;
        }
    }

    public Term expression_minus_1(Term arg0) {
        Term val0 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {

        }
        if (val0 != null && val0 instanceof Number) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            if (val0n instanceof Int) {
                return new Int(val0n.intValue() * -1);
            } else if (val0n instanceof alice.tuprolog.Double) {
                return new alice.tuprolog.Double(val0n.doubleValue() * -1);
            } else if (val0n instanceof alice.tuprolog.Long) {
                return new alice.tuprolog.Long(val0n.longValue() * -1);
            } else if (val0n instanceof alice.tuprolog.Float) {
                return new alice.tuprolog.Float(val0n.floatValue() * -1);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Term expression_bitwise_not_1(Term arg0) {
        Term val0 = null;
        try {
            val0 = evalExpression(arg0);
        } catch (Throwable e) {

        }
        if (val0 != null && val0 instanceof Number) {
            return new alice.tuprolog.Long(~((alice.tuprolog.Number) val0).longValue());
        } else {
            return null;
        }
    }

    alice.tuprolog.Number getIntegerNumber(long num) {
        if (num > Integer.MIN_VALUE && num < Integer.MAX_VALUE) {
            return new Int((int) num);
        } else {
            return new alice.tuprolog.Long(num);
        }
    }

    public Term expression_plus_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && (val1 instanceof Number)) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            if (val0n.isInteger() && (val1n.isInteger())) {
                return getIntegerNumber(val0n.longValue() + val1n.longValue());
            } else {
                return new alice.tuprolog.Double(val0n.doubleValue()
                                                 + val1n.doubleValue());
            }
        } else {
            return null;
        }
    }

    public Term expression_minus_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && (val1 instanceof Number)) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            if (val0n.isInteger() && (val1n.isInteger())) {
                return getIntegerNumber(val0n.longValue() - val1n.longValue());
            } else {
                return new alice.tuprolog.Double(val0n.doubleValue()
                                                 - val1n.doubleValue());
            }
        } else {
            return null;
        }
    }

    public Term expression_multiply_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && (val1 instanceof Number)) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            if (val0n.isInteger() && (val1n.isInteger())) {
                return getIntegerNumber(val0n.longValue() * val1n.longValue());
            } else {
                return new alice.tuprolog.Double(val0n.doubleValue()
                                                 * val1n.doubleValue());
            }
        } else {
            return null;
        }
    }

    public Term expression_div_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && val1 instanceof Number) {
            Number val0n = (Number) val0;
            Number val1n = (Number) val1;
            if (val0n.isInteger() && val1n.isInteger()) {
                return getIntegerNumber(val0n.longValue() / val1n.longValue());
            } else {
                return new alice.tuprolog.Double(val0n.doubleValue()
                                                 / val1n.doubleValue());
            }
        } else {
            return null;
        }
    }

    public Term expression_integer_div_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && (val1 instanceof Number)) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            return getIntegerNumber(val0n.longValue() / val1n.longValue());
        } else {
            return null;
        }
    }

    public Term expression_pow_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && (val1 instanceof Number)) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            return new alice.tuprolog.Double(Math.pow(val0n.doubleValue(),
                                                      val1n.doubleValue()));
        } else {
            return null;
        }
    }

    public Term expression_bitwise_shift_right_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && val1 instanceof Number) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            return new alice.tuprolog.Long(val0n.longValue() >> val1n.longValue());
        } else {
            return null;
        }
    }

    public Term expression_bitwise_shift_left_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && val1 instanceof Number) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            return new alice.tuprolog.Long(val0n.longValue() << val1n.longValue());
        } else {
            return null;
        }
    }

    public Term expression_bitwise_and_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && val1 instanceof Number) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            return new alice.tuprolog.Long(val0n.longValue() & val1n.longValue());
        } else {
            return null;
        }
    }

    public Term expression_bitwise_or_2(Term arg0, Term arg1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(arg0);
            val1 = evalExpression(arg1);
        } catch (Throwable e) {

        }
        if (val0 != null && val1 != null && val0 instanceof Number
            && val1 instanceof Number) {
            alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
            alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
            return new alice.tuprolog.Long(val0n.longValue() | val1n.longValue());
        } else {
            return null;
        }
    }

    //
    // text/atom manipulation predicates
    //

    /**
     * bidirectional text/term conversion.
     */
    public boolean text_term_2(Term arg0, Term arg1) {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        getEngine().stdOutput(arg0.toString() +
                              "\n" + arg1.toString());
        if (!arg0.isGround()) {
            return unify(arg0, new Struct(arg1.toString()));
        } else {
            try {
                String text = alice.util.Tools.removeApices(arg0.toString());
                return unify(arg1, getEngine().toTerm(text));
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public boolean text_concat_3(Term source1, Term source2, Term dest)
            throws PrologError {
        source1 = source1.getTerm();
        source2 = source2.getTerm();
        dest = dest.getTerm();
        if (source1 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (source2 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if (!source1.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom",
                                         source1);
        }
        if (!source2.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "atom",
                                         source2);
        }
        return unify(dest, new Struct(((Struct) source1).getName()
                                      + ((Struct) source2).getName()));
    }

    public boolean num_atom_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg1 instanceof Var) {
            if (!(arg0 instanceof Number)) {
                throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                             "number", arg0);
            }
            alice.tuprolog.Number n0 = (alice.tuprolog.Number) arg0;
            String st = null;
            if (n0.isInteger()) {
                st = new java.lang.Integer(n0.intValue()).toString();
            } else {
                st = new java.lang.Double(n0.doubleValue()).toString();
            }
            return (unify(arg1, new Struct(st)));
        } else {
            if (!arg1.isAtom()) {
                throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                             "atom", arg1);
            }
            String st = ((Struct) arg1).getName();
            String st2 = "";
            for (int i = 0; i < st.length(); i++) {
                st2 += st.charAt(i);

                if (st.charAt(0) == '0' && st.charAt(1) == 39 && st.charAt(2) == 39 && st.length() == 4) {
                    String sti = "" + st.charAt(3);
                    byte[] b = sti.getBytes();
                    st2 = "" + b[0];
                }
                if (st.charAt(0) == '0' && st.charAt(1) == 'x' && st.charAt(2) >= 'a' && st.charAt(2) <= 'f' &&
                    st.length() == 3) {
                    String sti = "" + st.charAt(2);
                    int dec = java.lang.Integer.parseInt(sti, 16);
                    st2 = "" + dec;
                }
            }
            boolean before = true;
            boolean after = false;
            boolean between = false;
            int numBefore = 0;
            int numAfter = 0;
            int numBetween = 0;
            String st3 = null;
            String iBetween = "";
            for (int i = 0; i < st2.length(); i++) {
                if ((st2.charAt(i) < '0' || st2.charAt(i) > '9') && before) // find non number at first
                {
                    numBefore++;
                }

                between = false;
                if (st2.charAt(i) >= '0' && st2.charAt(i) <= '9') //found a number
                {
                    int k = 0;
                    for (int j = i + 1; j < st2.length(); j++) //into the rest of the string
                    {
                        if (st2.charAt(j) >= '0' && st2.charAt(j) <= '9' &&
                            j - i > 1) // control if there is another numbers
                        {
                            k += i + 1;
                            numBetween += 2;
                            iBetween = "" + k + j;
                            i += j;
                            j = st2.length();
                            between = true;
                        } else if (st2.charAt(j) >= '0' && st2.charAt(j) <= '9' && j - i == 1) {
                            k++;
                        }
                    }

                    if (!between) {
                        before = false;
                        after = true;
                    } else {
                        before = false;
                    }
                }

                if ((st2.charAt(i) < '0' || st2.charAt(i) > '9') && after) {
                    numAfter++;
                }
            }
            for (int i = 0; i < numBefore; i++) {
                if (st2.charAt(i) == ' ') {
                    st3 = st2.substring(i + 1);
                } else if (st2.charAt(i) == '\\' && (st2.charAt(i + 1) == 'n' || st2.charAt(i + 1) == 't')) {
                    st3 = st2.substring(i + 2);
                    i++;
                } else if (st2.charAt(i) != '-' && st2.charAt(i) != '+') {
                    st3 = "";
                }
            }
            for (int i = 0; i < numBetween; i += 2) {
                for (int j = java.lang.Integer.parseInt("" + iBetween.charAt(i));
                     j < java.lang.Integer.parseInt("" + iBetween.charAt(i + 1)); j++) {
                    if (st2.charAt(j) != '.' && (st2.charAt(i) != 'E' || (st2.charAt(i) != 'E' &&
                                                                          (st2.charAt(i + 1) != '+' ||
                                                                           st2.charAt(i + 1) != '-'))) &&
                        (st2.charAt(i) != 'e' ||
                         (st2.charAt(i) != 'e' && (st2.charAt(i + 1) != '+' || st2.charAt(i + 1) != '-')))) {
                        st3 = "";
                    }
                }
            }
            for (int i = 0; i < numAfter; i++) {
                if ((st2.charAt(i) != 'E' ||
                     (st2.charAt(i) != 'E' && (st2.charAt(i + 1) != '+' || st2.charAt(i + 1) != '-'))) &&
                    st2.charAt(i) != '.' && (st2.charAt(i) != 'e' || (st2.charAt(i) != 'e' &&
                                                                      (st2.charAt(i + 1) != '+' ||
                                                                       st2.charAt(i + 1) != '-')))) {
                    st3 = "";
                }
            }
            if (st3 != null) {
                st2 = st3;
            }
            Term term = null;
            try {
                term = new Int(java.lang.Integer.parseInt(st2));
            } catch (Exception ex) {
            }
            if (term == null) {
                try {
                    term = new alice.tuprolog.Double(java.lang.Double
                                                             .parseDouble(st2));
                } catch (Exception ex) {
                }
            }
            if (term == null) {
                throw PrologError.domain_error(getEngine().getEngineManager(), 2,
                                               "num_atom", arg1);
            }
            return (unify(arg0, term));
        }
    }

    // throw/1
    public boolean throw_1(Term error) throws PrologError {
        throw new PrologError(error);
    }

    private static final String THEORY;

    static {
        try {
            THEORY = Tools.loadText(BasicLibrary.class.getResourceAsStream(BasicLibrary.class.getSimpleName() + ".pl"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getTheory() {
        return THEORY;
    }

    // Java guards for Prolog predicates

    public boolean arg_guard_3(Term arg0, Term arg1, Term arg2)
            throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if (!(arg0 instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "integer", arg0);
        }
        if (!arg1.isCompound()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "compound", arg1);
        }
        Int arg0int = (Int) arg0;
        if (arg0int.intValue() < 1) {
            throw PrologError.domain_error(getEngine().getEngineManager(), 1, "greater_than_zero", arg0);
        }
        return true;
    }

    public boolean $dynamic_predicates_1(Term arg0) {
        Struct clauses = new Struct(
                getEngine().getTheoryManager()
                        .dynamicClausesStream()
                        .map(ClauseInfo::getHead)
                        .map(Term::copy)
        );

        return unify(arg0, clauses);
    }

    public boolean $static_predicates_1(Term arg0) {
        Struct clauses = new Struct(
                getEngine().getTheoryManager()
                        .staticClausesStream()
                        .map(ClauseInfo::getHead)
                        .map(Term::copy)
        );

        return unify(arg0, clauses);
    }

    public boolean $predicates_1(Term arg0) {
        Struct clauses = new Struct(
                getEngine().getTheoryManager()
                        .clausesStream()
                        .map(ClauseInfo::getHead)
                        .map(Term::copy)
        );

        return unify(arg0, clauses);
    }

    public boolean clause_guard_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!(arg0 instanceof Struct)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "callable", arg0);
        }
        ensureNonStatic((Struct) arg0, "access", "private_procedure");
        return true;
    }

    public boolean call_guard_1(Term arg0) throws PrologError {
        arg0 = arg0.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!arg0.isAtom() && !arg0.isCompound()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "callable", arg0);
        }
        return true;
    }

    public boolean all_solutions_predicates_guard_3(Term arg0, Term arg1,
                                                    Term arg2) throws PrologError {
        arg1 = arg1.getTerm();

        if (arg1 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if (!arg1.isAtom() && !arg1.isCompound()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                                         "callable", arg1);
        }
        return true;
    }

    public boolean retract_guard_1(Term arg0) throws PrologError {
        arg0 = arg0.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!(arg0 instanceof Struct)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "clause", arg0);
        }
        return true;
    }

    public boolean member_guard_2(Term arg0, Term arg1) throws PrologError {
        arg1 = arg1.getTerm();
        if (!(arg1 instanceof Var) && !(arg1.isList())) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "list",
                                         arg1);
        }
        return true;
    }

    public boolean reverse_guard_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        if (!(arg0 instanceof Var) && !(arg0.isList())) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "list",
                                         arg0);
        }
        return true;
    }

    public boolean delete_guard_3(Term arg0, Term arg1, Term arg2)
            throws PrologError {
        arg1 = arg1.getTerm();
        if (!(arg1 instanceof Var) && !(arg1.isList())) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "list",
                                         arg1);
        }
        return true;
    }

    public boolean element_guard_3(Term arg0, Term arg1, Term arg2)
            throws PrologError {
        arg1 = arg1.getTerm();
        if (!(arg1 instanceof Var) && !(arg1.isList())) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "list",
                                         arg1);
        }
        return true;
    }

    // Internal Java predicates which are part of the bagof/3 and setof/3
    // algorithm

    //Alberto
    public boolean $wt_copyAndRetainFreeVar_2(Term arg0, Term arg1) {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        int id = getEngine().getEngineManager().getEnv().getNDemoSteps();
        return unify(arg1, arg0.copyAndRetainFreeVar(new IdentityHashMap<Var, Var>(), id));
    }

    public boolean $wt_unify_3(Term witness, Term wtList, Term tList) {
        Struct list = (Struct) wtList.getTerm();
        Struct result = new Struct();
        for (java.util.Iterator<? extends Term> it = list.listIterator(); it.hasNext(); ) {
            Struct element = (Struct) it.next();
            Term w = element.getArg(0);
            Term t = element.getArg(1);
            if (unify(witness, w)) {
                result.append(t);
            }
        }
        return unify(tList, result);
    }

    public boolean $s_next0_3(Term witness, Term wtList, Term sNext) {
        Struct list = (Struct) wtList.getTerm();
        Struct result = new Struct();
        for (java.util.Iterator<? extends Term> it = list.listIterator(); it.hasNext(); ) {
            Struct element = (Struct) it.next();
            Term w = element.getArg(0);
            if (!unify(witness, w)) {
                result.append(element);
            }
        }
        return unify(sNext, result);
    }

    public boolean iterated_goal_term_2(Term term, Term goal) {
        Term t = term.getTerm();
        Term igt = t.iteratedGoalTerm();
        return unify(igt, goal);
    }

    /**
     * Defines some synonyms
     */
    public String[][] getSynonymMap() {
        return new String[][]{{"+", "expression_plus", "functor"},
                              {"-", "expression_minus", "functor"},
                              {"*", "expression_multiply", "functor"},
                              {"/", "expression_div", "functor"},
                              {"**", "expression_pow", "functor"},
                              {">>", "expression_bitwise_shift_right", "functor"},
                              {"<<", "expression_bitwise_shift_left", "functor"},
                              {"/\\", "expression_bitwise_and", "functor"},
                              {"\\/", "expression_bitwise_or", "functor"},
                              {"//", "expression_integer_div", "functor"},
                              {"\\", "expression_bitwise_not", "functor"}};
    }
}