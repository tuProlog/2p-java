/*Castagna 06/2011*/
package alice.tuprolog.interfaces;

import alice.tuprolog.*;
import alice.tuprolog.Double;
import alice.tuprolog.Number;

import java.lang.Float;
import java.lang.Long;
import java.util.stream.Stream;

public interface TermVisitor<T> {

    @FunctionalInterface
    interface Indexer<X> {
        X get(int i);
    }

    T visitCompound(Struct struct, String functor, int arity, Indexer<Term> args);

    T visitList(Struct struct, Stream<Term> items);

    T visitAtom(Struct struct, String value);

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
        if (term.getTerm() instanceof Struct) {
            return visit((Struct) term.getTerm());
        } else if (term.getTerm() instanceof Number) {
            return visit((Number) term.getTerm());
        } else if (term instanceof Var) {
            return visit((Var) term.getTerm());
        } else {
            throw new IllegalStateException();
        }
    }

    T visit(Var variable);

    T visit(Number number);

    T visit(Double number);

    T visit(Int number);

    T visit(Long number);

    T visit(Float number);
}