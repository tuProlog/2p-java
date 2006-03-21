/**
 *Classe di test per l'uso avanzato.
 *Si testa la gestione delle teorie doppie.
 *
 *@author ENanetti
 */

 //LCG
 package alice.tuprolog;
 
 import junit.framework.*;
 
 
 
 public class TestAdvUse extends TestCase{
 	
 	//motore prolog
 	Prolog engine;
 	//teorie logiche da inserire
 	Theory th1, th2, th3; 
 	
 	
 	
 	/**
 	 *Impostazione suite dei test
 	 */ 	  	
 	public static Test suite(){
 		return new TestSuite(TestAdvUse.class);
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
 	 *metodo per testare il recupero di più teorie col nome uguale dal sistema
 	 */ 	
 	public void testMultiGet(){
 		
 		try{
 		
 			Theory[] received = engine.getTheories("Koremo");
 			
 			assertTrue("teorie estratte con nome Koremo", (received.length == 2 &&
 															th1.equals(received[0]) &&
 															th3.equals(received[1])));
 		}catch (InvalidTheoryException ite){
 			}
 	
 	}//fine multiget


 	/**
 	 *Metodo per testare il recupero delle posizioni di più teorie con lo stesso nome
 	 */
 	public void testMultiPos(){
 		try{
 			
 			int[] getPos = engine.getTheoryPos("Koremo");
 			
 			assertTrue("Posizione teorie multiple", (getPos.length == 2 &&
 													(getPos[0] == 1) &&
 													(getPos[1] == 3))); 			
 			
 		}catch(Exception e){}
 		
 	}
 	//fine testMultiPos
 	
 	
 	/**
 	 *Metodo per testare rimozione con teorie multiple omonime, toglie l'ultima.
 	 */
 	public void testMultiRemove(){
 		
 		try{
 			
 			engine.removeTheory("Koremo");
 			
 			//recupero le teorie con lo stesso nome, suppongo che sia solo 1 rimasta
 			Theory[] received = engine.getTheories("Koremo");
 			
 			assertTrue("Rimozione in ambiente multiplo", (received.length == 1 &&
 															received[0].equals(th1)));
			
			//tentativo di risoluzione, test su DB clausole
			SolveInfo info = engine.solve("uni(3)."); 															
			assertTrue("Risultato insesistente", !info.isSuccess());
 															
 		}catch(Exception e){}
 		
 	}//fine testMultiRemove
 	

 	/**
 	 *metodo per testare il funzionamento standard della th default
 	 */
 	public void testMultiDefault(){
 		
 		try{
 			//impostazione teoria senza nome esplicito
 			Theory defTh = new Theory("double(X,Y):- Y is 2*X.");
 			engine.addTheory(defTh);
 			
 			
 			Theory[] received = engine.getTheories("default"); 			
 			
 			assertTrue("Gestione default", (received.length == 2 &&
 											received[0].getName().equals("default") &&
 											defTh.equals(received[1])));
 											
			//anche le clausole della nuova default sono inserite nel DB e valide
 			SolveInfo info = engine.solve("double(5,X).");
 			assertTrue("Risoluzione secondo default", (info.isSuccess()));
 			System.out.println("Soluzione ottenuta: "+info.getSolution().toString());								
			 											
 		}catch(Exception e){}
 		
 	}
 	//fine testDefault
 	
 	
 	
 	/**
 	 *Main per invocare l'esecuzione di JUNIT
 	 */ 	
 	public static void main(String args[]){
				
		junit.swingui.TestRunner.run(TestAdvUse.class);
	}//Fine del main con l'invocazioe del comando di test
	
 }//fine classe