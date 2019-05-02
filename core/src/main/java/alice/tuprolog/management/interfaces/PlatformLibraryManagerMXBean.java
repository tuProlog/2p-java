package alice.tuprolog.management.interfaces;

//Alberto
public interface PlatformLibraryManagerMXBean {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	String fetchCurrentPlatform();
	String fetchCurrentLibraries();
	String fetchCurrentExternalLibraries();
	boolean loadLibraryIntoEngine(String libraryClass);
	boolean unloadLibraryFromEngine(String libraryClass);

}
