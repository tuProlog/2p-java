import alice.tuprolog.*;
import java.io.*;

public class Console  {

    public static void main (String args[]) throws Exception {
        
        Prolog engine=new Prolog();
        if (args.length>0){
            engine.setTheory(new Theory(new FileInputStream(args[0])));
        }
        
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        
        while (true) {
            String goal;
            do { 
                System.out.print("?- ");
                goal=stdin.readLine(); 
            } while (goal.equals(""));
            
            try {
                SolveInfo info = engine.solve(goal);
                if (info.isHalt()){
                    break;
                } else if (!info.isSuccess()) {
                    System.out.println("no.");
                } else {
                    if (!engine.hasOpenAlternatives()){
                        System.out.print("yes. \n" + info.getSubstitution()+ "\n");
                    } else {
                        // show the first solution
                        System.out.println(info.getSubstitution() + " ?");
                        String answer = stdin.readLine();
                        // check for next solutions, if the user asks for them
                        while (answer.equals(";") && engine.hasOpenAlternatives()) {
                            info = engine.solveNext();
                            if (!info.isSuccess()) {
                                System.out.println("no.");
                                break;
                            } else {
                                System.out.println(info.getSubstitution() + " ?");
                                answer = stdin.readLine();
                            }
                        }
                        // redundant tests to obtain a more user-friendly console
                        if (!answer.equals(";")){
                            System.out.print("yes. \n"+info.getSubstitution()+"\n");
                        } else if (!engine.hasOpenAlternatives()) {
                            System.out.println("no.");
                        }
                    }
                }
            } catch (MalformedGoalException ex){
                System.err.println("syntax error.");
            }
        }
        
        // save the current theory on the file specified by the third argument
        Theory finalTheory=engine.getTheory();

        if (args.length>0){
            new FileOutputStream(args[0]).write(finalTheory.toString().getBytes());
        }
    }
} 


























































