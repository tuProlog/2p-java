package alice.tuprologx.middleware.agents.jade;

import java.util.Properties;
import java.util.Set;

import alice.tuprologx.middleware.agents.interfaces.IAgentPlatformWrapper;

import jade.core.ContainerID;
import jade.core.Location;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

//Alberto
public final class JadePlatformWrapper implements IAgentPlatformWrapper {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	private Runtime rt = null;
	private AgentContainer agentContainer = null;
	private AgentController agentController = null;
	private String agentId = null;
    private String agentClass = null;
    private Properties props = null;
    
    @Override
	public boolean configure(Properties props, boolean isMainContainer){
    	this.props = props;
		rt = Runtime.instance();
        rt.setCloseVM(true);
        agentId = props.getProperty("agentId");
        agentClass = props.getProperty("agentClass");
        Profile profile = new ProfileImpl();
        Set<Object> keys = props.keySet();
        for(Object key : keys){
        	profile.setParameter((String)key, props.getProperty((String)key));
        }
        if(isMainContainer){
        	agentContainer = rt.createMainContainer(profile);
        }
        else{
        	agentContainer = rt.createAgentContainer(profile);
        }
        try {
			agentController = agentContainer.createNewAgent(agentId, agentClass, null);
			agentController.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
			return false;
		}
        return true;
	}

    @Override
	@SuppressWarnings("unchecked")
	public AgentController getAgentController() throws StaleProxyException{
		return agentController;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public AgentContainer getAgentContainer(){
		return agentContainer;
	}

	@Override
	public void migrateTo(String destination) {
		Location location = new ContainerID(destination, null);
		try {
			getAgentController().move(location);
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public AgentContainer createContainer(String name) {
		Profile profile = new ProfileImpl();
        Set<Object> keys = props.keySet();
        for(Object key : keys){
        	profile.setParameter((String)key, props.getProperty((String)key));
        }
        profile.setParameter("container-name", name);
		return rt.createAgentContainer(profile);
	}

}
