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
import java.io.Serializable;

/**
 * This class represents a substitution of a solve result information,
 * providing a list of variables and related bound values
 *
 * @see SolveInfo
 * @see Var
 *
 *
 *
 */
public class Substitution implements Serializable {

    Var[] vars;

    public Substitution(Var[] v) {
        vars=v;
    }

    /**
     * gets the value of a variable
     * present in the substitution
     */
    public Term get(String varName){
        for (int i=0; i<vars.length; i++){
            if (vars[i]!=null && vars[i].getName().equals(varName)){
                return vars[i].getTerm();
            }
        }
        return null;
    }

    public Var[]  toVarArray(){
        return vars;
    }

    public String toString(){
        String st="";
        for (int i=0; i<vars.length; i++){
            if (vars[i]!=null && !vars[i].isAnonymous() &&
                    vars[i].isBound() &&
                    (!vars[i].getTerm().isVar() ||
                      (!((Var)(vars[i].getTerm())).getName().startsWith("_")))){
                st=st+vars[i]+"  ";
            }
        }
        return st;
    }
}