package alice.tuprologx.middleware.agents.jade.prolog.LPaaS;

import java.io.IOException;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.exceptions.MalformedGoalException;
import alice.tuprologx.middleware.LPaaS.LPaaSRequest;
import alice.tuprologx.middleware.LPaaS.LPaaSResponse;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

//Alberto
public final class LPaaSBehaviour extends CyclicBehaviour {
	
	/**
     * @author Alberto Sita
     * 
     */

	private static final long serialVersionUID = 1L;
	private transient Prolog prolog = null;
	private String componentConfiguratorId = null;

	public void setEngine(Prolog prolog){
		this.prolog = prolog;
	}
	
	public void setComponentConfiguratorId(String componentConfiguratorId){
		this.componentConfiguratorId = componentConfiguratorId;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			LPaaSRequest lpaas_request = null;
			try {
				lpaas_request = (LPaaSRequest) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
				return;
			}
			String component = lpaas_request.getComponentName();
			String action = lpaas_request.getComponentAction();
			String content = lpaas_request.getContent();
		    ACLMessage reply = msg.createReply();
		    LPaaSResponse lpaas_response = new LPaaSResponse();
		    try{
		    	String message = null;
				SolveInfo info = null;
				if(action.equalsIgnoreCase(LPaaSRequest.SOLVE))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solve('"+content+"'), Result).");
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_N))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solveN('"+content+"', "+lpaas_request.getNumSol()+"), Result).");
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_N_TIMED))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solveN('"+content+"', "+lpaas_request.getNumSol()+", "+lpaas_request.getTimeout()+"), Result).");
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_NEXT))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solveNext, Result).");
				else if(action.equalsIgnoreCase(LPaaSRequest.SOLVE_NEXT_TIMED))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solveNext("+lpaas_request.getTimeout()+"), Result).");
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_ALL))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solveAll('"+content+"'), Result).");
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_ALL_TIMED))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solveAll('"+content+"', "+lpaas_request.getTimeout()+"), Result).");
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_TIMED))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", solve('"+content+"', "+lpaas_request.getTimeout()+"), Result).");
				else if(action.equalsIgnoreCase(LPaaSRequest.ADD_GOAL))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", addGoal('"+content+"'), Result).");
				else if(action.equalsIgnoreCase(LPaaSRequest.REMOVE_GOAL))
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", removeGoal('"+content+"'), Result).");
				else if(action.equalsIgnoreCase(LPaaSRequest.ADD_THEORY))	
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", addTheory('"+content+"'), Result).");
				else if(action.equalsIgnoreCase(LPaaSRequest.IS_GOAL))	
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", isGoal('"+content+"'), Result).");
				else if(action.equalsIgnoreCase(LPaaSRequest.GET_GOAL_LIST))	
					info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", getGoalList, Result).");
				else
					message = "Invalid action selected!";
				if(info != null){
					try {
						lpaas_response.setResponse(alice.util.Tools.removeApices(info.getVarValue("Result").getTerm().toString()));
					} catch (Exception e) {
						message = "Error in accessing result!";
					}
				}
				lpaas_response.setMessage(message);
		    	reply.setPerformative(ACLMessage.INFORM);
		        try {
					reply.setContentObject(lpaas_response);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			} catch (MalformedGoalException e) {
				reply.setPerformative(ACLMessage.INFORM);
				lpaas_response.setMessage("Malformed goal exception: "+content);
				try {
					reply.setContentObject(lpaas_response);
				} catch (IOException ex) {
					e.printStackTrace();
					return;
				}
			} finally {
				myAgent.send(reply);
			}
		}
		else {
			block();
		}
	}

}

