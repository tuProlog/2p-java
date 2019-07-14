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

import java.util.*;

/**
 * This class represents a variable term.
 * Variables are identified by a name (which must starts with
 * an upper case letter) or the anonymous ('_') name.
 *
 * @see Term
 */
public class Var extends Term {

    /* Identify kind of renaming */
    final static int ORIGINAL = -1;
    final static int PROGRESSIVE = -2;
    final static String ANY = "_";
    private static final long serialVersionUID = 1L;
    private static long fingerprint = 0; //Alberto //static version as global counter

    private String name;
    private StringBuilder completeName;    /* Reviewed by Paolo Contessi */
    private Term link;                   /* link is used for unification process */
    private long internalTimestamp;      /* internalTimestamp is used for fix vars order (resolveTerm()) */
    private int ctxid;                  /* id of ExecCtx owners of this var util for renaming*/

    private long fingerPrint; //fingerPrint is a unique id (per run) used for var comparison

    @SuppressWarnings({"deprecated"})
    public static Var of(String name) {
        return new Var(name);
    }

    @SuppressWarnings({"deprecated"})
    public static Var of() {
        return new Var();
    }

    @SuppressWarnings({"deprecated"})
    public static Var underscore() {
        return new Var();
    }

    @SuppressWarnings({"deprecated"})
    public static Var anonymous() {
        return new Var();
    }

    /**
     * Creates a variable identified by a name.
     * <p>
     * The name must starts with an upper case letter or the underscore. If an underscore is
     * specified as a name, the variable is anonymous.
     *
     * @param n is the name
     * @throws InvalidTermException if n is not a valid Prolog variable name
     */
    @Deprecated
    public Var(String n) {
        link = null;
        ctxid = Var.ORIGINAL; //no execCtx owners
        internalTimestamp = 0;
        fingerPrint = getFingerprint();
        if (n.equals(ANY)) {
            name = null;
            completeName = new StringBuilder();
        } else if (Character.isUpperCase(n.charAt(0)) || n.startsWith(ANY)) {
            name = n;
            completeName = new StringBuilder(n);
        } else {
            throw new InvalidTermException("Illegal variable name: " + n);
        }
    }

    /**
     * Creates an anonymous variable
     * <p>
     * This is equivalent to build a variable with name _
     */
    @Deprecated
    public Var() {
        name = null;
        completeName = new StringBuilder();
        link = null;
        ctxid = Var.ORIGINAL;
        internalTimestamp = 0;
        fingerPrint = getFingerprint();
    }

    /**
     * Creates a internal engine variable.
     *
     * @param n     is the name
     * @param id    is the id of ExecCtx
     * @param alias code to discriminate external vars
     * @param count  is timestamp
     */
    private Var(String n, int id, int alias, long count/*, boolean isCyclic*/) {
        name = n;
        completeName = new StringBuilder();
        internalTimestamp = count;
        //this.isCyclic = isCyclic;
        fingerPrint = getFingerprint();
        link = null;
        if (id < 0) {
            id = Var.ORIGINAL;
        }
        rename(id, alias);
    }

    //Alberto
    static synchronized long getFingerprint() { //called by Var constructors
        fingerprint++;
        return fingerprint;
    }

    /**
     * De-unify the variables of list
     */
    public static void free(List<Var> varsUnified) {
        for (Var v : varsUnified) {
            v.free();
        }
    }

    /**
     * Rename variable (assign completeName)
     */
    void rename(int idExecCtx, int count) { /* Reviewed by Paolo Contessi */
        ctxid = idExecCtx;

        if (ctxid > Var.ORIGINAL) {
            completeName = completeName
                    .delete(0, completeName.length())
                    .append(name).append("_e").append(ctxid);
        } else if (ctxid == ORIGINAL) {
            completeName = completeName
                    .delete(0, completeName.length())
                    .append(name);
        } else if (ctxid == PROGRESSIVE) {
            completeName = completeName
                    .delete(0, completeName.length())
                    .append("_").append(count);
        }
    }

