/**
 *Classe di test per le situazioni di errore.
 *Si testa la gestione delle operazioni sbagliate sul sistema.
 *
 *@author ENanetti
 */
 
//LCG
 package alice.tuprolog;
 
 import junit.framework.*;
 
 
 
 public class TestWrongCases extends TestCase{
 	
 	//motore prolog
 	Prolog engine;
 	//teorie logiche da inserire
 	Theory th1, th2, th3; 
 	
 	
 	
 	/**
 	 *Impostazione suite dei test
 	 */ 	  	
 	public static Test suite(){
 		return new TestSuite(TestWrongCases.class);
 	}// Fine suite per il test
 	
 	
 	/**
 	 *Metodo di setUp richiamato ad ogni test per avere ambiente pulito
 	 */
 	public void setUp(){
 		engine = new Prolog();
 		
 		try{
 			th1= new Theory("fatt(0,1).\n"+
 						"fatt(N,Y) :- N>0, N1 is N-1, fatt(N1,Y1), Y is N*Y1.","Koremo");
 			th2= new Theory(new java.io.FileInputStream("fatty.pl"), "Are");
 			th3= new Theory("uni(X):- X is 2.","Koremo");
 		
 		
 			engine.addTheory(th1);
 			engine.addTheory(th2);
 			engine.addTheory(th3);
 			
 		}catch (Exception e){}
 		
 		
 	}//fine setUp

 	
 
 	/**
 	 *metodo per testare il caso di errore se il nome della teoria non esiste
 	 *Test basato su eccezione, se non scatta allora il test fallisce
 	 */
 	public void testWrongRemove(){
 			
 		try{
 			engine.removeTheory("Machigai");
 			Assert.fail("Teoria rimossa correttamente");
 		}catch(InvalidTheoryException ite){
 			
 		}
 		
 	}//fine testWrongRemove
 	
 	
 	
 	/**
 	 *metodo per testare caso di errore per recupero posizione th inesistenti
 	 */
 	public void testWrongGetPos(){
 		
 		try{
 			int[] pos = engine.getTheoryPos("Machigai");
 			Assert.fail("Posizioni recuperate correttamente");
 			 				
 		}catch(InvalidTheoryException ite){
 		
 		}
	
 	}//fine testWrongGetPos
 
 	
 	/**
 	 *metodo per testare caso di errore per recupero teorie inesistenti
 	 */
 	public void testWrongGetTheory(){
 		
 		try{
 			Theory[] received = engine.getTheories("Machigai");
 			Assert.fail("Teorie recuperate correttamente");
 			 				
 		}catch(InvalidTheoryException ite){
 		
 		}
 		
 	}//fineWrongGetTheory
 

  	/**
 	 *Main per invocare l'esecuzione di JUNIT
 	 */ 	
 	public static void main(String args[]){
				
		junit.swingui.TestRunner.run(TestWrongCases.class);
	}//Fine del main con l'invocazioe del comando di test
	
 	
 }//fine classe