/*Castagna 06/2011*/
package alice.tuprolog;

import java.util.function.IntFunction;
import java.util.stream.Stream;

public interface TermVisitor<T> {

    default T getDefaultValue(Term term) {
        return null;
    }

    default T visitCompound(Struct struct, String functor, int arity, IntFunction<Term> args) {
        return getDefaultValue(struct);
    }

    default T visitList(Struct struct, Stream<Term> items) {
        return getDefaultValue(struct);
    }

    default T visitAtom(Struct struct, String value) {
        return getDefaultValue(struct);
    }

    default T visit(Struct struct) {
        if (struct.isAtom()) {
            return visitAtom(struct, struct.getName());
        } else if (struct.isList()) {
            return visitList(struct, struct.listStream().map(Term.class::cast));
        } else {
            return visitCompound(struct, struct.getName(), struct.getArity(), struct::getArg);
        }
    }

    default T visit(Term term) {
        if (term instanceof Var) {
            return visit((Var) term);
        } else if (term instanceof Struct) {
            return visit((Struct) term);
        } else if (term instanceof Number) {
            return visit((Number) term);
        } else {
            return onError(term);
        }
    }

    default T visitFreeVariable(Var variable) {
        return getDefaultValue(variable);
    }

    default T visitBoundVariable(Var variable, Term link) {
        return visit(link);
    }

    default T visit(Var variable) {
        if (variable.isBound()) {
            return visitBoundVariable(variable, variable.getTerm());
        } else {
            return visitFreeVariable(variable);
        }
    }

    default T visit(Number number) {
        if (number instanceof Double) {
            return visit((Double) number);
        } else if (number instanceof Float) {
            return visit((Float) number);
        } else if (number instanceof Int) {
            return visit((Int) number);
        } else if (number instanceof Long) {
            return visit((Long) number);
        } else {
            throw new IllegalStateException();
        }
    }

    default T visit(Double number) {
        return getDefaultValue(number);
    }

    default T visit(Int number) {
        return getDefaultValue(number);
    }

    default T visit(Long number) {
        return getDefaultValue(number);
    }

    default T visit(Float number) {
        return getDefaultValue(number);
    }

    default T onError(Term term) {
        throw new NullPointerException();
    }
}