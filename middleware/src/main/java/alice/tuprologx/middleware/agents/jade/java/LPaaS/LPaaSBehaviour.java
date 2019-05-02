package alice.tuprologx.middleware.agents.jade.java.LPaaS;

import java.io.IOException;

import alice.tuprologx.middleware.LPaaS.LPaaSComponent;
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
	
	private transient LPaaSAgent lPaaSAgent = null;
	
	public void setLPaaSAgent(LPaaSAgent lPaaSAgent) {
		this.lPaaSAgent = lPaaSAgent;
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
			LPaaSComponent comp = (LPaaSComponent) this.lPaaSAgent.getComponent(component);
			String action = lpaas_request.getComponentAction();
			String content = lpaas_request.getContent();
		    ACLMessage reply = msg.createReply();
		    LPaaSResponse lpaas_response = new LPaaSResponse();
		    try{
				String message = null;
				if(action.equalsIgnoreCase(LPaaSRequest.SOLVE))
					lpaas_response.setResponse(comp.solve(content));
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_NEXT))
					lpaas_response.setResponse(comp.solveNext());
				else if(action.equalsIgnoreCase(LPaaSRequest.SOLVE_NEXT_TIMED))
					lpaas_response.setResponse(comp.solveNext(lpaas_request.getTimeout()));
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_N))
					lpaas_response.setResponse(comp.solveN(content, lpaas_request.getNumSol()));
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_N_TIMED))
					lpaas_response.setResponse(comp.solveN(content, lpaas_request.getNumSol(), lpaas_request.getTimeout()));
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_ALL))
					lpaas_response.setResponse(comp.solveAll(content));
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_ALL_TIMED))
					lpaas_response.setResponse(comp.solveAll(content, lpaas_request.getTimeout()));
				else if (action.equalsIgnoreCase(LPaaSRequest.SOLVE_TIMED))
					lpaas_response.setResponse(comp.solve(content, lpaas_request.getTimeout()));
				else if(action.equalsIgnoreCase(LPaaSRequest.IS_GOAL))	
					lpaas_response.setResponse(comp.isGoal(content));
				else if(action.equalsIgnoreCase(LPaaSRequest.GET_GOAL_LIST))	
					lpaas_response.setResponse(comp.getGoalList());
				else if(action.equalsIgnoreCase(LPaaSRequest.ADD_GOAL))
					lpaas_response.setResponse(comp.addGoal(content));
				else if(action.equalsIgnoreCase(LPaaSRequest.REMOVE_GOAL))
					lpaas_response.setResponse(comp.removeGoal(content));
				else if(action.equalsIgnoreCase(LPaaSRequest.ADD_THEORY))	
					lpaas_response.setResponse(comp.addTheory(content));
				else
					message = "Invalid action selected!";
				lpaas_response.setMessage(message);
		    	reply.setPerformative(ACLMessage.INFORM);
		        try {
					reply.setContentObject(lpaas_response);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			} catch (Exception e) {
				reply.setPerformative(ACLMessage.INFORM);
				lpaas_response.setMessage("An exception has occured. Please check your request and retry!");
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
