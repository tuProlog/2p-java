package alice.tuprologx.pj.meta;

import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.annotations.parser.*;
import alice.tuprologx.pj.annotations.parser.PrologTree.*;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.engine.*;

import java.lang.reflect.*;

/**
 *
 * @author Maurizio
 */
public class PrologMetaMethod {
    
    /**
	 * @uml.property  name="_theMethod"
	 */
    private Method _theMethod;
    /**
	 * @uml.property  name="_enclosing"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="_methods:alice.tuprologx.pj.meta.PrologMetaClass"
	 */
    private PrologMetaClass _enclosing;
    /**
	 * @uml.property  name="_annotation"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PrologMethod _annotation;
    /**
	 * @uml.property  name="_theory"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private Theory _theory;
    /**
	 * @uml.property  name="_arity"
	 */
    private int _arity;
    /**
	 * @uml.property  name="_predicateName"
	 */
    private String _predicateName;
    /**
	 * @uml.property  name="_clauses"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
    private Clause<?,?>[] _clauses; 
    /*
    private Vector<String> variableNames;
    private Vector<String> inputVariables;
    private Vector<String> outputVariables;
    
    private boolean multipleResult;
    */
    /** Creates a new instance of MetaPrologClass */
    public PrologMetaMethod(PrologMetaClass cl, Method m) {
        _theMethod = m;        
        _enclosing = cl;
        _annotation = _theMethod.getAnnotation(PrologMethod.class);
        initTheory();
        initClauses();
    }
    
    void initClauses() {
        //String linkName;                
        if (_annotation.predicate().equals(""))  {
            _predicateName = _theMethod.getName();
            _arity = _theMethod.getTypeParameters().length;
        }
        else {
            PredicateExpr p = new Parser(_annotation.predicate()).parsePredicate();        
            _predicateName = p.name;        
            _arity = p.variables.size();
        }        
        _clauses = _theory.find(_predicateName, _arity);
        /*if (_clauses.length == 0) {
            _clauses = _enclosing.getTheory().find(_predicateName, _arity);
        }
        if (_clauses.length == 0) {
            for (PrologMetaField metaField : _enclosing.getPrologFields()) {
                _clauses = metaField.getTemplate().find(_predicateName, _arity);
            }
        }
        if (_clauses.length == 0) {
            throw new InvalidPredicateException(_predicateName+"/"+_arity);
        }*/
    }
    
    private void initTheory() {
        _theory = new Theory(_annotation.clauses());
    }
    
    public Theory getTheory() {
        return _theory;
    }
    
    public void setTheory(Theory t) {
        _theory = t;
        initClauses();
    }
    
    public Clause<?,?>[] getClauses() {
        return _clauses;
    }
    
    public int getArity() {
        return _arity;
    }
    
    public boolean hasMultipleOutput() {
        return _theMethod.getReturnType().equals(Iterable.class);
    }
    
    public Object invoke(PrologObject o, Term<?>... args) {
        try {
            return _theMethod.invoke(o,(Object)args);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
    
    public PrologMetaClass getEnclosingMeta() {
        return _enclosing;
    }    
    
    public Method getJavaMethod() {
        return _theMethod;
    }
    
    public String getPredicateName() {
        return _predicateName;
    }
}
