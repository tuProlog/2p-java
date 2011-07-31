package alice.tuprolog;

/**
 * Identifier of single subGoal during the demo.
 * @author Alex Benini
 *
 */
public class DefaultSubGoalId implements SubGoalId {
    
    /**
	 * @uml.property  name="root"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private SubGoalTree root;
    /**
	 * @uml.property  name="index"
	 */
    private int index;
    /**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private DefaultSubGoalId parent; 
    
    DefaultSubGoalId(DefaultSubGoalId parent, SubGoalTree root, int index) {
        this.parent = parent;
        this.root = root;
        this.index = index;
    }
    
    /**
	 * @return
	 * @uml.property  name="parent"
	 */
    DefaultSubGoalId getParent() {
        return parent;
    }
    
    /**
	 * @return
	 * @uml.property  name="root"
	 */
    SubGoalTree getRoot() {
        return root;
    }
    
    /**
	 * @return
	 * @uml.property  name="index"
	 */
    int getIndex() {
        return index;
    }
    
    
    
    public String toString() {
        return root.getChild(index).toString();
    }

}