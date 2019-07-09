package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import junit.framework.TestCase;

public class StructTestCase extends TestCase {

    public void testStructWithNullArgument() {
        try {
            new Struct("p", (Term) null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            new Struct("p", Int.of(1), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            new Struct("p", Int.of(1), Int.of(2), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            new Struct("p", Int.of(1), Int.of(2), Int.of(3), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            new Struct("p", Int.of(1), Int.of(2), Int.of(3), Int.of(4), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            new Struct("p", Int.of(1), Int.of(2), Int.of(3), Int.of(4), Int.of(5), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            new Struct("p", Int.of(1), Int.of(2), Int.of(3), Int.of(4), Int.of(5), Int.of(6), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Term[] args = new Term[]{new Struct("a"), null, Var.of("P")};
            new Struct("p", args);
            fail();
        } catch (InvalidTermException expected) {
        }
    }

    public void testStructWithNullName() {
        try {
            new Struct((String) null, Int.of(1), Int.of(2));
            fail();
        } catch (InvalidTermException expected) {
        }
    }

    /**
     * Structs with an emptyWithStandardOperators name can only be atoms.
     */
    public void testStructWithEmptyName() {
        try {
            new Struct("", Int.of(1), Int.of(2));
            fail();
        } catch (InvalidTermException expected) {
        }
        assertEquals(0, new Struct("").getName().length());
    }

    public void testEmptyList() {
        Struct list = Struct.emptyList();
        assertTrue(list.isList());
        assertTrue(list.isEmptyList());
        assertEquals(0, list.listSize());
        assertEquals("[]", list.getName());
        assertEquals(0, list.getArity());
    }

    /**
     * Another correct method of building an emptyWithStandardOperators list
     */
    public void testEmptyListAsSquaredStruct() {
        Struct emptyList = Struct.of("[]");
        assertTrue(emptyList.isList());
        assertTrue(emptyList.isEmptyList());
        assertEquals("[]", emptyList.getName());
        assertEquals(0, emptyList.getArity());
        assertEquals(0, emptyList.listSize());
    }

    /**
     * A wrong method of building an emptyWithStandardOperators list
     */
    public void testEmptyListAsDottedStruct() {
        Struct notAnEmptyList = new Struct(".");
        assertFalse(notAnEmptyList.isList());
        assertFalse(notAnEmptyList.isEmptyList());
        assertEquals(".", notAnEmptyList.getName());
        assertEquals(0, notAnEmptyList.getArity());
    }

    /**
     * Use dotted structs to build lists with content
     */
    public void testListAsDottedStruct() {
        Struct notAnEmptyList = new Struct(".", new Struct("a"), new Struct(".", new Struct("b"), Struct.emptyList()));
        assertTrue(notAnEmptyList.isList());
        assertFalse(notAnEmptyList.isEmptyList());
        assertEquals(".", notAnEmptyList.getName());
        assertEquals(2, notAnEmptyList.getArity());
    }

    public void testListFromArgumentArray() {
        assertEquals(Struct.emptyList(), Struct.list(new Term[0]));

        Term[] args = new Term[2];
        args[0] = new Struct("a");
        args[1] = new Struct("b");
        Struct list = Struct.list(args);
        assertEquals(Struct.emptyList(), list.listTail().listTail());
    }

    public void testListSize() {
        Struct list = new Struct(new Struct("a"),
                                 new Struct(new Struct("b"),
                                            new Struct(new Struct("c"), Struct.emptyList())));
        assertTrue(list.isList());
        assertFalse(list.isEmptyList());
        assertEquals(3, list.listSize());
    }

    public void testNonListHead() throws InvalidTermException {
        Struct s = new Struct("f", Var.of("X"));
        try {
            assertNotNull(s.listHead()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testNonListTail() {
        Struct s = new Struct("h", Int.of(1));
        try {
            assertNotNull(s.listTail()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testNonListSize() throws InvalidTermException {
        Struct s = new Struct("f", Var.of("X"));
        try {
            assertEquals(0, s.listSize()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testNonListIterator() {
        Struct s = new Struct("f", Int.of(2));
        try {
            assertNotNull(s.listIterator()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testToList() {
        Struct emptyList = Struct.emptyList();
        Struct emptyListToList = new Struct(new Struct("[]"), Struct.emptyList());
        assertEquals(emptyListToList, emptyList.toList());
    }

    public void testToString() throws InvalidTermException {
        Struct emptyList = Struct.emptyList();
        assertEquals("[]", emptyList.toString());
        Struct s = new Struct("f", Var.of("X"));
        assertEquals("f(X)", s.toString());
        Struct list = new Struct(new Struct("a"),
                                 new Struct(new Struct("b"),
                                            new Struct(new Struct("c"), Struct.emptyList())));
        assertEquals("[a,b,c]", list.toString());
    }

    public void testAppend() {
        Struct emptyList = Struct.emptyList();
        Struct list = new Struct(new Struct("a"),
                                 new Struct(new Struct("b"),
                                            new Struct(new Struct("c"), Struct.emptyList())));
        emptyList.append(new Struct("a"));
        emptyList.append(new Struct("b"));
        emptyList.append(new Struct("c"));
        assertEquals(list, emptyList);
        Struct tail = new Struct(new Struct("b"),
                                 new Struct(new Struct("c"), Struct.emptyList()));
        assertEquals(tail, emptyList.listTail());

        emptyList = Struct.emptyList();
        emptyList.append(Struct.emptyList());
        assertEquals(new Struct(Struct.emptyList(), Struct.emptyList()), emptyList);

        Struct anotherList = new Struct(new Struct("d"),
                                        new Struct(new Struct("e"), Struct.emptyList()));
        list.append(anotherList);
        assertEquals(anotherList, list.listTail().listTail().listTail().listHead());
    }

    public void testIteratedGoalTerm() throws Exception {
        Var x = Var.of("X");
        Struct foo = new Struct("foo", x);
        Struct term = new Struct("^", x, foo);
        assertEquals(foo, term.iteratedGoalTerm());
    }

    public void testIsList() {
        Struct notList = new Struct(".", new Struct("a"), new Struct("b"));
        assertFalse(notList.isList());
    }

    public void testIsAtomic() {
        Struct emptyList = Struct.emptyList();
        assertTrue(emptyList.isAtomic());
        Struct atom = new Struct("atom");
        assertTrue(atom.isAtomic());
        Struct list = Struct.list(Int.of(0), Int.of(1));
        assertFalse(list.isAtomic());
        Struct compound = new Struct("f", new Struct("a"), new Struct("b"));
        assertFalse(compound.isAtomic());
        Struct singleQuoted = new Struct("'atom'");
        assertTrue(singleQuoted.isAtomic());
        Struct doubleQuoted = new Struct("\"atom\"");
        assertTrue(doubleQuoted.isAtomic());
    }

    public void testIsAtom() {
        Struct emptyList = Struct.emptyList();
        assertTrue(emptyList.isAtom());
        Struct atom = new Struct("atom");
        assertTrue(atom.isAtom());
        Struct list = Struct.list(Int.of(0), Int.of(1));
        assertFalse(list.isAtom());
        Struct compound = new Struct("f", new Struct("a"), new Struct("b"));
        assertFalse(compound.isAtom());
        Struct singleQuoted = new Struct("'atom'");
        assertTrue(singleQuoted.isAtom());
        Struct doubleQuoted = new Struct("\"atom\"");
        assertTrue(doubleQuoted.isAtom());
    }

    public void testIsCompound() {
        Struct emptyList = Struct.emptyList();
        assertFalse(emptyList.isCompound());
        Struct atom = new Struct("atom");
        assertFalse(atom.isCompound());
        Struct list = Struct.list(new Term[]{Int.of(0), Int.of(1)});
        assertTrue(list.isCompound());
        Struct compound = new Struct("f", new Struct("a"), new Struct("b"));
        assertTrue(compound.isCompound());
        Struct singleQuoted = new Struct("'atom'");
        assertFalse(singleQuoted.isCompound());
        Struct doubleQuoted = new Struct("\"atom\"");
        assertFalse(doubleQuoted.isCompound());
    }

    public void testEqualsToObject() {
        Struct s = new Struct("id");
        assertFalse(s.equals(new Object()));
    }

}
