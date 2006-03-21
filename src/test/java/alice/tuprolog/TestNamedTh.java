/**
 *Classe di test per usare JUNIT.
 *Faccio i test per vedere il funz corretto dei nomi delle teorie
 *
 *@author ENanetti
 */

//LCG
 package alice.tuprolog;
 
 import junit.framework.*;
 
 public class TestNamedTh extends TestCase{
 	
 	Theory Th;
 	String nome;
 	
 	
 	public static Test suite(){
 		return new TestSuite(TestNamedTh.class);
 	}// Fine suite per il test
 	
 	
 	
 	/**
 	 *metodo per il test del funzionamento di dafult
 	 */ 	
 	public void testDefault(){
 		try{
 			Th = new Theory("");
 		
 			nome = Th.getName();
 		
 			assertTrue("Set nome di default per teoria anonima", nome.equals("default"));
 		}catch (InvalidTheoryException ite){
 				
 		}
 	}//fine testDefault
 	
 	
 	
 	/**
 	 *metodo per il settaggio del nome
 	 */
 	public void testName(){
 		nome= new String("NomeTeoria");
 		
 		try{
 			Th = new Theory("",nome);
 		}catch (InvalidTheoryException ite){ }
 		
 		assertTrue("Recupero nome teoria", Th.getName().equals(nome));
 		
 	}//fine testName 	
 	


 	/**
 	 *metodo per il controllo della concatenazione tra teorie
 	 */
 	public void testWrongAppend(){
 		nome= new String("NomeTeoria");
		
		//cerca di appendere due teorie con nomi diversi, deve fallire
 		try{
 			Theory Th1 = new Theory("");
 			Th = new Theory("",nome);
 			Th.append(Th1);
 			Assert.fail();
 		}catch (InvalidTheoryException ite){ }
	
 	}//fine testName 
 	
 	
 	
 	/**
 	 *Main per invocare l'esecuzione di JUNIT
 	 */ 	
 	public static void main(String args[]){
				
		junit.swingui.TestRunner.run(TestNamedTh.class);
	}//Fine del main con l'invocazioe del comando di test
	
 	
 }//fine classe di test