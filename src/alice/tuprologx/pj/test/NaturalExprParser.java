/*
 * ExprParser.java
 *
 * Created on July 19, 2007, 4:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;

import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class NaturalExprParser {    
  @TRACE 
  @PrologMethod (clauses={"parse_exp(X):-s(X,[]).",
    "s(S0,S) :- np(S0,S1), vp(S1,S).",
    "np(S0,S) :- det(S0,S1), n(S1,S).",
    "vp(S0,S) :- tv(S0,S1), np(S1,S).",
    "vp(S0,S) :- v(S0,S).",
    "det(S0,S) :- S0=[the|S].",
    "det(S0,S) :- S0=[a|S].",
    "det(S0,S) :- S0=[every|S].",
    "n(S0,S) :- S0=[man|S].",
    "n(S0,S) :- S0=[woman|S].",
    "n(S0,S) :- S0=[park|S].",
    "tv(S0,S) :- S0=[loves|S].",
    "tv(S0,S) :- S0=[likes|S].",
    "v(S0,S) :- S0=[walks|S]."})
  public abstract <$E extends List<?>> Boolean parse_exp($E expr);

  public static void main(String[] args) throws Exception {
    NaturalExprParser ep = PJ.newInstance(NaturalExprParser.class);    
    java.util.List<Object> s1 = java.util.Arrays.asList(new Object[] {"a","man","loves","every","woman"});    
    java.util.List<Object> s2 = java.util.Arrays.asList(new Object[] {"every","woman","likes","the","park"});    
    java.util.List<Object> s3 = java.util.Arrays.asList(new Object[] {"every","dog","likes","sitting","in", "the","park"});    
    System.out.println(ep.parse_exp(new List<Atom>(s1)));
    System.out.println(ep.parse_exp(new List<Atom>(s2)));
    System.out.println(ep.parse_exp(new List<Atom>(s3)));
  }  
}



