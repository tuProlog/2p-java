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
package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import alice.util.ArrayIterator;
import alice.util.DeepLogicListIterator;
import alice.util.LogicListIterator;
import alice.util.LogicTupleIterator;
import com.codepoetics.protonpack.StreamUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import java.util.ArrayList;

/**
 * Struct class represents both compound prolog term
 * and atom term (considered as 0-arity compound).
 */
public class Struct extends Term {

    private static final Pattern ATOM_REGEX = Pattern.compile("^[a-z][a-zA-Z0-9_]*$");

    @SuppressWarnings("unused")
    private String type = "Struct";

    /**
     * name of the structure
     */
    private String name;
    /**
     * args array
     */
    private Term[] arg;
    /**
     * arity
     */
    private int arity;
    /**
     * to speedup hash map operation
     */
    private String predicateIndicator;
    /**
     * primitive behaviour
     */
    private transient PrimitiveInfo primitive;
    /**
     * it indicates if the term is resolved
     */
    private boolean resolved = false;


    public static Struct emptySet() {
        return Struct.atom("{}");
    }

    public static Struct set(Term term) {
        return Struct.of("{}", term);
    }

    public static Struct set(Term term1, Term term2, Term terms) {
        return Struct.of("{}", tuple(term1, term2, terms));
    }

    public static Struct set(Collection<? extends Term> terms) {
        switch (terms.size()) {
            case 0: return emptySet();
            case 1: return set(terms.iterator().next());
            default: return set(tuple(terms));
        }
    }

    public static Struct set(Stream<? extends Term> terms) {
        return set(terms.collect(Collectors.toList()));
    }

    public static Struct set(Iterable<? extends Term> terms) {
        return set(StreamUtils.stream(terms));
    }

    public static Struct set(Iterator<? extends Term> terms) {
        return set(StreamUtils.stream(() -> terms));
    }

    public static Struct cut() {
        return Struct.atom("!");
    }

    public static Struct truth(boolean value) {
        return Struct.atom(value ? "true" : "fail");
    }

    public static Struct directive(Term body) {
        return Struct.of(":-", body);
    }

    public static Struct rule(Term head, Term body) {
        return Struct.of(":-", head, body);
    }

    @SuppressWarnings({"deprecated"})
    public static Struct atom(String atom) {
        return new Struct(atom);
    }

    @SuppressWarnings({"deprecated"})
    public static Struct of(String functor, Term term1, Term... terms) {
        return new Struct(functor, Stream.concat(Stream.of(term1), Stream.of(terms)).toArray(Term[]::new));
    }

    public static Struct of(String functor, Term[] terms) {
        return new Struct(functor, terms);
    }

    @SuppressWarnings({"deprecated"})
    public static Struct cons(Term head, Term tail) {
        return new Struct(head, tail);
    }

    @SuppressWarnings({"deprecated"})
    public static Struct emptyList() {
        return new Struct();
    }

    @SuppressWarnings({"deprecated"})
    public static Struct list(Term... terms) {
        return list(new ArrayIterator<>(terms), emptyList());
    }

    @SuppressWarnings({"deprecated"})
    public static Struct list(Stream<? extends Term> terms) {
        return list(terms.iterator(), emptyList());
    }

    @SuppressWarnings({"deprecated"})
    public static Struct list(Iterator<? extends Term> terms) {
        return list(terms, emptyList());
    }

    @SuppressWarnings({"deprecated"})
    public static Struct list(Iterable<? extends Term> terms) {
        return list(terms.iterator(), emptyList());
    }

    public static Struct list(Term[] items, Term last) {
        return list(new ArrayIterator<>(items), last);
    }

    public static Struct list(Stream<? extends Term> items, Term last) {
        return list(items.iterator(), last);
    }

    public static Struct list(Iterable<? extends Term> items, Term last) {
        return list(items.iterator(), last);
    }

