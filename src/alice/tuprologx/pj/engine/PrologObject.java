/*
 * PrologObject.java
 *
 * Created on April 2, 2007, 1:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.engine;

import alice.tuprologx.pj.meta.*;
import alice.tuprologx.pj.model.*;
/**
 * @author  maurizio
 */
public interface PrologObject {    
    /**
	 * @uml.property  name="metaPrologClass"
	 * @uml.associationEnd  
	 */
    PrologMetaClass getMetaPrologClass();
    /**
	 * @uml.property  name="theory"
	 * @uml.associationEnd  
	 */
    Theory getTheory();
    /**
	 * @param t
	 * @uml.property  name="theory"
	 */
    void setTheory(Theory t);
}
