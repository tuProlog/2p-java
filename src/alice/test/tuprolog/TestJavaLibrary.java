/*
 * Created on Nov 22, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package alice.test.tuprolog;

import alice.tuprolog.lib.*;
import alice.tuprolog.*;

/**
 * @author aricci
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestJavaLibrary {

	public static void main(String[] args) throws Exception {
	
		Prolog eng = new Prolog();
		
		String goal = "java_object('java.lang.Integer',[1],one).";
		eng.solve(goal);
		
		JavaLibrary lib = (JavaLibrary) eng.getLibrary("alice.tuprolog.lib.JavaLibrary");
		Object obj = lib.getRegisteredDynamicObject(new Struct("one"));
		System.out.println(obj);
	}
}
