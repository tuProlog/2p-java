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
import java.io.*;

/**
 *
 * SolveInfo class represents the result of a solve
 * request made to the engine, providing information
 * about the solution
 *
 *
 *
 */
public class SolveInfo implements Serializable  {

	private boolean isSuccess;

    /** goal variable state after demonstration */
    private Var[] bindings;

    /** goal subject of demonstration */
    private Term goal;

	
    SolveInfo(Var[] vars,Term g){
		isSuccess=true;
        bindings=vars;
        goal=g;
    }

	SolveInfo(){
		isSuccess=false;
	}
    /**
     * Checks if the solve request was successful
     *
     * @return true if the solve was successful
     */
    public boolean isSuccess(){
        return isSuccess;
    }

    
    /**
     * gets the value of a variable
     * present in the substitution
     */
    public Term getTerm(String varName) throws NoSolutionException, UnknownVarException  {
        if (isSuccess){        
            for (int i=0; i<bindings.length; i++){
                if (bindings[i]!=null && bindings[i].getName().equals(varName)){
                    return bindings[i].getTerm();
                }
            }
            throw new UnknownVarException();
        }else {
            throw new NoSolutionException();
        }
    }
    
    /**
     *  Gets the solution of the request
     *
     *  @exception NoSolutionException if the solve request has not
     *             solution
     */
    public Term  getSolution() throws NoSolutionException {
        if (isSuccess){
            return goal;
        } else {
            throw new NoSolutionException();
        }
    }


    public Var[]  toVarArray() throws NoSolutionException {
        if (isSuccess){
            return bindings;
        }else {
            throw new NoSolutionException();
        }
    }
    
    public String toString(){
        if (isSuccess){
            String st="";
            for (int i=0; i<bindings.length; i++){
                if (bindings[i]!=null && !bindings[i].isAnonymous() &&
                        bindings[i].isBound() &&
                        (!bindings[i].getTerm().isVar() ||
                          (!((Var)(bindings[i].getTerm())).getName().startsWith("_")))){
                    st=st+bindings[i]+"  ";
                }
            }
            return st;
        }else {
            return "false";
        }
    }
    
}