    @Override
    public boolean equals(Object t) {
        if (super.equals(t)) return true;
        if (!(t instanceof Term)) return false;

        Term thiz = getTerm();
        Term other = ((Term) t).getTerm();

        if (thiz instanceof Var) {
            if (other instanceof Var) {
                return thiz.toString().equals(other.toString());
            }
        } else {
            if (!(other instanceof Var)) {
                return thiz.equals(other);
            }
        }

        return false;
    }

    @Override
    public boolean equals(Term other, EnumSet<Comparison> comparison) {
        Term thiz = getTerm();
        Term otherTerm = other.getTerm();

        if (comparison.contains(Comparison.VARIABLES_AS_PLACEHOLDERS)) {
            return thiz instanceof Var && otherTerm instanceof Var;
        } else if (comparison.contains(Comparison.VARIABLES_BY_NAME)) {
            if (thiz instanceof Var) {
                if (otherTerm instanceof Var) {
                    return Objects.equals(((Var) thiz).name, ((Var) otherTerm).name);
                }
            } else {
                if (!(otherTerm instanceof Var)) {
                    return thiz.equals(otherTerm, comparison);
                }
            }
        } else if (comparison.contains(Comparison.VARIABLES_BY_COMPLETE_NAME)) {
            if (thiz instanceof Var) {
                if (otherTerm instanceof Var) {
                    return ((Var) thiz).getName().equals(((Var) otherTerm).getName());
                }
            } else {
                if (!(otherTerm instanceof Var)) {
                    return thiz.equals(otherTerm, comparison);
                }
            }
        } else {
            return thiz == otherTerm;
        }

        return false;
    }

    @Override
    public int hashCode() {
        final Term thiz = getTerm();

        if (thiz instanceof Var) {
            return thiz.toString().hashCode();
        } else {
            return thiz.hashCode();
        }
    }

    /**
     * Gets a copy of this variable.
     * <p>
     * if the variable is not present in the list passed as argument,
     * a copy of this variable is returned and added to the list. If instead
     * a variable with the same time identifier is found in the list,
     * then the variable in the list is returned.
     */
    @Override
    Term copy(Map<Var, Var> vMap, int idExecCtx) {
        Term tt = getTerm();
        if (tt == this) {
            Var v = vMap.get(this);
            if (v == null) {
                //No occurence of v before
                v = new Var(name, idExecCtx, 0, internalTimestamp/*, this.isCyclic*/);
                vMap.put(this, v);
            }
            return v;
        } else {
            return tt.copy(vMap, idExecCtx);
        }
    }

    @Override //Alberto
    public Term copyAndRetainFreeVar(Map<Var, Var> vMap, int idExecCtx) {
        Term tt = getTerm();
        if (tt == this) {
            Var v = vMap.computeIfAbsent(this, k -> this);
            return v;
        } else {
            return tt.copy(vMap, idExecCtx);
        }
    }

    /**
     * Gets a copy of this variable.
     */
    @Override
    Term copy(Map<Var, Var> vMap, Map<Term, Var> substMap) {
        Var v;
        Object temp = vMap.get(this);
        if (temp == null) {
            v = new Var(null, Var.PROGRESSIVE, vMap.size(), internalTimestamp/*, this.isCyclic*/);
            vMap.put(this, v);
        } else {
            v = (Var) temp;
        }

        //if(v.isCyclic) //Alberto
        //	return v;

        Term t = getTerm();
        if (t instanceof Var) {
            Object tt = substMap.get(t);
            if (tt == null) {
                substMap.put(t, v);
                v.link = null;
            } else {
                v.link = (tt != v) ? (Var) tt : null;
            }
        }
        if (t instanceof Struct) {
            v.link = t.copy(vMap, substMap);
        }
        if (t instanceof Number) {
            v.link = t;
        }
        return v;
    }

