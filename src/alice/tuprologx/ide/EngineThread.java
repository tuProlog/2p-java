/*
 * Created on 28-ago-2004
 *
 */
package alice.tuprologx.ide;

import java.util.Observable;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Library;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;

/**
 * @author <a href="mailto:simone.pellegrini@studio.unibo.it">Simone Pellegrini</a>
 *
 */
public class EngineThread extends Observable implements Runnable {

	private Prolog _engine = null;
	private String _goal = null;
	private SolveInfo info =  null;
	private boolean _halted = false;
	private boolean _running = false;

	public EngineThread(Prolog engine, String goal) {
		_engine = engine;
		_goal = goal;
	}

	public synchronized void run() {
		EngineStatus status = null;
		try {
			_running = true;
			info = _engine.solve(_goal);
			status = new EngineStatus();
			status.setInfo(info);
			status.hasAlternatives(_engine.hasOpenAlternatives());
			status.setFirstSolution(true);
		} catch (MalformedGoalException e) {
			status = new EngineStatus();
			status.setError("syntax error");
			_running = false;
		} finally{
			this.setChanged();
			this.notifyObservers(status);
		}
		while(!_halted){
			try {
				_running = false;
				wait();
				_running = true;
				info = _engine.solveNext();
				status = new EngineStatus();
				status.setInfo(info);
				status.hasAlternatives(_engine.hasOpenAlternatives());
			} catch (NoMoreSolutionException e2) {
				status.setError("No more solution founded.");
				_running = false;
			} catch (InterruptedException e) {
			} finally{
				this.setChanged();
				this.notifyObservers(status);
			}
		}
	}

	public synchronized void nextSolution() {
		this.notify();
	}
	
	public synchronized void acceptSolution(){
		_engine.solveEnd();
		_halted = true;
		EngineStatus status = new EngineStatus();
		status.setInfo(info);
		status.hasAlternatives(false);
		status.setAccepted(true);
		this.setChanged();
		notifyObservers(status);
	}
	
	/* this method must be not synchronized */
	public void stopExcution(){
		_engine.solveHalt();
		_running = false;
		_halted = true;
	}
	
	public boolean isRunning(){
		return _running;
	}
	
	public void setTheory(Theory t) throws InvalidTheoryException, EngineRunningException{
		if(isRunning())
			throw new EngineRunningException();
		_engine.setTheory(t);
	}
	
	public Theory getTheory() throws EngineRunningException{
		if(isRunning())
			throw new EngineRunningException();
		return _engine.getTheory();
	}

	/**
	 * @param libraryClassname
	 * @return
	 */
	public Library getLibrary(String libraryClassname) throws EngineRunningException {
		if(isRunning())
			throw new EngineRunningException();
		return _engine.getLibrary(libraryClassname);
	}

	/**
	 * @param library
	 * @throws InvalidLibraryException
	 */
	public void loadLibrary(String library) throws InvalidLibraryException, EngineRunningException {
		if(isRunning())
			throw new EngineRunningException();
		_engine.loadLibrary(library);
	}

	/**
	 * @param library
	 * @throws InvalidLibraryException
	 */
	public void unloadLibrary(String library) throws InvalidLibraryException, EngineRunningException {
		if(isRunning())
			throw new EngineRunningException();
		_engine.unloadLibrary(library);
	}

	public void clearTheory() throws EngineRunningException{
		if(isRunning())
			throw new EngineRunningException();
		_engine.clearTheory();
	}

	public void setGoal(String goal) {
		this._goal = goal;
	}

}

