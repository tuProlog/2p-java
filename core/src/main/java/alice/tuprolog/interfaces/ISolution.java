package alice.tuprolog.interfaces;

import alice.tuprolog.exceptions.NoSolutionException;
import alice.tuprolog.exceptions.UnknownVarException;

public interface ISolution<Q, S, T> {

    <Z extends T> Z agetVarValue(String varName) throws NoSolutionException;

    <Z extends T> Z getTerm(String varName) throws NoSolutionException, UnknownVarException;

    boolean isSuccess();

    boolean isHalted();

    boolean hasOpenAlternatives();

    S getSolution() throws NoSolutionException;

    Q getQuery();

    java.util.List<? extends T> getBindingVars() throws NoSolutionException;
}
