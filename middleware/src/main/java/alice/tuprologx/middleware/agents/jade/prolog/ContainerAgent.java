package alice.tuprologx.middleware.agents.jade.prolog;

import java.util.Properties;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.exceptions.InvalidLibraryException;
import jade.core.Agent;

//Alberto
public abstract class ContainerAgent extends Agent {
	
	/**
     * @author Alberto Sita
     * 
     */

	private static final long serialVersionUID = 1L;
	private transient Prolog mainEngine = null;
	private String engineState = null;
	private String agentState = null;
	private String componentConfiguratorId = null;
	private String confFile = null;
	
	@Override
    protected void setup() {
		setupProlog();
		try {
			Properties props = getBootProperties();
			componentConfiguratorId = props.getProperty("component-configurator-id");
			confFile = props.getProperty("component-configurator-file");
			if(componentConfiguratorId == null || confFile == null){
				System.out.println("Cannot start agent, please check the configuration file!");
				doDelete();
			}
			mainEngine.solve("createComponentConfigurator("+componentConfiguratorId+", '"+confFile+"').");
		} catch (Exception e) {
			e.printStackTrace();
			doDelete();
		}
		super.setup();
	}
	
	private void setupProlog() {
		mainEngine = new Prolog();
		try {
			mainEngine.loadLibrary("alice.tuprologx.middleware.lib.ComponentConfiguratorLibrary");
		} catch (InvalidLibraryException e) {
			e.printStackTrace();
			doDelete();
		}
	}

	@Override
	protected void beforeMove() {
		try {
			SolveInfo result = mainEngine.solve("componentConfiguratorPreMigrationSetup("+componentConfiguratorId+", State).");
			agentState = result.getTerm("State").getTerm().toString().trim();
			engineState = mainEngine.toJSON(Prolog.INCLUDE_KB_IN_SERIALIZATION);
		} catch (Exception e) {
			e.printStackTrace();
			doDelete();
		}
        super.beforeMove();
    }

    @Override
    protected void afterMove() {
    	try {
    		reload();
		} catch (Exception e) {
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
    	try {
    		if(mainEngine != null){
    			mainEngine.solve("deleteComponentConfigurator("+componentConfiguratorId+").");
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			super.takeDown();
		}
	}
    
    private void reload() throws Exception {
    	mainEngine = Prolog.fromJSON(engineState);
    	if(mainEngine == null)
    		throw new Exception("Cannot create engine from its JSON state");
		mainEngine.solve("createComponentConfigurator("+componentConfiguratorId+").");
		mainEngine.solve("componentConfiguratorPostMigrationSetup("+componentConfiguratorId+", "+agentState+").");
	}
	
	public Prolog getMainEngine(){
		return mainEngine;
	}
	
	public String getComponentConfiguratorId(){
		return componentConfiguratorId;
	}
	
}
