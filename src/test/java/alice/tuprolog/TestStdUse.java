/**
 *Classe di test per usare JUNIT.
 *Faccio i test per vedere il funz corretto della gestione delle teorie logiche.
 *Test standard, teorie con nomi unici.
 *
 *@author ENanetti
 */

//LCG
 package alice.tuprolog;
 
 import junit.framework.*;
 
 
 public class TestStdUse extends TestCase{
 	
 	//motore prolog
 	Prolog engine;
 	//teorie logiche da inserire
 	Theory th1, th2, th3; 
 	
 	
 	/**
 	 *Impostazione suite dei test
 	 */ 	  	
 	public static Test suite(){
 		return new TestSuite(TestStdUse.class);
 	}// Fine suite per il test
 	
 	
 	/**
 	 *Metodo di setUp richiamato ad ogni test per avere ambiente pulito
 	 */
 	public void setUp() {
 		
 		engine = new Prolog();
 		
 		try{
 			th1= new Theory("fatt(0,1).\n"+
 						"fatt(N,Y) :- N>0, N1 is N-1, fatt(N1,Y1), Y is N*Y1.","Kore");
 			th2= new Theory("uni(X):- X is 2.","Are");
 			th3= new Theory(new java.io.FileInputStream("alice/tuprolog/fatty.pl"), "Sore");
 		
 			engine.addTheory(th1);
 			engine.addTheory(th2);
 		
 		}catch (Exception e){
 			
 			fail("" + e);//LCG
 			
 		}
 		
 	}//fine setUp
 	
 	
 	/**
 	 *metodo per testare l'aggiunta di più teorie
 	 */
 	public void testAddTheory() throws InvalidTheoryException {//LCG
 		
		engine.addTheory(th3);
	
		int ikutsu = engine.getTheoryNames().length;
	
		assertTrue("Numero delle teorie correntemente attive", ikutsu==4);
 				
 	}//fine testAddTheory
 	
 	
 	
 	/**
 	 *metodo per testare la rimozione di una teoria
 	 */
 	public void testRemoveTheory() throws InvalidTheoryException {//LCG
 		
		engine.removeTheory("Kore");
	
		int ikutsu = engine.getTheoryNames().length;
	
		String[] lista = engine.getTheoryNames();
	
		assertTrue("Rimozione di una teoria", (lista[0].equals("default") 
												&& lista[1].equals(th2.getName()) 
												&& ikutsu==2));

 	}//fine testRemoveTheory
 	
 	
 	/**
 	 *metodo per testare il settaggio di un'unica teoria
 	 */
 	public void testSetTheory() throws InvalidTheoryException {//LCG
 		
		engine.setTheory(th3);
		
		int ikutsu = engine.getTheoryNames().length;
		
		String nometeoria = engine.getTheoryNames()[0];
		
		assertTrue("Settaggio unica teoria", (ikutsu==1 && nometeoria.equals(th3.getName())));

 	} //fine testSetTheory
 	
 	
 	/**
 	 *metodo per testare il recupero corretto dei nomi delle teorie attive
 	 */
 	public void testgetTheoryNames(){
 		
 		String[] lista = engine.getTheoryNames();
 		
 		assertTrue("Recupero dei nomi", (lista[0].equals("default") 
 										&& lista[1].equals(th1.getName()) 
 										&& lista[2].equals(th2.getName())));
 	
 	}
 	//fine getTheoryNames()
 	 
 	
	/**
	 *metodo per testare che ogni teoria è davvero attiva nel sistema.
	 */
	public void testSolve() throws PrologException {//LCG
		
		SolveInfo info = engine.solve("fatt(4,K).");
		assertTrue("Soluzione del fattoriale",info.isSuccess());
				
		info = engine.solve("uni(3).");
		assertTrue("Soluzione dell'identità",(!info.isSuccess()));
	
	}//fine testSolve()
 	 
 	 
 	
 	
 	/**
 	 *Main per invocare l'esecuzione di JUNIT
 	 */ 	
 	public static void main(String args[]){
				
		junit.swingui.TestRunner.run(TestStdUse.class);
	}//Fine del main con l'invocazioe del comando di test
 
 	
 }//fine classe di test
 