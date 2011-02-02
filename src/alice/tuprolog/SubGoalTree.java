package alice.tuprolog;
import java.util.*;


public class SubGoalTree extends AbstractSubGoalTree {
	
	private ArrayList terms;
        //private LinkedList terms;
	
	public SubGoalTree() {
		terms = new ArrayList();
                //terms = new LinkedList();
	}

        public SubGoalTree(ArrayList terms) {
		this.terms=terms;
	}
	
	public void addChild(Term term) {
		SubGoalElement l = new SubGoalElement(term);
		terms.add(l);
	}
	
	public SubGoalTree addChild() {
		SubGoalTree r = new SubGoalTree();
		terms.add(r);
		return r;
	}
	
	public AbstractSubGoalTree getChild(int i) {
		return (AbstractSubGoalTree)terms.get(i);
	}
	
	public Iterator iterator() {
		return terms.iterator();
	}
	
	public int size() {
		return terms.size();
	}
	
	public boolean isLeaf() { return false; }
	public boolean isRoot() { return true; }
	
	public String toString() {
		String result = " [ ";
		Iterator i = terms.iterator();
		if (i.hasNext())
			result = result + ((AbstractSubGoalTree)i.next()).toString();
		while (i.hasNext()) {
			result = result + " , " + ((AbstractSubGoalTree)i.next()).toString();
		}
		return result + " ] ";
	}

        public boolean removeChild(int i) {
            try {
                terms.remove(i);
                return true;
            } catch (Exception e) {
                return false;
           }
        }
        public SubGoalTree copy(){
            return new SubGoalTree(terms);
        }
}
