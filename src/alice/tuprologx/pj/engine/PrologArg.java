/*
 * PrologArg.java
 *
 * Created on March 12, 2007, 10:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.engine;
import java.lang.annotation.*;
import alice.tuprologx.pj.model.*;
/**
 *
 * @author maurizio
 */
public class PrologArg<X extends Term<X>> {
    
    private Term<X> _theArg;
    private TermKind[] _annotations;    
    
    /** Creates a new instance of PrologArg */
    public PrologArg(Object arg, TermKind[] annotations) {        
        this(annotations);
        _theArg = (Term<X>)arg;            
    }
    
    public PrologArg(TermKind[] annotations) {     
        _annotations = annotations;
    }
    
    public boolean isInputArgument() {
        for (TermKind tk : _annotations) {
            switch (tk) {
                case INPUT : return true;
            }
        }
        return false;
    }

    public boolean isOutputArgument() {
        for (TermKind tk : _annotations) {
            switch (tk) {
                case OUTPUT : return true;
            }
        }
        return false;
    }

    public boolean isIsGround() {        
        for (TermKind tk : _annotations) {
            switch (tk) {
                case GROUND : return true;
            }
        }
        return false;
    }
    
    public boolean isIsHidden() {        
        for (TermKind tk : _annotations) {
            switch (tk) {
                case HIDE : return true;
            }
        }
        return false;
    }
    
    public Term<X> getTerm() {
        return _theArg;
    }
    
    protected void setTerm(Object o) {
        _theArg = (Term<X>)o;
    }
}
