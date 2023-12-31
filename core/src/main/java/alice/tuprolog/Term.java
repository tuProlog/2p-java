/*
 * tuProlog - Copyright (C) 2001-2007  aliCE team at deis.unibo.it
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
import alice.tuprolog.parser.ParseException;
import alice.tuprolog.parser.PrologExpressionVisitor;
import alice.tuprolog.parser.PrologParserFactory;
import alice.util.OneWayList;

import java.io.Serializable;
import java.util.*;

//import java.util.ArrayList;

/**
 * Term class is the root abstract class for prolog data type
 *
 * @see Struct
 * @see Var
 * @see Number
 */
public abstract class Term implements Serializable {

    public boolean equals(Term other, Comparison comparison1, Comparison... comparisons) {
        return equals(other, EnumSet.of(comparison1, comparisons));
    }

    /**
     * Static service to create a Term from a string.
     *
     * @param st the string representation of the term
     * @return the term represented by the string
     * @throws InvalidTermException if the string does not represent a valid term
     */
    public static Term createTerm(String st) {
        try {
            return PrologParserFactory.getInstance()
                    .parseExpressionWithStandardOperators(st)
                    .accept(PrologExpressionVisitor.get());
        } catch (ParseException e) {
            throw e.toInvalidTermException();
        }
    }

    /**
     * @deprecated Use {@link Term#createTerm(String)} instead.
     */
    @Deprecated
    public static Term parse(String st) {
        return Term.createTerm(st);
    }

    /**
     * Static service to create a Term from a string, providing an
     * external operator manager.
     *
     * @param st the string representation of the term
     * @param op the operator manager used to build the term
     * @return the term represented by the string
     * @throws InvalidTermException if the string does not represent a valid term
     */
    public static Term createTerm(String st, OperatorManager op) {
        try {
            return PrologParserFactory.getInstance()
                    .parseExpression(st, op)
                    .accept(PrologExpressionVisitor.get());
        } catch (ParseException e) {
            throw e.toInvalidTermException();
        }
    }

    /**
     * @deprecated Use {@link Term#createTerm(String, OperatorManager)} instead.
     */
    @Deprecated
    public static Term parse(String st, OperatorManager op) {
        return Term.createTerm(st, op);
    }

    /**
     * Gets an iterator providing
     * a term stream from a source text
     */
    public static Iterator<Term> getIterator(String text) {
        return new Iterator<Term>() {

            private final Iterator<Term> i = PrologParserFactory.getInstance()
                    .parseClausesWithStandardOperators(text)
                    .map(PrologExpressionVisitor.asFunction())
                    .iterator();

            @Override
            public boolean hasNext() {
                try {
                    return i.hasNext();
                } catch (ParseException e) {
                    throw e.toInvalidTermException();
                }
            }

            @Override
            public Term next() {
                try {
                    return i.next();
                } catch (ParseException e) {
                    throw e.toInvalidTermException();
                }
            }
        };
    }

    /**
     * is this term a prolog numeric term?
     *
     * @deprecated Use <code>instanceof Number</code> instead.
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * is this term a struct?
     *
     * @deprecated Use <code>instanceof Struct</code> instead.
     */
    public boolean isStruct() {
        return false;
    }

    /**
     * is this term a variable?
     *
     * @deprecated Use <code>instanceof Var</code> instead.
     */
    public boolean isVar() {
        return false;
    }

    /**
     * is this term a null term?
     */
    public boolean isEmptyList() {
        return false;
    }

    /**
     * is this term a constant prolog term?
     */
    public boolean isAtomic() {
        return false;
    }

    /**
     * is this term a prolog compound term?
     */
    public boolean isCompound() {
        return false;
    }

    /**
     * is this term a prolog (alphanumeric) atom?
     */
    public boolean isAtom() {
        return false;
    }

    public boolean isCons() {
        return false;
    }

    /**
     * is this term a prolog list?
     */
    public boolean isList() {
        return false;
    }

    /**
     * is this term a ground term?
     */
    public boolean isGround() {
        return false;
    }

    /**
     * Tests for the equality of two object terms
     */
    public boolean equals(Object t) {
        return this == t;
    }

    /**
     * is term greater than term t?
     */
    public abstract boolean isGreater(Term t);

