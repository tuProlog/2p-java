package alice.tuprolog;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Terms {

    public static Term parse(String parse) {
        return Term.createTerm(parse);
    }

    public static Struct cut() {
        return Struct.cut();
    }

    public static Struct truth(boolean value) {
        return Struct.truth(value);
    }

    public static Struct directive(Term body) {
        return Struct.of(":-", body);
    }

    public static Struct rule(Term head, Term body) {
        return Struct.of(":-", head, body);
    }

    public static Struct atom(String atom) {
        return Struct.atom(atom);
    }

    public static Struct struct(String functor, Term first, Term... others) {
        return Struct.of(functor, first, others);
    }

    public static Struct compound(String functor, Term first, Term... others) {
        return struct(functor, first, others);
    }

    public static Struct cons(Term head, Term tail) {
        return Struct.cons(head, tail);
    }

    public static Struct emptyList() {
        return Struct.emptyList();
    }

    public static Struct list(Term... terms) {
        return Struct.list(terms);
    }

    public static Struct list(Stream<? extends Term> terms) {
        return Struct.list(terms);
    }

    public static Struct list(Collection<? extends Term> terms) {
        return Struct.list(terms);
    }

    public static Struct list(Iterator<? extends Term> terms) {
        return Struct.list(terms);
    }

    public static Struct list(Iterable<? extends Term> terms) {
        return Struct.list(terms);
    }

    public static Struct tuple(Term term1, Term term2, Term... terms) {
        return Struct.tuple(term1, term2, terms);
    }

    public static Struct tuple(Collection<? extends Term> terms) {
        return Struct.tuple(terms);
    }

    public static Struct tuple(Iterable<? extends Term> terms) {
        return Struct.tuple(terms);
    }

    public static Struct tuple(Iterator<? extends Term> terms) {
        return Struct.tuple(terms);
    }

    public static Struct tuple(Stream<? extends Term> terms) {
        return Struct.tuple(terms);
    }

    public static Struct tuple(List<? extends Term> terms) {
        return Struct.tuple(terms);
    }

    public Var var(String name) {
        return Var.of(name);
    }

    public Var var() {
        return Var.anonymous();
    }

    public Var whatever() {
        return Var.whatever();
    }

    public Var any() {
        return Var.any();
    }

    public static Number number(java.lang.Number value) {
        return Number.of(value);
    }

    public static Int number(int value) {
        return Int.of(value);
    }

    public static Int number(Integer value) {
        return Int.of(value);
    }

    public static Float number(float value) {
        return Float.of(value);
    }

    public static Float number(java.lang.Float value) {
        return Float.of(value);
    }

    public static Double number(double value) {
        return Double.of(value);
    }

    public static Double number(java.lang.Double value) {
        return Double.of(value);
    }

    public static Long number(long value) {
        return Long.of(value);
    }

    public static Long number(java.lang.Long value) {
        return Long.of(value);
    }
}
