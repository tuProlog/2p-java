package alice.tuprolog.parsing;

import alice.tuprolog.OperatorManager;
import alice.tuprolog.Term;
import org.junit.Assert;

abstract public class BaseTestPrologParsing {

    protected void assertEquals(Term x, Term y) {
        Assert.assertTrue(String.format("Failing assertion: <%s> == <%s>", x, y), x.match(true, y));
    }

    protected <T> void assertEquals(T x, T y) {
        Assert.assertEquals(String.format("Failing assertion: <%s> == <%s>", x, y), x, y);
    }

    protected OperatorManager getOperatorManager() {
        return new OperatorManager();
    }

    protected Term parseTerm(String string) {

        final OperatorManager ops = getOperatorManager();

        if (ops == null) {
            return Term.createTerm(string);
        } else {
            return Term.createTerm(string, ops);
        }

    }

}