    /**
     * Tests if this term is (logically) equal to another
     */
    public boolean isEqual(Term t) {
        return equals(t);
//        return this.toString().equals(t.toString());
    }

    /**
     * Gets the actual term referred by this Term. if the Term is a bound variable, the method gets the Term linked to the variable
     */
    public abstract Term getTerm();

    /**
     * Unlink variables inside the term
     */
    public abstract void free();

    /**
     * Resolves variables inside the term, starting from a specific time count.
     * <p>
     * If the variables has been already resolved, no renaming is done.
     *
     * @param count new starting time count for resolving process
     * @return the new time count, after resolving process
     */
    abstract long resolveTerm(long count);

    /**
     * Resolves variables inside the term
     * <p>
     * If the variables has been already resolved, no renaming is done.
     */
    public void resolveTerm() {
        resolveTerm(System.currentTimeMillis());
    }

    //Alberto

    /**
     * gets a engine's copy of this term.
     *
     * @param idExecCtx Execution Context identified
     */
    public Term copyGoal(AbstractMap<Var, Var> vars, int idExecCtx) {
        return copy(vars, idExecCtx);
    }

    /**
     * gets a copy of this term for the output
     */
    public Term copyResult(Collection<Var> goalVars, List<Var> resultVars) {
        IdentityHashMap<Var, Var> originals = new IdentityHashMap<Var, Var>();
        for (Var key : goalVars) {
            Var clone = Var.underscore();
            if (!key.isAnonymous()) {
                clone = Var.of(key.getOriginalName());
            }
            originals.put(key, clone);
            resultVars.add(clone);
        }
        return copy(originals, new IdentityHashMap<Term, Var>());
    }

    //Alberto

    /**
     * gets a copy (with renamed variables) of the term.
     * <p>
     * The list argument passed contains the list of variables to be renamed
     * (if emptyWithStandardOperators list then no renaming)
     *
     * @param idExecCtx Execution Context identifier
     */
    abstract Term copy(Map<Var, Var> vMap, int idExecCtx);

    public Term copy() {
        return copy(new HashMap<>(), Var.ORIGINAL);
    }

    //Alberto
    public abstract Term copyAndRetainFreeVar(Map<Var, Var> vMap, int idExecCtx);

    /**
     * gets a copy for result.
     */
    abstract Term copy(Map<Var, Var> vMap, Map<Term, Var> substMap);

    /**
     * Try to unify two terms
     *
     * @param mediator have the reference of EngineManager
     * @param t1       the term to unify
     * @return true if the term is unifiable with this one
     */
    public boolean unify(Prolog mediator, Term t1) {
        EngineManager engine = mediator.getEngineManager();
        resolveTerm();
        t1.resolveTerm();
        List<Var> v1 = new LinkedList<Var>(); /* Reviewed by: Paolo Contessi (was: ArrayList()) */
        List<Var> v2 = new LinkedList<Var>(); /* Reviewed by: Paolo Contessi (was: ArrayList()) */
        boolean ok = unify(v1, v2, t1, mediator.getFlagManager().isOccursCheckEnabled());
        if (ok) {
            ExecutionContext ec = engine.getCurrentContext();
            if (ec != null) {
                int id = (engine.getEnv() == null) ? Var.PROGRESSIVE : engine.getEnv().nDemoSteps;
                // Update trailingVars
                ec.trailingVars = new OneWayList<List<Var>>(v1, ec.trailingVars);
                // Renaming after unify because its utility regards not the engine but the user
                int count = 0;
                for (Var v : v1) {
                    v.rename(id, count);
                    if (id >= 0) {
                        id++;
                    } else {
                        count++;
                    }
                }
                for (Var v : v2) {
                    v.rename(id, count);
                    if (id >= 0) {
                        id++;
                    } else {
                        count++;
                    }
                }
            }
            return true;
        }
        Var.free(v1);
        Var.free(v2);
        return false;
    }

    /**
     * Tests if this term is unifiable with an other term.
     * No unification is done.
     * <p>
     * The test is done outside any demonstration context
     *
     * @param t                    the term to checked
     * @param isOccursCheckEnabled
     * @return true if the term is unifiable with this one
     */
    public boolean match(boolean isOccursCheckEnabled, Term t) {
        resolveTerm();
        t.resolveTerm();
        List<Var> v1 = new LinkedList<Var>();
        List<Var> v2 = new LinkedList<Var>();
        boolean ok = unify(v1, v2, t, isOccursCheckEnabled);
        Var.free(v1);
        Var.free(v2);
        return ok;
    }

