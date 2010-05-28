/*
 * LengthSample.java
 *
 * Created on July 23, 2007, 3:47 PM
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
@PrologClass(clauses={"size(X,Y):-length(X,Y)."})
public abstract class LengthSample {
    
  @PrologMethod
  public abstract <$Ls extends Term<? extends List<?>>, $Ln extends Term<Int>> Iterable<Compound2<$Ls,$Ln>> size($Ls expr, $Ln rest);                           
    
  @PrologMethod
  public abstract <$Ls extends List<?>, $Ln extends Int> Boolean size($Ls expr, $Ln rest);                           
  
  @PrologMethod
  public abstract <$Ls extends List<?>, $Ln extends Int> $Ln size($Ls expr);                           
  
  @PrologMethod
  public abstract <$Ls extends List<?>, $Ln extends Int> $Ls size($Ln expr);                           
  
  @PrologMethod
  public abstract <$Ls extends List<?>, $Ln extends Int> Iterable<Compound2<$Ls,$Ln>> size();                           
  
  public static void main(String[] args) throws Exception {
    LengthSample ls = PJ.newInstance(LengthSample.class);    
    java.util.List<?> v = java.util.Arrays.asList(new Object[] {12,"twelve",false});        
    List<?> list = new List<Term<?>>(v);
    Int length = new Int(3);
    System.out.println(ls.size(list, new Int(3))); //true 'list' is of size 3
    System.out.println(ls.size(list)); //length of 'list' is 3
    System.out.println(ls.size(length)); //[_,_,_] is a list whose size is 3
    int cont = 0;
    for (Term<?> t : ls.size()) { //{[],[_],[_,_], ...} all list whose size is less than 5
        System.out.println(t); 
        if (cont++ == 5) break;
    }
  }  
}
