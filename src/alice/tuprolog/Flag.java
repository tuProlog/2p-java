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

/**
 * This class represents a prolog Flag
 */
class Flag implements java.io.Serializable {

    private String name;
    private Struct valueList;
    private Term   value;
    private Term   defaultValue;
    private boolean modifiable;
    private String  libraryName;

    Flag(String name, Struct valueSet, Term defValue, boolean modifiable, String library){
        this.name = name;
        this.valueList = valueSet;
        defaultValue = defValue;
        this.modifiable = modifiable;
        libraryName = library;
        value = defValue;
    }

    public boolean isValidValue(Term value){
        java.util.Iterator it=valueList.listIterator();
        while (it.hasNext()){
            Term t=(Term)it.next();
            if (value.match(t)){
                return true;
            }
        }
        return false;
    }

    public String getName(){
        return name;
    }

    public Struct getValueList(){
        return valueList;
    }

    public boolean setValue(Term value) {
        if (isValidValue(value)){
            this.value = value;
            return true;
        } else {
            return false;
        }
    }

    public Term getValue() {
        return value;
    }

    public boolean isModifiable(){
        return modifiable;
    }

    public String getLibraryName(){
        return libraryName;
    }
}