    public Var copy() {
        return (Var) copy(new HashMap<>(), 0);
    }

    /**
     * De-unify the variable
     */
    public void free() {
        link = null;
    }

    /**
     * Gets the name of the variable
     */
    public String getName() {
        if (name != null) {
            return completeName.toString();
        } else {
            return ANY;
        }
    }

    public void setName(String s) {
        this.name = s;
    }

    /**
     * Gets the name of the variable
     */
    public String getOriginalName() {
        if (name != null) {
            return name;
        } else {
            return ANY + "" + this.fingerPrint; //Alberto
        }
    }

    /**
     * Gets the term which is referred by the variable.
     * <p>
     * For unbound variable it is the variable itself, while
     * for bound variable it is the bound term.
     */
    public Term getTerm() {
        Term tt = this;
        Term t = link;
        while (t != null) {
            tt = t;
            if (t instanceof Var) {
                t = ((Var) t).link;
            } else {
                break;
            }
        }
        return tt;
    }

    /**
     * Gets the term which is direct referred by the variable.
     */
    public Term getLink() {
        return link;
    }

    /**
     * Set the term which is direct bound
     */
    void setLink(Term l) {
        link = l;
    }

    /**
     * Set the timestamp
     */
    void setInternalTimestamp(long t) {
        internalTimestamp = t;
    }

    @Deprecated
    public boolean isNumber() {
        return false;
    }

    @Deprecated
    public boolean isStruct() {
        return false;
    }

    @Deprecated
    public boolean isVar() {
        return true;
    }

