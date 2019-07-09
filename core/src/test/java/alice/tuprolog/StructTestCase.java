package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;
import junit.framework.TestCase;

public class StructTestCase extends TestCase {

    public void testStructWithNullArgument() {
        try {
            Struct.of("p", (Term) null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Struct.of("p", Int.of(1), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Struct.of("p", Int.of(1), Int.of(2), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Struct.of("p", Int.of(1), Int.of(2), Int.of(3), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Struct.of("p", Int.of(1), Int.of(2), Int.of(3), Int.of(4), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Struct.of("p", Int.of(1), Int.of(2), Int.of(3), Int.of(4), Int.of(5), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Struct.of("p", Int.of(1), Int.of(2), Int.of(3), Int.of(4), Int.of(5), Int.of(6), null);
            fail();
        } catch (InvalidTermException expected) {
        }
        try {
            Term[] args = new Term[]{Struct.of("a"), null, Var.of("P")};
            Struct.of("p", args);
            fail();
        } catch (InvalidTermException expected) {
        }
    }

    public void testStructWithNullName() {
        try {
            Struct.of((String) null, Int.of(1), Int.of(2));
            fail();
        } catch (InvalidTermException expected) {
        }
    }

    /**
     * Structs with an emptyWithStandardOperators name can only be atoms.
     */
    public void testStructWithEmptyName() {
        try {
            Struct.of("", Int.of(1), Int.of(2));
            fail();
        } catch (InvalidTermException expected) {
        }
        assertEquals(0, Struct.of("").getName().length());
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
        Struct notAnEmptyList = Struct.of(".");
        assertFalse(notAnEmptyList.isList());
        assertFalse(notAnEmptyList.isEmptyList());
        assertEquals(".", notAnEmptyList.getName());
        assertEquals(0, notAnEmptyList.getArity());
    }

    /**
     * Use dotted structs to build lists with content
     */
    public void testListAsDottedStruct() {
        Struct notAnEmptyList = Struct.of(".", Struct.of("a"), Struct.of(".", Struct.of("b"), Struct.emptyList()));
        assertTrue(notAnEmptyList.isList());
        assertFalse(notAnEmptyList.isEmptyList());
        assertEquals(".", notAnEmptyList.getName());
        assertEquals(2, notAnEmptyList.getArity());
    }

    public void testListFromArgumentArray() {
        assertEquals(Struct.emptyList(), Struct.list(new Term[0]));

        Term[] args = new Term[2];
        args[0] = Struct.of("a");
        args[1] = Struct.of("b");
        Struct list = Struct.list(args);
        assertEquals(Struct.emptyList(), list.listTail().listTail());
    }

    public void testListSize() {
        Struct list = Struct.cons(Struct.of("a"),
                                 Struct.cons(Struct.of("b"),
                                            Struct.cons(Struct.of("c"), Struct.emptyList())));
        assertTrue(list.isList());
        assertFalse(list.isEmptyList());
        assertEquals(3, list.listSize());
    }

    public void testNonListHead() throws InvalidTermException {
        Struct s = Struct.of("f", Var.of("X"));
        try {
            assertNotNull(s.listHead()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testNonListTail() {
        Struct s = Struct.of("h", Int.of(1));
        try {
            assertNotNull(s.listTail()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testNonListSize() throws InvalidTermException {
        Struct s = Struct.of("f", Var.of("X"));
        try {
            assertEquals(0, s.listSize()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testNonListIterator() {
        Struct s = Struct.of("f", Int.of(2));
        try {
            assertNotNull(s.listIterator()); // just to make an assertion...
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("The structure " + s + " is not a list.", e.getMessage());
        }
    }

    public void testToList() {
        Struct emptyList = Struct.emptyList();
        Struct emptyListToList = Struct.cons(Struct.of("[]"), Struct.emptyList());
        assertEquals(emptyListToList, emptyList.toList());
    }

    public void testToString() throws InvalidTermException {
        Struct emptyList = Struct.emptyList();
        assertEquals("[]", emptyList.toString());
        Struct s = Struct.of("f", Var.of("X"));
        assertEquals("f(X)", s.toString());
        Struct list = Struct.cons(Struct.of("a"),
                                 Struct.cons(Struct.of("b"),
                                            Struct.cons(Struct.of("c"), Struct.emptyList())));
        assertEquals("[a,b,c]", list.toString());
    }

    public void testAppend() {
        Struct emptyList = Struct.emptyList();
        Struct list = Struct.cons(Struct.of("a"),
                                 Struct.cons(Struct.of("b"),
                                            Struct.cons(Struct.of("c"), Struct.emptyList())));
        emptyList.append(Struct.of("a"));
        emptyList.append(Struct.of("b"));
        emptyList.append(Struct.of("c"));
        assertEquals(list, emptyList);
        Struct tail = Struct.cons(Struct.of("b"),
                                 Struct.cons(Struct.of("c"), Struct.emptyList()));
        assertEquals(tail, emptyList.listTail());

        emptyList = Struct.emptyList();
        emptyList.append(Struct.emptyList());
        assertEquals(Struct.cons(Struct.emptyList(), Struct.emptyList()), emptyList);

        Struct anotherList = Struct.cons(Struct.of("d"),
                                        Struct.cons(Struct.of("e"), Struct.emptyList()));
        list.append(anotherList);
        assertEquals(anotherList, list.listTail().listTail().listTail().listHead());
    }

    public void testIteratedGoalTerm() throws Exception {
        Var x = Var.of("X");
        Struct foo = Struct.of("foo", x);
        Struct term = Struct.of("^", x, foo);
        assertEquals(foo, term.iteratedGoalTerm());
    }

    public void testIsList() {
        Struct notList = Struct.of(".", Struct.of("a"), Struct.of("b"));
        assertFalse(notList.isList());
    }

    public void testIsAtomic() {
        Struct emptyList = Struct.emptyList();
        assertTrue(emptyList.isAtomic());
        Struct atom = Struct.of("atom");
        assertTrue(atom.isAtomic());
        Struct list = Struct.list(Int.of(0), Int.of(1));
        assertFalse(list.isAtomic());
        Struct compound = Struct.of("f", Struct.of("a"), Struct.of("b"));
        assertFalse(compound.isAtomic());
        Struct singleQuoted = Struct.of("'atom'");
        assertTrue(singleQuoted.isAtomic());
        Struct doubleQuoted = Struct.of("\"atom\"");
        assertTrue(doubleQuoted.isAtomic());
    }

    public void testIsAtom() {
        Struct emptyList = Struct.emptyList();
        assertTrue(emptyList.isAtom());
        Struct atom = Struct.of("atom");
        assertTrue(atom.isAtom());
        Struct list = Struct.list(Int.of(0), Int.of(1));
        assertFalse(list.isAtom());
        Struct compound = Struct.of("f", Struct.of("a"), Struct.of("b"));
        assertFalse(compound.isAtom());
        Struct singleQuoted = Struct.of("'atom'");
        assertTrue(singleQuoted.isAtom());
        Struct doubleQuoted = Struct.of("\"atom\"");
        assertTrue(doubleQuoted.isAtom());
    }

    public void testIsCompound() {
        Struct emptyList = Struct.emptyList();
        assertFalse(emptyList.isCompound());
        Struct atom = Struct.of("atom");
        assertFalse(atom.isCompound());
        Struct list = Struct.list(new Term[]{Int.of(0), Int.of(1)});
        assertTrue(list.isCompound());
        Struct compound = Struct.of("f", Struct.of("a"), Struct.of("b"));
        assertTrue(compound.isCompound());
        Struct singleQuoted = Struct.of("'atom'");
        assertFalse(singleQuoted.isCompound());
        Struct doubleQuoted = Struct.of("\"atom\"");
        assertFalse(doubleQuoted.isCompound());
    }

    public void testEqualsToObject() {
        Struct s = Struct.of("id");
        assertFalse(s.equals(new Object()));
    }

}
