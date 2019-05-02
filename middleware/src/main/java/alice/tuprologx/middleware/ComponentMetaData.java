package alice.tuprologx.middleware;

import java.util.Properties;

//Alberto
public final class ComponentMetaData {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	Properties props = new Properties();
	
	public synchronized void put(String key, String property){
		props.put(key, property);
	}
	
	public synchronized String get(String key){
		return (String) props.getProperty(key);
	}

}
