package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import junit.framework.TestCase;

public class DoubleTestCase extends TestCase {

    public void testIsAtomic() {
        assertTrue(alice.tuprolog.Double.of(0).isAtomic());
    }

    public void testIsAtom() {
        assertFalse(alice.tuprolog.Double.of(0).isAtom());
    }

    public void testIsCompound() {
        assertFalse(alice.tuprolog.Double.of(0).isCompound());
    }

    public void testEqualsToStruct() {
        alice.tuprolog.Double zero = alice.tuprolog.Double.of(0);
        Struct s = new Struct();
        assertFalse(zero.equals(s));
    }

    public void testEqualsToVar() throws InvalidTermException {
        alice.tuprolog.Double one = alice.tuprolog.Double.of(1);
        Var x = new Var("X");
        assertFalse(one.equals(x));
    }

    public void testEqualsToDouble() {
        alice.tuprolog.Double zero = alice.tuprolog.Double.of(0);
        alice.tuprolog.Double one = alice.tuprolog.Double.of(1);
        assertFalse(zero.equals(one));
        alice.tuprolog.Double anotherZero = alice.tuprolog.Double.of(0.0);
        assertTrue(anotherZero.equals(zero));
    }

    public void testEqualsToFloat() {
        // TODO Test Double numbers for equality with Float numbers
    }

    public void testEqualsToInt() {
        alice.tuprolog.Double doubleOne = alice.tuprolog.Double.of(1.0);
        Int integerOne = Int.of(1);
        assertFalse(doubleOne.equals(integerOne));
    }

    public void testEqualsToLong() {
        // TODO Test Double numbers for equality with Long numbers
    }

}
