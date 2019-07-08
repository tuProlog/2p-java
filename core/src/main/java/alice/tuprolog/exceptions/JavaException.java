package alice.tuprolog.exceptions;

import alice.tuprolog.Int;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * @author Matteo Iuliani
 */
public class JavaException extends PrologException {

    // eccezione Java che rappresenta l'argomento di java_throw/1
    private Throwable wrapped;

    public JavaException(Throwable wrapped) {
        this.wrapped = wrapped;
    }

    public Struct toStruct() {
        return getException();
    }

    @Deprecated
    public Struct getException() {
        // java_exception
        String java_exception = wrapped.getClass().getName();
        // Cause
        Term causeTerm = null;
        Throwable cause = wrapped.getCause();
        if (cause != null) {
            causeTerm = new Struct(cause.toString());
        } else {
            causeTerm = Int.of(0);
        }
        // Message
        Term messageTerm = null;
        String message = wrapped.getMessage();
        if (message != null) {
            messageTerm = new Struct(message);
        } else {
            messageTerm = Int.of(0);
        }
        // StackTrace
        Struct stackTraceTerm = new Struct();
        StackTraceElement[] elements = wrapped.getStackTrace();
        for (StackTraceElement element : elements) {
            stackTraceTerm.append(new Struct(element.toString()));
        }
        // return
        return new Struct(java_exception, causeTerm, messageTerm,
                          stackTraceTerm);
    }

}
