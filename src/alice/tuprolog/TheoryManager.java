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
import	java.io.*;
import  java.util.*;

/**
 * This class defines the engine clauses manager.
 * <p>
 * The clauses are stored in a Hashtable grouped by functor
 * ( each group is a list that keeps the right clause sequence)
 *
 * @see Theory
 *
 *
 *
 */
class TheoryManager implements Serializable {

    /** asserted or retracted clause list */
    private   alice.util.LinkedList   transientClauseList;

    /** register dbase changed in transientClauseList? */
    private   boolean   updateTransientClauseList;

    /** clause dbase */
    protected HashMap   clauseDBase;

    /** number of clauses */
    protected int       clauseNumber;

    /**  engine owner of the manager*/
    protected Prolog  engine;

    /** eventually start goal of a theory */
    private Struct startGoal;

    public TheoryManager(Prolog vm){
        transientClauseList  = new alice.util.LinkedList();
        updateTransientClauseList  = false;
        clauseDBase  = new HashMap();
        clauseNumber  = 0;
        engine =vm;
    }

    /**
     * gets elements list related to key k
     */
    private alice.util.LinkedQueue getQueue(Object k) {
        alice.util.LinkedQueue q = (alice.util.LinkedQueue)clauseDBase.get(k);
        if(q == null) {
            q = new alice.util.LinkedQueue();
            clauseDBase.put(k,q);
        }
        return(q);
    }

    /**
     * inserting of a clause at the head of the dbase
     */
    public void assertA(Struct clause,boolean dyn,String libName) {
        ClauseInfo  d = new ClauseInfo(clause,engine,dyn,libName);
        //System.out.println("ASSERTA "+clause);
        alice.util.LinkedQueue q = getQueue(((Struct)(d.clause.getArg(0))).getHashKey());
        q.insFirst(d);
        transOp(d,true,true);
    }

    /**
     * inserting of a clause at the end of the dbase
     */
    public void assertZ(Struct clause,boolean dyn,String libName) {
        //System.out.println("ASSERTZ "+clause);
        ClauseInfo  d = new ClauseInfo(clause,engine,dyn,libName);
        alice.util.LinkedQueue q = getQueue(((Struct)(d.clause.getArg(0))).getHashKey());
        q.insLast(d);
        transOp(d,true,false);
    }

    /**
     * removing from dbase the first clause with head unifying with clause
     * (m if a free substitution index and t is the first free timestamp)
     */
    public ClauseInfo retract(Struct cl,int t,int m) {
        Struct clause     = (new ClauseInfo(cl,engine,true,null)).clause;
        ClauseInfo  r = null;
        alice.util.LinkedQueue q = getQueue(((Struct)(clause.getArg(0))).getHashKey());
        alice.util.LinkedList  l = q.head;
        while(!l.isEmptyList()) {
            ClauseInfo d = (ClauseInfo)l.head; l = l.tail;
            if(d.flag) {
                boolean f = clause.unify(d.clause,m);
                clause.free(m);
                d.clause.free(m);
                if(f) {
                    r = d;
                    break;
                }
            }
        }
        if(r == null){
            return(null);
        }
        transOp(r,false,false);
        return(new ClauseInfo(r.clause,t,true,null));
    }

    /**
     * finds all the clauses unifying with head and returns a list of
     * renamed (without logic names) clause copies
     */
    public alice.util.LinkedList find(Struct head,int t,int m) {
        alice.util.LinkedQueue r = new alice.util.LinkedQueue();
        alice.util.LinkedQueue q = getQueue(head.getHashKey());//head);
        alice.util.LinkedList  l = q.head;
        while(!l.isEmptyList()) {
            ClauseInfo d = (ClauseInfo)l.head;
            l = l.tail;
            if(d.flag) {
                boolean f = head.unify(d.clause.getArg(0),m);
                head.free(m);
                d.clause.free(m);
                if(f){
                    r.insLast(new ClauseInfo(d.clause,t,d.dynamic,null));
                }
            }
        }
        return(r.head);
    }

