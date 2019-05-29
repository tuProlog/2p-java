package alice.tuprolog;

import java.util.Set;

public interface Scope {

    Set<Var> getVariables();

    Struct structOf(String name, Term... args);

    default Struct atomOf(String name) {
        return structOf(name);
    }

    Var varOf(String name);

    Var any();
}
