package alice.tuprologx.middleware.agents.interfaces;

import alice.tuprolog.SolveInfo;

//Alberto
public interface IAgentCapabilities {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	SolveInfo getSolveInfo();
	
	void addBehaviour(Object behaviour);

}
