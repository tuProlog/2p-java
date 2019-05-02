package alice.tuprologx.middleware;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import alice.tuprologx.middleware.agents.interfaces.IAgentBehaviour;
import alice.tuprologx.middleware.agents.interfaces.IAgentPlatformWrapper;
import alice.tuprologx.middleware.exceptions.MiddlewareException;
import alice.tuprologx.middleware.interfaces.IComponentConfiguratorFactory;

//Alberto
public class Middleware {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	private final String MIDDLEWARE_CONF = "middleware.properties";
	
	private static Middleware middleware = null;
	private String agentWrapperClass = null;
	private String agentBehaviourClass = null;
	
	private IComponentConfiguratorFactory componentConfiguratorFactory = null;
	
	static{
		if (middleware == null){
			try {
				middleware = new Middleware();
			} catch (MiddlewareException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public static Middleware getMiddleware(){
		return middleware;
	}
	
	public IComponentConfiguratorFactory getComponentConfiguratorFactory(){
		return componentConfiguratorFactory;
	}
	
	public IAgentPlatformWrapper getNewAgentWrapper(){
		try {
			return (IAgentPlatformWrapper) Class.forName(agentWrapperClass).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Middleware() throws MiddlewareException {
		Properties props = new Properties();
		try {
			InputStream input = new FileInputStream(MIDDLEWARE_CONF);
			props.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new MiddlewareException("File: middleware.properties not found!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new MiddlewareException("IOException: "+e.getMessage());
		}
		String componentConfiguratorFactory = props.getProperty("componentConfiguratorFactoryClass");
		if(componentConfiguratorFactory == null){
			throw new MiddlewareException("Property: componentConfiguratorFactoryClass cannot be null!");
		} else {
			try {
				this.componentConfiguratorFactory = (IComponentConfiguratorFactory)Class.forName(componentConfiguratorFactory).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		agentWrapperClass = props.getProperty("agentWrapperClass");
		if(agentWrapperClass == null){
			throw new MiddlewareException("Property: agentWrapperClass cannot be null!");
		}
		agentBehaviourClass = props.getProperty("agentBehaviourClass");
	}

	public IAgentBehaviour getNewGenericAgentBehaviour() throws MiddlewareException {
		if(agentBehaviourClass == null){
			throw new MiddlewareException("Property: distributedAgentBehaviourClass not configured!");
		}
		try {
			return (IAgentBehaviour) Class.forName(agentBehaviourClass).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
