package alice.tuprolog;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * 
 * @author <a href="mailto:giulio.piancastelli@unibo.it">Giulio Piancastelli</a>
 */
public class TermIteratorTestCase extends TestCase {
	
	public void testEmptyIterator() {
		String theory = "";
		Iterator i = Term.getIterator(theory);
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException expected) {}
	}
	
	public void testIterationCount() {
		String theory = "q(1)." + "\n" +
		                "q(2)." + "\n" +
		                "q(3)." + "\n" +
		                "q(5)." + "\n" +
		                "q(7).";
		Iterator i = Term.getIterator(theory);
		int count = 0;
		for (; i.hasNext(); count++)
			i.next();
		assertEquals(5, count);
		assertFalse(i.hasNext());
	}
	
	public void testMultipleHasNext() {
		String theory = "p. q. r.";
		Iterator i = Term.getIterator(theory);
		assertTrue(i.hasNext());
		assertTrue(i.hasNext());
		assertTrue(i.hasNext());
		assertEquals(new Struct("p"), i.next());
	}
	
	public void testMultipleNext() {
		String theory = "p(X):-q(X),X>1." + "\n" +
		                "q(1)." + "\n" +
						"q(2)." + "\n" +
						"q(3)." + "\n" +
						"q(5)." + "\n" +
						"q(7).";
		Iterator i = Term.getIterator(theory);
		assertTrue(i.hasNext());
		i.next(); // skip the first term
		assertEquals(new Struct("q", new Int(1)), i.next());
		assertEquals(new Struct("q", new Int(2)), i.next());
		assertEquals(new Struct("q", new Int(3)), i.next());
		assertEquals(new Struct("q", new Int(5)), i.next());
		assertEquals(new Struct("q", new Int(7)), i.next());
		// no more terms
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException expected) {}
	}
	
	public void testIterationOnWrongTheory() {
		String theory = "q(1)." + "\n" +
		                "q(2)." + "\n" +
						"q(3) " + "\n" + // missing the End-Of-Clause!
						"q(5)." + "\n" +
						"q(7).";
		Iterator i = Term.getIterator(theory);
		assertTrue(i.hasNext());
		assertEquals(new Struct("q", new Int(1)), i.next());
		assertTrue(i.hasNext());
		assertEquals(new Struct("q", new Int(2)), i.next());
		assertFalse(i.hasNext());
		try {
			System.out.println(i.next());
			fail();
		} catch (NoSuchElementException expected) {}
	}
	
	public void testRemoveOperationNotSupported() {
		String theory = "p(1).";
		Iterator i = Term.getIterator(theory);
		assertNotNull(i.next());
		try {
			i.remove();
			fail();
		} catch (UnsupportedOperationException expected) {}
	}

}
