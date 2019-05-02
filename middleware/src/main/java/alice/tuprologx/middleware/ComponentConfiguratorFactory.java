package alice.tuprologx.middleware;

import alice.tuprologx.middleware.interfaces.IComponentConfigurator;
import alice.tuprologx.middleware.interfaces.IComponentConfiguratorFactory;

//Alberto
class ComponentConfiguratorFactory implements IComponentConfiguratorFactory {
	
	/**
     * @author Alberto Sita
     * 
     */

	@Override
	public IComponentConfigurator newComponentConfigurator() {
		return new ComponentConfigurator();
	}

	@Override
	public IComponentConfigurator newComponentConfigurator(String fileName) {
		return new ComponentConfigurator(fileName);
	}

}
