/*
 * tuProlog - Copyright (C) 2001-2006  aliCE team at deis.unibo.it
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
import java.util.*;
import java.util.stream.Stream;

/**
 * This class manages Prolog operators.
 *
 * @see Operator
 */
public class OperatorManager implements Serializable {

    public static OperatorManager empty() {
        return new OperatorManager();
    }

    public static OperatorManager defaultOperators() {
        return new DefaultOperatorManager();
    }

    private final SortedSet<Operator> operators;

    @Deprecated
    public OperatorManager() {
        this(Collections.emptyList());
    }

    /**
     * lowest operator priority
     */
    public static final int OP_LOW = 1;
    /**
     * highest operator priority
     */
    public static final int OP_HIGH = 1200;

    private OperatorManager(Collection<Operator> operators) {
        this.operators = new TreeSet<>(operators);
    }

    /**
     * Creates a new operator. If the operator is already provided,
     * it replaces it with the new one
     *
     * @see #add(Operator)
     */
    @Deprecated
    public void opNew(String name, String type, int prio) {
        add(name, type, prio);
    }

    public OperatorManager add(String name, Associativity type, int prio) {
        return add(Operator.of(name, type, prio));
    }

    public OperatorManager add(String name, String type, int prio) {
        return add(Operator.of(name, type, prio));
    }

    public OperatorManager addAll(Operator... operators) {
        return addAll(Stream.of(operators));
    }

    public OperatorManager addAll(Collection<Operator> operators) {
        return addAll(operators.stream());
    }

    public OperatorManager addAll(Stream<Operator> operators) {
        operators.forEach(this::add);
        return this;
    }

    public OperatorManager addAll(OperatorManager other) {
        return addAll(Objects.requireNonNull(other).operators);
    }

    /**
     * Creates a new operator. If the operator is already provided,
     * it replaces it with the new one
     */
    public OperatorManager add(Operator operator) {
        Objects.requireNonNull(operator);

        if (operator.getPriority() >= OP_LOW && operator.getPriority() <= OP_HIGH) {
            operators.add(operator);
        } else {
            throw new IllegalArgumentException(
                    "Illegal priority for " + operator + " it must be in the range " + OP_LOW + ".." + OP_HIGH);
        }

        return this;
    }

    /**
     * Returns the priority of an operator (0 if the operator is not defined).
     */
    @Deprecated
    public int opPrio(String name, String type) {
        return getOperatorPriority(name, type);
    }

    public int getOperatorPriority(String name, Associativity type) {
        Objects.requireNonNull(name);
        return operators.subSet(Operator.of(name, type, OP_HIGH), Operator.of(name, type, OP_LOW))
                        .first()
                        .getPriority();
    }

    public int getOperatorPriority(String name, String type) {
        return getOperatorPriority(name, Associativity.valueOf(type.toUpperCase()));
    }

    /**
     * Returns the priority nearest (lower) to the priority of a defined operator
     */
    @Deprecated
    public int opNext(int prio) {
        return getNearestPriority(prio);
    }

    public int getNearestPriority(int priority) {
        return operators.subSet(
                Operator.of("", Associativity.values(0), OP_HIGH + 1),
                Operator.of("", Associativity.values(0), priority - 1)
        ).last().getPriority();
    }

    /**
     * Gets the list of the operators currently defined
     *
     * @return the list of the operators
     */
    public List<Operator> getOperators() {
        return new ArrayList<>(operators);
    }

    public OperatorManager clone() {
        return new OperatorManager(operators);
    }

    @Deprecated
    public void reset() {
        operators.clear();
    }


    public OperatorManager clear() {
        operators.clear();
        return this;
    }

}