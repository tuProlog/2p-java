package alice.tuprolog;

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:giulio.piancastelli@unibo.it">Giulio Piancastelli</a>
 */
public class StructIteratorTestCase extends TestCase {

    public void testEmptyIterator() {
        Struct list = Struct.emptyList();
        Iterator<? extends Term> i = list.listIterator();
        assertFalse(i.hasNext());
        try {
            i.next();
            fail();
        } catch (NoSuchElementException expected) {
        }
    }

    public void testIteratorCount() {
        Struct list = Struct.list(Int.of(1), Int.of(2), Int.of(3), Int.of(5), Int.of(7));
        Iterator<? extends Term> i = list.listIterator();
        int count = 0;
        for (; i.hasNext(); count++) {
            i.next();
        }
        assertEquals(5, count);
        assertFalse(i.hasNext());
    }

    public void testMultipleHasNext() {
        Struct list = Struct.list(Struct.atom("p"), Struct.atom("q"), Struct.atom("r"));
        Iterator<? extends Term> i = list.listIterator();
        assertTrue(i.hasNext());
        assertTrue(i.hasNext());
        assertTrue(i.hasNext());
        assertEquals(Struct.atom("p"), i.next());
    }

    public void testMultipleNext() {
        Struct list = Struct.list(Int.of(0), Int.of(1), Int.of(2), Int.of(3), Int.of(5), Int.of(7));
        Iterator<? extends Term> i = list.listIterator();
        assertTrue(i.hasNext());
        i.next(); // skip the first term
        assertEquals(Int.of(1), i.next());
        assertEquals(Int.of(2), i.next());
        assertEquals(Int.of(3), i.next());
        assertEquals(Int.of(5), i.next());
        assertEquals(Int.of(7), i.next());
        // no more terms
        assertFalse(i.hasNext());
        try {
            i.next();
            fail();
        } catch (NoSuchElementException expected) {
        }
    }

    public void testRemoveOperationNotSupported() {
        Struct list = Struct.cons(Int.of(1), Struct.emptyList());
        Iterator<? extends Term> i = list.listIterator();
        assertNotNull(i.next());
        try {
            i.remove();
            fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

}
