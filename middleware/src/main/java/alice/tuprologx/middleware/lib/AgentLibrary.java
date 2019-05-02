package alice.tuprologx.middleware.lib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import alice.tuprolog.Library;
import alice.tuprolog.PrologError;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.exceptions.InvalidObjectIdException;
import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.tuprolog.lib.OOLibrary;
import alice.tuprologx.middleware.Middleware;
import alice.tuprologx.middleware.agents.interfaces.IAgentPlatformWrapper;

//Alberto
public class AgentLibrary extends Library {

	/**
     * @author Alberto Sita
     * 
     */
	
	private static final long serialVersionUID = 1L;
	
	public String getTheory() {
		return   "configureAgents :- \n"
				+ "findall(agent(PropertiesFile, IsMain), agent(PropertiesFile, IsMain), List),\n"
				+ "configureAgentsFromList(List).\n"
				+ ""
				+ "configureAgentsFromList([]).\n"
				+ ""
				+ "configureAgentsFromList([agent(PropertiesFile, IsMain)|T]) :- \n"
				+ "configureAgent(PropertiesFile, IsMain),\n"
				+ "configureAgentsFromList(T).\n"
				+ ""
				+ "configureAgent(PropertiesFile, IsMain) :-\n"
				+ "'$checkOOLibrary',\n"
				+ "'$spawnAgent'(PropertiesFile, IsMain).\n"
				+ ""
				+ "findallAgents(L) :- \n"
				+ "findall(Id, agentId(Id), L).\n"
				+ ""
				+ "destroyAgentWrapper(Id) :-\n"
				+ "Id <- getAgentContainer returns C,\n"
				+ "C <- kill,\n"
				+ "retract(agentId(Id)).\n"
				+ ""
				+ "migrateAgent(Id, Destination) :-\n"
				+ "Id <- migrateTo(Destination).\n"
				+ ""
				+"createAgentContainer(Id, Name) :-\n"
				+ "'$bindContainer'(Id, Name).\n"
				+ ""
				+ "destroyAgentContainer(Container) :-\n"
				+ "Container <- kill,\n"
				+ "'$unbindContainer'(Container).\n"
				+ "";
	}
	
	public boolean $spawnAgent_2(Term properties, Term isMain) throws PrologError {
		String main = isMain.getTerm().toString().trim();
		Properties props = configureProperties(properties);
		if(props == null)
			return false;
		spawnAgent(main, props);
		String agentId = props.getProperty("agentId");
		if(agentId == null){
			String msg = "agentId is null, please check configuration file.";
			throw new PrologError(new Struct(msg), msg);
		}
		return assertAgentId(agentId);
	}
	
	public boolean $bindContainer_2(Term id, Term containerId) throws PrologError {
		String agentid = id.getTerm().toString().trim();
		String contid = containerId.getTerm().toString().trim();
		try {
			IAgentPlatformWrapper aw = (IAgentPlatformWrapper)((OOLibrary)engine.getLibrary("alice.tuprolog.lib.OOLibrary")).getRegisteredObject(new Struct(agentid));
			Object obj = aw.createContainer(contid);
			((OOLibrary)engine.getLibrary("alice.tuprolog.lib.OOLibrary")).register(new Struct(contid), obj);
		} catch (InvalidObjectIdException e) {
			e.printStackTrace();
			String msg = "Cannot register agent container into OOLibrary.";
			throw new PrologError(new Struct(msg), msg);
		}
		return true;
	}
	
	public boolean $unbindContainer_1(Term containerId) throws PrologError {
		String id = containerId.getTerm().toString().trim();
		try {
			((OOLibrary)engine.getLibrary("alice.tuprolog.lib.OOLibrary")).unregister(new Struct(id));
		} catch (InvalidObjectIdException e) {
			e.printStackTrace();
			String msg = "Cannot unregister agent container from OOLibrary.";
			throw new PrologError(new Struct(msg), msg);
		}
		return true;
	}
	
	private void spawnAgent(String main, Properties props) throws PrologError {
		IAgentPlatformWrapper aw = null;
		boolean isConfig = false;
		if(main.equalsIgnoreCase("yes")){
			aw = Middleware.getMiddleware().getNewAgentWrapper();
			isConfig = aw.configure(props, true);
		} else {
			aw = Middleware.getMiddleware().getNewAgentWrapper();
			isConfig = aw.configure(props, false);
		}
		if(!isConfig){
			String msg = "Problems during agent configuration, please check configuration file.";
			throw new PrologError(new Struct(msg), msg);
		}
		try {
			((OOLibrary)engine.getLibrary("alice.tuprolog.lib.OOLibrary")).register(new Struct(props.getProperty("agentId")), aw);
		} catch (InvalidObjectIdException e) {
			e.printStackTrace();
			String msg = "Cannot register agent into OOLibrary.";
			throw new PrologError(new Struct(msg), msg);
		}
	}

	private Properties configureProperties(Term properties){
		Properties props = new Properties();
		String file = properties.getTerm().toString().trim();
		try {
			FileInputStream input = new FileInputStream(alice.util.Tools.removeApices(file));
			props.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return props;
	}

	private boolean assertAgentId(String agentId) {
		try {
			engine.addTheory(new Theory("agentId("+agentId+")."));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
