package alice.tuprolog;

//import java.io.File;
//import java.io.IOException;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("serial")
public class EngineManager implements java.io.Serializable {
	
	private Prolog vm;
	private Hashtable<Integer, EngineRunner> runners;	//key: id; obj: runner
	private Hashtable<Integer, Integer> threads;	//key: pid; obj: id
	private int rootID;
	private int rootPID;
	private int baseID;
	private int basePID;
	
	private Hashtable<String, TermQueue> queues;
	private Hashtable<String, ReentrantLock> locks;

	public void initialize(Prolog vm) {
		this.vm=vm;
		runners=new Hashtable<Integer,EngineRunner>();
		threads = new Hashtable<Integer,Integer>();
		queues =new Hashtable<String, TermQueue>();
		locks = new Hashtable<String, ReentrantLock>();
		
	}
	
	public synchronized boolean threadCreate(Term threadID, Term goal) {
		if (goal == null) return false;
		if (goal instanceof Var) 
			goal = goal.getTerm();
		
		int id = runners.size()+1;
		
		EngineRunner er = new EngineRunner(id);
		er.initialize(vm);
		
		if (!vm.unify(threadID, new Int(id))) return false;
		
		er.setGoal(goal);
		addRunner(er, id);
		Thread t = new Thread(er);
		addThread(t.getId(), id);
		
		t.start();
		return true;
	}
	
	public SolveInfo join(int id) {
		EngineRunner er = findRunner(id);
		if (er==null || er.isDetached()) return null;
		/*toSPY
		 * System.out.println("Thread id "+runnerId()+" - prelevo la soluzione (join)");*/
		SolveInfo solution = er.read();
		/*toSPY
		 * System.out.println("Soluzione: "+solution);*/
		removeRunner(id);
		return solution;
	}
	
	public SolveInfo read (int id) {
		EngineRunner er = findRunner(id);
		if (er==null || er.isDetached()) return null;
		/*toSPY
		 * System.out.println("Thread id "+runnerId()+" - prelevo la soluzione (read) del thread di id: "+er.getId());
		 */
		SolveInfo solution = er.read();
		/*toSPY
		 * System.out.println("Soluzione: "+solution);
		 */
		return solution;
	}
	
	public boolean hasNext (int id){
		EngineRunner er = findRunner(id);
		if (er==null || er.isDetached()) return false;
		return er.hasOpenAlternatives();
	}
	
	public boolean nextSolution (int id){
		EngineRunner er = findRunner(id);
		if (er==null) return false;
		/*toSPY
		 * System.out.println("Thread id "+runnerId()+" - next_solution: risveglio il thread di id: "+er.getId());
		 */
		boolean bool = er.nextSolution();
		return bool;
	}
	
	public void detach (int id) {
		EngineRunner er= findRunner(id);
		if (er==null) return;
		er.detach();
		removeRunner(er.getId());
	}
	
	public boolean sendMsg (int dest, Term msg){
		EngineRunner er = findRunner(dest);
		if (er==null) return false;
		Term msgcopy = msg.copy(new LinkedHashMap<Var,Var>(), 0);
		er.sendMsg(msgcopy);
		return true;
	}
	
	public boolean sendMsg(String name, Term msg) {
		TermQueue queue = queues.get(name);
		if (queue==null) return false;
		Term msgcopy = msg.copy(new LinkedHashMap<Var,Var>(), 0);
		queue.store(msgcopy);
		return true;
	}
	
	public boolean getMsg(int id, Term msg){
		EngineRunner er = findRunner(id);
		if (er==null) return false;
		return er.getMsg(msg);
	}
	
	public boolean getMsg(String name, Term msg){
		EngineRunner er=findRunner();
		if (er==null) return false;
		TermQueue queue = queues.get(name);
		if (queue==null) return false;
		return queue.get(msg, vm, er);
	}
	
	public boolean waitMsg(int id, Term msg){
		EngineRunner er=findRunner(id);
		if (er==null) return false;
		return er.waitMsg(msg);
	}	
	
	public boolean waitMsg(String name, Term msg){
		EngineRunner er=findRunner();
		if (er==null) return false;
		TermQueue queue=queues.get(name);
		if (queue==null) return false;
		return queue.wait(msg, vm, er);
	}
	
	public boolean peekMsg(int id, Term msg){
		EngineRunner er = findRunner(id);
		if (er==null) return false;
		return er.peekMsg(msg);
	}
	
	public boolean peekMsg(String name, Term msg){
		TermQueue queue = queues.get(name);
		if (queue==null) return false;
		return queue.peek(msg, vm);
	}

	public boolean removeMsg(int id, Term msg){
		EngineRunner er=findRunner(id);
		if (er==null) return false;
		return er.removeMsg(msg);
	}
	
	public boolean removeMsg(String name, Term msg){
		TermQueue queue=queues.get(name);
		if (queue==null) return false;
		return queue.remove(msg, vm);
	}
	
	private void removeRunner(int id){
		synchronized (runners) {
			EngineRunner er=runners.get(id);
			if (er==null) return;
			runners.remove(id);
		}
	}
	
	private void addRunner(EngineRunner er, int id){
		synchronized (runners){
			runners.put(id, er);
		}
	}
	
	public void removeThread(long pid){
		synchronized (threads) {
			threads.remove(pid);
		}
	}
	
	private void addThread(long pid, int id){
		synchronized (threads){
			threads.put((int) pid, id);
		}
	}
	
	void cut() {
		findRunner().cut();
	}
	
