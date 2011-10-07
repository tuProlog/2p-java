package alice.tuprolog;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ExceptionsTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for exceptions");
		suite.addTestSuite(BasicLibraryExceptionsTestCase.class);
		suite.addTestSuite(BuiltInExceptionsTestCase.class);
		suite.addTestSuite(DCGLibraryExceptionsTestCase.class);
		suite.addTestSuite(IOLibraryExceptionsTestCase.class);
		suite.addTestSuite(ISOLibraryExceptionsTestCase.class);
		suite.addTestSuite(JavaLibraryExceptionsTestCase.class);
		suite.addTestSuite(JavaThrowCatchTestCase.class);
		suite.addTestSuite(ThrowCatchTestCase.class);
		return suite;
	}

}
