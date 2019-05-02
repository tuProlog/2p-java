package alice.tuprologx.middleware.launcher;

import java.io.InputStream;
import java.util.Scanner;

import alice.tuprolog.Prolog;
import alice.tuprologx.middleware.Middleware;
import alice.tuprologx.middleware.interfaces.IAgentConfiguratorComponent;
import alice.tuprologx.middleware.interfaces.IComponentConfigurator;

//Alberto
public final class Launcher {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	private IComponentConfigurator componentConfigurator = null;
	private IAgentConfiguratorComponent agentconfigurator = null;
	
	public Launcher(String confFile, String configuratorComponent){
		componentConfigurator = Middleware.getMiddleware().getComponentConfiguratorFactory().newComponentConfigurator(confFile);
		agentconfigurator = (IAgentConfiguratorComponent) componentConfigurator.getComponent(configuratorComponent);
	}
	
	public IAgentConfiguratorComponent getAgentConfigurator(){
		return agentconfigurator;
	}
	
	public IComponentConfigurator getComponentConfigurator(){
		return componentConfigurator;
	}
	
	public void loop(InputStream is){
		Scanner scanner = new Scanner(is);
		while(true){
			String goal = scanner.nextLine();
			if(goal.equals("close_terminal"))
				break;
			try {
				agentconfigurator.solveGoal(goal);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			System.out.println();
		}
		System.out.println("Terminal closed, not the infrastructure!");
		scanner.close();
		componentConfigurator.destroy();
		componentConfigurator = null;
		agentconfigurator = null;
	}

	public static void main(String[] args) {
		System.out.println("Welcome to the tuProlog "+Prolog.getVersion()+" middleware platform. Trying to launch agent(s)...");
		Launcher bootstrap = new Launcher(args[0], args[1]);
		bootstrap.loop(System.in);
	}
	
}

