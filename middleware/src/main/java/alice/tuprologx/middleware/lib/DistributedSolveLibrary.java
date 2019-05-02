package alice.tuprologx.middleware.lib;

import alice.tuprolog.Library;
import alice.tuprolog.PrologError;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprologx.middleware.Middleware;
import alice.tuprologx.middleware.LPaaS.LPaaSRequest;
import alice.tuprologx.middleware.agents.interfaces.IAgentCapabilities;
import alice.tuprologx.middleware.agents.interfaces.IAgentBehaviour;
import alice.tuprologx.middleware.exceptions.MiddlewareException;

//Alberto
public class DistributedSolveLibrary extends Library {

	/**
     * @author Alberto Sita
     * 
     */
	
	private static final long serialVersionUID = 1L;
	
	private IAgentCapabilities myAgentCapabilities = null;
	
	public void setAgentSupport(IAgentCapabilities myAgent){
		this.myAgentCapabilities = myAgent;
	}
	
	public IAgentCapabilities getAgentSupport(){
		return myAgentCapabilities;
	}
	
	public String getTheory() {
		return    "distributedSolve(remote(X), remote(Y), Na, Nb, A, B, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolve'(X, Na, A),\n"
				+ "'$distributedSolve'(Y, Nb, B),\n"
				+ "Z.\n"
				+ ""
				+"distributedSolve(remoteNext(X), remote(Y), Na, Nb, A, B, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveNext'(X, Na, A),\n"
				+ "'$distributedSolve'(Y, Nb, B),\n"
				+ "Z.\n"
				+ ""
				+"distributedSolve(remote(X), remoteNext(Y), Na, Nb, A, B, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolve'(X, Na, A),\n"
				+ "'$distributedSolveNext'(Y, Nb, B),\n"
				+ "Z.\n"
				+ ""
				+"distributedSolve(remoteNext(X), remoteNext(Y), Na, Nb, A, B, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveNext'(X, Na, A),\n"
				+ "'$distributedSolveNext'(Y, Nb, B),\n"
				+ "Z.\n"
				+ ""
				+ "distributedSolve(remote(X), Na, A, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolve'(X, Na, A),\n"
				+ "Z.\n"
				+ ""
				+ "distributedSolve(remoteNext(X), Na, A, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveNext'(X, Na, A),\n"
				+ "Z.\n"
				+ "" 
				+"distributedSolveTimed(remote(X), remote(Y), Na, Nb, A, B, Time, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveTimed'(X, Na, A, Time),\n"
				+ "'$distributedSolveTimed'(Y, Nb, B, Time),\n"
				+ "Z.\n"
				+ ""
				+"distributedSolveTimed(remoteNext(X), remote(Y), Na, Nb, A, B, Time, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveNextTimed'(X, Na, A, Time),\n"
				+ "'$distributedSolveTimed'(Y, Nb, B, Time),\n"
				+ "Z.\n"
				+ ""
				+"distributedSolveTimed(remote(X), remoteNext(Y), Na, Nb, A, B, Time, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveTimed'(X, Na, A, Time),\n"
				+ "'$distributedSolveNextTimed'(Y, Nb, B, Time),\n"
				+ "Z.\n"
				+ ""
				+"distributedSolveTimed(remoteNext(X), remoteNext(Y), Na, Nb, A, B, Time, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveNextTimed'(X, Na, A, Time),\n"
				+ "'$distributedSolveNextTimed'(Y, Nb, B, Time),\n"
				+ "Z.\n"
				+ ""
				+ "distributedSolveTimed(remote(X), Na, A, Time, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveTimed'(X, Na, A, Time),\n"
				+ "Z.\n"
				+ ""
				+ "distributedSolveTimed(remoteNext(X), Na, A, Time, local(Z)) :-\n"
				+ "'$checkAgentSupport',\n"
				+ "'$distributedSolveNextTimed'(X, Na, A, Time),\n"
				+ "Z.\n"
				+ "";
	}
	
	public boolean $checkAgentSupport_0() throws PrologError{
		if(getAgentSupport() == null){
			String msg = "Agent support is null, please check your code.";
			throw new PrologError(new Struct(msg), msg);
		}
		else
			return true;
	}
	
	public boolean $distributedSolve_3(Term realq, Term varName, Term var) throws PrologError {
		return doWork(realq, varName, var, LPaaSRequest.SOLVE, 0);
	}
	
	public boolean $distributedSolveNext_3(Term realq, Term varName, Term var) throws PrologError {
		return doWork(realq, varName, var, LPaaSRequest.SOLVE_NEXT, 0);
	}
	
	public boolean $distributedSolveTimed_4(Term realq, Term varName, Term var, Term time) throws PrologError {
		String t = time.getTerm().toString();
		long tt = 0;
		try{
			tt = Long.parseLong(t);
		} catch (Exception e){
			String msg = "Time indication is not correct.";
			throw new PrologError(new Struct(msg), msg);
		}
		return doWork(realq, varName, var, LPaaSRequest.SOLVE_TIMED, tt);
	}
	
	public boolean $distributedSolveNextTimed_4(Term realq, Term varName, Term var, Term time) throws PrologError {
		String t = time.getTerm().toString();
		long tt = 0;
		try{
			tt = Long.parseLong(t);
		} catch (Exception e){
			String msg = "Time indication is not correct.";
			throw new PrologError(new Struct(msg), msg);
		}
		return doWork(realq, varName, var, LPaaSRequest.SOLVE_NEXT_TIMED, tt);
	}
	
	private boolean doWork(Term realq, Term varName, Term var, String what, long time) throws PrologError {
		IAgentBehaviour dab = null;
		try {
			dab = Middleware.getMiddleware().getNewGenericAgentBehaviour();
		} catch (MiddlewareException e) {
			e.printStackTrace();
			String msg = "Runtime error, please check your middleware configuration.";
			throw new PrologError(new Struct(msg), msg);
		}
		dab.setAgent(getAgentSupport());
		dab.setContent(realq.getTerm().toString()+".");
		dab.setMessageType(what);
		if(time > 0){
			dab.setTimeout(time);
		}
		getAgentSupport().addBehaviour(dab);
		SolveInfo info = null;
		for(int i=0; i<3; i++){
			try {
				//wait for exec, then at least retry
				Thread.sleep(1100); //can be improved
			} catch (InterruptedException e) {
				return false;
			}
			info = getAgentSupport().getSolveInfo();
			if(info != null){
				break;
			}
		}
		Term s = null;
		if(info == null){
			s = new Struct();
			return engine.unify(var, s);
		}
		try {
			s = info.getVarValue(alice.util.Tools.removeApices(var.getTerm().toString()));
		} catch (Exception e) {
			return false;
		}
		if(s == null){
			return false;
		}
		return engine.unify(var, s);
	}

}
