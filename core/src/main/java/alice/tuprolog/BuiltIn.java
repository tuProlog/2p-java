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

import alice.tuprolog.exceptions.InvalidLibraryException;
import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.util.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Library of built-in predicates
 *
 * @author Alex Benini
 */

public class BuiltIn extends Library {

    private static final long serialVersionUID = 1L;

    public BuiltIn(Prolog mediator) {
        super(mediator);
    }

    /**
     * Convert a term to a goal before executing it by means of call/1. See
     * section 7.6.2 of the ISO Standard for details.
     * <ul>
     * <li>If T is a variable then G is the control construct call, whose
     * argument is T.</li>
     * <li>If the principal functor of T is t ,?/2 or ;/2 or ->/2, then each
     * argument of T shall also be converted to a goal.</li>
     * <li>If T is an atom or compound term with principal functor FT, then G is
     * a predication whose predicate indicator is FT, and the arguments, if any,
     * of T and G are identical.</li>
     * </ul>
     * Note that a variable X and a term call(X) are converted to identical
     * bodies. Also note that if T is a number, then there is no goal which
     * corresponds to T.
     */
    static Term convertTermToGoal(Term term) {
        if (term instanceof Number) {
            return null;
        }
        if (term instanceof Var && ((Var) term).getLink() instanceof Number) {
            return null;
        }
        term = term.getTerm();
        if (term instanceof Var) {
            return Struct.of("call", term);
        }
        if (term instanceof Struct) {
            Struct s = (Struct) term;
            String pi = s.getPredicateIndicator();
            if (pi.equals(";/2") || pi.equals(",/2") || pi.equals("->/2")) {
                for (int i = 0; i < s.getArity(); i++) {
                    Term t = s.getArg(i);
                    Term arg = convertTermToGoal(t);
                    if (arg == null) {
                        return null;
                    }
                    s.setArg(i, arg);
                }
            }
        }
        return term;
    }

    /**
     * Defines some synonyms
     */
    public String[][] getSynonymMap() {
        return new String[][]{{"!", "cut", "predicate"},
                              {"=", "unify", "predicate"},
                              {"\\=", "deunify", "predicate"},
                {",", "comma", "predicate"},
                {"op", "$op", "predicate"},
                              {"solve", "initialization", "directive"},
                              {"consult", "include", "directive"},
                              {"load_library", "$load_library", "directive"}};
    }

    public boolean fail_0() {
        return false;
    }

    public boolean true_0() {
        return true;
    }

    public boolean halt_0() {
        System.exit(0);
        return true;
    }

    public boolean cut_0() {
        getEngine().getEngineManager().cut();
        return true;
    }