    public static Struct list(Iterator<? extends Term> items, Term last) {
        Objects.requireNonNull(last);
        Objects.requireNonNull(items);

        if (!items.hasNext()) {
            if (last.isList()) {
                return (Struct) last;
            } else {
                throw new IllegalArgumentException(String.format("Cannot build a list of the %s term alone", last));
            }
        }

        return new Struct(items, last);
    }

    public static Struct tuple(Term term1, Term term2, Term... terms) {
        return tuple(
                Stream.concat(
                        Stream.of(term1, term2),
                        Stream.of(terms)
                )
        );
    }

    public static Struct tuple(Collection<? extends Term> terms) {
        return tuple(new ArrayList<>(terms));
    }

    public static Struct tuple(Iterable<? extends Term> terms) {
        return tuple(StreamUtils.stream(terms));
    }

    public static Struct tuple(Iterator<? extends Term> terms) {
        return tuple(StreamUtils.stream(() -> terms));
    }

    public static Struct tuple(Stream<? extends Term> terms) {
        return tuple(terms.collect(Collectors.toList()));
    }

    public static Struct tuple(List<? extends Term> terms) {
        final int size = terms.size();

        if (size < 2) {
            throw new IllegalArgumentException(String.format("Tuples can be created out of 2 or more terms, only %d have been provided", size));
        }

        Struct tuple = Struct.of(",", terms.get(size - 2), terms.get(size - 1));
        for (int i = size - 3; i >= 0; i--) {
            tuple = Struct.of(",", terms.get(i), tuple);
        }
        return tuple;
    }

    private Struct(String name, int arity) {
        if (name == null) {
            throw new InvalidTermException("The functor of a Struct cannot be null");
        }
        if (name.length() == 0 && arity > 0) {
            throw new InvalidTermException("The functor of a non-atom Struct cannot be an emptyWithStandardOperators string");
        }
        this.name = name;
        this.arity = arity;
        if (this.arity > 0) {
            arg = new Term[this.arity];
        }
        predicateIndicator = this.name + "/" + this.arity;
        resolved = false;
    }


    /**
     * Builds a compound, with an array of arguments
     */
    @Deprecated
    private Struct(String f, Term... argList) {
        this(f, argList.length);
        for (int i = 0; i < argList.length; i++) {
            if (argList[i] == null) {
                throw new InvalidTermException("Arguments of a Struct cannot be null");
            } else {
                arg[i] = argList[i];
            }
        }
    }


    /**
     * Builds a structure representing an emptyWithStandardOperators list
     */
    @Deprecated
    public Struct() {
        this("[]", 0);
        resolved = true;
    }

    @Deprecated
    public Struct(Collection<? extends Term> terms) {
        this(terms.iterator());
    }

    /**
     * Builds a list providing head and tail
     */
    @Deprecated
    public Struct(Term h, Term t) {
        this(".", 2);
        arg[0] = Objects.requireNonNull(h);
        arg[1] = Objects.requireNonNull(t);
    }

    /**
     * Builds a list specifying the elements
     */
    @Deprecated
    public Struct(Term... argList) {
        this(argList, 0);
    }

    @Deprecated
    public Struct(Stream<? extends Term> stream) {
        this(stream.iterator());
    }

    private Struct(Iterator<? extends Term> i, Term last) {
        this(".", 2);

        arg[0] = i.next();

        if (i.hasNext()) {
            arg[1] = new Struct(i, last);
        } else {
            arg[1] = last;
        }
    }

    @Deprecated
    public Struct(Iterator<? extends Term> i) {
        this(".", 2);
        if (i.hasNext()) {
            arg[0] = Objects.requireNonNull(i.next());
            arg[1] = new Struct(i);
        } else {
            // build an emptyWithStandardOperators list
            name = "[]";
            arity = 0;
            arg = null;
        }
    }

    /**
     * Builds a compound, with a linked list of arguments
     */
    @Deprecated
    Struct(String f, Deque<Term> al) {
        name = f;
        arity = al.size();
        if (arity > 0) {
            arg = new Term[arity];
            for (int c = 0; c < arity; c++) {
                arg[c] = Objects.requireNonNull(al.removeFirst());
            }
        }
        predicateIndicator = name + "/" + arity;
        resolved = false;
    }

