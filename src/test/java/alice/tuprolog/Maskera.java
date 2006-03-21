package alice.tuprolog;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class Maskera extends JPanel{
	
	//area di testo
	protected JTextArea area;
	
	
	/**
	 *Costruttore
	 */
	public Maskera(){
		super();
			
		area = new JTextArea(20,50);
		area.setEditable(false);
		JScrollPane jsp = new JScrollPane(area);
		
		add(jsp);
				
	}//fine costruttore
	
	/**
	 *metodo per scrivere MSG sull'area testo
	 */
	public void println(String msg){
		area.append(msg+"\n");
	}
	//fine println
	
	
	/**
	 *metodo per mostrare il pannello grafico
	 */
	public void showGUI(){
		
		JFrame frame =new JFrame("Default output");
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

		Container c= frame.getContentPane();
		//maskera mask = new maskera();
		
		
		c.add(this);
					
				
		frame.pack();
		frame.setVisible(true);
		
		
	}//fine showGUI	
		
}//fine classe Maskera
