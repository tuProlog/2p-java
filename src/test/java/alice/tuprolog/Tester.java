/**
 *Classe di test per vedere che cosa fa il Context
 */
 
 package alice.tuprolog;
 
 import junit.framework.*;//LCG

 
 public class Tester extends TestCase {
 
	 //LCG
	 public static void main(String[] args) throws Exception{
		 
		 new Tester().test();
		 
	 }
	 
 	//public static void main(String[] args) throws Exception{
	 public void test() {//LCG
 	
		 try {
			 
	 		Prolog engine = new Prolog();
	 		Maskera mask = new Maskera();//LCG
	 		
	 		mask.showGUI();
	 			
	 		 		
	 		Theory th = new Theory("fatt(0,1).\n"+
	 								"fatt(N,Y) :- N>0, N1 is N-1, fatt(N1,Y1), Y is N*Y1.","pippo");
	 	 	
	 	 	Theory th1 = new Theory(new java.io.FileInputStream("alice/tuprolog/fatty.pl"), "pippo");
	 	 	Theory th2 = new Theory(new java.io.FileInputStream("alice/tuprolog/prova.pl"), "test");
	 	 	Theory th3 = new Theory("uni(X):- X is 2.","pallino");
	 		
	 		
	 		mask.println("Situazione iniziale teorie: "+engine.getWholeTheory().toString()); 
	
	 		
	 		engine.addTheory(th);
	 		engine.addTheory(th1); 
	 		engine.addTheory(th2); 		
	
			engine.addTheory(th3);
			 	
	 		
	 		mask.println("Situazione attuale TOTALE teorie: "+engine.getWholeTheory().toString()); 
	
	 		mask.println("Nomi delle teorie caricate attualmente: ");
	 		String[] tmp = engine.getTheoryNames();
	 		 		
	 		for(int k=0; k<tmp.length; k++){
	 			
	 			
	 			mask.println("Teoria n "+k+": "+tmp[k]);
	 		}
	 		
	 		int[] tmppos = engine.getTheoryPos("pippo");
	 		for(int k=0; k<tmppos.length; k++){
	 			
	 			
	 			mask.println("Teoria trovata alla pos: "+tmppos[k]);
	 		}
	 		
	 		
	 		Theory[] listTh = engine.getTheories("pippo");
	 		for(int k=0; k<listTh.length; k++){
	 			
	 			
	 			mask.println("Teorie trovate dal nome: "+listTh[k].toString());
	 		}
	
	
	
	 		mask.println("\n\nTentativo di estrazione teoria singola.");
	 		
		
	 		 		
	 		SolveInfo info = engine.solve("fatt(4,K).");
	 		if (info.isSuccess()){
	 			mask.println("********* Soluzione: "+info.toString()+"\n");
	 		} else 
				mask.println("mi dispiace ma non va bene: "+info.toString());
		
	 		
	
	 		
	 		
	 		
	 		
	
	 		 			
	// 		mask.println("\nTeoria estratta da vettore: "+ prova.getName() + engine.getTheoryByName("pippo").toString()); 
	 		
	 		
	 		
	 		mask.println("Nomi delle teorie caricate attualmente: ");
	 		tmp = engine.getTheoryNames();
	 		 		
	 		for(int k=0; k<tmp.length; k++){
	 			
	 			
	 			mask.println("Teoria n "+k+": "+tmp[k]);
	 		}
	 		
	 		
	 	//	engine.removeTheory("pippo");
	 				
	 		
	 		
	 		mask.println("Situazione attuale teorie: "+engine.getWholeTheory().toString());
	 		
	 		mask.println("Nomi delle teorie caricate attualmente: ");
	 		tmp = engine.getTheoryNames();
	 		 		
	 		for(int k=0; k<tmp.length; k++){
	 			
	 			
	 			mask.println("Teoria n "+k+": "+tmp[k]);
	 		}
	 		
	 		
	 		
	 		
	 		info = engine.solve("prova.");
	 		if (info.isSuccess()){
	 			mask.println("********* Soluzione: "+info.toString()+"\n");
	 		} else 
				mask.println("mi dispiace ma non va bene: "+info.toString());
	 
	 		 		
	 		
	 		///DA QUI SOTTO
	 		
	 		//engine.removeTheory("default");
	 		
	 		//engine.addTheory(th3);
	 		
	  		info = engine.solve("provarem.");
	 		if (info.isSuccess()){
	 			mask.println("********* Soluzione: "+info.toString()+"\n");
	 		} else 
				mask.println("mi dispiace ma non va bene: "+info.toString()); 		
	 		
	 		
	 		
	 		
	 		mask.println("\n\nSituazione attuale teorie: "+engine.getWholeTheory().toString());
	 		
	 		mask.println("Nomi delle teorie caricate attualmente: ");
	 		tmp = engine.getTheoryNames();
	 		 		
	 		for(int k=0; k<tmp.length; k++){
	 			
	 			
	 			mask.println("Teoria n "+k+": "+tmp[k]);
	 		}
	 		
	 		engine.removeTheory("pallino");
	 		mask.println("\nRimozione corretta....");
	 		
	 		mask.println("Nomi delle teorie caricate attualmente: ");
	 		tmp = engine.getTheoryNames();
	 		 		
	 		for(int k=0; k<tmp.length; k++){
	 			
	 			
	 			mask.println("Teoria n "+k+": "+tmp[k]);
	 		}
	 		
	 		
	// 		engine.addTheory(th2);
	// 		mask.println("\nSituazione database dopo riINSERIMENTO: "+engine.getTheory().toString()); 
	 		
			
	//		mask.println("\nProva creazione nome interno: "+engine.getTheName(th)); 
	// 		engine.setTheory(th2);
	// 		mask.println("\nSituazione database dopo settaggio: "+engine.getTheory().toString()); 
	 		
	 		//setTheory non aggiorna il vettore delle teorie caricate (per ora)
	// 		mask.println("\nTeoria estratta da vettore: "+ prova.getName() + engine.getTheoryByName("dafile2").toString()); 
 		
		 } catch (Exception ex) {
			 
			 fail("" + ex);
			 
		 }
	
	 }//fine main
 	
}//fine classe
