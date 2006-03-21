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
 * @version 1.4.1
 */
class TheoryManager implements Serializable {
	
    /** asserted or retracted clause list */
    private   alice.util.LinkedList   transientClauseList;

    /** register dbase changed in transientClauseList? */
    private   boolean   updateTransientClauseList;

    /** clause dbase */
    protected HashMap   clauseDBase;
    
    /** HM to store Names corresponding to inner ClauseNames */
   	private HashMap 	clauseNames;    				//ENANETTI

    /** number of clauses */
    protected int       clauseNumber;

    /**  engine owner of the manager*/
    protected Prolog  engine;

    /** eventually start goal of a theory */
    private Struct startGoal;

    private Theory consultedTheory;
    
    /* runtime loaded theories */						//ENanetti
    private ArrayList currentTheories;
	
	/*counter to track theory identity*/
    static private int ThCount = 0;
    
    
    public TheoryManager(Prolog vm){
        transientClauseList  = new alice.util.LinkedList();
        updateTransientClauseList  = false;
        clauseDBase  = new HashMap();
        clauseNames = new HashMap();					
        clauseNumber  = 0;
        engine =vm;
        currentTheories = new ArrayList();
        //Crea una propria teoria interna, vuota, chiamata "default" 
        try {
            consultedTheory = new Theory("","default");
            addTheory(consultedTheory);
            
            //currentTheories.add(consultedTheory);
        } catch (Exception ex){
            
        }
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
    

    private void checkPresence(Struct clause, String libName, alice.util.LinkedList list){
        Struct head = (Struct)clause.getArg(0);
 
        Library lib = engine.getLibraryPredicate(head.getName(),head.getArity());
        if (lib!=null){
            if (libName!=null){
                engine.warn("a builtin predicate with signature equals to the head of clause "+clause+" in library "+libName+"  has been defined in library "+
                        lib.getClass().getName());
            } else {
                engine.warn("a builtin predicate with signature equals to the head of clause "+clause+" has been defined in library "+
                        lib.getClass().getName());
            }
        } else {
            lib = engine.getLibraryFunctor(head.getName(),head.getArity());
            if (lib!=null){
                if (libName!=null){
                    engine.warn("a builtin functor with signature equals to the head of clause "+clause+" in library "+libName+"  has been defined in library "+
                            lib.getClass().getName());
                } else {
                    engine.warn("a builtin functor with signature equals to the head of clause "+clause+" has been defined in library "+
                            lib.getClass().getName());
                }
            } else {
                if (list.head!=null){
                    String libn=((ClauseInfo)(list.head)).libName;
                    if (libn!=null && !libn.equals(libName)){
                        if (libName!=null){
                            engine.warn("a clause with the same head of "+clause+" in library "+libName+"  has been defined in library "+
                                    ((ClauseInfo)list.head).libName+" as "+((ClauseInfo)list.head).clause);
                        } else {
                            engine.warn("a clause with the same head of "+clause+"  has been defined in library "+
                                    ((ClauseInfo)list.head).libName+" as "+((ClauseInfo)list.head).clause);
                        }
                    }
                }
            }
        }
    }
    

    /**
     * inserting of a clause at the head of the dbase
     */
    public void assertA(Struct clause,boolean dyn,String libName,boolean backtrackable) {
        ClauseInfo  d = new ClauseInfo(clause,engine,dyn,libName);
        //System.out.println("ASSERTA "+clause);
        alice.util.LinkedQueue q = getQueue(((Struct)d.clause.getArg(0)).getHashKey());
        //
        //
        if (engine.isWarning()){
            checkPresence(d.clause,libName,q.head);
        }
        //
        q.insFirst(d);
        if (backtrackable){
            transOp(d,true,true);
        } else {
            d.stillValid = true;
            clauseNumber++;
        }
    }

    /**
     * inserting of a clause at the end of the dbase
     */
    public void assertZ(Struct clause,boolean dyn,String libName, boolean backtrackable) {
        //System.out.println("ASSERTZ "+clause);
        ClauseInfo  d = new ClauseInfo(clause,engine,dyn,libName);
        alice.util.LinkedQueue q = getQueue(((Struct)(d.clause.getArg(0))).getHashKey());
        //
        //
        

        //
        /*QUI.     ENANETTI
         *
         *CONTROLLO CHE LA TEORIA RELATIVA SIA GIA' CARICATA.
         *SE NO, GESTIONE NORMALE!!!
         *SE SI', CONTROLLO DI OGNI ELEMENTO DELLA q E INSERIMENTO DELLE CLAUSOLE NUOVE (TUTTE) DOPO QUELLE OPPORTUNE
         *RIPRISTINO CON LE ALTRE CLAUSOLE RESTANTI.
         */
/*         
         //se teoria già caricata
         if (alreadyLoaded(libName) != -1){
         	//LQ temporanea per passarci i dati letti man mano
         	alice.util.LinkedQueue q1 = new alice.util.LinkedQueue();
         	//LQ da DB clausole temporanee, per teorie già esistenti.
         	//alice.util.LinkedQueue tmpq = getQueue2(((Struct)(d.clause.getArg(0))).getHashKey());
         	
         	alice.util.LinkedList l = q.head;
         	while(!l.isEmptyList()){
         		ClauseInfo ci = (ClauseInfo)l.head;  l=l.tail;
         		//se in nome delle clausola recuperata è uguale a quella che voglio inserire, metti la nuova subito dopo
         		if(libName.equals(ci.libName)){
         			System.out.println("Clausola gia' presente fase1, procedo.");
         			//lettura della testa della lista restante, elemento successivo	
       				if (!l.isEmptyList()){
       					
       				ClauseInfo ci2 = (ClauseInfo)l.head;
         			System.out.println("Clausola letta: "+ci2.toString());
         			//se anche il successivo è uguale, inserisci il primo nella lista e vai oltre
         			if (libName.equals(ci2.libName)) {
         				System.out.println("Clausola gia' presente fase2, procedo.");
         				q1.insLast(ci);
         			}
         			}
         			else{
         				q1.insLast(ci);
         				q1.insLast(d);
         				
         				while(!tmpq.isEmptyQueue()){
         					q1.insLast((ClauseInfo)tmpq.remFirst());
         				}//fine while	
         			
         			}//fine else
         			
         		}//fine if
         		else
         		//la clausola estratta dal DB esistente non mi interessa, mantengo l'ordine.
         			q1.insLast(ci);
         		
         	}//fine while
         	
         	q = q1;         	
         }//fine if
         else{
*/         
         //gestione standard, aggiunge la clausola in fondo al DB 
         	
         	checkPresence(d.clause,libName,q.head);
         	
         	q.insLast(d);
         
         	
        if (backtrackable){
            transOp(d,true,false);
        } else {
            d.stillValid = true;
            clauseNumber++;       
        }
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
            if(d.stillValid) {
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
    public alice.util.LinkedList find(Term headt,int t,int m) {
        alice.util.LinkedQueue r = new alice.util.LinkedQueue();
        
        if (headt.isStruct()){
        	    Struct head = (Struct)headt;
	        alice.util.LinkedQueue q = getQueue(head.getHashKey());//head);
	        alice.util.LinkedList  l = q.head;
	        while(!l.isEmptyList()) {
	            ClauseInfo d = (ClauseInfo)l.head;
	            l = l.tail;
	            if(d.stillValid) {
	                boolean f = head.unify(d.clause.getArg(0),m);
	                head.free(m);
	                d.clause.free(m);
	                if(f){
	                    r.insLast(new ClauseInfo(d.clause,t,d.dynamic,null));
	                }
	            }
	        }
        } else if (headt.isVar()){
        		Iterator it = clauseDBase.values().iterator();
			while (it.hasNext()){
				alice.util.LinkedList  l = ((alice.util.LinkedQueue)it.next()).head;
			    while(!l.isEmptyList()) {
			        ClauseInfo d = (ClauseInfo)l.head;
			        l = l.tail;
			        if(d.stillValid && d.dynamic) {
			        		r.insLast(new ClauseInfo(d.clause,t,d.dynamic,null));
			        }
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
                if (d.stillValid && d.dynamic) {
                    //System.out.println("clause "+d.clause);
                    Term body=d.clause.getArg(1);
                    while (body.isStruct() && ((Struct)body).getName().equals(",")){
                        Term t=((Struct)body).getArg(0);
                        engine.identify(t,false);
                        //System.out.println("identifying "+t);
                        body=((Struct)body).getArg(1);
                    }
                    engine.identify(body,false);
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
                if(d.stillValid){
                    r.insLast(new ClauseInfo(d.clause,engine,d.dynamic,null));
                }
            }
        }
        return(r.head);
    }

	
    /**
     * returns a list of dbase valid clause copies, grouped by functor
     * list is related to the specified theory
     */
     //ENanetti 
     
    public alice.util.LinkedList elements(String name) {
        alice.util.LinkedQueue r = new alice.util.LinkedQueue();
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            alice.util.LinkedList  l = q.head;
            while(!l.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)l.head; l = l.tail;
                if(d.stillValid && (name.equals(d.libName))){
                    r.insLast(new ClauseInfo(d.clause,engine,d.dynamic,name));
                }
            }
        }
        return(r.head);
    }
	//fine elements(name)



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
                if(d.stillValid && d.dynamic){
                    list.add(d.clause);
                }
            }
        }
        return list;
    }
    
    
   
