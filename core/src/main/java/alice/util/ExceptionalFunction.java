package alice.util;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface ExceptionalFunction<T, R, E extends Throwable> {
    R apply(T arg) throws E;

    static <T, R, E extends Throwable> Function<T, R> wrap(ExceptionalFunction<T, R, E> f) {
        return x -> {
            try {
                return f.apply(x);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <T, R, E extends Throwable> Function<T, R> wrap(ExceptionalFunction<T, R, E> f, BiFunction<E, T, R> onError) {
        return x -> {
            try {
                return f.apply(x);
            } catch (Throwable e) {
                return onError.apply((E) e, x);
            }
        };
    }
}
