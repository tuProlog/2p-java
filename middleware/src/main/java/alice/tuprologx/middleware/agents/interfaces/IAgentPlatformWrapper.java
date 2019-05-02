package alice.tuprologx.middleware.agents.interfaces;

import java.util.Properties;

//Alberto
public interface IAgentPlatformWrapper {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	<T> T getAgentController() throws Exception;
	<T> T createContainer(String name);
	<T> T getAgentContainer();
	
	void migrateTo(String destination);
	boolean configure(Properties props, boolean isMainContainer);

}
