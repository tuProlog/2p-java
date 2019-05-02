package alice.tuprologx.middleware.LPaaS;

import java.io.Serializable;

//Alberto
public final class LPaaSRequest implements Serializable {
	
	/**
     * @author Alberto Sita
     * 
     */

	private static final long serialVersionUID = 1L;
	
	public static final String SOLVE = "solve";
	public static final String SOLVE_N = "solveN";
	public static final String SOLVE_N_TIMED = "solveNTimed";
	public static final String SOLVE_ALL = "solveAll";
	public static final String SOLVE_ALL_TIMED = "solveAllTimed";
	public static final String SOLVE_TIMED = "solveTimed";
	public static final String ADD_GOAL = "addGoal";
	public static final String REMOVE_GOAL = "removeGoal";
	public static final String ADD_THEORY = "addTheory";
	public static final String IS_GOAL = "isGoal";
	public static final String GET_GOAL_LIST = "getGoalList";
	public static final String SOLVE_NEXT = "solveNext";
	public static final String SOLVE_NEXT_TIMED = "solveNextTimed";
	
	private String componentName = null;
	private String componentAction = null;
	private String content = null;
	private long timeout = 0;
	private int numSol = 0;
	
	public String getComponentName() {
		return componentName;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public String getComponentAction() {
		return componentAction;
	}
	
	public void setComponentAction(String componentAction) {
		this.componentAction = componentAction;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public int getNumSol() {
		return numSol;
	}

	public void setNumSol(int numSol) {
		this.numSol = numSol;
	}

}
