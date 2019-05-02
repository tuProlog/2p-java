package alice.tuprologx.middleware.agents.interfaces;

import alice.tuprolog.SolveInfo;

//Alberto
public interface IAgentBehaviour {

	/**
     * @author Alberto Sita
     * 
     */
	
	void setMessageType(String messageType);
	void setAgent(IAgentCapabilities agent);
	void setContent(String content);
	void setTimeout(long timeout);
	
	SolveInfo getSolveInfo();
	
}
