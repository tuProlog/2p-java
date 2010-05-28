package alice.tuprologx.pj.test;

import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.engine.*;

@PrologClass (clauses = {"remove([X|Xs],X,Xs).",
                         "remove([X|Xs],E,[X|Ys]):-remove(Xs,E,Ys)."})
public abstract class PermutationUtility {    
    
  @PrologMethod (predicate="permutation(@X,-!Y)",
                 signature="(X)->{Y}",
                 keepSubstitutions=true,
                 types={"List<Int>","List<Int>"},
                 clauses={"permutation([],[]).",
                          "permutation(Xs,[X|Ys]):-remove(Xs,X,Zs), permutation(Zs,Ys)."})
  public abstract Iterable<Var<List<Int>>> permutations(List<Int> list);  

  public static void main(String[] args) throws Exception{
    PermutationUtility pu = PJ.newInstance(PermutationUtility.class);
    java.util.Collection<Integer> l=java.util.Arrays.<Integer>asList(new Integer[]{1,2,3});
    for (Var<List<Int>> p : pu.permutations(Term.<List<Int>>fromJava(l))) {      
      System.out.println(p.toJava());
    }
  }
}