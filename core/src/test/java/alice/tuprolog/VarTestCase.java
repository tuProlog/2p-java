package alice.tuprolog;

import junit.framework.TestCase;

public class VarTestCase extends TestCase {

    public void testIsAtomic() {
        assertFalse(Var.of("X").isAtomic());
    }

    public void testIsAtom() {
        assertFalse(Var.of("X").isAtom());
    }

    public void testIsCompound() {
        assertFalse(Var.of("X").isCompound());
    }

}
