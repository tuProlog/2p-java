import alice.tuprolog.*;
import alice.tuprolog.lib.*;
import java.io.*;

public class Test {
    public static void main (String args[]) {
        Prolog engine=new Prolog();
        try {
            
            SolveInfo info=engine.solve(new Struct("append",
                                            new Var("X"),
                                            new Var("Y"),
                                            new Struct(new Term[]{new Int(1), new Int(2)})));
            
            
            if (info.isSuccess()){
                // acquire solution and substitution
                Term sol=info.getSolution();
                System.out.println("Solution "+sol);
                Substitution sub=info.getSubstitution();
                System.out.println("Bindings "+sub);
                // open choice points?
                if (engine.hasOpenAlternatives()){
                    // ask for another solution
                    info=engine.solveNext();
                    if (info.isSuccess()){
                        sol=info.getSolution();
                        System.out.println("An other solution "+sol);
                    }
                }
            }
            
            String theory = " p(X,Y):-q(X),r(Y).\n"+
                            " q(1).\n"+
                            " r(1).\n"+
                            " r(2).\n";
            engine.setTheory(new Theory(theory));
            
            SolveInfo info2=engine.solve("p(1,X).");
            System.out.println(info2.getSubstitution());
            
            FileOutputStream os=new FileOutputStream("test.pl");
            os.write(theory.getBytes());
            os.close();
            
            engine.setTheory(new Theory(new FileInputStream("test.pl")));
            
            info2=engine.solve("p(X,X).");
            System.out.println(info2.getSolution());
                                         

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
