package alice.tuprologx.middleware.interfaces;

//Alberto
public interface IComponentConfiguratorFactory {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	IComponentConfigurator newComponentConfigurator();
	IComponentConfigurator newComponentConfigurator(String fileName);

}
