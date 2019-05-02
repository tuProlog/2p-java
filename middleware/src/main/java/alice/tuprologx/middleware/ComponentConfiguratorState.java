package alice.tuprologx.middleware;

import java.io.Serializable;
import java.util.HashMap;

//Alberto
final class ComponentConfiguratorState implements Serializable {
	
	/**
     * @author Alberto Sita
     * 
     */

	private static final long serialVersionUID = 1L;
	
	private String configFileLocation = null;
	private HashMap<String, String> componentsStatus = null;

	public String getConfigFileLocation() {
		return configFileLocation;
	}

	public void setConfigFileLocation(String configFileLocation) {
		this.configFileLocation = configFileLocation;
	}

	public HashMap<String, String> getComponentsStatus() {
		return componentsStatus;
	}

	public void setComponentsStatus(HashMap<String, String> componentsStatus) {
		this.componentsStatus = componentsStatus;
	}

}
