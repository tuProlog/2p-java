package alice.tuprologx.ide;

import alice.tuprolog.*;
import alice.tuprolog.event.*;
import alice.tuprologx.ide.EngineThread;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ConsoleManager
    implements QueryListener, Console, PropertyChangeListener
{
    /**
	 * @uml.property  name="dialog"
	 * @uml.associationEnd  
	 */
    private ConsoleDialog dialog;
    /**
	 * @uml.property  name="inputField"
	 * @uml.associationEnd  
	 */
    private InputField inputField;
    /**
	 * @uml.property  name="engine"
	 * @uml.associationEnd  
	 */
    private Prolog engine;
    //private PJProlog pjengine;
    /**
	 * @uml.property  name="ide"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private IDE ide;
    /**
	 * @uml.property  name="queryEventList"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="alice.tuprolog.event.QueryEvent"
	 */
    private ArrayList<QueryEvent> queryEventList;
    /**
	 * @uml.property  name="solveType"
	 */
    private int solveType;
    /**
	 * @uml.property  name="timeFromBeginSolving"
	 */
    private long timeFromBeginSolving=-1;
    /**
	 * @uml.property  name="millsStopEngine"
	 */
    private int millsStopEngine;
    /* listeners registrated for virtual machine information to display events */
    /**
	 * @uml.property  name="informationToDisplayListeners"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="alice.tuprologx.ide.InformationToDisplayListener"
	 */
    private ArrayList<InformationToDisplayListener> informationToDisplayListeners;

    public ConsoleManager(IDE ide)
    {
        this.ide = ide;
        informationToDisplayListeners = new ArrayList<InformationToDisplayListener>();
        queryEventList = new ArrayList<QueryEvent>();
    }

    /**
	 * @param dialog
	 * @uml.property  name="dialog"
	 */
    public void setDialog(ConsoleDialog dialog){this.dialog = dialog;}

    /**
	 * @param solveType
	 * @uml.property  name="solveType"
	 */
    public void setSolveType(int solveType){this.solveType = solveType;}

    /**
	 * @return
	 * @uml.property  name="solveType"
	 */
    public int getSolveType(){return solveType;}

    /**
	 * @param engine
	 * @uml.property  name="engine"
	 */
    public void setEngine(Prolog engine){this.engine = engine;}

    /**
	 * @param inputField
	 * @uml.property  name="inputField"
	 */
    public void setInputField(InputField inputField){this.inputField = inputField;}

    /**
	 * @return
	 * @uml.property  name="dialog"
	 */
    public ConsoleDialog getDialog(){return dialog;}

    public void solve()
    {
        dialog.clearResults();
        if (!getGoal().equals(""))
        {
            if (!ide.isFeededTheory())
            {
                try
                {
                    engine.setTheory(new Theory(ide.getEditorContent()));
                    ide.setFeededTheory(true);
                }
                catch (InvalidTheoryException e)
                {
                    dialog.setStatusMessage("Error setting theory: Syntax Error at/before line " + e.line);
                    return;
                }
            }
            dialog.enableStopButton(true);
            try
            {
                ide.enableTheoryCommands(false);
                dialog.setStatusMessage("Solving...");
                new EngineThread(engine, getGoal(), this).start();
            }
            catch (Exception e)
            {
                dialog.setStatusMessage("Error: " + e);
            }
        
        }
        else//if (getGoal.equals(""))
            /**
             * without this setStatusMessage if getGoal is void still remains
             * status message of the precedent solve operation
             */ 
            dialog.setStatusMessage("Ready.");
    }

    public void solveAll()
    {
        dialog.enableStopButton(true);
        dialog.enableSolutionCommands(false);
        timeFromBeginSolving = System.currentTimeMillis();
        solve();
    }

    // method of QueryListener interface
    public void newQueryResultAvailable(QueryEvent event)
    {
        queryEventList.add(event);
        boolean display = false;
        if (getSolveType()==1)//if there is information about a solveAll operation
        {
            if (System.currentTimeMillis() - timeFromBeginSolving > millsStopEngine && millsStopEngine!=0)
            {
                stopEngine();
                dialog.setStatusMessage("Time overflow: "+(System.currentTimeMillis() - timeFromBeginSolving));
                display = true;
            }
            else
            {
                if(engine.hasOpenAlternatives())
                {
                    getNextSolution();
                }
                else
                {
                    stopEngine();
                    display = true;
                    setStatusMessage("Ready.");
                }
            }
        }
        if (getSolveType()==0)//if there is information about a solve operation
        {
            display = true;
            setStatusMessage("Yes.");
        }
        if (display)
        {
            notifyInformationToDisplayEvent(new InformationToDisplayEvent(engine, queryEventList, getSolveType()));
            queryEventList.clear();
        }
    }

    //methods of Console interface
    public boolean hasOpenAlternatives()
    {
        return engine.hasOpenAlternatives();
    }
    public void enableTheoryCommands(boolean flag)
    {
        ide.enableTheoryCommands(flag);
    }
    public void getNextSolution()
    {
        new EngineThread(engine).start();
    }
    public void acceptSolution()
    {
        engine.solveEnd();
    }
    public void stopEngine()
    {
        // stop the tuProlog engine
        engine.solveHalt();
    }
    public String getGoal()
    {
        return inputField.getGoal();
    }

    public void addInformationToDisplayListener(InformationToDisplayListener listener)
    {
        informationToDisplayListeners.add(listener);
    }
    public void removeInformationToDisplayListener(InformationToDisplayListener listener)
    {
        informationToDisplayListeners.remove(listener);
    }
    public void notifyInformationToDisplayEvent(InformationToDisplayEvent e)
    {
        for(InformationToDisplayListener itdl: informationToDisplayListeners){
            itdl.onInformation(e);
        }
    }

    public void enableStopButton(boolean flag)
    {
        dialog.enableStopButton(flag);
    }
    public void setStatusMessage(String message)
    {
        dialog.setStatusMessage(message);
    }

    public void propertyChange(PropertyChangeEvent event) {
        String propertyName = event.getPropertyName();
        if (propertyName.equals("millsStopEngine"))
        {
            millsStopEngine=((Integer)event.getNewValue()).intValue();
        }
        /*Castagna 06/2011*/  
        if (propertyName.equals("notifyExceptionEvent"))
        {
        	engine.setException(((Boolean)event.getNewValue()).booleanValue());
        }
        /**/
    }

}
