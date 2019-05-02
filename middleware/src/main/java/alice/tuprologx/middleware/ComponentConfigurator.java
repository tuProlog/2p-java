package alice.tuprologx.middleware;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;
import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.tuprolog.exceptions.MalformedGoalException;
import alice.tuprolog.json.JSONSerializerManager;
import alice.tuprologx.middleware.annotations.PostConstructCall;
import alice.tuprologx.middleware.annotations.PostMigrationSetup;
import alice.tuprologx.middleware.annotations.PreMigrationSetup;
import alice.tuprologx.middleware.annotations.PrologConfiguration;
import alice.tuprologx.middleware.annotations.PrologManagement;
import alice.tuprologx.middleware.annotations.AsPrototype;
import alice.tuprologx.middleware.annotations.OnDispose;
import alice.tuprologx.middleware.exceptions.ComponentConfiguratorException;
import alice.tuprologx.middleware.interfaces.IComponentConfigurator;

//Alberto
public final class ComponentConfigurator implements IComponentConfigurator {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	private static final long serialVersionUID = 1L;
	
	public static final boolean ENABLE_AUTOWIRE = true;
	public static final boolean DISABLE_AUTOWIRE = false;
	
	private boolean configured = false;
	private String configFileLocation = null;
	
	private transient HashMap<String, Object> components = null;
	private transient HashMap<String, ComponentMetaData> componentsMetaData = null;
	private HashMap<String, String> componentsStatus = null;
	
	public ComponentConfigurator() {}
	
