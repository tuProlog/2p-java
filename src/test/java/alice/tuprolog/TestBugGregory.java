/*
 * Created on May 23, 2005
 * 
 */

//LCG
package alice.tuprolog;

import junit.framework.*;//LCG

/**
 * 
 *
 * @author aricci
 *
 */
public class TestBugGregory extends TestCase {//LCG
	
	//public static void main(String[] args) throws Exception {
    public void test() {//LCG
    	
    	try {
    		
        	Theory theory = new Theory("a :- b, (d ; e).\ne.");
        	Term query = new Struct("a");

        	Prolog engine = new Prolog();

        	engine.setTheory(theory);
        	Theory before = engine.getWholeTheory();
        	System.out.println("Before:\n" + before);

        	System.out.println("?- " + query + ".");
        	System.out.println(engine.solve(query).isSuccess());

        	engine.setTheory(before);
        	Theory after = engine.getWholeTheory();
        	System.out.println("\nAfter:\n" + after);

        	System.out.println("?- " + query + ".");
        	System.out.println(engine.solve(query).isSuccess());
        	
    	} catch (Exception ex) {
    		
    		fail("" + ex);
    		
    	}

    }
    
}