	ExecutionContext getCurrentContext() {
		EngineRunner runner=findRunner();
		if(runner!=null)
			return runner.getCurrentContext();
		if(!runners.containsKey(baseID))
			libCall();
		return runners.get(baseID).getCurrentContext();
	}
	
	boolean hasOpenAlternatives() {
		EngineRunner runner = findRunner();
		return runner.hasOpenAlternatives();
	}

	boolean isHalted() {
		EngineRunner runner =findRunner();
		return runner.isHalted();
	}
	
	void pushSubGoal(SubGoalTree goals) {
		EngineRunner runner= findRunner();
		runner.pushSubGoal(goals);
		
	}
	
	public synchronized SolveInfo solve(Term query) {
		rootPID = (int) Thread.currentThread().getId();
		rootID = 1;
		
		EngineRunner er = new EngineRunner(rootID);
		er.initialize(vm);
		er.setGoal(query);
		
		addRunner(er, rootID);
		addThread(rootPID, rootID);
		
		return er.solve();
	}
	
	public synchronized void libCall() {
		basePID = (int) Thread.currentThread().getId();
		baseID = 0;
		
		EngineRunner er = new EngineRunner(baseID);
		er.initialize(vm);
		
		addRunner(er, baseID);
		addThread(basePID, baseID);
	}
	
	public void solveEnd() {
		EngineRunner er = findRunner();
		er.solveEnd();
		runners=new Hashtable<Integer,EngineRunner>();
		threads=new Hashtable<Integer,Integer>();
		queues =new Hashtable<String, TermQueue>();
		locks = new Hashtable<String, ReentrantLock>();
	}
	
	public void solveHalt() {
		EngineRunner er = findRunner();
		if(er.getId() == rootID)
			er.solveHalt();
		else{
			java.util.Enumeration<EngineRunner> ers=runners.elements();
			while (ers.hasMoreElements()) {
				EngineRunner current=ers.nextElement();
				current.solveHalt();		
			}
		}
	}
	
	public synchronized SolveInfo solveNext() throws NoMoreSolutionException {
		EngineRunner er = findRunner(rootID);
		return er.solveNext();
	}
	
	void spy(String action, Engine env) {
		EngineRunner runner = findRunner();
		runner.spy(action, env);
	}
	
	/**
	 * 
	 * @return L'EngineRunner associato al thread di id specificato.
	 * 
	 */
	
	private EngineRunner findRunner (int id){
		if(!runners.containsKey(id)) return null;
		synchronized(runners){
			return runners.get(id);
		}
	}
	
	private EngineRunner findRunner(){
		int key = (int) Thread.currentThread().getId();
		//if(key == rootPID) return runners.get(rootID);
		int id = 0;
		synchronized(threads){
			if(threads.containsKey(key))
				id = threads.get(key);
			else
				return runners.get(rootID);
		}
		synchronized(runners){
			return runners.get(id);
		}
	}
	
	//Ritorna l'identificativo del thread corrente
	public int runnerId (){
		EngineRunner er=findRunner();
		return er.getId();
	}
	
	public boolean createQueue (String name){
		synchronized (queues){
			if (queues.containsKey(name)) return true;
			TermQueue newQ = new TermQueue();
			queues.put(name, newQ);		
			}
		return true;
	}
	
	public void destroyQueue (String name) {
		synchronized (queues){
			queues.remove(name);
		}
	}
	
	public int queueSize(int id){
		EngineRunner er = findRunner(id);
		return er.msgQSize();
	}

	public int queueSize(String name){
		TermQueue q=queues.get(name);
		if (q==null) return -1;
		return q.size();
	}
	
	public boolean createLock(String name){
		synchronized (locks){
			if (locks.containsKey(name)) return true;
			ReentrantLock mutex=new ReentrantLock();
			locks.put(name, mutex);
		}
		return true;
	}
	
	public void destroyLock(String name){
		synchronized (locks){
			locks.remove(name);
		}
	}
	
	public boolean mutexLock(String name){
		ReentrantLock mutex = locks.get(name);
		if (mutex==null) {
			createLock(name);
			return mutexLock(name);
		}
		mutex.lock();
		/*toSPY
		 * System.out.println("Thread id "+runnerId()+ " - mi sono impossessato del lock");
		 */
		return true;
	}

	
	public boolean mutexTryLock(String name){
		ReentrantLock mutex=locks.get(name);
		if (mutex==null) return false;
		/*toSPY
		 * System.out.println("Thread id "+runnerId()+ " - provo ad impossessarmi del lock");
		 */
		return mutex.tryLock();
	}
	
	public boolean mutexUnlock (String name){
		ReentrantLock mutex=locks.get(name);
		if (mutex==null) return false;
		try{
			mutex.unlock();
			/*toSPY
			 * System.out.println("Thread id "+runnerId()+ " - Ho liberato il lock");
			 */
			return true;
		}
		catch(IllegalMonitorStateException e){
			return false;
		}
	}
	
	public boolean isLocked(String name){
		ReentrantLock mutex=locks.get(name);
		if (mutex==null) return false;
		return mutex.isLocked();
	}
	
	public void unlockAll(){
		synchronized (locks){
			Set<String> mutexList=locks.keySet();
			Iterator<String> it=mutexList.iterator();
			
			while (it.hasNext()){
				ReentrantLock mutex=locks.get(it.next());
				boolean unlocked=false;
				while(!unlocked){
					try{
						mutex.unlock();
					}
					catch(IllegalMonitorStateException e){
						unlocked=true;
					}
				}
			}
		}
	}

	Engine getEnv() {
		EngineRunner er=findRunner();
		return er.env;
	}
	
	public void identify(Term t) {
		EngineRunner er=findRunner();
		er.identify(t);
	}	
}