	public ComponentConfigurator(String fileName){
		setConfigFileLocation(fileName);
		try {
			configure();
		} catch (ComponentConfiguratorException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configure() throws ComponentConfiguratorException {
		configure(ComponentConfigurator.ENABLE_AUTOWIRE);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T createComponentFromClassName(String klass, String id) {
		T obj = null;
		try {
			obj = (T) Class.forName(klass).newInstance();
			boolean isPrototype = obj.getClass().isAnnotationPresent(AsPrototype.class);
			Field[] fields = obj.getClass().getDeclaredFields();
			boolean ok = false;
			for(Field field : fields){
				if(field.isAnnotationPresent(PrologConfiguration.class)){
					ok = true;
					configureEngine(field, obj, isPrototype);
					Method[] methods = obj.getClass().getMethods();
					for(Method method : methods){
						if(method.isAnnotationPresent(PostConstructCall.class)){
							method.invoke(obj, (Object[])null);
						}
					}
					//only one engine for a single component.
					break;
				}
			}
			if(!ok)
				throw new ComponentConfiguratorException("component does not declare a prolog engine!");
			if(!isPrototype){
				components.put(id, obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		return obj;
	}

	private InputStream getTheoryFromFile(String fileName) {
		InputStream input = null;
		try {
			input = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return input;
	}

	private void prePassivate(String id) {
		Object obj = components.get(id);
		Field prologEngine = null;
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for(Field field : fields){
				if(field.isAnnotationPresent(PrologConfiguration.class)){
					prologEngine = field;
					break;
				} 
			}
			if(prologEngine == null)
				return;
			prologEngine.setAccessible(true);
			Prolog engine = (Prolog) prologEngine.get(obj);
			engine.stopMXBeanServer();
			prologEngine.setAccessible(false);
			componentPreMigrationSetup(obj);
			componentsStatus.put(id, engine.toJSON(Prolog.INCLUDE_KB_IN_SERIALIZATION));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		} 
	}

	private void postActivate(String id) {
		Object obj = components.get(id);
		Field prologEngine = null;
		String prologState = componentsStatus.get(id);
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for(Field field : fields){
				if(field.isAnnotationPresent(PrologConfiguration.class)){
					prologEngine = field;
					break;
				}
			}
			if(prologEngine == null)
				throw new ComponentConfiguratorException("component does not declare a prolog engine!");
			if(prologState == null){
				configureEngine(prologEngine, obj, false);
			} else {
				prologEngine.setAccessible(true);
				Prolog prolog = Prolog.fromJSON(prologState);
				if(configureMXBeanServer(prologEngine, prolog))
					prolog.startMXBeanServer();
				prologEngine.set(obj, prolog);
				prologEngine.setAccessible(false);
			}
			componentPostMigrationSetup(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 
	}

	private void configureEngine(Field prologEngine, Object obj, boolean isPrototype) throws MalformedGoalException, IllegalArgumentException, IllegalAccessException {
		PrologConfiguration config = prologEngine.getAnnotation(PrologConfiguration.class);
		String prologTheory = config.prologTheory();
		String[] directives = config.directives();
		String[] fromFiles = config.fromFiles();
		Prolog prolog = new Prolog();
		try {
			if(!prologTheory.trim().isEmpty())
				prolog.setTheory(new Theory(prologTheory));
			for(String file : fromFiles){
				if(!file.trim().isEmpty()){
					prolog.addTheory(new Theory(getTheoryFromFile(file.trim())));
				}
			}
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String goal : directives){
			if(!goal.isEmpty())
				prolog.solve(goal);
		}
		if(configureMXBeanServer(prologEngine, prolog))
			if(!isPrototype)
			prolog.startMXBeanServer();
		prologEngine.setAccessible(true);
		prologEngine.set(obj, prolog);
		prologEngine.setAccessible(false);
	}

	private boolean configureMXBeanServer(Field prologEngine, Prolog prolog) {
		if(prologEngine.isAnnotationPresent(PrologManagement.class)){
			PrologManagement management = prologEngine.getAnnotation(PrologManagement.class);
			String host = management.host();
			int port = management.port();
			boolean lazy = management.lazyBoot();
			String adaptor = management.adaptor();
			String credentialFile =  management.credentialFile();
			String SSLconfigFile = management.SSLconfigFile();
			prolog.setHost(host);
			prolog.setPort(port);
			prolog.setAdaptor(adaptor);
			prolog.setCredentialFile(credentialFile);
			prolog.setSSLconfigFile(SSLconfigFile);
			return !lazy;
		}
		return false;
	}

	@Override
	public void configure(boolean withInjection) throws ComponentConfiguratorException {
		if(isConfigured())
			return;
		components = new HashMap<String, Object>();
		componentsMetaData = new HashMap<String, ComponentMetaData>();
		InputStream input = null;
		Properties props = new Properties();
		try {
			input = new FileInputStream(getConfigFileLocation());
			props.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ComponentConfiguratorException("File: "+configFileLocation+" not found!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ComponentConfiguratorException("IOException: "+e.getMessage());
		}
		int i=1;
		for(;;i++){
			String id = props.getProperty("componentId."+i);
			String className = props.getProperty("componentClass."+i);
			if(id == null){
				if(className == null)
					break;
				else
					throw new ComponentConfiguratorException("Component id cannot be null!");
			}
			ComponentMetaData componentMetaData = new ComponentMetaData();
			componentMetaData.put("componentId", id);
			componentMetaData.put("componentClass", className);
			componentsMetaData.put(id, componentMetaData);
			Object obj = null;
			try {
				if(Class.forName(className).isAnnotationPresent(AsPrototype.class)){
					continue;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ComponentConfiguratorException("Could not find class: "+className);
			}
			if(withInjection)
				obj = createComponentFromClassName(className, id);
			else {
				try {
					obj = Class.forName(className).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new ComponentConfiguratorException("Could not create component: "+id);
				}
				components.put(id, obj);
			}
			if(obj == null)
				throw new ComponentConfiguratorException("Could not create component: "+id);
		}
		configured = true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getComponent(String id) {
		Object o = components.get(id);
		if (o == null){
			ComponentMetaData metaData = componentsMetaData.get(id);
			if(metaData == null)
				return null;
			String className = metaData.get("componentClass");
			return createComponentFromClassName(className, id);
		}
		return o;
	}

	@Override
	public void removeComponent(String id) {
		Object obj = components.get(id);
		if(obj != null){
			stopMXBeanServer(obj);
			cleanup(obj);
			components.remove(id);
		}
	}

	private void cleanup(Object obj) {
		Method[] methods = obj.getClass().getMethods();
		for(Method method : methods){
			if(method.isAnnotationPresent(OnDispose.class)){
				try {
					method.invoke(obj, (Object[])null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void componentPreMigrationSetup(Object obj) {
		Method[] methods = obj.getClass().getMethods();
		for(Method method : methods){
			if(method.isAnnotationPresent(PreMigrationSetup.class)){
				try {
					method.invoke(obj, (Object[])null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void componentPostMigrationSetup(Object obj) {
		Method[] methods = obj.getClass().getMethods();
		for(Method method : methods){
			if(method.isAnnotationPresent(PostMigrationSetup.class)){
				try {
					method.invoke(obj, (Object[])null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ComponentMetaData getComponentMetaData(String id) {
		return componentsMetaData.get(id);
	}

	@Override
	public void postMigrationSetup() throws ComponentConfiguratorException {
		configure(DISABLE_AUTOWIRE);
		Set<String> keys = components.keySet();
		for(String key : keys){
			postActivate(key);
		}
		configured = true;
	}

	@Override
	public void preMigrationSetup() {
		setComponentsStatus(new HashMap<String, String>());
		configured = false;
		Set<String> keys = components.keySet();
		for(String key : keys){
			prePassivate(key);
		}
	}

	private void stopMXBeanServer(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field : fields){
			if(field.isAnnotationPresent(PrologManagement.class)){
				field.setAccessible(true);
				Prolog engine = null;
				try {
					engine = (Prolog) field.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					return;
				}
				engine.stopMXBeanServer();
				field.setAccessible(false);
			} 
		}
	}

	@Override
	public boolean isConfigured() {
		return configured;
	}

	@Override
	public String getConfigFileLocation() {
		return configFileLocation;
	}
	
	@Override
	public void setConfigFileLocation(String fileName) {
		configFileLocation = fileName;
	}

	@Override
	public void destroy() {
		Set<String> keys = components.keySet();
		for(String key : keys){
			Object obj = components.get(key);
			stopMXBeanServer(obj);
			cleanup(obj);
		}
		configFileLocation = null;
		components = null;
		componentsMetaData = null;
		componentsStatus = null;
		configured = false;
	}

	@Override
	public String toJSON() {
		ComponentConfiguratorState state = new ComponentConfiguratorState();
		state.setComponentsStatus(componentsStatus);
		state.setConfigFileLocation(configFileLocation);
		return JSONSerializerManager.toJSON(state);
	}
	
	@Override
	public void fromJSON(String jsonString) {
		ComponentConfiguratorState state = JSONSerializerManager.fromJSON(jsonString, ComponentConfiguratorState.class);
		setConfigFileLocation(state.getConfigFileLocation());
		setComponentsStatus(state.getComponentsStatus());
	}

	private void setComponentsStatus(HashMap<String, String> componentsStatus) {
		this.componentsStatus = componentsStatus;
	}

}
