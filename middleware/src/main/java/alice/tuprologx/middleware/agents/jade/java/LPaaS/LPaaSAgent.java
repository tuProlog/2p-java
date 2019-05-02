package alice.tuprologx.middleware.agents.jade.java.LPaaS;

import java.util.ArrayList;
import java.util.Properties;

import alice.tuprologx.middleware.agents.jade.java.ContainerAgent;
import jade.core.ServiceException;
import jade.core.security.SecurityHelper;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

//Alberto
public final class LPaaSAgent extends ContainerAgent {

	/**
     * @author Alberto Sita
     * 
     */
	
	private static final long serialVersionUID = 1L;
	
	private String logicalName = null;
	private String type = null;
	private LPaaSBehaviour behaviour = null;
	private LPaaSSecureBehaviour secureBehaviour = null;
	private transient SecurityHelper mySecurityHelper = null;
	private boolean isSecure = false;
	
	@Override
	protected void setup() {
		super.setup();
		registerInDF();
		Properties props = getBootProperties();
	    String security = props.getProperty("LPaaSSecureBehaviour");
	    if(security == null || !security.equals("yes")) {
	    	behaviour = new LPaaSBehaviour();
			behaviour.setLPaaSAgent(this);
			addBehaviour(behaviour);
	    } else {
	    	isSecure = true;
	    	try {
	    		mySecurityHelper = (SecurityHelper) getHelper("jade.core.security.Security");
	    	} catch (ServiceException e) {
	    		e.printStackTrace();
	    		doDelete();
	    	}
	    	ArrayList<String> configurators = new ArrayList<String>();
	    	for(int i=1;;i++){
	    		String configuratorId = props.getProperty("configurator."+i);
	    		if(configuratorId != null) {
	    			configurators.add(configuratorId);
	    		} else {
	    			break;
	    		}
	    	}
	    	secureBehaviour = new LPaaSSecureBehaviour();
	    	secureBehaviour.setLPaaSAgent(this);
	    	secureBehaviour.setConfigurators(configurators);
	    	secureBehaviour.setSecurityHelper(getMySecurityHelper());
			addBehaviour(secureBehaviour);
	    }
	    System.out.println("LPaaS Agent: started.");
	}
	
    @Override
    protected void afterMove() {
    	super.afterMove();
    	try {
    		mySecurityHelper = (SecurityHelper) getHelper("jade.core.security.Security");
    	} catch (ServiceException e) {
    		e.printStackTrace();
    		doDelete();
    	}
 	    if(!isSecure) {
 	    	behaviour.setLPaaSAgent(this);
 	    } else {
 	    	secureBehaviour.setLPaaSAgent(this);
 	    	secureBehaviour.setSecurityHelper(getMySecurityHelper());
 	    }
    }
	
    private void registerInDF() {
    	DFAgentDescription dfd = new DFAgentDescription();
	    dfd.setName(getAID());
	    ServiceDescription sd = new ServiceDescription();
	    Properties props = getBootProperties();
	    logicalName = props.getProperty("agent-logical-name");
	    type = props.getProperty("agent-service-type");
	    if(logicalName == null || type == null) {
	    	System.out.println("Cannot start agent, please check the configuration file!");
	    	doDelete();
	    }
	    sd.setType(getType());
	    sd.setName(getLogicalName());
	    dfd.addServices(sd);
	    try {
	    	DFService.register(this, dfd);
	    }
	    catch (FIPAException fe) {
	      fe.printStackTrace();
	      doDelete();
	    }
	    System.out.println("LPaaS Agent: registered on Jade DFService as Name: "+getLogicalName()+", Type: "+getType()+".");
	}

	@Override 
	protected void takeDown() {
		deregisterFromDF();
		super.takeDown();
		System.out.println("LPaaS Agent: stopped.");
	}

	private void deregisterFromDF() {
    	try {
			DFService.deregister(this);
		    System.out.println("LPaaS Agent: Name: "+getLogicalName()+", Type: "+getType()+" unregistered from Jade DFService.");
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	String getType() {
		return type;
	}

	String getLogicalName() {
		return logicalName;
	}

	SecurityHelper getMySecurityHelper() {
		return mySecurityHelper;
	}

}
