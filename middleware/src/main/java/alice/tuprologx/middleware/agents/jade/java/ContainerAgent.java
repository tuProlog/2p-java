package alice.tuprologx.middleware.agents.jade.java;

import java.util.Properties;

import alice.tuprologx.middleware.ComponentMetaData;
import alice.tuprologx.middleware.Middleware;
import alice.tuprologx.middleware.exceptions.ComponentConfiguratorException;
import alice.tuprologx.middleware.interfaces.IComponentConfigurator;

import jade.core.Agent;

//Alberto
public abstract class ContainerAgent extends Agent {
	
	/**
     * @author Alberto Sita
     * 
     */

	private static final long serialVersionUID = 1L;
	private IComponentConfigurator componentConfigurator = null;
	
	@Override
	protected void setup() {
		Properties props = getBootProperties();
		String confFile = props.getProperty("component-configurator-file");
		if(confFile == null){
			System.out.println("Cannot start agent, please check the configuration file!");
			doDelete();
		}
		componentConfigurator = Middleware.getMiddleware().getComponentConfiguratorFactory().newComponentConfigurator(confFile);
		super.setup();
	}
	
	@Override
	protected void beforeMove() {
		componentConfigurator.preMigrationSetup();
        super.beforeMove();
    }

    @Override
    protected void afterMove() {
    	try {
			reload();
		} catch (ComponentConfiguratorException e) {
			e.printStackTrace();
			doDelete();
		}
        super.afterMove();
    }
    
    @Override
    protected void beforeClone(){
    	System.out.println("Clone behaviour not yet implemented for the ContainerAgent!");
    	super.beforeClone();
    }
    
    @Override
    protected void afterClone(){
    	System.out.println("Clone behaviour not yet implemented for the ContainerAgent!");
    	super.afterClone();
    }
    
    @Override
    protected void takeDown(){
    	if(componentConfigurator != null)
    		componentConfigurator.destroy();
    	super.takeDown();
	}
    
    private void reload() throws ComponentConfiguratorException {
    	componentConfigurator.postMigrationSetup();
	}
	
	public Object getComponent(String id){
		return componentConfigurator.getComponent(id);
	}
	
	public void removeComponent(String id){
		componentConfigurator.removeComponent(id);
	}
	
	public ComponentMetaData getComponentMetadata(String id){
		return componentConfigurator.getComponentMetaData(id);
	}
	
}
