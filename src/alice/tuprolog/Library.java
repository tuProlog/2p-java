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

import java.lang.reflect.*;
import java.io.Serializable;
import java.util.*;

/**
 *
 * This abstract class is the base class for developing
 * tuProlog built-in libraries, which can be dynamically
 * loaded by prolog objects.
 * <p>
 * Each library can expose to engine:
 * <ul>
 * <li> a theory (as a string assigned to theory field)
 * <li> builtin predicates: each method whose signature is
 *       boolean name_arity(Struct goal)
 *   is considered a built-in predicate provided by the library
 * <li> builtin evaluable functors: each method whose signature is
 *       Term name_arity(Struct goal)
 *   is considered a built-in functors provided by the library
 * </ul>
 * <p>
 * typically theory is assigned in library constructor
 *
 *
 *
 */
public abstract class Library  implements Serializable {

    private static final Class[] ARG_ARRAY = new Class[]{ Struct.class };

    /** prolog core which loaded the library */
    private Prolog engine;

    /** operator mapping*/
    private String[][] opMappingCached;

    public Library(){
        opMappingCached = getOperatorSynonymMap();
    }

    /**
     * Gets the theory provided with the library
     *
     * Empty theory is provided by default.
     */
    public String getTheory(){
        return "";
    }
    
    /**
     * Gets the operator synonym mapping, as array of
     * elements like  { synonym, original operator name}
     */
    public String[][] getOperatorSynonymMap(){
        return null;
    }

    protected Prolog getEngine(){
        return engine;
    }

    void setEngine(Prolog en){
        engine=en;
    }

    /**
     * tries to unify two terms
     *
     * The runtime (demo) context currently used by the engine
     * is deployed and altered.
     */
    protected boolean unify(Term a0,Term a1){
        return engine.unify(a0,a1);
    }

    /**
     * Evaluates an expression. Returns null value if the argument
     * is not an evaluable expression
     *
     * The runtime (demo) context currently used by the engine
     * is deployed and altered.
     */
    protected Term evalExpression(Term term){
        if (term==null){
            return null;
        }

        term=term.getTerm();
        if (term.isNumber()){
            return term;
        }

        Struct t=(Struct)term;
        if (!t.isBuiltIn()){
            return null;
        }

        BuiltIn bt=t.getBuiltIn();
        // check for library functors
        if (bt.isLibraryFunctor()){
            return (bt.evalAsFunctor());
        } else {
            return null;
        }
    }

    /**
     * tries to unify two terms
     *
     * The runtime (demo) context currently used by the engine
     * is deployed and altered.
     */
    protected Term getRenamedCopy(Term term){
        return engine.getRenamedCopy(term);
    }

    /**
     * gets the list of predicates defined in the library
     */
    public List getPredicates(){
        try {
            java.lang.reflect.Method[] mlist=this.getClass().getMethods();
            ArrayList list=new ArrayList();
            for (int i=0; i<mlist.length; i++){
                String name=mlist[i].getName();
                Class[] clist=mlist[i].getParameterTypes();
                Class   rclass=mlist[i].getReturnType();
                if (clist.length==1 && rclass.getName().equals("boolean")
                        && clist[0].getName().endsWith(".Struct")){
                    int index=name.lastIndexOf('_');
                    if (index!=-1){
                        try {
                            int arity=Integer.parseInt(name.substring(index+1,name.length()));
                            list.add(name);
                        } catch (Exception ex){
                        }
                    }
                }
            }
            return list;
        } catch (Exception ex){
            return null;
        }
    }

    /**
     * gets the list of functors defined in the library
     */
    public List getFunctors(){
        try {
            java.lang.reflect.Method[] mlist=this.getClass().getMethods();
            ArrayList list=new ArrayList();
            for (int i=0; i<mlist.length; i++){
                String name=mlist[i].getName();
                Class[] clist=mlist[i].getParameterTypes();
                Class   rclass=mlist[i].getReturnType();
                if (clist.length==1 && rclass.getName().endsWith(".Term")
                        && clist[0].getName().endsWith(".Struct")){
                    int index=name.lastIndexOf('_');
                    if (index!=-1){
                        try {
                            String arityPart=name.substring(index+1,name.length());
                            int arity=Integer.parseInt(arityPart);
                            list.add(name);
                            //
                            // adding also or synonims
                            //
                            if (opMappingCached!=null){
                                String rawName=name.substring(0,index);
                                for (int j=0; j<opMappingCached.length; j++){
                                    String[] map=opMappingCached[j];
                                    if (map[1].equals(rawName)){
                                        list.add(map[0]+"_"+arityPart);
                                    }
                                }
                            }
                        } catch (Exception ex){
                            ex.printStackTrace();
                            return null;
                        }
                    }
                }
            }
            return list;
        } catch (Exception ex){
            return null;
        }
    }
    
    /**
     * Gets the method linked to a builtin (null value if
     * the builtin has not any linked service)
     *
     */
    public Method getLinkedMethod(Struct s){
        try {
            return this.getClass().getMethod(s.getName()+"_"+s.getArity(),ARG_ARRAY);
        } catch (NoSuchMethodException ex){
            // try with synonims
            if (opMappingCached!=null){
                String rawName=s.getName();
                for (int j=0; j<opMappingCached.length; j++){
                    String[] map=opMappingCached[j];
                    if (map[0].equals(rawName)){
                        try {
                            return this.getClass().getMethod(map[1]+"_"+s.getArity(),ARG_ARRAY);
                        } catch (NoSuchMethodException ex1){
                            return null;
                        }
                    }
                }
            }
            return null;
        }
    }


    /**
     * method invoked by prolog engine when library is
     * going to be removed
     */
    public void dismiss(){
    }

    /**
     * method invoked when the engine is going
     * to demonstrate a goal
     */
    public void onSolveBegin(Term goal){
    }

    /**
     * method invoked when the engine has
     * finished a demostration
     */
    public void onSolveEnd(){
    }
}