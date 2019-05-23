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

import alice.tuprolog.interfaces.IOperatorManager;
import alice.tuprolog.json.JSONSerializerManager;
import alice.tuprolog.management.interfaces.OperatorManagerMXBean;

import java.io.Serializable;
import java.util.*;

/**
 * This class manages Prolog operators.
 *
 * @see Operator
 */
public class OperatorManager implements IOperatorManager, Serializable, OperatorManagerMXBean {

    @Deprecated
    public OperatorManager() {
    }

    public static OperatorManager empty() {
        return new OperatorManager();
    }

    public static OperatorManager defaultOperators() {
        return new DefaultOperatorManager();
    }

    /**
     * lowest operator priority
     */
    public static final int OP_LOW = 1;
    /**
     * highest operator priority
     */
    public static final int OP_HIGH = 1200;

    /**
     * current known operators
     */
    private OperatorRegister operatorList = new OperatorRegister();

    /**
     * Creates a new operator. If the operator is already provided,
     * it replaces it with the new one
     */
    //Usato anche nel management JMX
    public synchronized void opNew(String name, String type, int prio) {
        final Operator op = new Operator(name, type, prio);
        if (prio >= OP_LOW && prio <= OP_HIGH) {
            operatorList.addOperator(op);
        }
    }

    /**
     * Returns the priority of an operator (0 if the operator is not defined).
     */
    //Usato anche nel management JMX
    public synchronized int opPrio(String name, String type) {
        Operator o = operatorList.getOperator(name, type);
        return (o == null) ? 0 : o.prio;
    }

    /**
     * Returns the priority nearest (lower) to the priority of a defined operator
     */
    //Usato anche nel management JMX
    public synchronized int opNext(int prio) {
        int n = 0;
        for (Operator opFromList : operatorList) {
            if (opFromList.prio > n && opFromList.prio < prio) {
                n = opFromList.prio;
            }
        }
        return n;
    }

    /**
     * Gets the list of the operators currently defined
     *
     * @return the list of the operators
     */
    public synchronized List<Operator> getOperators() {
        return new LinkedList<Operator>(operatorList);
    }

    /*Castagna 06/2011*/
    /* Francesco Fabbri
     * 16/05/2011
     * Clone operation added
     */
    public IOperatorManager clone() {
        OperatorManager om = new OperatorManager();
        om.operatorList = (OperatorRegister) this.operatorList.clone();
        return om;
    }

    //Alberto
    @Override
    public String fetchAllOperators() {
        List<Operator> l = getOperators();
        return JSONSerializerManager.toJSON(l);
    }

    ///Management

    @Override
    public synchronized void reset() {
        operatorList = new OperatorRegister();
    }

    /**
     * Register for operators
     * Cashes operator by name+type description.
     * Retains insertion order as LinkedHashSet.
     * <p/>
     * todo Not 100% sure if 'insertion-order-priority' should be completely replaced
     * by the explicit priority given to operators.
     *
     * @author ivar.orstavik@hist.no
     */
    private static class OperatorRegister extends LinkedHashSet<Operator> /*Castagna 06/2011*/ implements Cloneable/**/ {
        //map of operators by name and type
        //key is the nameType of an operator (for example ":-xfx") - value is an Operator
        private HashMap<String, Operator> nameTypeToKey = new HashMap<String, Operator>();

        public boolean addOperator(Operator op) {
            final String nameTypeKey = op.name + op.type;
            Operator matchingOp = nameTypeToKey.get(nameTypeKey);
            if (matchingOp != null) {
                super.remove(matchingOp);       //removes found match from the main list
            }
            nameTypeToKey.put(nameTypeKey, op); //writes over found match in nameTypeToKey map
            return super.add(op);               //adds new operator to the main list
        }

        public Operator getOperator(String name, String type) {
            return nameTypeToKey.get(name + type);
        }

        /*Castagna 06/2011*/
        @Override
        public Object clone() {
            OperatorRegister or = (OperatorRegister) super.clone();
            Iterator<Operator> ior = or.iterator();
            or.nameTypeToKey = new HashMap<String, Operator>();
            while (ior.hasNext()) {
                Operator o = ior.next();
                or.nameTypeToKey.put(o.name + o.type, o);
            }
            return or;
        }
    }
}