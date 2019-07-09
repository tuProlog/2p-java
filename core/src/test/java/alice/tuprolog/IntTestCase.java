package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import junit.framework.TestCase;

public class IntTestCase extends TestCase {

    public void testIsAtomic() {
        assertTrue(Int.of(0).isAtomic());
    }

    public void testIsAtom() {
        assertFalse(Int.of(0).isAtom());
    }

    public void testIsCompound() {
        assertFalse(Int.of(0).isCompound());
    }

    public void testEqualsToStruct() {
        Struct s = new Struct();
        Int zero = Int.of(0);
        assertFalse(zero.equals(s));
    }

    public void testEqualsToVar() throws InvalidTermException {
        Var x = Var.of("X");
        Int one = Int.of(1);
        assertFalse(one.equals(x));
    }

    public void testEqualsToInt() {
        Int zero = Int.of(0);
        Int one = Int.of(1);
        assertFalse(zero.equals(one));
        Int anotherZero = Int.of(1 - 1);
        assertTrue(anotherZero.equals(zero));
    }

    public void testEqualsToLong() {
        // TODO Test Int numbers for equality with Long numbers
    }

    public void testEqualsToDouble() {
        Int integerOne = Int.of(1);
        alice.tuprolog.Double doubleOne = alice.tuprolog.Double.of(1);
        assertFalse(integerOne.equals(doubleOne));
    }

    public void testEqualsToFloat() {
        // TODO Test Int numbers for equality with Float numbers
    }

}
