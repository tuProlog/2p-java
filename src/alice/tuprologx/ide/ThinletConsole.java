/*
 * tuProlog - Copyright (C) 2001-2003  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuprologx.ide;

import alice.tuprolog.*;
import alice.util.thinlet.*;

import java.beans.*;

/**
 * A complete console for the tuProlog engine, featuring an input field for
 * submitting queries, and two areas for displaying solutions to a goal and
 * output text due to particular predicates (e.g. <code>write/1</code>).
 * 
 * @author	<a href="mailto:giulio.piancastelli@studio.unibo.it">Giulio Piancastelli</a>
 * @version	1.0 - 13-nov-02
 */

public class ThinletConsole extends Thinlet implements OutputListener {

	/** The Prolog engine referenced by the console. */
    private Prolog engine;
    /**
     * The IDE the console belongs to, necessary to manage theory-related
     * commands and to enable or disable them when a goal's resolution has
     * been started.
     */
    private IDE ide;
    /** Used to store informations on a goal's solution. */
	private SolveInfo info;
    /** The input field used to get queries. */
    private InputField inputField;
    /** A message describing the status of the console. */
    private String statusMessage;
    /** Used for components interested in changes of console's properties. */
    private PropertyChangeSupport propertyChangeSupport;

	public ThinletConsole(IDE ide) {
		try {
			add(parse("ThinletConsole.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
        this.ide = ide;
        propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Get the goal in the query field, feed a Prolog engine with
	 * it, and display the solution provided.
	 */
	public void solve() {
		Thread t = new ProcessInput(this);
		t.start();
	}

    /**
     * Get the Prolog engine referenced by the console.
     *
     * @return The Prolog engine referenced by the console.
     */
	public Prolog getEngine() {
		return engine;
	}

    /**
     * Set the Prolog engine referenced by the console.
     *
     * @param engine an <code>alice.tuprolog.Prolog</code> engine.
     */
	public void setEngine(Prolog engine) {
		this.engine = engine;
		engine.addOutputListener(this);
	}

    /**
     * Set the input field used by the console to retrieve queries.
     *
     * @param inputField the input field we want the console to use.
     */
    public void setInputField(InputField inputField) {
        this.inputField = inputField;
        // hopefully this will not be needed soon...
        this.inputField.setConsole(this);
    }

    /**
     * Set the console status.
     *
     * @param message The message describing the new status of the console.
     */
    public void setStatusMessage(String message) {
        String oldStatusMessage = getStatusMessage();
        statusMessage = message;
        propertyChangeSupport.firePropertyChange("StatusMessage", oldStatusMessage, statusMessage);
    }

    /**
     * Get the console status as a <code>java.lang.String</code> message.
     *
     * @return the current status of the console as a <code>java.lang.String</code>
     * message.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Get the goal entered in the input field.
     *
     * @return The goal entered in the input field.
     */
	protected String getGoal() {
        return inputField.getGoal();
	}

    /**
     * Show a solution to the current goal in the apposite solution area.
     *
     * @param solution The solution to be shown.
     */
	protected void showSolution(String solution) {
		Object solutionArea = find("solutionArea");
		setString(solutionArea, "text", solution);
	}

    /**
     * Enable or disable solution-related buttons.
     *
     * @param flag true if the buttons have to be enabled, false otherwise.
     */
	protected void enableSolutionCommands(boolean flag) {
		Object nextButton = find("nextButton");
		setBoolean(nextButton, "enabled", flag);
		Object acceptButton = find("acceptButton");
		setBoolean(acceptButton, "enabled", flag);
	}

    /**
     * Enable or disable the possibility of asking for goals to be solved.
     *
     * @param flag true if the query device has to be enabled, false otherwise.
     */
	protected void enableSolveCommands(boolean flag) {
        inputField.enableSolveCommands(flag);
	}

    /**
     * Try to solve the next open alternative for the current goal.
     */
	public void getNextSolution() {
		try {
            setStatusMessage("Solving...");
            info = getEngine().solveNext();
			if (getEngine().isHalted()) {
                // exitForm(null);
			} else
                if (!info.isSuccess()) {
                    showSolution("no.");
                    // updateSlot(inputGoal.getText(),"no.","");
                    enableSolutionCommands(false);
                    enableSolveCommands(true);
                    ide.enableTheoryCommands(true);
                    setStatusMessage("Ready.");
                } else {
                    setStatusMessage("Found another solution (open alternatives can be explored).");
                    String binds = info.toString();
                    showSolution(binds + "\n" + getEngine().toString(info.getSolution()) + "\n?");
                }
        } catch (NoMoreSolutionException e) {
            showSolution("no.");
            setStatusMessage("Ready.");
            // updateSlot(inputGoal.getText(),"no.","");
            enableSolutionCommands(false);
            enableSolveCommands(true);
            ide.enableTheoryCommands(true);
		} catch (Exception e) {
            showSolution("General Failure.");
            setStatusMessage("Ready.");
			enableSolutionCommands(false);
			enableSolveCommands(true);
            ide.enableTheoryCommands(true);
        }
    }

    /**
     * Accept the found solution to the current goal, showing it on the console
     * and closing all the possible open alternatives.
     */
	public void acceptSolution() {
		try {
            String binds = info.toString();
            if (binds.equals(""))
                showSolution("yes.\nSolution: " + getEngine().toString(info.getSolution()));
            else
                showSolution("yes.\n" + binds + "\nSolution: " + getEngine().toString(info.getSolution()));
            // outputScrollPane.getHorizontalScrollBar().setValue(0);
            // updateSlot(inputGoal.getText(), "Solution: " + engine.toString(info.getSolution()), binds);
            setStatusMessage("Ready.");
            enableSolutionCommands(false);
            enableSolveCommands(true);
            ide.enableTheoryCommands(true);
        } catch (Exception e) {
            // Do nothing...
		}
	}

	/* Implementing OutputListener */

	public void onOutput(OutputEvent event) {
		Object outputArea = find("outputArea");
		String output = getString(outputArea, "text");
		setString(outputArea, "text", output + event.getMsg());
    }

    /**
     * A separate <code>java.lang.Thread</code> spawn to find the first solution
     * to a given goal.
     */
	class ProcessInput extends Thread {

		private ThinletConsole console;

		ProcessInput(ThinletConsole t) {
			console = t;
		}

		public void run(){
			String goal = console.getGoal();
			try {
                // Feed the engine with the current editor contents, if necessary
                if (!ide.isFeededTheory())
                    // something better? i.e. must I go through console?
                    console.getEngine().setTheory(new Theory(ide.getEditorContent()));

                setStatusMessage("Solving...");
				info = console.getEngine().solve(goal);
				if (console.getEngine().isHalted()) {
					// exitForm(null);
				} else
					if (!info.isSuccess()) {
						console.showSolution("no.");
						// updateSlot(inputGoal.getText(),"no.","");
                        console.setStatusMessage("Ready.");
					} else
						if (!console.getEngine().hasOpenAlternatives()) {
							String binds = info.toString();
							if (binds.equals(""))
								console.showSolution("yes.\nSolution: " + info.getSolution().toString());
							else
								console.showSolution("yes.\n" + binds + "\nSolution: " + info.getSolution().toString());
                            console.setStatusMessage("Ready.");
							// console.updateSlot(console.inputGoal.getText(), "Solution: " + engine.toString(info.getSolution()), binds);
							// console.outputScrollPane.getHorizontalScrollBar().setValue(0);
						} else {
                            console.setStatusMessage("Found a solution (open alternatives can be explored).");
							String binds = info.toString();
							console.showSolution(binds + "\n" + info.getSolution().toString() + "\n?");
							// console.outputScrollPane.getHorizontalScrollBar().setValue(0);
							console.enableSolutionCommands(true);
							console.enableSolveCommands(false);
                            ide.enableTheoryCommands(false);
						}
			} catch (MalformedGoalException e){
				console.showSolution("syntax error.");
                console.setStatusMessage("Ready.");
            } catch (InvalidTheoryException e) {
                console.showSolution("error reading theory.");
                // something better? i.e. must I go through console?
                console.setStatusMessage("Error setting theory: Syntax Error at/before line " + e.line);
			} catch (Exception e){
				console.showSolution("generic error. "+ e.toString());
				console.setStatusMessage("Ready.");
			}
		}

	} // end ProcessInput class

} // end ThinletConsole class