package alice.tuprologx.middleware.lib;

import alice.tuprolog.Library;
import alice.tuprolog.PrologError;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.exceptions.InvalidObjectIdException;
import alice.tuprolog.lib.OOLibrary;
import alice.tuprologx.middleware.Middleware;
import alice.tuprologx.middleware.interfaces.IComponentConfigurator;

//Alberto
public class ComponentConfiguratorLibrary extends Library {

	/**
     * @author Alberto Sita
     * 
     */
	
	private static final long serialVersionUID = 1L;
	
	public String getTheory() {
		return    "createComponentConfigurator(Id, ConfigFile) :-\n"
		 		+ "'$checkOOLibrary',\n"
		 		+ "'$registerComponentConfiguratorToOOLibrary'(Id, ConfigFile).\n"
		 		+ ""
		 		+ "createComponentConfigurator(Id) :-\n"
		 		+ "'$checkOOLibrary',\n"
			 	+ "'$registerComponentConfiguratorToOOLibrary'(Id).\n"
			 	+ ""
		 		+ "removeComponent(Id, ComponentName) :-\n"
		 		+ "Id <- removeComponent(ComponentName).\n"
		 		+ ""
		 		+ "getComponentMetaData(Id, ComponentName, MetaDataKey, Output) :-\n"
		 		+ "Id <- getComponentMetaData(ComponentName) returns MetaData,\n"
		 		+ "MetaData <- get(MetaDataKey) returns Output.\n"
		 		+ ""
		 		+ "isConfigured(Id, IsConfigured) :-\n"
		 		+ "Id <- isConfigured returns IsConfigured.\n"
		 		+ ""
		 		+ "getConfigFileLocation(Id, ConfigFileLocation) :-\n"
		 		+ "Id <- getConfigFileLocation returns ConfigFileLocation.\n"
		 		+ ""
		 		+ "setConfigFileLocation(Id, ConfigFileLocation) :-\n"
		 		+ "Id <- setConfigFileLocation(ConfigFileLocation).\n"
		 		+ ""
		 		+ "componentConfiguratorPreMigrationSetup(Id, State) :-\n"
		 		+ "Id <- preMigrationSetup,\n"
		 		+ "Id <- toJSON returns State.\n"
		 		+ ""
		 		+ "componentConfiguratorPostMigrationSetup(Id, State) :-\n"
		 		+ "Id <- fromJSON(State),\n"
		 		+ "Id <- postMigrationSetup.\n"
		 		+ ""
		 		+ "invoke(Id, ComponentName, Operation, Result) :-\n"
		 		+ "Id <- getComponent(ComponentName) returns ComponentInstance,\n"
		 		+ "ComponentInstance <- Operation returns Result.\n"
		 		+ ""
		 		+ "invoke_no_result(Id, ComponentName, Operation) :-\n"
		 		+ "Id <- getComponent(ComponentName) returns ComponentInstance,\n"
		 		+ "ComponentInstance <- Operation.\n"
		 		+ ""
		 		+ "deleteComponentConfigurator(Id) :-\n"
		 		+ "'$checkOOLibrary',\n"
		 		+ "Id <- destroy,\n"
		 		+ "'$unregisterComponentConfiguratorFromOOLibrary'(Id).\n"
		 		+ "";
	 }
	
	public boolean $checkOOLibrary_0() throws PrologError{
		if (((OOLibrary)engine.getLibrary("alice.tuprolog.lib.OOLibrary")) == null){
			String msg = "alice.tuprolog.lib.OOLibrary is required.\nPlease load this library into your tuProlog system.";
			throw new PrologError(new Struct(msg), msg);
		} else
			return true;
	}
	
	public boolean $registerComponentConfiguratorToOOLibrary_1(Term id){
		String identificativo = alice.util.Tools.removeApices(id.getTerm().toString());
		IComponentConfigurator prologContainer = Middleware.getMiddleware().getComponentConfiguratorFactory().newComponentConfigurator();
		return registerInOOLibrary(identificativo, prologContainer);
	}
	
	public boolean $registerComponentConfiguratorToOOLibrary_2(Term id, Term configFile){
		String config = alice.util.Tools.removeApices(configFile.getTerm().toString());
		String identificativo = alice.util.Tools.removeApices(id.getTerm().toString());
		IComponentConfigurator prologContainer = Middleware.getMiddleware().getComponentConfiguratorFactory().newComponentConfigurator(config);
		return registerInOOLibrary(identificativo, prologContainer);
	}
	
	private boolean registerInOOLibrary(String identificativo, IComponentConfigurator prologContainer) {
		try {
			((OOLibrary)engine.getLibrary("alice.tuprolog.lib.OOLibrary")).register(new Struct(identificativo), prologContainer);
		} catch (InvalidObjectIdException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean $unregisterComponentConfiguratorFromOOLibrary_1(Term id){
		String identificativo = alice.util.Tools.removeApices(id.getTerm().toString());
		try {
			((OOLibrary)engine.getLibrary("alice.tuprolog.lib.OOLibrary")).unregister(new Struct(identificativo)); 
		} catch (InvalidObjectIdException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
