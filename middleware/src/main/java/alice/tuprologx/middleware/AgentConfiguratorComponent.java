package alice.tuprologx.middleware;

import alice.tuprolog.Prolog;
import alice.tuprologx.middleware.annotations.PrologConfiguration;
import alice.tuprologx.middleware.interfaces.IAgentConfiguratorComponent;
import alice.tuprologx.middleware.annotations.AsSingleton;
import alice.tuprologx.middleware.annotations.PostConstructCall;

//Alberto
@AsSingleton
public final class AgentConfiguratorComponent implements IAgentConfiguratorComponent {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	@PrologConfiguration(fromFiles = {"./agent_configurator.pl"},
			directives = {"load_library('alice.tuprologx.middleware.lib.ComponentConfiguratorLibrary').",
					"load_library('alice.tuprologx.middleware.lib.AgentLibrary')."})
	private Prolog prolog;
	
	@Override
	public final void solveGoal(String action) throws Exception {
		System.out.println(prolog.solve(action).toString());
	}

	@Override
	@PostConstructCall
	public final void configureAgents() {
		try {
			prolog.solve("configureAgents.");
		} 
		catch (Exception e) {/*nothing*/}
	}

}