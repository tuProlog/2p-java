/*
 * tuProlog - Copyright (C) 2001-2004  aliCE team at deis.unibo.it
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
package alice.tuprologx.ide;

import alice.tuprolog.SolveInfo;

/**
 * @author <a href="mailto:simone.pellegrini@studio.unibo.it">Simone Pellegrini</a>
 */

public class EngineStatus{
    /**
	 * @uml.property  name="info"
	 * @uml.associationEnd  
	 */
    private SolveInfo info = null;
    /**
	 * @uml.property  name="hasAlternatives"
	 */
    private boolean hasAlternatives = false;
    /**
	 * @uml.property  name="error"
	 */
    private String error = null;
    
    /**
	 * @uml.property  name="_isFirstSolution"
	 */
    private boolean _isFirstSolution = false;
    /**
	 * @uml.property  name="_isAccepted"
	 */
    private boolean _isAccepted = false;
    /**
	 * @return  Returns the error.
	 * @uml.property  name="error"
	 */
    public String getError() {
        return error;
    }
    /**
	 * @param error  The error to set.
	 * @uml.property  name="error"
	 */
    public void setError(String error) {
        this.error = error;
    }
    
    public boolean isFirstSolution(){
        return _isFirstSolution;
    }
    
    public void setFirstSolution(boolean value){
        this._isFirstSolution = value;
    }
    
    public boolean isAccepted(){
        return _isAccepted;
    }
    
    public void setAccepted(boolean value){
        this._isAccepted = value;
    }
    /**
     * @return Returns the hasAlternatives.
     */
    public boolean hasAlternatives() {
        return hasAlternatives;
    }
    /**
     * @param hasAlternatives The hasAlternatives to set.
     */
    public void hasAlternatives(boolean hasAlternatives) {
        this.hasAlternatives = hasAlternatives;
    }
    /**
	 * @return  Returns the info.
	 * @uml.property  name="info"
	 */
    public SolveInfo getInfo() {
        return info;
    }
    /**
	 * @param info  The info to set.
	 * @uml.property  name="info"
	 */
    public void setInfo(SolveInfo info) {
        this.info = info;
    }
}
