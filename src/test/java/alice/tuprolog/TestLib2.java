/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

//LCG
package alice.tuprolog;

import junit.framework.*;//LCG

/**
 * @author aricci
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestLib2 extends TestCase {//LCG

	//public static void main(String[] args) throws Exception {
	public void test() {//LCG

		try {
			
			Prolog engine = new Prolog();
			
	        //engine.loadLibrary("alice.tuprolog.test.TestLibrary");
			
			
			
			Library lib = new TTestLibrary();
			engine.loadLibrary(lib);
	
			engine.solve("N is sum(1,3), println(sum(N)).");
			
		} catch (Exception ex) {
			
			fail("" + ex);
			
		}
		
	}
	
}