    public boolean isEmptyList() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isEmptyList();
        }
    }

    public boolean isAtomic() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isAtomic();
        }
    }

    public boolean isCompound() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isCompound();
        }
    }

    public boolean isAtom() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isAtom();
        }
    }

    public boolean isList() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isList();
        }
    }

    @Override
    public boolean isCons() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isCons();
        }
    }

    @Override
    public boolean isEmptySet() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isEmptySet();
        }
    }

    @Override
    public boolean isSet() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isSet();
        }
    }

    @Override
    public boolean isTuple() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isTuple();
        }
    }

    public boolean isGround() {
        Term t = getTerm();
        if (t == this) {
            return false;
        } else {
            return t.isGround();
        }
    }

    /**
     * Tests if this variable is ANY
     */
    public boolean isAnonymous() {
        return name == null;
    }

    /**
     * Tests if this variable is bound
     */
    public boolean isBound() {
        return link != null;
    }

    /**
     * finds var occurence in a Struct, doing occur-check.
     * (era una findIn)
     *
     * @param vl     TODO
     * @param t
     */
    private boolean occurCheck(List<Var> vl, Struct t) {
        int arity = t.getArity();
        for (int c = 0; c < arity; c++) {
            Term at = t.getTerm(c);
            if (at instanceof Struct) {
                if (occurCheck(vl, (Struct) at)) {
                    return true;
                }
            } else if (at instanceof Var) {
                Var v = (Var) at;
                if (v.link == null) {
                    vl.add(v);
                }
                if (this == v) {
                    return true;
                }
            }
        }
        return false;
    }

    //Alberto
    private void checkVar(List<Var> vl, Term t) {
        Struct st = (Struct) t;
        int arity = st.getArity();
        for (int c = 0; c < arity; c++) {
            Term at = st.getTerm(c);
            if (at instanceof Var) {
                Var v = (Var) at;
                if (v.link == null) {
                    vl.add(v);
                }
            } else if (at instanceof Struct) {
                checkVar(vl, at);
            }
        }
    }

    /**
     * Resolve the occurence of variables in a Term
     */
    long resolveTerm(long count) {
        Term tt = getTerm();
        if (tt != this) {
            return tt.resolveTerm(count);
        } else {
            internalTimestamp = count;
            return count++;
        }
    }

    /**
     * var unification.
     * <p>
     * First, verify the Term eventually already unified with the same Var
     * if the Term exist, unify var with that term, in order to handle situation
     * as (A = p(X) , A = p(1)) which must produce X/1.
     * <p>
     * If instead the var is not already unified, then:
     * <p>
     * if the Term is a var bound to X, then try unification with X
     * so for example if A=1, B=A then B is unified to 1 and not to A
     * (note that it's coherent with chronological backtracking:
     * the eventually backtracked A unification is always after
     * backtracking of B unification.
     * <p>
     * if are the same Var, unification must succeed, but without any new
     * bindings (to avoid cycles for extends in A = B, B = A)
     * <p>
     * if the term is a number, then it's a success and new link is created
     * (retractable by means of a code)
     * <p>
     * if the term is a compound, then occur check test is executed:
     * the var must not appear in the compound ( avoid X=p(X),
     * or p(X,X)=p(Y,f(Y)) ); if occur check is ok
     * then it's success and a new link is created (retractable by a code)
     * (test done if occursCheck is enabled)
     */
    boolean unify(List<Var> vl1, List<Var> vl2, Term t, boolean isOccursCheckEnabled) {
        Term tt = getTerm();
        if (tt == this) {
            t = t.getTerm();
            if (t instanceof Var) {
                ((Var) t).fingerPrint = this.fingerPrint; //Alberto
                if (this == t) {
                    try {
                        vl1.add(this);
                    } catch (NullPointerException e) {
                    }
                    return true;
                }
            } else if (t instanceof Struct) {
                if (isOccursCheckEnabled) {
                    if (occurCheck(vl2, (Struct) t)) {
                        //this.isCyclic = true;  //Alberto -> da usare quando si supporteranno i termini ciclici
                        return false; // da togliere
                    }
                } else {
                    checkVar(vl2, t); //Alberto
                }
            } else if (!(t instanceof Number)) {
                return false;
            }
            link = t;
            try {
                vl1.add(this);
            } catch (NullPointerException e) {
            }
            return true;
        } else {
            return (tt.unify(vl1, vl2, t, isOccursCheckEnabled));
        }
    }

    public boolean isGreater(Term t) {
        Term tt = getTerm();
        if (tt == this) {
            t = t.getTerm();
            if (!(t instanceof Var)) {
                return false;
            }
            return fingerPrint > ((Var) t).fingerPrint; //Alberto
        } else {
            return tt.isGreater(t);
        }
    }

    /**
     * Gets the string representation of this variable.
     * <p>
     * For bounded variables, the string is <Var Name>/<bound Term>.
     */
    @Override
    public String toString() {
        Term tt = getTerm();
        if (name != null) {
            if (tt == this/* || this.isCyclic*/) {
                //if(this.isCyclic) //Alberto
                // return name;
                return completeName.toString();
            } else {
                return (completeName.toString() + " / " + tt.toString());
            }
        } else {
            if (tt == this /*|| this.isCyclic*/) {
                return ANY + "" + this.fingerPrint; //Alberto
            } else {
                return tt.toString();
            }
        }
    }


    /**
     * Gets the string representation of this variable, providing
     * the string representation of the linked term in the case of
     * bound variable
     */
    public String toStringFlattened() {
        Term tt = getTerm();
        if (name != null) {
            if (tt == this /*|| this.isCyclic*/) {
                //if(this.isCyclic)
                // return name;
                return completeName.toString();
            } else {
                return tt.toString();
            }
        } else {
            if (tt == this /*|| this.isCyclic*/) {
                return ANY + "" + this.fingerPrint; //Alberto
            } else {
                return tt.toString();
            }
        }
    }

    @Override
    public <T> T accept(TermVisitor<T> tv) {
        return tv.visit(this.getTerm());
    }

    @Override
    boolean unify(List<Var> varsUnifiedArg1, List<Var> varsUnifiedArg2, Term t) {
        return unify(varsUnifiedArg1, varsUnifiedArg2, t, true);
    }
}