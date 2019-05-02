package alice.tuprolog.management.interfaces;

//Alberto
public interface EngineManagerMXBean {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	String solve_untimed(String goal);
	String solveNext_untimed();
	String solve_timed(String goal, long maxTime);
	String solveNext_timed(long maxTime);
	String solveN_untimed(String goal, int numberSol);
	String solveN_timed(String goal, int numberSol, long maxTime);
	String solveAll_untimed(String goal);
	String solveAll_timed(String goal, long maxTime);
	
}
