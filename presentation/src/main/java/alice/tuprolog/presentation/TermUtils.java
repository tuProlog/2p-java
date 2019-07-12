package alice.tuprolog.presentation;

import alice.tuprolog.*;
import alice.tuprolog.Double;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TermUtils {
    private static final Prolog HELPER = new Prolog();

    public static Term dynamicObjectToTerm(Object object) {
        if (object instanceof java.lang.Double) {
            return Double.of((java.lang.Double) object);
        } else if (object instanceof java.lang.Integer) {
            return Int.of((Integer) object);
        } else if (object instanceof java.lang.Float) {
            return alice.tuprolog.Float.of((java.lang.Float) object);
        } else if (object instanceof java.lang.Long) {
            return alice.tuprolog.Long.of((java.lang.Long) object);
        } else if (object instanceof String) {
            return Struct.atom(object.toString());
        } else if (object instanceof List) {
            Term[] terms = ((List<?>) object).stream()
                                          .map(TermUtils::dynamicObjectToTerm)
                                          .toArray(Term[]::new);
            return Struct.list(terms);
        } else if (object instanceof Map) {
            final Map<String, Object> objectMap = (Map<String, Object>) object;
            if (objectMap.containsKey("var")) {
                final Var variable = Var.of(objectMap.get("var").toString());
                if (objectMap.get("val") != null) {
                    variable.unify(HELPER, dynamicObjectToTerm(objectMap.get("val")));
                }
                return variable;
            } else if (objectMap.containsKey("fun") && objectMap.containsKey("args")) {
                final Term[] arguments = ((List<?>) objectMap.get("args"))
                        .stream()
                        .peek(it -> {
                            if (it == null) throw new IllegalArgumentException();
                        }).map(TermUtils::dynamicObjectToTerm)
                        .toArray(Term[]::new);

                return Struct.of(objectMap.get("fun").toString(), arguments);
            }
        }
        throw new IllegalArgumentException();
    }

    public static Object termToDynamicObject(Term term) {
        return term.accept(new TermVisitor<Object>()     {

            @Override
            public Object visitCompound(final Struct struct, final String functor, final int arity, final IntFunction<Term> args) {
                final Map<String, Object> map = new LinkedHashMap<>();
                map.put("fun", functor);
                map.put("args", IntStream.range(0, arity).mapToObj(args).map(it -> it.accept(this)).collect(Collectors.toList()));
                return Collections.unmodifiableMap(map);
            }

            @Override
            public Object visitList(final Struct struct, final Stream<Term> items) {
                return Collections.singletonMap(
                        "list",
                        items.map(it -> it.accept(this)).collect(Collectors.toList())
                );
            }

            @Override
            public Object visitSet(final Struct struct, final Stream<Term> items) {
                return Collections.singletonMap(
                        "set",
                        items.map(it -> it.accept(this)).collect(Collectors.toList())
                );
            }

            @Override
            public Object visitTuple(final Struct struct, final Stream<Term> items) {
                return Collections.singletonMap(
                        "tuple",
                        items.map(it -> it.accept(this)).collect(Collectors.toList())
                );
            }

            @Override
            public Object visitCons(final Struct struct, final Stream<Term> items) {
                final List<Object> elems = new ArrayList<>(items.map(it -> it.accept(this)).collect(Collectors.toList())) ;
                final int lastIndex = elems.size() - 1;

                elems.set(lastIndex, Collections.singletonMap("tail", elems.get(lastIndex)));

                return Collections.singletonMap(
                        "list",
                        Collections.unmodifiableList(elems)
                );
            }

            @Override
            public Object visitAtom(final Struct struct, final String value) {
                return value;
            }

            @Override
            public Object visitFreeVariable(final Var variable) {
                final Map<String, Object> map = new HashMap<>();
                map.put("var", variable.getName());
                return Collections.unmodifiableMap(map);
            }

            @Override
            public Object visitBoundVariable(final Var variable, final Term link) {
                final Map<String, Object> map = new HashMap<>();
                map.put("var", variable.getName());
                map.put("val", link.accept(this));
                return Collections.unmodifiableMap(map);
            }

            @Override
            public Object visit(final Double number) {
                return number.doubleValue();
            }

            @Override
            public Object visit(final Int number) {
                return number.intValue();
            }

            @Override
            public Object visit(final alice.tuprolog.Long number) {
                return number.longValue();
            }

            @Override
            public Object visit(final alice.tuprolog.Float number) {
                return number.floatValue();
            }
        });
    }
}
