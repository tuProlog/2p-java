import alice.tuprolog.*;
import java.io.*;

public class TestConsole {
    public static void main (String args[]) {
        BufferedReader stdIn=new BufferedReader(new InputStreamReader(System.in));
        try {
            Prolog engine=new Prolog();
            //engine.setTheory(new Theory(new FileInputStream(args[0])));
            // demonstrating a goal specified as a string
            SolveInfo info=engine.solve(args[0]);
            if (!info.isSuccess()){
                System.out.println("no.");
            } else if (!engine.hasOpenAlternatives()){
                System.out.println("yes: "+info.getSubstitution());
            } else {                
                // acquire solution and substitution
                do {
                    System.out.println(info.getSubstitution()+"?");
                    String answer = stdIn.readLine();
                    if (answer.equals(";")){
                        info = engine.solveNext();
                        if (!info.isSuccess()){
                            System.out.println("no.");
                            break;
                        }
                    } else {
                        System.out.println("yes: "+info.getSubstitution());
                        break;
                    }
                } while (info.isSuccess()||engine.hasOpenAlternatives());
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
