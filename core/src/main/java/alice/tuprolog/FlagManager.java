/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuprolog;

import java.util.ArrayList;

/**
 * Administrator of flags declared
 *
 * @author Alex Benini
 */
class FlagManager {

    /**
     * mediator owner of the manager
     */
    protected Prolog mediator;
    /* flag list */
    private ArrayList<Flag> flags;

    FlagManager() { //Alberto
        flags = new ArrayList<Flag>();

        //occursCheck flag -> a default è on!
        Struct s = Struct.emptyList();
        s.append(Struct.atom("on"));
        s.append(Struct.atom("off"));
        defineFlag("occursCheck", s, Struct.atom("on"), true, "BuiltIn");
    }

    /**
     * Config this Manager
     */
    public void initialize(Prolog vm) {
        mediator = vm;
    }

    /**
     * Defines a new flag
     */
    public synchronized boolean defineFlag(String name, Struct valueList, Term defValue,
                                           boolean modifiable, String libName) {
        flags.add(new Flag(name, valueList, defValue, modifiable, libName));
        return true;
    }

    public synchronized boolean setFlag(String name, Term value) {
        java.util.Iterator<Flag> it = flags.iterator();
        while (it.hasNext()) {
            Flag flag = it.next();
            if (flag.getName().equals(name)) {
                if (flag.isModifiable() && flag.isValidValue(value)) {
                    flag.setValue(value);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public synchronized Struct getPrologFlagList() {
        Struct flist = Struct.emptyList();
        java.util.Iterator<Flag> it = flags.iterator();
        while (it.hasNext()) {
            Flag fl = it.next();
            flist = Struct.cons(Struct.of("flag", Struct.atom(fl.getName()), fl.getValue()), flist);
        }
        return flist;
    }

    public synchronized Term getFlag(String name) {
        java.util.Iterator<Flag> it = flags.iterator();
        while (it.hasNext()) {
            Flag fl = it.next();
            if (fl.getName().equals(name)) {
                return fl.getValue();
            }
        }
        return null;
    }

    // restituisce true se esiste un flag di nome name, e tale flag ?
    // modificabile
    public synchronized boolean isModifiable(String name) {
        java.util.Iterator<Flag> it = flags.iterator();
        while (it.hasNext()) {
            Flag flag = it.next();
            if (flag.getName().equals(name)) {
                return flag.isModifiable();
            }
        }
        return false;
    }

    // restituisce true se esiste un flag di nome name, e Value ? un valore
    // ammissibile per tale flag
    public boolean isValidValue(String name, Term value) {
        java.util.Iterator<Flag> it = flags.iterator();
        while (it.hasNext()) {
            Flag flag = it.next();
            if (flag.getName().equals(name)) {
                return flag.isValidValue(value);
            }
        }
        return false;
    }

    //Alberto
    public synchronized boolean isOccursCheckEnabled() {
        for (Flag f : flags) {
            if (f.getName().equals("occursCheck")) {
                return f.getValue().toString().equals("on");
            }
        }
        return false;
    }

}