    /**
     * Tests if this term is unifiable with an other term.
     * No unification is done.
     * <p>
     * The test is done outside any demonstration context
     *
     * @param t the term to checked
     * @return true if the term is unifiable with this one
     */
    public boolean match(Term t) {
        return match(true, t); //Alberto
    }

    public boolean isEmptySet() {
        return false;
    }

    public boolean isSet() {
        return false;
    }

    public boolean isTuple() {
        return false;
    }

    /**
     * Tries to unify two terms, given a demonstration context
     * identified by the mark integer.
     * <p>
     * Try the unification among the term and the term specified
     *
     * @param varsUnifiedArg1      Vars unified in myself
     * @param varsUnifiedArg2      Vars unified in term t
     * @param isOccursCheckEnabled
     */
    abstract boolean unify(List<Var> varsUnifiedArg1, List<Var> varsUnifiedArg2, Term t, boolean isOccursCheckEnabled);

    // term representation

    /**
     * Tries to unify two terms, given a demonstration context
     * identified by the mark integer.
     * <p>
     * Try the unification among the term and the term specified
     *
     * @param varsUnifiedArg1 Vars unified in myself
     * @param varsUnifiedArg2 Vars unified in term t
     */
    abstract boolean unify(List<Var> varsUnifiedArg1, List<Var> varsUnifiedArg2, Term t);

    /**
     * Gets the string representation of this term
     * as an X argument of an operator, considering the associative property.
     */
    String toStringAsArgX(OperatorManager op, int prio) {
        return toStringAsArg(op, prio, true);
    }

    /**
     * Gets the string representation of this term
     * as an Y argument of an operator, considering the associative property.
     */
    String toStringAsArgY(OperatorManager op, int prio) {
        return toStringAsArg(op, prio, false);
    }

    /**
     * Gets the string representation of this term
     * as an argument of an operator, considering the associative property.
     * <p>
     * If the boolean argument is true, then the term must be considered
     * as X arg, otherwise as Y arg (referring to prolog associative rules)
     */
    String toStringAsArg(OperatorManager op, int prio, boolean x) {
        return toString();
    }

    /*Castagna 06/2011*/

    /**
     * The iterated-goal term G of a term T is a term defined
     * recursively as follows:
     * <ul>
     * <li>if T unifies with ^(_, Goal) then G is the iterated-goal
     * term of Goal</li>
     * <li>else G is T</li>
     * </ul>
     */
    public Term iteratedGoalTerm() {
        return this;
    }

    /**
     * Visitor pattern
     *
     * @param tv - Visitor
     */
    public abstract <T> T accept(TermVisitor<T> tv);

    public <T extends Term> T castTo(Class<T> klass) {
        return klass.cast(this);
    }

    public static Comparator<Term> lexicographicComparator() {
        return (t1, t2) -> {
            if (t1.getTerm() instanceof Var) {
                if (!(t2.getTerm() instanceof Var)) {
                    return -1;
                }
            } else {
                if (t2.getTerm() instanceof Var) {
                    return 1;
                }
            }
            return t1.toString().compareTo(t2.toString());
        };
    }

    public abstract boolean equals(Term other, EnumSet<Term.Comparison> comparison);

    public enum Comparison {
        VARIABLES_AS_PLACEHOLDERS,
        VARIABLES_BY_NAME,
        VARIABLES_BY_COMPLETE_NAME,
        NUMBERS_BY_TYPE,
        NUMBERS_BY_VALUE,
        CONSTANTS_BY_REPRESENTED_VALUE;

        public static final EnumSet<Comparison> STRICT = EnumSet.of(VARIABLES_BY_COMPLETE_NAME, NUMBERS_BY_TYPE);
        public static final EnumSet<Comparison> NORMAL = EnumSet.of(VARIABLES_BY_NAME, NUMBERS_BY_VALUE);
        public static final EnumSet<Comparison> LOOSE = EnumSet.of(VARIABLES_BY_NAME, CONSTANTS_BY_REPRESENTED_VALUE);
        public static final EnumSet<Comparison> STRUCTURAL = EnumSet.of(VARIABLES_AS_PLACEHOLDERS, NUMBERS_BY_TYPE);

    }
}