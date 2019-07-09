package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:giulio.piancastelli@unibo.it">Giulio Piancastelli</a>
 */
public class TermIteratorTestCase extends TestCase {

    public void testEmptyIterator() {
        String theory = "";
        Iterator<Term> i = Term.getIterator(theory);
        assertFalse(i.hasNext());
        try {
            i.next();
            fail();
        } catch (NoSuchElementException expected) {
        }
    }

    public void testIterationCount() {
        String theory = "q(1)." + "\n" +
                        "q(2)." + "\n" +
                        "q(3)." + "\n" +
                        "q(5)." + "\n" +
                        "q(7).";
        Iterator<Term> i = Term.getIterator(theory);
        int count = 0;
        for (; i.hasNext(); count++) {
            i.next();
        }
        assertEquals(5, count);
        assertFalse(i.hasNext());
    }

    public void testMultipleHasNext() {
        String theory = "p. \n q. \n r.";
        Iterator<Term> i = Term.getIterator(theory);
        assertTrue(i.hasNext());
        assertTrue(i.hasNext());
        assertTrue(i.hasNext());
        assertEquals(Struct.atom("p"), i.next());
    }

    public void testMultipleNext() {
        String theory = "p(X):-q(X),X>1." + "\n" +
                        "q(1)." + "\n" +
                        "q(2)." + "\n" +
                        "q(3)." + "\n" +
                        "q(5)." + "\n" +
                        "q(7).";
        Iterator<Term> i = Term.getIterator(theory);
        assertTrue(i.hasNext());
        i.next(); // skip the first term
        assertEquals(Struct.of("q", Int.of(1)), i.next());
        assertEquals(Struct.of("q", Int.of(2)), i.next());
        assertEquals(Struct.of("q", Int.of(3)), i.next());
        assertEquals(Struct.of("q", Int.of(5)), i.next());
        assertEquals(Struct.of("q", Int.of(7)), i.next());
        // no more terms
        assertFalse(i.hasNext());
        try {
            i.next();
            fail();
        } catch (NoSuchElementException expected) {
        }
    }

    public void testIteratorOnInvalidTerm() {
        String t = "q(1)"; // missing the End-Of-Clause!
        try {
            Iterator<? extends Term> i = Term.getIterator(t);
            i.hasNext();
            fail();
        } catch (InvalidTermException expected) {
        }
    }

    public void testIterationOnInvalidTheory() {
        String theory = "q(1)." + "\n" +
                        "q(2)." + "\n" +
                        "q(3) " + "\n" + // missing the End-Of-Clause!
                        "q(5)." + "\n" +
                        "q(7).";
        Struct firstTerm = Struct.of("q", Int.of(1));
        Struct secondTerm = Struct.of("q", Int.of(2));
        Iterator<Term> i1 = Term.getIterator(theory);
        assertTrue(i1.hasNext());
        assertEquals(firstTerm, i1.next());
        assertTrue(i1.hasNext());
        assertEquals(secondTerm, i1.next());
        try {
            i1.hasNext();
            i1.next();
            fail();
        } catch (InvalidTermException expected) {
        }
        Iterator<Term> i2 = Term.getIterator(theory);
        assertEquals(firstTerm, i2.next());
        assertEquals(secondTerm, i2.next());
        try {
            i2.next();
            fail();
        } catch (InvalidTermException expected) {
        }
    }

    public void testRemoveOperationNotSupported() {
        String theory = "p(1).";
        Iterator<Term> i = Term.getIterator(theory);
        assertNotNull(i.next());
        try {
            i.remove();
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

}
