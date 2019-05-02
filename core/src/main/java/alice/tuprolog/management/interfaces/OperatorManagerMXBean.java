package alice.tuprolog.management.interfaces;

//Alberto
public interface OperatorManagerMXBean {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	void reset();
	String fetchAllOperators();
	void opNew(String name, String type, int prio);
	int opPrio(String name, String type);
	int opNext(int prio);
	
}
