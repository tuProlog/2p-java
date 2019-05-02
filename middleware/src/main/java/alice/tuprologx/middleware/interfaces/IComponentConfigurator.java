package alice.tuprologx.middleware.interfaces;

import java.io.Serializable;

import alice.tuprologx.middleware.exceptions.ComponentConfiguratorException;

//Alberto
public interface IComponentConfigurator extends Serializable {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	void configure() throws ComponentConfiguratorException;
	void configure(boolean withInjection) throws ComponentConfiguratorException;
	void postMigrationSetup() throws ComponentConfiguratorException;
	void removeComponent(String id);
	void preMigrationSetup();
	boolean isConfigured();
	
	<T> T getComponent(String id);
	<T> T getComponentMetaData(String id);
	String getConfigFileLocation();
	String toJSON();
	
	void fromJSON(String jsonString);
	void setConfigFileLocation(String fileName);
	void destroy();
	
}
