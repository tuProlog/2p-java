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
import java.util.*;
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

    /** core flag after demonstration */
    private int flag;
    private int haltCode=0;

    /** goal variable state after demonstration */
    private Var[] bindings;

    /** goal subject of demonstration */
    private Term goal;

    SolveInfo(int fl,Var[] vars,Term g,int haltCode_){
        flag=fl;
        haltCode=haltCode_;
        bindings=vars;
        goal=g;
    }

    /**
     * Checks if the solve request was successful
     *
     * @return true if the solve was successful
     */
    public boolean isSuccess(){
        return flag==Prolog.TRUE || flag==Prolog.TRUE_CP;
    }

    /**
     * Gets information about variables and bound values
     * related to the solution
     *
     *  @exception NoSolutionException if the solve request has not
     *             solution
     */
    public Substitution getSubstitution() throws NoSolutionException {
        if (flag==Prolog.TRUE_CP || flag==Prolog.TRUE)
            return new Substitution(bindings);
        else
            throw new NoSolutionException();
    }

    /**
     *  Gets the solution of the request
     *
     *  @exception NoSolutionException if the solve request has not
     *             solution
     */
    public Term  getSolution() throws NoSolutionException {
        if (flag==Prolog.TRUE_CP || flag==Prolog.TRUE){
            return goal;
        } else {
            throw new NoSolutionException();
        }
    }

    /**
     * Checks if the solve request was stopped by means of an halt predicate
     */
    public boolean isHalt(){
        return flag==Prolog.HALT;
    }

    /**
     * Gets the halt code of the solve request stopped with an halt predicate
     *
     * @exception NoHaltException if the request was not stopped by means of
     *            an halt predicate
     */
    public int getHaltCode() throws NoHaltException {
        if (flag!=Prolog.HALT){
            throw new NoHaltException();
        }
        return haltCode;
    }
}