    /**
     * finds all the clauses unifying with head and returns a list of
     * renamed (without logic names) clause copies
     */
    public void rebindBuiltins() {
        //System.out.println("rebinding buintins----------------------------");
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            alice.util.LinkedList  l = q.head;
            while(!l.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)l.head;
                l = l.tail;
                if (d.flag && d.dynamic) {
                    //System.out.println("clause "+d.clause);
                    Term body=d.clause.getArg(1);
                    while (body.isStruct() && ((Struct)body).getName().equals(",")){
                        Term t=((Struct)body).getArg(0);
                        engine.identify(t);
                        //System.out.println("identifying "+t);
                        body=((Struct)body).getArg(1);
                    }
                    engine.identify(body);
                }
            }
        }
    }


    /**
     * returns a list of dbase valid clause copies, grouped by functor
     */
    public alice.util.LinkedList elements() {
        alice.util.LinkedQueue r = new alice.util.LinkedQueue();
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            alice.util.LinkedList  l = q.head;
            while(!l.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)l.head; l = l.tail;
                if(d.flag){
                    r.insLast(new ClauseInfo(d.clause,engine,d.dynamic,null));
                }
            }
        }
        return(r.head);
    }

    /**
     * returns a list of dbase valid dynamic clause
     */
    public ArrayList getDynamicClauseList() {
        ArrayList list=new ArrayList();
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            alice.util.LinkedList  l = q.head;
            while(!l.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)l.head;
                l = l.tail;
                if(d.flag && d.dynamic){
                    list.add(d.clause);
                }
            }
        }
        return list;
    }

    /**
     * resets dbase, clearing and and filling it with elements
     */
    public void elements(alice.util.LinkedList elements,boolean dynamicClauses,String libName) {
        clear(true);
        addElements(elements,dynamicClauses,libName);
    }

    /**
     * appends a clauses list to the dbase (in the same order
     * of elements)
     */
    public void addElements(alice.util.LinkedList elements,boolean dynamicClauses,String libName) {
        while(!elements.isEmptyList()) {
            ClauseInfo d = (ClauseInfo)elements.head;
            elements = elements.tail;
            assertZ(d.clause,dynamicClauses,libName);
        }
    }

    /**
     * empties the dbase.
     * <p>
     * if onlyDynamic, thenremove only clauses tagged 'dynamic'
     */
    public void clear(boolean onlyDynamic) {
        if (!onlyDynamic){
            clauseDBase = new HashMap();
            clauseNumber = 0;
        } else {
            Iterator it=clauseDBase.values().iterator();
            while (it.hasNext()){
                alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
                alice.util.LinkedList  l = q.head;
                while(!l.isEmptyList()) {
                    ClauseInfo d = (ClauseInfo)l.head;
                    l = l.tail;
                    if (d.flag && d.dynamic){
                        d.flag=false;
                        clauseNumber--;
                    }
                }
            }
            optimize();
        }
    }

    /**
     * remove all the clauses of lib theory
     */
    public void removeLibraryTheory(String libName) {
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            alice.util.LinkedList  l = q.head;
            while(!l.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)l.head;
                l = l.tail;
                if (d.flag && d.libName!=null && libName.equals(d.libName)){
                    d.flag=false;
                    clauseNumber--;
                }
            }
        }
        optimize();
    }

    /**
     * starts a transaction on the dbase
     */
    public void transBegin() {
        transientClauseList = new alice.util.LinkedList();
        updateTransientClauseList = true;
    }

    /**
     * makes a transaction operation on the dbase.
     * <p>
     * d is the clause specified by the operation;
     * if f is true then insert, else delete clause from dbase;
     * if p is true then head insertion, else queue insertion
     *
     */
    public void transOp(ClauseInfo d,boolean f,boolean p) {
        if(f) {
            clauseNumber++;
            engine.spy("INSERT" + (p ? "A" : "Z") + ": " + d.clause + "\n");
        }
        else {
            clauseNumber--;
            engine.spy("DELETE: " + d.clause + "\n");
        }
        d.flag = f;
        if(updateTransientClauseList){
            transientClauseList = new alice.util.LinkedList(d,transientClauseList);
        }
    }

    /**
     * returns current liste of asserted/retracted clauses
     */
    public alice.util.LinkedList transStatus() {
        return(transientClauseList);
    }

    /**
     * rollbacks dbase to a state spcified by the retracted/asserted clauses list
     */
    public void transRestore(alice.util.LinkedList ol) {
        while(transientClauseList != ol) {
            ClauseInfo d = (ClauseInfo)transientClauseList.head;
            transientClauseList = transientClauseList.tail;
            if(d.flag) {
                clauseNumber--;
                engine.spy("RESTORE-: " + d.clause + "\n");
            }
            else {
                clauseNumber++;
                engine.spy("RESTORE+: " + d.clause + "\n");
            }
            d.flag = !d.flag;
        }
    }

    /**
     * ends a transaction.
     * <p>
     * if f is false, then rolls back the dbase to the state
     * before transaction begin; if f is true, then commit changes
     */
    public boolean transEnd(boolean f) {
        if(!f) {
            while(!transientClauseList.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)transientClauseList.head;
                transientClauseList = transientClauseList.tail;
                if(d.flag) {
                    clauseNumber--;
                    engine.spy("RESTORE-: " + d.clause + "\n");
                }
                else {
                    clauseNumber++;
                    engine.spy("RESTORE+: " + d.clause + "\n");
                }
                d.flag = !d.flag;
            }
        }
        updateTransientClauseList = false;
        return(!transientClauseList.isEmptyList());
    }

    /**
     * compacts dbase deleting items invalidated by retract op.
     */
    public void optimize() {
        int n = 0;
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            int      b = q.length();
            while(b-- > 0) {
                ClauseInfo d = (ClauseInfo)q.remFirst();
                if(d.flag){
                    q.insLast(d);
                } else {
                    n++;
                }
            }
        }
        if(n > 0){
            engine.spy("OPTIMIZE: " + n + "\n");
        }
    }

    /**
     * consults a theory.
     * <p>
     * if m is true, then set theory, if m is false then add theory;
     * if dynamicTheory is true, then the clauses are marked as dynamic
     */
    public void consult(Theory theory,boolean m,boolean dynamicTheory,String libName)
                throws InvalidTheoryException {
        if (theory==null){
            throw new InvalidTheoryException();
        }
        transBegin();
        startGoal=null;
        alice.util.LinkedQueue        q  = new alice.util.LinkedQueue();
        Term lastTerm=null;
        boolean r=true;
        if (theory.isTextual()){
            Parser p = new Parser(engine.getOperatorManager(),theory.toString());
            int nc=0;
            while(p.readTerm(true) != Parser.EOF){
                if (p.getCurrentTerm()!=null && p.getCurrentTermType()== Parser.TERM &&
                    p.getCurrentTerm().isStruct()) {
                    boolean result=parseClause((Struct)p.getCurrentTerm(),q,dynamicTheory,libName);
                    if (!result){
                        r = false;
                        break;
                    }
                    nc++;
                    engine.spy("READ #"+nc+": " + p.getCurrentTerm());
                } else {
                    r = false;
                    break;
                }
            }
            if (r){
                if(m){
                    elements(q.head,dynamicTheory,libName);
                } else {
                    addElements(q.head,dynamicTheory,libName);
                }
                transEnd(true);
            } else {
                transEnd(false);
                throw new InvalidTheoryException(p.getCurrentLine(),p.getCurrentPos());
            }
        } else {
            try {
                Struct list = theory.getClauseListRepresentation();
                while (!list.isEmptyList()){
                    boolean result=parseClause((Struct)list.listHead(),q,dynamicTheory,libName);
                    if (!result){
                        transEnd(false);
                        throw new InvalidTheoryException();
                    }
                    list=list.listTail();
                }
                if(m){
                    elements(q.head,dynamicTheory,libName);
                } else {
                    addElements(q.head,dynamicTheory,libName);
                }
                transEnd(true);
            } catch (Exception ex){
                transEnd(false);
                throw new InvalidTheoryException();
            }
        }
    }


    private boolean parseClause(Struct co,alice.util.LinkedQueue clauses,boolean dynamicTheory,String libName){
        try {
            ClauseInfo info=new ClauseInfo(co,engine,dynamicTheory,libName);
            String name=co.getName();
            if ((name.equals("':-'")||name.equals(":-")) && co.getArity()==1 && co.getTerm(0).isStruct()){
                // directive management
                Struct dir=(Struct)co.getTerm(0);
                if (dir.getName().equals("op") && dir.getArity()==3){
                    Term t0=dir.getTerm(0);
                    Term t1=dir.getTerm(1);
                    Term t2=dir.getTerm(2);
                    if (t0.isNumber() && t2.isAtom() && t1.isAtom()){
                        String st=((Struct)t1).getName();
                        if (st.equals("fx")|st.equals("fy")|st.equals("xf")|st.equals("yf")|
                        st.equals("xfx")|st.equals("yfx")|st.equals("xfy")){
                            engine.getOperatorManager().opNew(((Struct)dir.getArg(2)).getName(),
                                                              ((Struct)dir.getArg(1)).getName(),
                                                              ((Number)t0).intValue());
                            //System.out.println("new operator: "+dir);
                        }
                    }
                } else if (dir.getName().equals("flag") && dir.getArity()==4){
                    Term flagName=dir.getTerm(0);
                    Term flagSet=dir.getTerm(1);
                    Term flagDefault=dir.getTerm(2);
                    Term flagModifiable=dir.getTerm(3);
                    if (flagSet.isList() && (flagModifiable.equals(Term.TRUE)||flagModifiable.equals(Term.FALSE))){
                        engine.defineFlag(flagName.toString(), (Struct)flagSet, flagDefault, flagModifiable.equals(Term.TRUE), libName);
                    }
                } else if ((dir.getName().equals("solve")||dir.getName().equals("initialization")) && dir.getArity()==1){
                    if (dir.getTerm(0).isStruct()){
                        startGoal=(Struct)dir.getTerm(0);
                    }
                } else if (dir.getName().equals("load_library") && dir.getArity()==1){
                    try {
                        engine.loadLibrary(alice.util.Tools.removeApices(dir.getTerm(0).toString()));
                    } catch (Exception ex){
                        return false;
                    }
                } else if (dir.getName().equals("consult") && dir.getArity() ==1){
                    try {
                        engine.addTheory(new Theory(
                            new FileInputStream(alice.util.Tools.removeApices(dir.getTerm(0).toString()))));
                    } catch (Exception ex){
                        return false;
                    }
                } else {
                    // unknown directive -> consider like a simple predicate
                    clauses.insLast(info);
                }
            } else {
                clauses.insLast(info);
            }
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * gets the goal eventually defined by last parsed theory.
     */
    public Struct getStartGoal(){
        return startGoal;
    }

    /**
     * saves the dbase on a output stream.
     */
    public boolean save(OutputStream os,boolean onlyDynamic){
        optimize();
        boolean          r  = true;
        DataOutputStream ds = new DataOutputStream(os);
        alice.util.LinkedList          l  = elements();
        while(!l.isEmptyList()) {
            ClauseInfo d = (ClauseInfo)l.head; l = l.tail;
            if (d.flag && !(onlyDynamic && !d.dynamic)) {
                try {
                    ds.writeBytes(d.toString(engine.getOperatorManager()));
                } catch(Exception e) {
                    r = false;
                    break;
                }
            }
        }
        return(r);
    }

}