    /**
     * returns a list of dbase valid dynamic clause, related to the specified theory
     */
     //ENanetti 
     
    public ArrayList getNameList(String thName) {
        ArrayList list=new ArrayList();
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            alice.util.LinkedList  l = q.head;
            while(!l.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)l.head;
                l = l.tail;
                if(d.stillValid && d.dynamic && (d.libName.equals(thName))){
                    list.add(d.clause);
                }
            }
        }
        return list;
    }
    //fine getNameList
    
    
    
    
    /**
     * remove all the clauses of specified theory from the main database
     *
     *@param thName the name of the theory to remove
     *@throws InvalidTheoryException if specified Theory is not present
     *
     */
     //ENanetti
    public void removeTheory(String thName) throws InvalidTheoryException{
    	    	    	
		//recupero dell'ultima teoria inserita con il nome voluto dal vettore 
		Theory toRemove = getLastInsTheory(thName);
		//se la teoria restituita è nulla -> eccezione
		if (toRemove == null) throw new InvalidTheoryException();
		
		//estraggo la stringa col nome interno, rimuovendola dalla HM
		String innerName = (String)clauseNames.remove(toRemove);
		    	    	
        Iterator it=clauseDBase.values().iterator();
        while (it.hasNext()){
            alice.util.LinkedQueue q = (alice.util.LinkedQueue)it.next();
            alice.util.LinkedList  l = q.head;
            while(!l.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)l.head;
                l = l.tail;
                if (d.stillValid && d.libName!=null && innerName.equals(d.libName)){
                    d.stillValid=false;
                    clauseNumber--;
                }
            }
        }
        optimize();
                
    }
    //fine removeTheory
    


    

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
            assertZ(d.clause,dynamicClauses,libName,true);
        }
    }

    /**
     * Clears the clause dbase.
     * 
     * if onlyDynamic, it removes only clauses tagged 'dynamic'
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
                    if (d.stillValid && d.dynamic){
                        d.stillValid=false;
                        clauseNumber--;
                    }
                }
            }
            optimize();
        }
        try {
            consultedTheory = new Theory("");
        } catch (Exception ex){
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
                if (d.stillValid && d.libName!=null && libName.equals(d.libName)){
                    d.stillValid=false;
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
     *
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
        d.stillValid = f;
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
            if(d.stillValid) {
                clauseNumber--;
                engine.spy("RESTORE-: " + d.clause + "\n");
            }
            else {
                clauseNumber++;
                engine.spy("RESTORE+: " + d.clause + "\n");
            }
            d.stillValid = !d.stillValid;
        }
    }

    /**
     * Ends a transaction.
     *
     * if f is false, then rolls back the dbase to the state
     * before transaction begin; if f is true, then commit changes
     */
    public boolean transEnd(boolean f) {
        if(!f) {
            while(!transientClauseList.isEmptyList()) {
                ClauseInfo d = (ClauseInfo)transientClauseList.head;
                transientClauseList = transientClauseList.tail;
                if(d.stillValid) {
                    clauseNumber--;
                    engine.spy("RESTORE-: " + d.clause + "\n");
                }
                else {
                    clauseNumber++;
                    engine.spy("RESTORE+: " + d.clause + "\n");
                }
                d.stillValid = !d.stillValid;
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
                if(d.stillValid){
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
     * Consults a theory.
     *
     * @param theory theory to add 
     * @param add if it is true, then adds the theory,else sets the theory.
     * @param dynamicTheory if it is true, then the clauses are marked as dynamic
     * @param libName if it not null, then the clauses are marked to belong to the specificed library
     */
    public void consult(Theory theory,boolean add,boolean dynamicTheory,String libName)
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
                if(add){
                    addElements(q.head,dynamicTheory,libName);
                } else {
                    elements(q.head,dynamicTheory,libName);
                }
                transEnd(true);
            } else {
                engine.spy("ERROR in " + p.getCurrentTerm());
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
                if (add){
                    addElements(q.head,dynamicTheory,libName);
                } else {
                    elements(q.head,dynamicTheory,libName);
                }
                transEnd(true);
            } catch (Exception ex){
                transEnd(false);
                throw new InvalidTheoryException();
            }
        }
        if (libName==null){
	        if (add){
	            consultedTheory = new Theory(consultedTheory.toString() + "\n" + theory.toString());
	        } else {
	            consultedTheory = new Theory(theory.toString());
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
                    
                } else if (dir.getName().equals("consult") && dir.getArity() ==2){
                	//ENANETTI: Consult with specified theory name
                    try {
                        engine.addTheory(new Theory(
                            new FileInputStream(alice.util.Tools.removeApices(dir.getTerm(0).toString())), 
                            					dir.getTerm(1).toString()));
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
    public  boolean save(OutputStream os,boolean onlyDynamic){
        optimize();
        boolean          r  = true;
        DataOutputStream ds = new DataOutputStream(os);
        alice.util.LinkedList          l  = elements();
        while(!l.isEmptyList()) {

            ClauseInfo d = (ClauseInfo)l.head; l = l.tail;
            if (d.stillValid && !(onlyDynamic && !d.dynamic)) {
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


	/**
	 * Adds specified theory to the database.
	 *
	 *@param th the theory to be added
	 */
	 //ENANETTI
	public void addTheory(Theory th) throws InvalidTheoryException{
	
		//aggiunta della teoria alla lista che le registra
		currentTheories.add(th);
		
		//global counter incremented, to keep unique theory
		ThCount++;
		
		String innerName = th.getName()+ThCount;
		
		//inserimento corrispondenza Teoria-nomeClausole nella HM
		clauseNames.put(th, innerName);
				
		
		//da effettuare controllo su nome "default"
		if(innerName!=null)										//ENANETTI
        	consult(th, true, true, innerName);					//ENANETTI
        else    												//ENANETTI
        	consult(th, true, true, null);						//ENANETTI
/*        
        int i=alreadyLoaded(th);
        //controllo sull'esistenza della teoria nel vettore
        if (i != -1){
        	Theory tmpTh = (Theory)currentTheories.get(i);
        	//appendo la nuova teoria a quella nuova
        	tmpTh.append(th);
        	//ripristino il vettore
        	currentTheories.set(i,tmpTh);
        }
        //se non è già caricata la aggiungo in coda
        else
*/
        
	}
	//fine addTheory



    /**
     *	Gets current theory loaded from the whole database
     *
     *@return a new Theory made of all loaded theories, as only one
     */
    //ENANETTI 
    public Theory getWholeTheory(){
    	try{
    		return new Theory(getTheory(true),"£allClauses");
    	} catch(InvalidTheoryException ite){
    		return null;	
    	}
    }
    //fine getTheory pubblica
    
    /**
     * Gets current theory
     * 
     * @param onlyDynamic if true, fetches only dynamic clauses
     */
    private String getTheory(boolean onlyDynamic){
        optimize();
        StringBuffer buffer = new StringBuffer();	
        alice.util.LinkedList l  = elements();
        while(!l.isEmptyList()) {
            ClauseInfo d = (ClauseInfo)l.head; l = l.tail;
            if (d.stillValid && !(onlyDynamic && !d.dynamic)) {
                try {
                    buffer.append(d.toString(engine.getOperatorManager()));
                    buffer.append("\n");
                } catch(Exception e) {
                    return null;
                }
            }
        }
        return buffer.toString();
    }
    
    
   
    /**
     * Gets a String representing the specified theory
     * 
     * @param onlyDynamic if true, fetches only dynamic clauses
     * @param name desired Theory's name
     * @return a String representing the theory
     */
    private String getTheory(String name, boolean onlyDynamic){
        optimize();
        StringBuffer buffer = new StringBuffer();	
        
        alice.util.LinkedList l  = elements(name);
        //alice.util.LinkedList l  = elements();  	Enanetti
        while(!l.isEmptyList()) {
            ClauseInfo d = (ClauseInfo)l.head; l = l.tail;
            if (d.stillValid && (name.equals(d.libName)) && !(onlyDynamic && !d.dynamic)) {
                try {
                    buffer.append(d.toString(engine.getOperatorManager()));
                    buffer.append("\n");
                } catch(Exception e) {
                    return null;
                }
            }
        }
        return buffer.toString();
    }
    //fine getTheory(String,bool)
    
    
    /**
     * Gets an array of Theories with the same name
     *
     *@param name the name of the wanted theory
     *@return an array of Theories, all with the same name
     *@throws an exception if there is no theory with the specified name
     */
     //ENANETTI
    public Theory[] getTheories(String name) throws InvalidTheoryException{
    	Theory[] thlist;
    	
    	Vector v = new Vector();
    	
    	//ciclo che compila il vettore
    	for(int k=0; k<currentTheories.size();k++){
    		Theory nowTh = (Theory)currentTheories.get(k);
    		
    		if (nowTh.getName().equals(name)){
    			v.add(nowTh);
    			
    		}//fine if
    	    		
    	}//fine for
    	if (v.size()==0) throw new InvalidTheoryException();
    	
    	
    	thlist = new Theory[v.size()];
    	for (int k=0; k<v.size(); k++){
    		thlist[k] = (Theory)v.get(k);
    	}
    	return thlist;
    	
    }//fine getTheories    
    
    
    /**
     * Return an array of int representing all the positions where the specified theory is set
     *
     *@param ThName the name of the wanted theory
     *@throws an exception if the there is no theory with the specified name
     */
    //ENANETTI
    public int[] getTheoryPos(String ThName) throws InvalidTheoryException{
    	int[] positions;
    	Vector v = new Vector();
    	
    	//LCG
    	//ciclo che compila il vettore
    	for(int k=0; k<currentTheories.size();k++){
    		
    		Theory theory = (Theory)currentTheories.get(k);
    		
    		if (theory.getName().equals(ThName))
    			v.add(new Integer(k));
    	    
    	}//fine for
    	
    	//Se non ci sono elementi solleva un'eccezione
    	if (v.size() == 0) throw new InvalidTheoryException();
    	
    	positions = new int[v.size()];
    	for (int k=0; k<v.size(); k++){
    		positions[k] = ((Integer)v.get(k)).intValue();
    	}
    	
    	return positions;
    	
    }//fine getTheoryPos

       
    
    /**
     * Sets the specified theory as the only one active
     *
     *@param Th the theory to be set
     */
     //ENANETTI
    public void setTheory(Theory th) throws InvalidTheoryException{
		
		//azzera il vettore delle teorie e il DB dei nomi univoci e aggiunge quella indicata
		currentTheories = new ArrayList();					//ENANETTI
		clauseNames = new HashMap();
		
		
		currentTheories.add(th);							//ENANETTI
		//global counter incremented, to keep unique theory
		ThCount++;
		
		String innerName = th.getName()+ThCount;
		
		//inserimento corrispondenza Teoria-nomeClausole nella HM
		clauseNames.put(th, innerName);
		
	
		//consulta la teoria, settandola come unica caricata
		consult(th, false, true, innerName);
		
    }//fine setTheory
     
     
     
 	/**
 	 *Returns an array of String representing names of active theories
 	 *
 	 *@return currently loaded Theories
 	 */
 	//ENANETTI
 	public String[] getTheoryNames(){
 		
 		String[] names = new String[currentTheories.size()];
 		
 		//compila le stringhe con gli elementi disponibili
 		for(int k=0; k<currentTheories.size(); k++){
 		
 			names[k] = ((Theory)currentTheories.get(k)).getName();
 		}//fine for
 			
 		return names;
 	}//fine getTheoryNames
 
 
 	
 	/**
 	 * Metodo di utilità per verificare che il vettore delle teorie contenga la teoria
 	 * Restituisce la posizione della teoria se è già presente, -1 viceversa.
 	 *
 	 *@param th la teoria da verificare
 	 *@return la posizione in cui è la teoria oppure -1, se assente
 	 */
 	//ENANETTI 
 	private int alreadyLoaded(Theory th){
 		 		
 		for(int k=0;k<currentTheories.size();k++){
 			if (th.equals((Theory)currentTheories.get(k)))
 				return k; 			
 		}//fine for
 		
 		return -1;		
 		
 	}
 	//fine alreadyLoaded
 
 
  	/**
  	 * Metodo di utilità per verificare che il vettore delle teorie contenga la teoria
 	 * Restituisce la posizione della teoria se è già presente, -1 viceversa.
 	 */
 	 //ENANETTI
 	private int alreadyLoaded(String ThName){
 		 		
 		for(int k=0;k<currentTheories.size();k++){
 			if (ThName.equals(((Theory)currentTheories.get(k)).getName()))
 				return k; 			
 		}//fine for
 		
 		return -1;		
 		
 	}
 	//fine alreadyLoaded
 	
 
 
 	/**
 	 *ritorna un oggetto teoria, l'ultimo inserito nel vettore, con il nome corrispondente
 	 */
 	//ENANETTI 	
 	private Theory getLastInsTheory(String ThName){
 		int i = (currentTheories.size());
 		 		
 		for(int k=(i-1); k!=-1; k--){
			
			//se corrisponde rimuove la teoria dal vettore e la restituisce all'esterno 		
 			if(((Theory)currentTheories.get(k)).getName().equals(ThName)){
 				
 				return (Theory)currentTheories.remove(k);
				//restituisce l'ultimo oggetto theory inserito, il cui nome sia quello specificato.
 			}
 		}//fine for
 		return null;
 	}
 	//fine getLastInsTheory
 	
 
    /**
     * Gets last consulted theory
     * 
     * @return last theory
     */
    public Theory getLastConsultedTheory(){
        return consultedTheory;
    }


}
//end TheoryManager