    private Struct(Term[] argList, int index) {
        this(".", 2);
        if (index < argList.length) {
            arg[0] = Objects.requireNonNull(argList[index]);
            arg[1] = new Struct(argList, index + 1);
        } else {
            // build an emptyWithStandardOperators list
            name = "[]";
            arity = 0;
            arg = null;
        }
    }

    private Struct(int arity) {
        this.arity = arity;
        arg = new Term[this.arity];
    }

    /**
     * @deprecated Use Struct#getPredicateIndicator instead.
     */
    String getHashKey() {
        return getPredicateIndicator();
    }

    /**
     * @return
     */
    String getPredicateIndicator() {
        return predicateIndicator;
    }

    /**
     * Gets the number of elements of this structure
     */
    public int getArity() {
        return arity;
    }

    /**
     * Gets the functor name  of this structure
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the i-th element of this structure
     * <p>
     * No bound check is done
     */
    public Term getArg(int index) {
        return arg[index];
    }

    /**
     * Sets the i-th element of this structure
     * <p>
     * (Only for internal service)
     */
    public void setArg(int index, Term argument) {
        arg[index] = argument;
    }

    /**
     * Gets the i-th element of this structure
     * <p>
     * No bound check is done. It is equivalent to
     * <code>getArg(index).getTerm()</code>
     */
    public Term getTerm(int index) {
        if (!(arg[index] instanceof Var)) {
            return arg[index];
        }
        return arg[index].getTerm();
    }

    /**
     * is this term a prolog numeric term?
     */
    @Deprecated
    public boolean isNumber() {
        return false;
    }

    /**
     * is this term a struct
     */
    @Deprecated
    public boolean isStruct() {
        return true;
    }

    /**
     * is this term a variable
     */
    @Deprecated
    public boolean isVar() {
        return false;
    }

    public boolean isAtomic() {
        return arity == 0;
    }

    public boolean isCompound() {
        return arity > 0;
    }

    public boolean isAtom() {
        return (arity == 0 || isEmptyList());
    }

