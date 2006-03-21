/**
 *Classe di test per vedere che cosa fa il Context
 */
 
//LCG
package alice.tuprolog;
 
import junit.framework.*;//LCG
 

 public class Tester2 extends TestCase {
 
	 //LCG
	 public static void main(String[] args) throws Exception{
		 
		 new Tester2().test();
		 
	 }
	 
 	//public static void main(String[] args) throws Exception{
	public void test() {//LCG
 		
 		try {
 			
 			 Prolog engine = new Prolog();
 			 Maskera mask = new Maskera();//LCG
 			 mask.showGUI();
 			 
 			 Theory th1 = new Theory("doppio(X,Y):- Y is 2*X.","Doppio");
 			 Theory th2 = new Theory("fatt(0,1).\n"+
 								"fatt(N,Y) :- N>0, N1 is N-1, fatt(N1,Y1), Y is N*Y1.","Fattoriale");
 								
			 Theory thFile = new Theory(new java.io.FileInputStream("alice/tuprolog/dafile.pl"),"daFile");
			 
			 
			 engine.addTheory(th1);
			 engine.addTheory(th2);
			 engine.addTheory(thFile);
			 
			 mask.println("Teorie attive prima dell'azione");
			 for (int k=0; k<engine.getTheoryNames().length; k++){
			 	
			 	mask.println(engine.getTheoryNames()[k]);
			 
			 }//fine for
 			 
 			 
 			 engine.solve("modify.");
 			 mask.println("\nTeorie attive dopo l'azione");
			 for (int k=0; k<engine.getTheoryNames().length; k++){
			 	
			 	mask.println(engine.getTheoryNames()[k]);
			 
			 }//fine for
 			 
 			 mask.println("\nRisultato dell'invocazione 'triplo(4,K).'");
 			 SolveInfo info = engine.solve("triplo(4,K).");
 			 mask.println(info.toString());
 			 
 		} catch (Exception ex) {
 			
 			fail("" + ex);
 			
 		}
 			 
 	}//fine main
 
}//fine classe
