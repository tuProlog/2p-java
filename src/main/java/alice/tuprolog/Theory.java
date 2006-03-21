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

/**
 * This class represents prolog theory which can be provided
 * to a prolog engine.
 *
 * Actually theory incapsulates only textual representation
 * of prolog theories, without doing any check about validity
 *
 * @see Prolog
 *
 */
public class Theory implements Serializable {

    private String theory;
    private Struct clauseList;
	//nome della teoria inizializzato a "default"  ENANETTI
 	private String name = "default";
	
    /**
     * Creates a theory getting its source text from an input stream
     *
     * @param is the input stream acting as source
     */
    public Theory(InputStream is) throws IOException, InvalidTheoryException {
        byte[] info=new byte[is.available()];
        is.read(info);
        theory=new String(info);
    }

    /**
     * Creates a theory from its source text
     *
     * @param theory the source text
     * @throws s InvalidTheoryException if theory is null
     */
    public Theory(String theory) throws InvalidTheoryException {
        if (theory==null){
            throw new InvalidTheoryException();
        }
        this.theory=theory;
    }

    /**
     * Creates a theory from a clause list
     *
     * @param clauseList the source text
     * @throws s InvalidTheoryException if clauseList is null or is not a prolog list
     */
    public Theory(Struct clauseList) throws InvalidTheoryException{
        if (clauseList==null || !clauseList.isList()){
            throw new InvalidTheoryException();
        }
        this.clauseList=clauseList;
    }


    /**
     * Creates a theory getting its source text from an input stream.
     * Sets the name according to the specified parameter
     *
     * @param is the input stream acting as source
     * @param name the name of the Theory
     */
    public Theory(InputStream is, String name) throws IOException, InvalidTheoryException {
        byte[] info=new byte[is.available()];
        is.read(info);
        theory=new String(info);
        this.name=name;
    }


    /**
     * Creates a theory from its source text, with the desired name
     *
     * @param theory the source text
     * @param name the name of the theory
     * @throws s InvalidTheoryException if theory is null
     */
    public Theory(String theory, String name) throws InvalidTheoryException {
        if (theory==null){
            throw new InvalidTheoryException();
        }
        this.theory=theory;
        this.name=name;
    }

    /**
     * Creates a theory from a clause list, with the desired name
     *
     * @param clauseList the source text
     * @param name the name of the Theory
     * @throws s InvalidTheoryException if clauseList is null or is not a prolog list
     */
    public Theory(Struct clauseList, String name) throws InvalidTheoryException{
        if (clauseList==null || !clauseList.isList()){
            throw new InvalidTheoryException();
        }
        this.clauseList=clauseList;
        this.name=name;
    }





    /**
     * Adds (appends) a theory to this.
     *
     * @param th is the theory to be appended
     * @throws s InvalidTheoryException if the theory object are not compatibles (they are
     *  compatibles when both have been built from texts or both from clause lists)
     */
    public void append(Theory th) throws InvalidTheoryException {
        if (th.isTextual() && isTextual() && name.equals(th.getName())){
            theory=theory+th.theory;
        } else if (!th.isTextual() && !isTextual() && name.equals(th.getName())){
            clauseList.append(th.getClauseListRepresentation());
        } else {
            throw new InvalidTheoryException();
        }

    }

    /**
     * Checks if the theory has been built
     * from a text or a clause list
     *
     */
    boolean isTextual(){
        return theory!=null;
    }

    Struct getClauseListRepresentation(){
        return clauseList;
    }


 	/**
 	 *metodo per recuperare il nome della teoria.
 	 *Restituisce il nome della teoria se e' definito.
 	 *
 	 *@return il nome della teoria come stringa
 	 */
 	public String getName(){
 	
 		if(name != "") return name;
 		else return "";
 	}//fine getName
    
    

    public String toString(){
        if (theory!=null){
            return theory;
        } else {
            return clauseList.toString();
        }
    }



}