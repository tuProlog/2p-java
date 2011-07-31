package alice.tuprologx.pj.meta;

import java.lang.reflect.*;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author Maurizio
 */
public class PrologMetaField {
    
    /**
	 * @uml.property  name="_theField"
	 * @uml.associationEnd  qualifier="o:alice.tuprologx.pj.engine.PrologObject alice.tuprologx.pj.model.Term"
	 */
    private Field _theField;
    /**
	 * @uml.property  name="_enclosing"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="_fields:alice.tuprologx.pj.meta.PrologMetaClass"
	 */
    private PrologMetaClass _enclosing;
    /**
	 * @uml.property  name="_annotation"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PrologField _annotation;
    /**
	 * @uml.property  name="_template"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private Theory _template;
    
    /** Creates a new instance of MetaPrologClass */
    public PrologMetaField(PrologMetaClass cl, Field f) {
        _theField = f;
        _enclosing = cl;
        _annotation = _theField.getAnnotation(PrologField.class);
        initTheory();
    }
    
    public PrologMetaClass getEnclosingMeta() {
        return _enclosing;
    }
    
    public <T extends Term<?>> void setValue(PrologObject o, T t) {        
        try {
            _theField.set(o,t);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
    
    public <T extends Term<?>> T getValue(PrologObject o) {        
        try {
            return (T)_theField.get(o);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
    //getThis(X):-this(Z), Z.list <- get(X).
    //setThis(V):-this(Z), Z.list <- set(V).
    private void initTheory() {
        String fname = _theField.getName();
        String pname =_annotation.predicate();
        if (pname.length() == 0)
            pname = fname;
        _template = new Theory(pname + "(X):-this(Z), Z."+ fname + " <- get(X).\n" +
                             pname + " := V:-this(Z), Z."+ fname + " <- set(V).\n");
    }

    public Theory getTheory() {
        return _template;
    }
    
    public Field getJavaField() {
        return _theField;
    }
    
    public void init(PrologObject o) {
        String init = _annotation.init();
        if (init != "") {
            try {
                Term<?> t = Term.unmarshal(alice.tuprolog.Parser.parseSingleTerm(init));
                System.out.println("init field = "+t);
                setValue(o, t);
            }
            catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }
}