    private boolean assertImpl(Term arg0, boolean before) throws PrologError {
        arg0 = arg0.getTerm();
        final Struct struct0;

        if (arg0 instanceof Struct) {
            struct0 = (Struct) arg0;
            if (struct0.getName().equals(":-") && struct0.getArity() == 2) {
                ensureValidHead(struct0.getArg(0));
                ensureValidBody(struct0.getArg(1));
                ensureNonStatic((Struct) struct0.getArg(0), "modify", "static_clause");
            } else {
                ensureNonStatic(struct0, "modify", "static_clause");
            }
            if (before) {
                getEngine().getTheoryManager().assertA(struct0, true, null, false);
            } else {
                getEngine().getTheoryManager().assertZ(struct0, true, null, false);
            }
            return true;
        }
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "clause", arg0);
        }
    }

    public boolean asserta_1(Term arg0) throws PrologError {
        return assertImpl(arg0, true);
    }

    public boolean assertz_1(Term arg0) throws PrologError {
        return assertImpl(arg0, false);
    }

    private Struct ensureValidHead(Term head) throws PrologError {
        if (head instanceof Struct) {
            return (Struct) head;
        } else if (head instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "clause", head);
        }
    }

    private Term ensureValidBody(Term body) throws PrologError {
        if (body instanceof Struct) {
            Struct bodyStruct = (Struct) body;
            if (bodyStruct.getArity() == 2 && ",".equals(bodyStruct.getName())) {
                ensureValidBody(bodyStruct.getArg(0));
                ensureValidBody(bodyStruct.getArg(1));
            }
            return body;
        } else if (body instanceof Var) {
            return body;
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "clause", body);
        }
    }

    public boolean $log_2(Term format, Term args) {
        String f = Tools.removeApices(format.getTerm().toString());
        Object[] a;

        if (args.getTerm().isList()) {
            a = args.getTerm().castTo(Struct.class).listStream().map(Term::toString).toArray();
        } else {
            a = new Object[0];
        }

        System.out.printf(f, a);
        System.out.println();
        return true;
    }

    public boolean $log_1(Term format) {
        return $log_2(format, Struct.emptyList());
    }

    public boolean $retract_1(Term arg0) throws PrologError {
        arg0 = arg0.getTerm();
        final Struct struct0;

        if (arg0 instanceof Struct) {
            struct0 = (Struct) arg0;
            if (struct0.getName().equals(":-") && struct0.getArity() == 2) {
                ensureValidHead(struct0.getArg(0));
                ensureValidBody(struct0.getArg(1));
                ensureNonStatic((Struct) struct0.getArg(0), "modify", "static_clause");
            } else {
                ensureNonStatic(struct0, "modify", "static_clause");
            }
            Struct sarg0 = (Struct) arg0;
            ClauseInfo c = getEngine().getTheoryManager().retract(sarg0);
            // if clause to retract found -> retract + true
            if (c != null) {
                Struct clause = null;
                if (!sarg0.isClause()) {
                    clause = Struct.of(":-", arg0, Struct.atom("true"));
                } else {
                    clause = sarg0;
                }
                unify(clause, c.getClause());
                return true;
            }
            return false;
        }
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "clause", arg0);
        }
    }

    public boolean abolish_1(Term arg0) throws PrologError {
        arg0 = arg0.getTerm();
        final Struct struct0;
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg0 instanceof Struct
            && "/".equals((struct0 = (Struct) arg0).getName())
            && struct0.getArity() == 2
            && struct0.getArg(0) instanceof Struct
            && struct0.getArg(1) instanceof Number) {

            final String functor = ((Struct) struct0.getArg(0)).getName();
            final int arity = ((Number) struct0.getArg(1)).intValue();
            final String indicator = functor + "/" + arity;

            if (getEngine().getPrimitiveManager().isPredicate(indicator) || getEngine().getTheoryManager().isStatic(indicator)) {
                throw PrologError.permission_error(
                        getEngine().getEngineManager(),
                        "modify",
                        "static_procedure",
                        Struct.of("/", Struct.atom(functor), Int.of(arity)),
                        Struct.atom(String.format("No permission to modify static procedure `%s`", indicator))
                );
            }
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "predicate_indicator", arg0);
        }


        return getEngine().getTheoryManager().abolish((Struct) arg0);
    }

    public boolean halt_1(Term arg0) throws PrologError {
        if (arg0 instanceof Int) {
            System.exit(((Int) arg0).intValue());
        }
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "integer", arg0);
        }
    }

    /*
     * loads a tuprolog library, given its java class name
     */
    public boolean load_library_1(Term arg0) throws PrologError {
        arg0 = arg0.getTerm();
        if (!arg0.isAtom()) {
            if (arg0 instanceof Var) {
                throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
            } else {
                throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
            }
        }
        try {
            getEngine().getLibraryManager().loadLibrary(((Struct) arg0).getName());
            return true;
        } catch (Exception ex) {
            throw PrologError.existence_error(getEngine().getEngineManager(), 1, "class", arg0,
                                              Struct.atom(ex.getMessage()));
        }
    }

    /*
     * loads a tuprolog library, given its java class name and the list of the paths where may be contained
     */
    public boolean load_library_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (!arg0.isAtom()) {
            if (arg0 instanceof Var) {
                throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
            } else {
                throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
            }
        }
        if (!arg1.isList()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "list", arg1);
        }

        try {
            String[] paths = getStringArrayFromStruct((Struct) arg1);
            if (paths == null || paths.length == 0) {
                throw PrologError.existence_error(getEngine().getEngineManager(), 2, "paths", arg1, Struct.atom("Invalid paths' list."));
            }
            getEngine().getLibraryManager().loadLibrary(((Struct) arg0).getName(), paths);
            return true;

        } catch (Exception ex) {
            throw PrologError.existence_error(getEngine().getEngineManager(), 1, "class", arg0,
                                              Struct.atom(ex.getMessage()));
        }
    }

    private String[] getStringArrayFromStruct(Struct list) {
        String[] args = new String[list.listSize()];
        Iterator<? extends Term> it = list.listIterator();
        int count = 0;
        while (it.hasNext()) {
            String path = alice.util.Tools.removeApices(it.next().toString());
            args[count++] = path;
        }
        return args;
    }

    /*
     * unloads a tuprolog library, given its java class name
     */
    public boolean unload_library_1(Term arg0) throws PrologError {
        arg0 = arg0.getTerm();
        if (!arg0.isAtom()) {
            if (arg0 instanceof Var) {
                throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
            } else {
                throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
            }
        }
        try {
            getEngine().getLibraryManager().unloadLibrary(((Struct) arg0).getName());
            return true;
        } catch (Exception ex) {
            throw PrologError.existence_error(getEngine().getEngineManager(), 1, "class", arg0,
                                              Struct.atom(ex.getMessage()));
        }
    }

    /*
     * get flag list: flag_list(-List)
     */
    public boolean flag_list_1(Term arg0) {
        arg0 = arg0.getTerm();
        Struct flist = getEngine().getFlagManager().getPrologFlagList();
        return unify(arg0, flist);
    }

    public boolean comma_2(Term arg0, Term arg1) {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        Struct s = Struct.of(",", arg0, arg1);
        getEngine().getEngineManager().pushSubGoal(ClauseInfo.extractBody(s));
        return true;
    }

    /**
     * It is the same as call/1, but it is not opaque to cut.
     *
     * @throws PrologError
     */
    public boolean $call_1(Term goal) throws PrologError {
        goal = goal.getTerm();
        if (goal instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!isCallable(goal)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "callable", goal);
        }
        goal = convertTermToGoal(goal);
        if (goal == null) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "callable", goal);
        }
        getEngine().getEngineManager().identify(goal);
        getEngine().getEngineManager().pushSubGoal(ClauseInfo.extractBody(goal));
        return true;
    }

    /**
     * A callable term is an atom of a compound term. See the ISO Standard
     * definition in section 3.24.
     */
    private boolean isCallable(Term goal) {
        return (goal.isAtom() || goal.isCompound());
    }

    private void handleError(Throwable t) throws PrologError {
        // errore durante la valutazione
        if (t instanceof ArithmeticException) {
            ArithmeticException cause = (ArithmeticException) t;
            if (cause.getMessage().contains("by zero")) {
                throw PrologError.evaluation_error(getEngine().getEngineManager(), 2, "zero_divisor");
            } else {
                throw PrologError.evaluation_error(getEngine().getEngineManager(), 2, "overflow");
            }
        }
    }

    public boolean is_2(Term arg0, Term arg1) throws PrologError {
        if (arg1.getTerm() instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        Term val1 = null;
        try {
            val1 = evalExpression(arg1);
        } catch (Throwable t) {
            handleError(t);
        }
        if (val1 == null) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "evaluable", arg1.getTerm());
        } else {
            return unify(arg0.getTerm(), val1);
        }
    }

    public boolean unify_2(Term arg0, Term arg1) {
        return unify(arg0, arg1);
    }

    public boolean $match_2(Term arg0, Term arg1) {
        return arg0.match(arg1);
    }

    public boolean deunify_2(Term arg0, Term arg1) {
        return !unify(arg0, arg1);
    }

    // $tolist
    public boolean $tolist_2(Term arg0, Term arg1) throws PrologError {
        // transform arg0 to a list, unify it with arg1
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg0 instanceof Struct) {
            Term val0 = ((Struct) arg0).toList();
            return (val0 != null && unify(arg1, val0));
        }
        throw PrologError.type_error(getEngine().getEngineManager(), 1, "struct", arg0);
    }

    public boolean $fromlist_2(Term arg0, Term arg1) throws PrologError {
        // get the compound representation of the list
        // provided as arg1, and unify it with arg0
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg1 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if (!arg1.isList()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "list", arg1);
        }
        Term val1 = ((Struct) arg1).fromList();
        if (val1 == null) {
            return false;
        }
        return (unify(arg0, val1));
    }

    public boolean copy_term_2(Term arg0, Term arg1) {
        // unify arg1 with a renamed copy of arg0
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        int id = getEngine().getEngineManager().getEnv().nDemoSteps;
        return unify(arg1, arg0.copy(new IdentityHashMap<Var, Var>(), id));
    }

    public boolean $append_2(Term arg0, Term arg1) throws PrologError {
        // append arg0 to arg1
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg1 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if (!arg1.isList()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "list", arg1);
        }
        ((Struct) arg1).append(arg0);
        return true;
    }

    public boolean $find_2(Term arg0, Term arg1) throws PrologError {
        // look for clauses whose head unifies whith arg0 and enqueue them to
        // list arg1
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!arg1.isList()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "list", arg1);
        }
        List<ClauseInfo> l = null;
        try {
            l = getEngine().getTheoryManager().find(arg0);
        } catch (RuntimeException e) {

        }
        for (ClauseInfo b : l) {
            if (match(arg0, b.getHead())) {
                b.getClause().resolveTerm();
                ((Struct) arg1).append(b.getClause());
            }
        }
        return true;
    }

    // set_prolog_flag(+Name,@Value)
    public boolean set_prolog_flag_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if ((!arg0.isAtom() && !(arg0 instanceof Struct))) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "struct", arg0);
        }
        if (!arg1.isGround()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "ground", arg1);
        }
        String name = arg0.toString();
        if (getEngine().getFlagManager().getFlag(name) == null) {
            throw PrologError.domain_error(getEngine().getEngineManager(), 1, "prolog_flag",
                                           arg0);
        }
        if (!getEngine().getFlagManager().isValidValue(name, arg1)) {
            throw PrologError
                    .domain_error(getEngine().getEngineManager(), 2, "flag_value", arg1);
        }
        if (!getEngine().getFlagManager().isModifiable(name)) {
            throw PrologError.permission_error(getEngine().getEngineManager(), "modify", "flag",
                                               arg0, Int.of(0));
        }
        return getEngine().getFlagManager().setFlag(name, arg1);
    }

    // get_prolog_flag(@Name,?Value)
    public boolean get_prolog_flag_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!arg0.isAtom() && !(arg0 instanceof Struct)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "struct", arg0);
        }
        String name = arg0.toString();
        Term value = getEngine().getFlagManager().getFlag(name);
        if (value == null) {
            throw PrologError.domain_error(getEngine().getEngineManager(), 1, "prolog_flag",
                                           arg0);
        }
        return unify(value, arg1);
    }

    public boolean $op_3(Term arg0, Term arg1, Term arg2) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        arg2 = arg2.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (arg1 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 2);
        }
        if (arg2 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 3);
        }
        if (!(arg0 instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "integer", arg0);
        }
        if (!arg1.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 2, "atom", arg1);
        }
        if (!arg2.isAtom() && !arg2.isList()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 3, "atom_or_atom_list",
                                         arg2);
        }
        int priority = ((Int) arg0).intValue();
        if (priority < OperatorManager.OP_LOW || priority > OperatorManager.OP_HIGH) {
            throw PrologError.domain_error(getEngine().getEngineManager(), 1, "operator_priority", arg0);
        }
        String specifier = ((Struct) arg1).getName();
        if (!specifier.equals("fx") && !specifier.equals("fy")
            && !specifier.equals("xf") && !specifier.equals("yf")
            && !specifier.equals("xfx") && !specifier.equals("yfx")
            && !specifier.equals("xfy")) {
            throw PrologError.domain_error(getEngine().getEngineManager(), 2,
                                           "operator_specifier", arg1);
        }
        if (arg2.isList()) {
            for (Iterator<? extends Term> operators = ((Struct) arg2).listIterator(); operators.hasNext(); ) {
                Struct operator = (Struct) operators.next();
                getEngine().setOperatorManager(getEngine().getOperatorManager().add(operator.getName(), specifier, priority));
            }
        } else {
            getEngine().setOperatorManager(getEngine().getOperatorManager().add(((Struct) arg2).getName(), specifier, priority));
        }
        return true;
    }

    /*
     * DIRECTIVES
     */

    public void op_3(Term arg0, Term arg1, Term arg2) throws PrologError {
        $op_3(arg0, arg1, arg2);
    }

    public void flag_4(Term flagName, Term flagSet, Term flagDefault,
                       Term flagModifiable) {
        flagName = flagName.getTerm();
        flagSet = flagSet.getTerm();
        flagDefault = flagDefault.getTerm();
        flagModifiable = flagModifiable.getTerm();
        if (flagSet.isList()
            && (flagModifiable.equals(Struct.truth(true)) || flagModifiable.equals(Struct.atom("false")))) {
            // TODO libName che futuro deve avere?? --------------------
            String libName = "";

            getEngine().getFlagManager().defineFlag(flagName.toString(), (Struct) flagSet,
                                   flagDefault, flagModifiable.equals(Struct.truth(true)), libName);
        }
    }

    public void initialization_1(Term goal) {
        goal = goal.getTerm();
        if (goal instanceof Struct) {
            getEngine().getPrimitiveManager().identifyPredicate(goal);
            getEngine().getTheoryManager().addStartGoal((Struct) goal);
        }
    }

    public void $load_library_1(Term lib) throws InvalidLibraryException {
        lib = lib.getTerm();
        if (lib.isAtom()) {
            getEngine().getLibraryManager().loadLibrary(((Struct) lib).getName());
        }
    }

    public void include_1(Term theory) throws
            InvalidTheoryException, IOException {
        theory = theory.getTerm();
        String path = alice.util.Tools.removeApices(theory.toString());
        if (!new File(path).isAbsolute()) {
            path = getEngine().getCurrentDirectory() + File.separator + path;
        }
        getEngine().pushDirectoryToList(new File(path).getParent());
        getEngine().addTheory(Theory.parseLazilyWithOperators(new FileInputStream(path), getEngine().getOperatorManager()));
        getEngine().popDirectoryFromList();
    }
}