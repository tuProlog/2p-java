package alice.tuprologx.middleware.LPaaS.interfaces;

//Alberto
public interface LPaaS_ClientInterface {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	//Client methods
	String solve(String toSolve);
	String solve(String toSolve, long time);
	String solveNext();
	String solveNext(long time);
	String solveN(String toSolve, int num);
	String solveN(String toSolve, int num, long time);
	String solveAll(String toSolve);
	String solveAll(String toSolve, long time);
	
	String isGoal(String goal);
	String getGoalList();
	
}
