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

import alice.tuprolog.parser.dynamic.Associativity;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This class defines a tuProlog operator, in terms of a name,
 * a type, and a  priority.
 */
public final class Operator implements Serializable, Comparable<Operator> {

    /**
     * operator name
     */
    @Deprecated
    public final String name;
    /**
     * type(xf,yf,fx,fy,xfx,xfy,yfy,yfx
     */
    @Deprecated
    public final String type;
    /**
     * priority
     */
    @Deprecated
    public final int prio;
    private final Associativity associativity;

    @Deprecated
    public Operator(String name, Associativity type, int prio) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type).name().toLowerCase();
        this.associativity = type;
        this.prio = prio;
    }

    @Deprecated
    public Operator(String name, String type, int prio) {
        this(name, Associativity.valueOf(type.toUpperCase()), prio);
    }

    public static Operator of(String name, Associativity type, int priority) {
        return new Operator(name, type, priority);
    }

    public static Operator of(String name, String type, int priority) {
        return new Operator(name, type, priority);
    }

    public static Operator fx(String name, int priority) {
        return new Operator(name, Associativity.FX, priority);
    }

    public static Operator fy(String name, int priority) {
        return new Operator(name, Associativity.FY, priority);
    }

    public static Operator xfx(String name, int priority) {
        return new Operator(name, Associativity.XFX, priority);
    }

    public static Operator yfx(String name, int priority) {
        return new Operator(name, Associativity.YFX, priority);
    }

    public static Operator xfy(String name, int priority) {
        return new Operator(name, Associativity.XFY, priority);
    }

    public static Operator yf(String name, int priority) {
        return new Operator(name, Associativity.YF, priority);
    }

    public static Operator xf(String name, int priority) {
        return new Operator(name, Associativity.XF, priority);
    }

    public static Operator[] fx(int priority, String... names) {
        return Stream.of(names).map(name -> Operator.fx(name, priority)).toArray(Operator[]::new);
    }

    public static Operator[] fy(int priority, String... names) {
        return Stream.of(names).map(name -> Operator.fy(name, priority)).toArray(Operator[]::new);
    }

    public static Operator[] xfx(int priority, String... names) {
        return Stream.of(names).map(name -> Operator.xfx(name, priority)).toArray(Operator[]::new);
    }

    public static Operator[] yfx(int priority, String... names) {
        return Stream.of(names).map(name -> Operator.yfx(name, priority)).toArray(Operator[]::new);
    }

    public static Operator[] xfy(int priority, String... names) {
        return Stream.of(names).map(name -> Operator.xfy(name, priority)).toArray(Operator[]::new);
    }

    public static Operator[] yf(int priority, String... names) {
        return Stream.of(names).map(name -> Operator.yf(name, priority)).toArray(Operator[]::new);
    }

    public static Operator[] xf(int priority, String... names) {
        return Stream.of(names).map(name -> Operator.xf(name, priority)).toArray(Operator[]::new);
    }

    public String getName() {
        return name;
    }

    @Deprecated
    public String getType() {
        return type;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public int getPriority() {
        return prio;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Operator operator = (Operator) o;
        return prio == operator.prio &&
               name.equals(operator.name) &&
               associativity == operator.associativity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, associativity, prio);
    }

    @Override
    public String toString() {
        return String.format("op(%d, %s, %s)", getPriority(), getAssociativity().toString().toLowerCase(), getName());
    }

    @Override
    public int compareTo(final Operator o) {
        if (o == null) {
            return 1;
        }

        /*
         * Warning: the implementation of OperatorManager heavily relies on this compareTo implementation
         */

        if (getPriority() == o.getPriority()) {
            if (getAssociativity() == o.getAssociativity()) {
                return getName().compareTo(o.getName());
            } else {
                return getAssociativity().compareTo(o.getAssociativity());
            }
        } else {
            // Highest priority value (i.e. lowest priority) operators first
            return Integer.compare(o.getPriority(), getPriority());
        }
    }
}