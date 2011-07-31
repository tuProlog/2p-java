/*
 *
 *
 */
package alice.tuprolog;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;



/**
 * @author Alex Benini
 *
 * Core engine
 */
@SuppressWarnings("serial")
public class EngineManager implements java.io.Serializable {
    
    /**
	 * @uml.property  name="mediator"
	 * @uml.associationEnd  inverse="engineManager:alice.tuprolog.Prolog"
	 */
    private Prolog   mediator;
    /**
	 * @uml.property  name="theoryManager"
	 * @uml.associationEnd  
	 */
    private TheoryManager    theoryManager;
    /**
	 * @uml.property  name="primitiveManager"
	 * @uml.associationEnd  
	 */
    private PrimitiveManager primitiveManager;
    /**
	 * @uml.property  name="libraryManager"
	 * @uml.associationEnd  
	 */
    private LibraryManager   libraryManager;
    
    
    /* Current environment */
    /**
	 * @uml.property  name="env"
	 * @uml.associationEnd  
	 */
    Engine env;
    /* Last environment used */
    /**
	 * @uml.property  name="last_env"
	 * @uml.associationEnd  
	 */
    private Engine last_env;
    /* Stack environments of nidicate solving */
    /**
	 * @uml.property  name="stackEnv"
	 * @uml.associationEnd  multiplicity="(0 -1)" inverse="manager:alice.tuprolog.Engine"
	 */
    private LinkedList<Engine> stackEnv = new LinkedList<Engine>();
    
    /**
	 * @uml.property  name="sinfo"
	 * @uml.associationEnd  
	 */
    private SolveInfo sinfo;
    
    
    /**
	 * States
	 * @uml.property  name="iNIT"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State INIT;
    /**
	 * @uml.property  name="gOAL_EVALUATION"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State GOAL_EVALUATION;
    /**
	 * @uml.property  name="eXCEPTION"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="c:alice.tuprolog.State"
	 */
    final State EXCEPTION;
    /**
	 * @uml.property  name="rULE_SELECTION"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State RULE_SELECTION;
    /**
	 * @uml.property  name="gOAL_SELECTION"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State GOAL_SELECTION;
    /**
	 * @uml.property  name="bACKTRACK"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State BACKTRACK;
    /**
	 * @uml.property  name="eND_FALSE"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State END_FALSE;
    /**
	 * @uml.property  name="eND_TRUE"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State END_TRUE;
    /**
	 * @uml.property  name="eND_TRUE_CP"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State END_TRUE_CP;
    /**
	 * @uml.property  name="eND_HALT"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    final State END_HALT;
    
    public static final int HALT    = -1;
    public static final int FALSE   =  0;
    public static final int TRUE    =  1;
    public static final int TRUE_CP =  2;
    
    
    public EngineManager() {
        /* Istanzio gli stati */
        INIT            = new StateInit(this);
        GOAL_EVALUATION = new StateGoalEvaluation(this);
        EXCEPTION        = new StateException(this);
        RULE_SELECTION  = new StateRuleSelection(this);
        GOAL_SELECTION  = new StateGoalSelection(this);
        BACKTRACK       = new StateBacktrack(this);
        END_FALSE       = new StateEnd(this,FALSE);
        END_TRUE        = new StateEnd(this,TRUE);
        END_TRUE_CP     = new StateEnd(this,TRUE_CP);
        END_HALT        = new StateEnd(this,HALT);
    }
    
    
    /**
     * Config this Manager
     */
    void initialize(Prolog vm) {
        mediator            = vm;
        theoryManager    = vm.getTheoryManager();
        primitiveManager = vm.getPrimitiveManager();
        libraryManager   = vm.getLibraryManager();
    }
    
    void spy(String action, Engine env) {
        mediator.spy(action,env);
    }
    
    void warn(String message) {
        mediator.warn(message);
    }
    
    /*Castagna 06/2011*/
	void exception(String message) {
		mediator.exception(message);
	}
	/**/
    
    /**
     *  Solves a query
     *
     * @param g the term representing the goal to be demonstrated
     * @return the result of the demonstration
     * @see SolveInfo
     **/
    public SolveInfo solve(Term query) {
        try {
            query.resolveTerm();
            
            libraryManager.onSolveBegin(query);
            primitiveManager.identifyPredicate(query);
//            theoryManager.transBegin();
            
            freeze();
            env = new Engine(this, query);
            StateEnd result = env.run();
            defreeze();
            
            sinfo = new SolveInfo(
                    query,
                    result.getResultGoal(),
                    result.getResultDemo(),
                    result.getResultVars()
            );
            if (!sinfo.hasOpenAlternatives()) solveEnd();
            return sinfo;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new SolveInfo(query);
        }
    }
    
    /**
     * Gets next solution
     *
     * @return the result of the demonstration
     * @throws NoMoreSolutionException if no more solutions are present
     * @see SolveInfo
     **/
    public synchronized SolveInfo solveNext() throws NoMoreSolutionException {
        if (hasOpenAlternatives()) {
            refreeze();
            env.nextState = BACKTRACK;
            StateEnd result = env.run();
            defreeze();
            sinfo = new SolveInfo(
                    env.query,
                    result.getResultGoal(),
                    result.getResultDemo(),
                    result.getResultVars()
            );
            if (!sinfo.hasOpenAlternatives()) solveEnd();
            return sinfo;
        } else
            throw new NoMoreSolutionException();
    }
    
    /**
     * Halts current solve computation
     */
    public void solveHalt() {
        env.mustStop();
    }
    
    /**
     * Accepts current solution
     */
    public void solveEnd() {
//        theoryManager.transEnd(sinfo.isSuccess());
//        theoryManager.optimize();
        libraryManager.onSolveEnd();
    }
    
    
    private void freeze() {
        if(env==null) return;
        try {
            if (stackEnv.getLast()==env) return;
        } catch(NoSuchElementException e) {}
        stackEnv.addLast(env);
    }
    
    private void refreeze() {
        freeze();
        env = last_env;            
    }
    
    private void defreeze() {
        last_env = env;
        if (stackEnv.isEmpty()) return;
        env = (Engine)(stackEnv.removeLast());
    }
    
    
    /*
     * Utility functions for Finite State Machine
     */
    
    List<ClauseInfo> find(Term t) {
        return theoryManager.find(t);
    }
    
    void identify(Term t) {
        primitiveManager.identifyPredicate(t);
    }
    
//    void saveLastTheoryStatus() {
//        theoryManager.transFreeze();
//    }
    
    void pushSubGoal(SubGoalTree goals) {
        env.currentContext.goalsToEval.pushSubGoal(goals);
    }
    
    
    void cut() {
        env.choicePointSelector.cut(env.currentContext.choicePointAfterCut);
    }
    
    
    ExecutionContext getCurrentContext() {
        return (env==null)? null : env.currentContext;
    }
    
    
    /**
     * Asks for the presence of open alternatives to be explored
     * in current demostration process.
     *
     * @return true if open alternatives are present
     */
    boolean hasOpenAlternatives() {
        if (sinfo==null) return false;
        return sinfo.hasOpenAlternatives();
    }
    
    
    /**
     * Checks if the demonstration process was stopped by an halt command.
     * 
     * @return true if the demonstration was stopped
     */
    boolean isHalted() {
        if (sinfo==null) return false;
        return sinfo.isHalted();
    }
    
    
}