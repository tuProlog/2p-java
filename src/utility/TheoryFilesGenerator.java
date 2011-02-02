/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author michael
 */
public class TheoryFilesGenerator {
	private static final int N=100;
	public static void main(String[] args){
		PrintStream p = null;
		try {
			p = new PrintStream(new File("/home/michael/Desktop/th" + N +".pl"));

			for(int i=0; i<N; i++){
				for(int j=0; j<N; j++){
					p.println("p(" + i + "," + j + ").");
				}
			}


		} catch (FileNotFoundException ex) {
			Logger.getLogger(
				TheoryFilesGenerator.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if(p != null)
				p.close();

		}
	}
}
