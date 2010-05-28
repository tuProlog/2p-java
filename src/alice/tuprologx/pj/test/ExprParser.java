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
import java.util.StringTokenizer;
import java.util.Vector;
/**
 *
 * @author maurizio
 */
@PrologClass
public abstract class ExprParser {    
  
  @PrologMethod (clauses={"expr(L,R):-term(L,R).",
                          "expr(L,R):-term(L,['+'|R2]), expr(R2,R).",
                          "expr(L,R):-term(L,['-'|R2]), expr(R2,R)."})                          
  public abstract <$E extends List<?>, $R extends List<?>> Boolean expr($E expr, $R rest);                          
  
  
  @PrologMethod (clauses={"term(L,R):-fact(L,R).",
                          "term(L,R):-fact(L,['*'|R2]), term(R2,R).",
                          "term(L,R):-fact(L,['/'|R2]), term(R2,R)."})
  public abstract <$T extends List<?>, $R extends List<?>> Boolean term($T term, $R rest);                            
  
  
  @PrologMethod (clauses={"fact(L,R):-num(L,R).",
                          "fact(['(' | E],R):-expr(E,[')'|R])."})
  public abstract <$F extends List<?>, $R extends List<?>> Boolean fact($F fact, $R rest);                          
  
  
  @PrologMethod (clauses={"num([L|R],R):-num_atom(_,L)."})                          
  public abstract <$N extends List<?>, $R extends List<?>> Boolean num($N num, $R rest);                              
  
  
  static Vector<String> split(String s) {
      Vector<String> vs = new Vector<String>();
      for (String s1 : s.split("(?<!^)(\\b|(?=\\()|(?=\\))|(?=\\-)|(?=\\+)|(?=\\/)|(?=\\*))")) {      
          vs.add(s1);
      }      
      return vs;
  }
  
  
  public static void main(String[] args) throws Exception {
    ExprParser ep = PJ.newInstance(ExprParser.class);    
    //List<Atom> exp1 = new List<Atom>(java.util.Arrays.asList("12","+","(","3","*","4",")"));        
    //List<Atom> exp2 = new List<Atom>(java.util.Arrays.asList("(","12","+","(","3","*","4",")",")"));               
    String regexp = "(?<!^)(\\b|(?=\\()|(?=\\))|(?=\\-)|(?=\\+)|(?=\\/)|(?=\\*))";
    //List<Atom> exp1 = new Atom("12+(3*4)").split("+-/*()",true);        
    //List<Atom> exp2 = new Atom("(12+(3*4))").split("+-/*()",true);        
    
    //System.out.println(split("(pippo)"));
    //List<Atom> exp1 = List.tokenize(new StringTokenizer("(12+(3-4))+(1000)","+-*/()",true));        
    //List<Atom> exp2 = List.tokenize(new StringTokenizer("(12+(3*4))","+-*/()",true));        
    
    List<Atom> exp1 = new Atom("12+(3-4)").split(regexp);        
    List<Atom> exp2 = new Atom("(12+(3-4))").split(regexp);        
    
    System.out.println(split("12+(3-4)"));
    System.out.println(exp1);
    
    System.out.println(ep.expr(exp1, List.NIL)); //true, 12+(3*4) is an expression
    System.out.println(ep.fact(exp1, List.NIL)); //false, 12+(3*4) isn't a factor
    System.out.println(ep.expr(exp2, List.NIL));//true, (12+(3*4)) is an expression
    System.out.println(ep.fact(exp2, List.NIL));//true, (12+(3*4)) is a factor
  }  
}
