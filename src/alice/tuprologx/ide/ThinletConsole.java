/* * tuProlog - Copyright (C) 2001-2004  aliCE team at deis.unibo.it * * This library is free software; you can redistribute it and/or * modify it under the terms of the GNU Lesser General Public * License as published by the Free Software Foundation; either * version 2.1 of the License, or (at your option) any later version. * * This library is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU * Lesser General Public License for more details. * * You should have received a copy of the GNU Lesser General Public * License along with this library; if not, write to the Free Software * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA */package alice.tuprologx.ide;
import alice.tuprolog.*;
import alice.util.thinlet.*;

import java.beans.*;
import java.util.Observable;
import java.util.Observer;
/** * A complete console for the tuProlog engine, featuring an input field for * submitting queries, and two areas for displaying solutions to a goal and * output text due to particular predicates (e.g. <code>write/1</code>). *  * @author	<a href="mailto:giulio.piancastelli@studio.unibo.it">Giulio Piancastelli</a> * @author	<a href="mailto:simone.pellegrini@studio.unibo.it">Simone Pellegrini</a> * @version 1.0 - 13-nov-02 */
public class ThinletConsole extends Thinlet implements OutputListener, Observer {
	/** The Prolog engine referenced by the console. */	private EngineThread engine;
	/**	 * The IDE the console belongs to, necessary to manage theory-related	 * commands and to enable or disable them when a goal's resolution has been	 * started.	 */	private IDE ide;
	/** Used to store informations on a goal's solution. */	//private SolveInfo info;
	/** The input field used to get queries. */	private InputField inputField;
	/** A message describing the status of the console. */	private String statusMessage;
	/** Used for components interested in changes of console's properties. */	private PropertyChangeSupport propertyChangeSupport;
	public ThinletConsole(IDE ide) {		try {			add(parse("xml/ThinletConsole.xml"));		} catch (Exception e) {			e.printStackTrace();		}		this.ide = ide;		propertyChangeSupport = new PropertyChangeSupport(this);	}
	/**	 * Get the goal in the query field, feed a Prolog engine with it, and	 * display the solution provided.	 */	public void solve() {		if (!ide.isFeededTheory()){			try {				// something better? i.e. must I go through console?				getEngine().setTheory(						new Theory(ide.getEditorContent()));			} catch (InvalidTheoryException e) {				showSolution("error reading theory.");				// something better? i.e. must I go through console?				setStatusMessage("Error setting theory: Syntax Error at/before line "								+ e.line);			} catch (EngineRunningException e) {				setStatusMessage("Error setting theory: " + e.getMessage());			}		}		enableStopButton(true);		getEngine().addObserver(this);		getEngine().setGoal(getGoal());		new Thread(getEngine()).start();		this.setStatusMessage("Solving...");	}
	/**	 * Get the Prolog engine referenced by the console.	 * 	 * @return The Prolog engine referenced by the console.	 */	public EngineThread getEngine() {		return engine;	}
	/**	 * Set the Prolog engine referenced by the console.	 * 	 * @param engine an <code>alice.tuprolog.Prolog</code> engine.	 */	public void setEngine(EngineThread engine) {		this.engine = engine;	}
	/**	 * Set the input field used by the console to retrieve queries.	 * 	 * @param inputField the input field we want the console to use.
	 */
	public void setInputField(InputField inputField) {
		this.inputField = inputField;
		// hopefully this will not be needed soon...
		this.inputField.setConsole(this);
	}
	/**	 * Set the console status.	 * 	 * @param message The message describing the new status of the console.
	 */
	public void setStatusMessage(String message) {
		String oldStatusMessage = getStatusMessage();
		statusMessage = message;
		propertyChangeSupport.firePropertyChange("StatusMessage",				oldStatusMessage, statusMessage);	}
	/**	 * Get the console status as a <code>java.lang.String</code> message.	 * 	 * @return the current status of the console as a <code>java.lang.String</code> message.
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {		propertyChangeSupport.addPropertyChangeListener(listener);	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {		propertyChangeSupport.removePropertyChangeListener(listener);	}
	/**	 * Get the goal entered in the input field.	 * 	 * @return The goal entered in the input field.	 */	protected String getGoal() {		return inputField.getGoal();	}
	/**	 * Show a solution to the current goal in the apposite solution area.	 * 	 * @param solution The solution to be shown.
	 */	protected void showSolution(String solution) {		Object solutionArea = find("solutionArea");		setString(solutionArea, "text", solution);	}
	/**	 * Enable or disable solution-related buttons.	 * 	 * @param flag true if the buttons have to be enabled, false otherwise.
	 */
	protected void enableSolutionCommands(boolean flag) {		Object nextButton = find("nextButton");		setBoolean(nextButton, "enabled", flag);		Object acceptButton = find("acceptButton");		setBoolean(acceptButton, "enabled", flag);	}
	protected void enableStopButton(boolean flag) {		Object stopButton = find("stopButton");		setBoolean(stopButton, "enabled", flag);	}
	/**	 * Enable or disable the possibility of asking for goals to be solved.	 * 	 * @param flag true if the query device has to be enabled, false otherwise.
	 */
	protected void enableSolveCommands(boolean flag) {		inputField.enableSolveCommands(flag);	}
	/**	 * Try to solve the next open alternative for the current goal.	 */	public void getNextSolution() {		enableStopButton(true);		enableSolutionCommands(false);		this.showSolution("");		this.setStatusMessage("Solving...");		engine.nextSolution();	}
	/**	 * Accept the found solution to the current goal, showing it on the console	 * and closing all the possible open alternatives.	 */
	public void acceptSolution() {		enableStopButton(false);		enableSolutionCommands(false);		enableSolveCommands(true);		ide.enableTheoryCommands(true);		engine.acceptSolution();	}
	public void stopEngine() {		// stop the tuProlog engine		engine.stopExcution();		// disable button		enableSolutionCommands(false);		enableStopButton(false);	}
	/* Implementing OutputListener */	public void onOutput(OutputEvent event) {		System.out.println(event.getMsg());		Object outputArea = find("outputArea");		String output = getString(outputArea, "text");		setString(outputArea, "text", output + event.getMsg());	}
	public void update(Observable arg0, Object arg1) {		enableStopButton(false);		enableSolutionCommands(true);		try {			EngineStatus status = (EngineStatus) arg1;			if (status.getError() != null) {				this.showSolution("Error: " + status.getError());				this.setStatusMessage("Ready.");				enableSolutionCommands(false);			}			else {				if (status.getInfo() != null) {					if (status.getInfo().isSuccess()) {						String binds = status.getInfo().toString();						String append = "";						if(!status.hasAlternatives()){							enableSolutionCommands(false);							if(status.isAccepted())							    this.setStatusMessage("Ready.");							else							    this.setStatusMessage("No more solutions found.");						} else {							enableSolutionCommands(true);							if(status.isFirstSolution())							    this.setStatusMessage("Found a solution (open alternatives can be explored).");							else							    this.setStatusMessage("Found another solution (open alternatives can be explored).");							append="?";						}												if(binds.equals(""))							this.showSolution("yes.\nSolution: " + status.getInfo().getSolution() + append);						else							this.showSolution("yes.\nSolution: " + binds + "\n" + status.getInfo().getSolution() + append);													}else{						this.showSolution("no.");						enableSolutionCommands(false);						this.setStatusMessage("Ready.");					}				}			}					} catch (NoSolutionException e) {			this.showSolution("generic error. " + e.getMessage());			this.setStatusMessage("Ready.");		}	}
} // end ThinletConsole class