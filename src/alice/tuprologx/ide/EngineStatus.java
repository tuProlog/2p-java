/*
 * Created on 15-set-2004
 *
 */
package alice.tuprologx.ide;

import alice.tuprolog.SolveInfo;

/**
 * @author <a href="mailto:simone.pellegrini@studio.unibo.it">Simone Pellegrini</a>
 */

public class EngineStatus{
	private SolveInfo info = null;
	private boolean hasAlternatives = false;
	private String error = null;
	
	private boolean _isFirstSolution = false;
	private boolean _isAccepted = false;
	/**
	 * @return Returns the error.
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error The error to set.
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
	 * @return Returns the info.
	 */
	public SolveInfo getInfo() {
		return info;
	}
	/**
	 * @param info The info to set.
	 */
	public void setInfo(SolveInfo info) {
		this.info = info;
	}
}