    public boolean isGround() {
        for (int i = 0; i < arity; i++) {
            if (!arg[i].isGround()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object t) {
        if (super.equals(t)) return true;
        if (!(t instanceof Term)) return false;

        if (!(((Term) t).getTerm() instanceof Struct)) return false;

        Struct other = (Struct) ((Term) t).getTerm();

        return getName().equals(other.getName())
                && getArity() == other.getArity()
                && Arrays.equals(
                Optional.ofNullable(arg).orElseGet(() -> new Term[0]),
                Optional.ofNullable(other.arg).orElseGet(() -> new Term[0])
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getName(),
                getArity(),
                Arrays.hashCode(Optional.ofNullable(arg).orElseGet(() -> new Term[0]))
        );
    }

    /**
     * Check is this struct is clause or directive
     */
    public boolean isClause() {
        return (name.equals(":-") && arity > 1 && arg[0].getTerm() instanceof Struct);
    }

    public Term getTerm() {
        return this;
    }

    /**
     * Gets an argument inside this structure, given its name
     *
     * @param name name of the structure
     * @return the argument or null if not found
     */
    public Struct getArg(String name) {
        if (arity == 0) {
            return null;
        }
        for (int i = 0; i < arg.length; i++) {
            if (arg[i] instanceof Struct) {
                Struct s = (Struct) arg[i];
                if (s.getName().equals(name)) {
                    return s;
                }
            }
        }
        for (int i = 0; i < arg.length; i++) {
            if (arg[i] instanceof Struct) {
                Struct s = (Struct) arg[i];
                Struct sol = s.getArg(name);
                if (sol != null) {
                    return sol;
                }
            }
        }
        return null;
    }

    /**
     * Test if a term is greater than other
     */
    public boolean isGreater(Term t) {
        t = t.getTerm();
        if (!(t instanceof Struct)) {
            return true;
        } else {
            Struct ts = (Struct) t;
            int tarity = ts.arity;
            if (arity > tarity) {
                return true;
            } else if (arity == tarity) {
                if (name.compareTo(ts.name) > 0) {
                    return true;
                } else if (name.compareTo(ts.name) == 0) {
                    for (int c = 0; c < arity; c++) {
                        if (arg[c].isGreater(ts.arg[c])) {
                            return true;
                        } else if (!arg[c].isEqual(ts.arg[c])) {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets a copy of this structure
     *
     * @param vMap is needed for register occurence of same variables
     */
    Term copy(Map<Var, Var> vMap, int idExecCtx) {
        Struct t = new Struct(arity);
        t.resolved = resolved;
        t.name = name;
        t.predicateIndicator = predicateIndicator;
        t.primitive = primitive;
        for (int c = 0; c < arity; c++) {
            //if(!this.arg[c].isCyclic)
            t.arg[c] = arg[c].copy(vMap, idExecCtx);
            //else
            //	t.arg[c] = this.arg[c];
        }
        return t;
    }

    @Override
    public Term copyAndRetainFreeVar(Map<Var, Var> vMap, int idExecCtx) {
        Struct t = new Struct(arity);
        t.resolved = resolved;
        t.name = name;
        t.predicateIndicator = predicateIndicator;
        t.primitive = primitive;
        for (int c = 0; c < arity; c++) {
            //if(!this.arg[c].isCyclic)
            t.arg[c] = arg[c].getTerm().copyAndRetainFreeVar(vMap, idExecCtx);
            //qui una .getTerm() necessaria solo in $wt_list!
            //else
            //	t.arg[c] = this.arg[c];
        }
        return t;
    }

    /**
     * Gets a copy of this structure
     *
     * @param vMap is needed for register occurence of same variables
     */
    Term copy(Map<Var, Var> vMap, Map<Term, Var> substMap) {
        Struct t = new Struct(arity);
        t.resolved = false;
        t.name = name;
        t.predicateIndicator = predicateIndicator;
        t.primitive = null;
        for (int c = 0; c < arity; c++) {
            //if(!this.arg[c].isCyclic)
            t.arg[c] = arg[c].copy(vMap, substMap);
            //else
            //	t.arg[c] = this.arg[c];
        }
        return t;
    }

    public Struct copy() {
        return (Struct) super.copy();
    }

    /**
     * resolve term
     */
    long resolveTerm(long count) {
        if (resolved) {
            return count;
        } else {
            LinkedList<Var> vars = new LinkedList<Var>();
            return resolveTerm(vars, count);
        }
    }

    /**
     * Resolve name of terms
     *
     * @param vl    list of variables resolved
     * @param count start timestamp for variables of this term
     * @return next timestamp for other terms
     */
    long resolveTerm(LinkedList<Var> vl, long count) {
        long newcount = count;
        for (int c = 0; c < arity; c++) {
            Term term = arg[c];
            if (term != null) {
                //--------------------------------
                // we want to resolve only not linked variables:
                // so linked variables must get the linked term
                term = term.getTerm();
                //--------------------------------
                if (term instanceof Var) {
                    Var t = (Var) term;
                    t.setInternalTimestamp(newcount++);
                    if (!t.isAnonymous()) {
                        // searching a variable with the same name in the list
                        String name = t.getName();
                        Iterator<Var> it = vl.iterator();
                        Var found = null;
                        while (it.hasNext()) {
                            Var vn = it.next();
                            if (name.equals(vn.getName())) {
                                found = vn;
                                break;
                            }
                        }
                        if (found != null) {
                            arg[c] = found;
                        } else {
                            vl.add(t);
                        }
                    }
                } else if (term instanceof Struct) {
                    newcount = ((Struct) term).resolveTerm(vl, newcount);
                }
            }
        }
        resolved = true;
        return newcount;
    }

    @Override
    public boolean isCons() {
        return name.equals(".") && arity == 2;
    }

    @Override
    public boolean isList() {
        return isCons() && getArg(1).isList() || isEmptyList();
    }

    @Override
    public boolean isEmptyList() {
        return "[]".equals(name) && arity == 0;
    }

    @Override
    public boolean isEmptySet() {
        return "{}".equals(name) && arity == 0;
    }

    @Override
    public boolean isSet() {
        return "{}".equals(name) && arity <= 1;
    }

    @Override
    public boolean isTuple() {
        return ",".equals(name) && arity == 2;
    }

    /**
     * Gets the head of this structure, which is supposed to be a list.
     *
     * <p>
     * Gets the head of this structure, which is supposed to be a list.
     * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
     * </p>
     */
    public Term listHead() {
        if (!isList()) {
            throw new UnsupportedOperationException("The structure " + this + " is not a list.");
        }
        return arg[0].getTerm();
    }

    /**
     * Gets the tail of this structure, which is supposed to be a list.
     *
     * <p>
     * Gets the tail of this structure, which is supposed to be a list.
     * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
     * </p>
     */
    public Struct listTail() {
        if (!isList()) {
            throw new UnsupportedOperationException("The structure " + this + " is not a list.");
        }
        return (Struct) arg[1].getTerm();
    }

    /**
     * Gets the number of elements of this structure, which is supposed to be a list.
     *
     * <p>
     * Gets the number of elements of this structure, which is supposed to be a list.
     * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
     * </p>
     */
    public int listSize() {
        return (int) listStream().count();
    }

    /**
     * Gets an iterator on the elements of this structure, which is supposed to be a list.
     *
     * <p>
     * Gets an iterator on the elements of this structure, which is supposed to be a list.
     * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
     * </p>
     */
    public Iterator<? extends Term> listIterator() {
        try {
            return new LogicListIterator(this);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public Iterator<? extends Term> unfoldedListIterator() {
        try {
            return new DeepLogicListIterator(this);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public Iterator<? extends Term> unfoldedTupleIterator() {
        try {
            return new LogicTupleIterator(this);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public Stream<? extends Term> listStream() {
        return StreamUtils.ofNullable(this::listIterator);
    }

    public Stream<? extends Term> unfoldedListStream() {
        return StreamUtils.ofNullable(this::unfoldedListIterator);
    }

    public Stream<? extends Term> unfoldedTupleStream() {
        return StreamUtils.ofNullable(this::unfoldedTupleIterator);
    }

    /**
     * Gets a list Struct representation, with the functor as first element.
     */
    Struct toList() {
        Struct t = emptyList();
        for (int c = arity - 1; c >= 0; c--) {
            t = cons(arg[c].getTerm(), t);
        }
        return cons(new Struct(name), t);
    }

    /**
     * Gets a flat Struct from this structure considered as a List
     * <p>
     * If this structure is not a list, null object is returned
     */
    Struct fromList() {
        Term ft = arg[0].getTerm();
        if (!ft.isAtom()) {
            return null;
        }
        Struct at = (Struct) arg[1].getTerm();
        LinkedList<Term> al = new LinkedList<Term>();
        while (!at.isEmptyList()) {
            if (!at.isList()) {
                return null;
            }
            al.addLast(at.getTerm(0));
            at = (Struct) at.getTerm(1);
        }
        return new Struct(((Struct) ft).name, al);
    }

    /**
     * Appends an element to this structure supposed to be a list
     */
    public void append(Term t) {
        if (isEmptyList()) {
            name = ".";
            arity = 2;
            predicateIndicator = name + "/" + arity; /* Added by Paolo Contessi */
            arg = new Term[arity];
            arg[0] = t;
            arg[1] = new Struct();
        } else if (arg[1].isList()) {
            ((Struct) arg[1]).append(t);
        } else {
            arg[1] = t;
        }
    }

    /**
     * Inserts (at the head) an element to this structure supposed to be a list
     */
    void insert(Term t) {
        Struct co = new Struct();
        co.arg[0] = arg[0];
        co.arg[1] = arg[1];
        arg[0] = t;
        arg[1] = co;
    }

    /**
     * Try to unify two terms
     *
     * @param t   the term to unify
     * @param vl1 list of variables unified
     * @param vl2 list of variables unified
     * @return true if the term is unifiable with this one
     */
    boolean unify(List<Var> vl1, List<Var> vl2, Term t, boolean isOccursCheckEnabled) {
        // In fase di unificazione bisogna annotare tutte le variabili della struct completa.
        t = t.getTerm();
        if (t instanceof Struct) {
            Struct ts = (Struct) t;
            if (arity == ts.arity && name.equals(ts.name)) {
                for (int c = 0; c < arity; c++) {
                    if (!arg[c].unify(vl1, vl2, ts.arg[c], isOccursCheckEnabled)) {
                        return false;
                    }
                }
                return true;
            }
        } else if (t instanceof Var) {
            return t.unify(vl2, vl1, this, isOccursCheckEnabled);
        }
        return false;
    }

    /**
     * dummy method
     */
    public void free() {
    }

    /**
     * Get primitive behaviour associated at structure
     */
    public PrimitiveInfo getPrimitive() {
        return primitive;
    }

    /**
     * Check if this term is a primitive struct
     */
    public boolean isPrimitive() {
        return primitive != null;
    }

    /**
     * Set primitive behaviour associated at structure
     */
    void setPrimitive(PrimitiveInfo b) {
        primitive = b;
    }

    public boolean isFunctorAtomic() {
        return ATOM_REGEX.matcher(getName()).matches();
    }


    /**
     * Gets the string representation of this structure
     * <p>
     * Specific representations are provided for lists and atoms.
     * Names starting with upper case letter are enclosed in apices.
     */
    public String toString() {
        // emptyWithStandardOperators list case
        if (isEmptyList()) {
            return "[]";
        } else if (isEmptySet()) {
            return "{}";
        } else if (this.isList()) {
            return ("[" + toString0() + "]");
        } else if (isSet()) {
            return ("{" + toString0_bracket() + "}");
        } else {
            String s = (isFunctorAtomic() ? name : "'" + name + "'");
            if (arity > 0) {
                s = s + "(";
                for (int c = 1; c < arity; c++) {
                    if (!(arg[c - 1] instanceof Var)) {
                        s = s + arg[c - 1].toString() + ",";
                    } else {
                        s = s + ((Var) arg[c - 1]).toStringFlattened() + ",";
                    }
                }
                if (!(arg[arity - 1] instanceof Var)) {
                    s = s + arg[arity - 1].toString() + ")";
                } else {
                    s = s + ((Var) arg[arity - 1]).toStringFlattened() + ")";
                }
            }
            return s;
        }
    }

    private String toString0() {
        Term h = arg[0].getTerm();
        Term t = arg[1].getTerm();
        if (t.isEmptyList()) {
            if (h instanceof Var) {
                return ((Var) h).toStringFlattened();
            } else {
                return h.toString();
            }
        } else if (t.isList()) {
            Struct tl = (Struct) t;
            if (h instanceof Var) {
                return (((Var) h).toStringFlattened() + "," + tl.toString0());
            } else {
                return (h.toString() + "," + tl.toString0());
            }
        } else {
            String h0;
            String t0;
            if (h instanceof Var) {
                h0 = ((Var) h).toStringFlattened();
            } else {
                h0 = h.toString();
            }
            if (t instanceof Var) {
                t0 = ((Var) t).toStringFlattened();
            } else {
                t0 = t.toString();
            }
            return (h0 + "|" + t0);
        }
    }

    private String toString0_bracket() {
        if (arity == 0) {
            return "";
        } else if (arity == 1 && !((arg[0] instanceof Struct) && ((Struct) arg[0]).getName().equals(","))) {
            return arg[0].getTerm().toString();
        } else {
            // comma case 
            Term head = ((Struct) arg[0]).getTerm(0);
            Term tail = ((Struct) arg[0]).getTerm(1);
            StringBuffer buf = new StringBuffer(head.toString());
            while (tail instanceof Struct && ((Struct) tail).getName().equals(",")) {
                head = ((Struct) tail).getTerm(0);
                buf.append("," + head.toString());
                tail = ((Struct) tail).getTerm(1);
            }
            buf.append("," + tail.toString());
            return buf.toString();
        }
    }

    private String toStringAsList(OperatorManager op) {
        Term h = arg[0];
        Term t = arg[1].getTerm();
        if (t.isList()) {
            Struct tl = (Struct) t;
            if (tl.isEmptyList()) {
                return h.toStringAsArgY(op, 0);
            }
            return (h.toStringAsArgY(op, 0) + "," + tl.toStringAsList(op));
        } else {
            return (h.toStringAsArgY(op, 0) + "|" + t.toStringAsArgY(op, 0));
        }
    }

    String toStringAsArg(OperatorManager op, int prio, boolean x) {
        int p = 0;
        String v = "";

        if (name.equals(".") && arity == 2) {
            if (arg[0].isEmptyList()) {
                return ("[]");
            } else {
                return ("[" + toStringAsList(op) + "]");
            }
        } else if (name.equals("{}")) {
            return ("{" + toString0_bracket() + "}");
        }

        if (arity == 2) {
            if ((p = op.getOperatorPriority(name, "xfx")) >= OperatorManager.OP_LOW) {
                return (
                        (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                        arg[0].toStringAsArgX(op, p) +
                        " " + name + " " +
                        arg[1].toStringAsArgX(op, p) +
                        (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
            }
            if ((p = op.getOperatorPriority(name, "yfx")) >= OperatorManager.OP_LOW) {
                return (
                        (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                        arg[0].toStringAsArgY(op, p) +
                        " " + name + " " +
                        arg[1].toStringAsArgX(op, p) +
                        (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
            }
            if ((p = op.getOperatorPriority(name, "xfy")) >= OperatorManager.OP_LOW) {
                if (!name.equals(",")) {
                    return (
                            (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                            arg[0].toStringAsArgX(op, p) +
                            " " + name + " " +
                            arg[1].toStringAsArgY(op, p) +
                            (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
                } else {
                    return (
                            (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                            arg[0].toStringAsArgX(op, p) +
                            //",\n\t"+
                            "," +
                            arg[1].toStringAsArgY(op, p) +
                            (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
                }
            }
        } else if (arity == 1) {
            if ((p = op.getOperatorPriority(name, "fx")) >= OperatorManager.OP_LOW) {
                return (
                        (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                        name + " " +
                        arg[0].toStringAsArgX(op, p) +
                        (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
            }
            if ((p = op.getOperatorPriority(name, "fy")) >= OperatorManager.OP_LOW) {
                return (
                        (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                        name + " " +
                        arg[0].toStringAsArgY(op, p) +
                        (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
            }
            if ((p = op.getOperatorPriority(name, "xf")) >= OperatorManager.OP_LOW) {
                return (
                        (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                        arg[0].toStringAsArgX(op, p) +
                        " " + name + " " +
                        (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
            }
            if ((p = op.getOperatorPriority(name, "yf")) >= OperatorManager.OP_LOW) {
                return (
                        (((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
                        arg[0].toStringAsArgY(op, p) +
                        " " + name + " " +
                        (((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
            }
        }
        v = (isFunctorAtomic()? name : "'" + name + "'");
        if (arity == 0) {
            return v;
        }
        v = v + "(";
        for (p = 1; p < arity; p++) {
            v = v + arg[p - 1].toStringAsArgY(op, 0) + ",";
        }
        v = v + arg[arity - 1].toStringAsArgY(op, 0);
        v = v + ")";
        return v;
    }

    public Term iteratedGoalTerm() {
        if (name.equals("^") && arity == 2) {
            Term goal = getTerm(1);
            return goal.iteratedGoalTerm();
        } else {
            return super.iteratedGoalTerm();
        }
    }

    @Override
    public <T> T accept(TermVisitor<T> tv) {
        return tv.visit(this);
    }

    @Override
    boolean unify(List<Var> varsUnifiedArg1, List<Var> varsUnifiedArg2, Term t) {
        return unify(varsUnifiedArg1, varsUnifiedArg2, t, true);
    }

    public Term[] getArgs() {
        return Arrays.copyOf(arg, arg.length);
    }
}