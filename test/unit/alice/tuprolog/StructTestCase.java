package alice.tuprolog;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class StructTestCase extends TestCase {
	
	public void testEmptyList() {
		Struct list = new Struct();
		assertTrue(list.isList());
		assertTrue(list.isEmptyList());
		assertEquals(0, list.listSize());
		assertEquals("[]", list.getName());
		assertEquals(0, list.getArity());
	}

	/** Another correct method of building an empty list */
	public void testEmptyListAsSquaredStruct() {
		Struct emptyList = new Struct("[]");
		assertTrue(emptyList.isList());
		assertTrue(emptyList.isEmptyList());
		assertEquals("[]", emptyList.getName());
		assertEquals(0, emptyList.getArity());
		assertEquals(0, emptyList.listSize());
	}
	
	/** A wrong method of building an empty list */
	public void testEmptyListAsDottedStruct() {
		Struct notAnEmptyList = new Struct(".");
		assertFalse(notAnEmptyList.isList());
		assertFalse(notAnEmptyList.isEmptyList());
		assertEquals(".", notAnEmptyList.getName());
		assertEquals(0, notAnEmptyList.getArity());
	}
	
	/** Use dotted structs to build lists with content */
	public void testListAsDottedStruct() {
		Struct notAnEmptyList = new Struct(".", new Struct("a"), new Struct(".", new Struct("b"), new Struct()));
		assertTrue(notAnEmptyList.isList());
		assertFalse(notAnEmptyList.isEmptyList());
		assertEquals(".", notAnEmptyList.getName());
		assertEquals(2, notAnEmptyList.getArity());
	}
	
	public void testListFromArgumentArray() {
		assertEquals(new Struct(), new Struct(new Term[0]));
		
		Term[] args = new Term[2];
		args[0] = new Struct("a");
		args[1] = new Struct("b");
		Struct list = new Struct(args);
		assertEquals(new Struct(), list.listTail().listTail());
	}
	
	public void testListSize() {
		Struct list = new Struct(new Struct("a"),
				       new Struct(new Struct("b"),
				           new Struct(new Struct("c"), new Struct())));
		assertTrue(list.isList());
		assertFalse(list.isEmptyList());
		assertEquals(3, list.listSize());
	}
	
	public void testNonListHead() throws InvalidTermException {
		Struct s = new Struct("f", new Var("X"));
		try {
			assertNotNull(s.listHead()); // just to make an assertion...
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("The structure " + s + " is not a list.", e.getMessage());
		}
	}
	
	public void testNonListTail() {
		Struct s = new Struct("h", new Int(1));
		try {
			assertNotNull(s.listTail()); // just to make an assertion...
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("The structure " + s + " is not a list.", e.getMessage());
		}
	}
	
	public void testNonListSize() throws InvalidTermException {
		Struct s = new Struct("f", new Var("X"));
		try {
			assertEquals(0, s.listSize()); // just to make an assertion...
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("The structure " + s + " is not a list.", e.getMessage());
		}
	}
	
	public void testNonListIterator() {
		Struct s = new Struct("f", new Int(2));
		try {
			assertNotNull(s.listIterator()); // just to make an assertion...
			fail();
		} catch (UnsupportedOperationException e) {
			assertEquals("The structure " + s + " is not a list.", e.getMessage());
		}
	}
	
	public void testToList() {
		Struct emptyList = new Struct();
		Struct emptyListToList = new Struct(new Struct("[]"), new Struct());
		assertEquals(emptyListToList, emptyList.toList());
	}
	
	public void testToString() throws InvalidTermException {
		Struct emptyList = new Struct();
		assertEquals("[]", emptyList.toString());
		Struct s = new Struct("f", new Var("X"));
		assertEquals("f(X)", s.toString());
		Struct list = new Struct(new Struct("a"),
		          new Struct(new Struct("b"),
		        	  new Struct(new Struct("c"), new Struct())));
		assertEquals("[a,b,c]", list.toString());
	}
	
	public void testAppend() {
		Struct emptyList = new Struct();
		Struct list = new Struct(new Struct("a"),
				          new Struct(new Struct("b"),
				        	  new Struct(new Struct("c"), new Struct())));
		emptyList.append(new Struct("a"));
		emptyList.append(new Struct("b"));
		emptyList.append(new Struct("c"));
		assertEquals(list, emptyList);
		Struct tail = new Struct(new Struct("b"),
                          new Struct(new Struct("c"), new Struct()));
		assertEquals(tail, emptyList.listTail());
		
		emptyList = new Struct();
		emptyList.append(new Struct());
		assertEquals(new Struct(new Struct(), new Struct()), emptyList);
		
		Struct anotherList = new Struct(new Struct("d"),
				                 new Struct(new Struct("e"), new Struct()));
		list.append(anotherList);
		assertEquals(anotherList, list.listTail().listTail().listTail().listHead());
	}
	
	public void testIteratedGoalTerm() throws Exception {
		Var x = new Var("X");
		Struct foo = new Struct("foo", x);
		Struct term = new Struct("^", x, foo);
		assertEquals(foo, term.iteratedGoalTerm());
	}
	
	public void testIsList() {
		Struct notList = new Struct(".", new Struct("a"), new Struct("b"));
		assertFalse(notList.isList());
	}

}
