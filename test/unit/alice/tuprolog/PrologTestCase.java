package alice.tuprolog;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.Library;
import alice.tuprolog.Prolog;
import alice.tuprolog.event.SpyEvent;
import alice.tuprolog.event.SpyListener;

import junit.framework.TestCase;

public class PrologTestCase extends TestCase {
	
	public void testEngineInitialization() {
		Prolog engine = new Prolog();
		assertEquals(4, engine.getCurrentLibraries().length);
		assertNotNull(engine.getLibrary("alice.tuprolog.lib.BasicLibrary"));
		assertNotNull(engine.getLibrary("alice.tuprolog.lib.ISOLibrary"));
		assertNotNull(engine.getLibrary("alice.tuprolog.lib.IOLibrary"));
		assertNotNull(engine.getLibrary("alice.tuprolog.lib.JavaLibrary"));
	}
	
	public void testLoadLibraryAsString() throws InvalidLibraryException {
		Prolog engine = new Prolog();
		engine.loadLibrary("alice.tuprolog.StringLibrary");
		assertNotNull(engine.getLibrary("alice.tuprolog.StringLibrary"));
	}
	
	public void testLoadLibraryAsObject() throws InvalidLibraryException {
		Prolog engine = new Prolog();
		Library stringLibrary = new StringLibrary();
		engine.loadLibrary(stringLibrary);
		assertNotNull(engine.getLibrary("alice.tuprolog.StringLibrary"));
		Library javaLibrary = new alice.tuprolog.lib.JavaLibrary();
		engine.loadLibrary(javaLibrary);
		assertSame(javaLibrary, engine.getLibrary("alice.tuprolog.lib.JavaLibrary"));
	}
	
	public void testGetLibraryWithName() throws InvalidLibraryException {
		Prolog engine = new Prolog(new String[] {"alice.tuprolog.TestLibrary"});
		assertNotNull(engine.getLibrary("TestLibraryName"));
	}
	
	public void testSpyListenerManagement() {
		Prolog engine = new Prolog();
		SpyListener listener1 = new SpyListener() {
			public void onSpy(SpyEvent e) {}
		};
		SpyListener listener2 = new SpyListener() {
			public void onSpy(SpyEvent e) {}
		};
		engine.addSpyListener(listener1);
		engine.addSpyListener(listener2);
		assertEquals(2, engine.getSpyListenerList().size());
	}

}
