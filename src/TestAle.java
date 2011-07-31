import java.io.IOException;

import alice.tuprolog.*;

public class TestAle {

	/**
	 * @param args
	 * @throws InvalidLibraryException 
	 * @throws InvalidTheoryException 
	 * @throws MalformedGoalException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InvalidLibraryException, InvalidTheoryException, MalformedGoalException, IOException 
	{
		Prolog engine = new Prolog();
		//engine.unloadLibrary("alice.tuprolog.lib.BasicLibrary");
//		engine.unloadLibrary("alice.tuprolog.lib.JavaLibrary");
//		Theory th = new Theory(	"test1 :- java_object('javax.swing.JFileChooser',[],Dialog), " +
//								"Dialog <- showOpenDialog(_).");
		
		Theory th = new Theory("test :- X = 1, Y = 1, X =:= Y.");
		engine.addTheory(th);
		//engine.loadLibrary("alice.tuprolog.lib.JavaLibrary");
		SolveInfo info = engine.solve("test.");
		System.out.println(info.isSuccess());
		
		engine.unloadLibrary("alice.tuprolog.lib.BasicLibrary");
		info = engine.solve("test.");
		System.out.println(info.isSuccess());
	
	}

}
