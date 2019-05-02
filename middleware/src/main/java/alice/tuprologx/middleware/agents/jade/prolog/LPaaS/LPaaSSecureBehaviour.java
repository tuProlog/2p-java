package alice.tuprologx.middleware.agents.jade.prolog.LPaaS;

import java.io.IOException;
import java.util.ArrayList;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.exceptions.MalformedGoalException;
import alice.tuprologx.middleware.LPaaS.LPaaSRequest;
import alice.tuprologx.middleware.LPaaS.LPaaSResponse;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.security.SecurityHelper;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.security.JADEPrincipal;
import jade.util.leap.Iterator;

//Alberto
public final class LPaaSSecureBehaviour extends CyclicBehaviour {

	/**
     * @author Alberto Sita
     * 
     */

	public static final String GET_PRINCIPAL = "get-principal";
	
	private static final long serialVersionUID = 1L;
	
	private transient Prolog prolog = null;
	private String componentConfiguratorId = null;
	private transient SecurityHelper securityHelper = null;
	private ArrayList<String> configurators = null;
	
	public void setEngine(Prolog prolog){
		this.prolog = prolog;
	}
	
	public void setComponentConfiguratorId(String componentConfiguratorId){
		this.componentConfiguratorId = componentConfiguratorId;
	}
	
	public void setSecurityHelper(SecurityHelper securityHelper){
		this.securityHelper = securityHelper;
	}
	
	public void setConfigurators(ArrayList<String> configurators){
		this.configurators = configurators;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			if (securityHelper.getUseSignature(msg)) {
				if (GET_PRINCIPAL.equals(msg.getContent())) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					securityHelper.setUseSignature(reply);
					myAgent.send(reply);
				} else {
					if(securityHelper.getUseEncryption(msg)){
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
							else if(action.equalsIgnoreCase(LPaaSRequest.IS_GOAL))	
								info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", isGoal('"+content+"'), Result).");
							else if(action.equalsIgnoreCase(LPaaSRequest.GET_GOAL_LIST))	
								info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", getGoalList, Result).");
							else if (checkPrincipal(msg)){
								if(action.equalsIgnoreCase(LPaaSRequest.ADD_GOAL))
									info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", addGoal('"+content+"'), Result).");
								else if(action.equalsIgnoreCase(LPaaSRequest.REMOVE_GOAL))
									info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", removeGoal('"+content+"'), Result).");
								else if(action.equalsIgnoreCase(LPaaSRequest.ADD_THEORY))	
									info = prolog.solve("invoke("+componentConfiguratorId+", "+component+", addTheory('"+content+"'), Result).");
								else
									message = "Invalid action selected!";
							} else {
								message = "Invalid action selected! Malformed action or permission denied for: "+action;
							}
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
							securityHelper.setUseSignature(reply);
							Iterator it = reply.getAllReceiver();
							while (it.hasNext()) {
								AID receiver = (AID) it.next();
								//if (securityHelper.getTrustedPrincipal(receiver.getName()) == null) {
									try {
										JADEPrincipal principal = retrievePrincipal(receiver);
										securityHelper.addTrustedPrincipal(principal);
									}
									catch (Exception e) {
										e.printStackTrace();
										return;
									}
								//}
							}
							securityHelper.setUseEncryption(reply);
							myAgent.send(reply);
						}
					}
				}
			}
		}
		else {
			block();
		}
	}

	private boolean checkPrincipal(ACLMessage msg) {
		String owner = myAgent.getBootProperties().getProperty("owner").split(":")[0];
		if(owner == null){
			return false;
		}
		for(String c : configurators){
			if(c.equals(owner))
				return true;
		}
		return false;
	}
	
	private JADEPrincipal retrievePrincipal(AID id) throws Exception {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(id);
		request.setContent(GET_PRINCIPAL);
		securityHelper.setUseSignature(request);
		ACLMessage reply = FIPAService.doFipaRequestClient(myAgent, request, 5000);
		if (reply != null) {
			return securityHelper.getPrincipal(reply);
		}
		else {
			throw new Exception("No reply received to get-principal request for agent "+id.getName());
		}
	}

}
