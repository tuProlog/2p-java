package alice.tuprolog.management;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import alice.tuprolog.Prolog;
import alice.tuprolog.json.JSONSerializerManager;
import alice.tuprolog.management.interfaces.PrologMXBeanServerMXBean;
import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.ssl.SSLAdaptorServerSocketFactory;

//Alberto
public final class PrologMXBeanServer implements PrologMXBeanServerMXBean {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	public static final String HTTP_ADAPTOR = "httpAdaptor";
	//public static final String RMI_ADAPTOR = "rmiAdaptor"; //to do
	
	static final String ENGINE_MANAGER_NAME = "service:prolog=engineManager";
	static final String PRIMITIVE_MANAGER_NAME = "service:prolog=primitiveManager";
	static final String FLAG_MANAGER_NAME = "service:prolog=flagManager";
	static final String OPERATOR_MANAGER_NAME = "service:prolog=operatorManager";
	static final String THEORY_MANAGER_NAME = "service:prolog=theoryManager";
	static final String LABEL_MANAGER_NAME = "service:prolog=labelManager";
	static final String JSON_MANAGER_NAME = "service:prolog=jsonManager";
	static final String LIBRARY_MANAGER_NAME = "service:prolog=libraryManager";
	
	static final String THIS_SERVER = "service:server=mxBeanServer";
	
	private MBeanServer server = null;
	private String adaptor = null;
	private HttpAdaptor httpAdaptor = null;
	
	public void startMXBeanServer(Prolog prolog, String host, int port){
		this.startMXBeanServer(prolog, host, port, "", "", "");
	}
	
	public void startMXBeanServer(Prolog prolog, String host, int port, String adaptor){
		this.startMXBeanServer(prolog, host, port, adaptor, "", "");
	}
	
	public void startMXBeanServer(Prolog prolog, String host, int port, String adaptor, String credentialFile, String SSLconfigFile){
		this.adaptor = adaptor;
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		this.server = server;
		ObjectName engineManagerName = null;
		ObjectName primitiveManagerName = null;
		ObjectName flagManagerName = null;
		ObjectName operatorManagerName = null;
		ObjectName theoryManagerName = null;
		
		//ObjectName labelManagerName = null; //to do
		
		ObjectName jsonManagerName = null;
		ObjectName libraryManagerName = null;
		ObjectName thisServerName = null;
		try {
			engineManagerName = new ObjectName(ENGINE_MANAGER_NAME);
			primitiveManagerName = new ObjectName(PRIMITIVE_MANAGER_NAME);
			flagManagerName = new ObjectName(FLAG_MANAGER_NAME);
			operatorManagerName = new ObjectName(OPERATOR_MANAGER_NAME);
			theoryManagerName = new ObjectName(THEORY_MANAGER_NAME);
			
			//labelManagerName = new ObjectName(LABEL_MANAGER_NAME); //to do
			
			jsonManagerName = new ObjectName(JSON_MANAGER_NAME); 
			libraryManagerName = new ObjectName(LIBRARY_MANAGER_NAME);
			thisServerName = new ObjectName(THIS_SERVER);
			server.registerMBean(prolog.getEngineManager(), engineManagerName);
			server.registerMBean(prolog.getPrimitiveManager(), primitiveManagerName);
			server.registerMBean(prolog.getFlagManager(), flagManagerName);
			server.registerMBean(prolog.getOperatorManager(), operatorManagerName);
			server.registerMBean(prolog.getTheoryManager(), theoryManagerName);
			
			//label registration //to do
			
			server.registerMBean(new JSONSerializerManager(), jsonManagerName);
			server.registerMBean(prolog.getLibraryManager(), libraryManagerName);
			server.registerMBean(this, thisServerName);
			if(adaptor != null && !adaptor.isEmpty())
				startAdaptor(host, port, adaptor, credentialFile, SSLconfigFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void startAdaptor(String host, int port, String adaptor, String credentialFile, String SSLconfigFile) throws IOException, MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		if(adaptor.equalsIgnoreCase(PrologMXBeanServer.HTTP_ADAPTOR)){
			httpAdaptor = new HttpAdaptor();
			ObjectName httpAdapterName = new ObjectName("service:tuprologServer=HttpAdaptor");
			server.registerMBean(httpAdaptor, httpAdapterName);
			httpAdaptor.setPort(port);
			httpAdaptor.setHost(host);
			//Auth
			checkCredentialFile(credentialFile);
			//SSL
			checkSSL(SSLconfigFile);
			httpAdaptor.start();
		} // else...
		
		  //to do
	}

	private void checkCredentialFile(String credentialFile) throws IOException {
		BufferedReader br = null;
		String line = null;
		String username = null;
		String password = null;
		if(credentialFile != null && !credentialFile.isEmpty()){
			try {
				br = new BufferedReader(new FileReader(credentialFile));
			} catch (FileNotFoundException e) {
				System.out.println("Cannot find "+credentialFile+". The httpAdaptor will not be activated!");
				e.printStackTrace();
				return;
			}
			try {
				line = br.readLine();
				String[] credentials = line.split(" ");
				username = credentials[0];
				password = credentials[1];
			} catch (IOException e) {
				System.out.println("Cannot read "+credentialFile+". The httpAdaptor will not be activated!");
				e.printStackTrace();
				br.close();
				return;
			}
			br.close();
			httpAdaptor.addAuthorization(username, password);
			httpAdaptor.setAuthenticationMethod("basic");
		}
	}

	private void checkSSL(String SSLconfigFile) {
		if(SSLconfigFile != null && !SSLconfigFile.isEmpty()){
			Properties props = getPropsFromFile(SSLconfigFile);
			if(props == null){
				System.out.println("Cannot read "+SSLconfigFile+". The httpAdaptor will not be activated!");
				return;
			}
			SSLAdaptorServerSocketFactory factory = new SSLAdaptorServerSocketFactory();
			factory.setKeyStoreType(props.getProperty("key_store_type"));
			factory.setKeyStoreName(props.getProperty("key_store_name"));
			factory.setKeyStorePassword(props.getProperty("key_store_password"));
			factory.setKeyManagerAlgorithm(props.getProperty("key_manager_algorithm"));
			factory.setKeyManagerPassword(props.getProperty("key_manager_password"));
			factory.setSSLProtocol(props.getProperty("ssl_protocol"));
			
			//System.setProperty("javax.net.debug", "ssl,handshake,record"); //debug
			
			httpAdaptor.setSocketFactory(factory);
		}
	}

	private Properties getPropsFromFile(String SSL_configFile) {
		InputStream input = null;
		Properties props = new Properties();
		try {
			input = new FileInputStream(SSL_configFile);
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

	public MBeanServer getMBeanServer() {
		return server;
	}

	public HttpAdaptor getHttpAdaptor() {
		return httpAdaptor;
	}
	
	public String getAdaptor() {
		return adaptor;
	}
	
	public void stopAdaptor(String adaptor) {
		if (adaptor == null || adaptor.isEmpty())
			return;
		if(adaptor.equalsIgnoreCase(PrologMXBeanServer.HTTP_ADAPTOR)){
			getHttpAdaptor().stop();
		}// else...
		
		 //to do
	}

	///Management
	
	@Override
	public synchronized void shutdown() {
		stopAdaptor(getAdaptor());
		MBeanServerFactory.releaseMBeanServer(getMBeanServer());
	}

}
