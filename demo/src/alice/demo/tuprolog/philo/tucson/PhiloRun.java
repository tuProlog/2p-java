/**
 * 5 Philosophers example using Tucson Service as coordination model
 * with 3 prolog philosophers and 2 java philosophers
 *
 * this is a concrete example of coordination among etherogenous activities
 *
 * the prolog source is executed on our tuprolog virtual machine
 */

package alice.demo.tuprolog.philo.tucson;

import alice.logictuple.*;
import alice.tucson.api.*;

import java.util.*;
import java.io.*;


/**
 * the java philosopher
 */
class JavaPhilo implements Runnable {
    String name;
    TupleCentreId tid;
    int c1,c2;
    TucsonContext context;

    public JavaPhilo(String n,TupleCentreId tid_) throws TucsonException {
        name=n;
        tid=tid_;
        context = Tucson.enterDefaultContext();
        new Thread(this).start();
    }

    public void run(){
        System.out.println("[ "+name+" ] run");
        try {
            LogicTuple tc=context.rd(tid,new LogicTuple("philo",new Value(name),new Var("C1"),new Var("C2")));
            c1=(int)tc.getArg(1).intValue();
            c2=(int)tc.getArg(2).intValue();
            while (true){
                think();
                context.in(tid,new LogicTuple("chops",new Value(c1),new Value(c2)));
                eat();
                context.out(tid,new LogicTuple("chops",new Value(c1),new Value(c2)));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void think(){
        System.out.println("[ "+name+" ] thinking");
    }

    void eat(){
        System.out.println("[ "+name+" ] eating "+c1+" "+c2);
    }
}

/**
 * the prolog philosopher running on the tuprolog virtual machine
 */
class PrologPhilo {
    PrologPhilo(String name, TupleCentreId tid){
        String theory=makeTheory(tid,name);
        new alice.tuprolog.Agent(theory);
    }

    String makeTheory(TupleCentreId tid,String name){
        return
        ":-load_library('alice.tuprolog.lib.TucsonLibrary').                                              \n"+
        ":-solve(go).                                                                               \n"+
        "think(Name):-                                                                              \n"+
        "              atom_chars(N1, [ '[ ',Name,' ] thinking']),                                  \n"+
        "              write(N1),nl.                                                                \n"+
        "eat(Name,C1,C2):-                                                                          \n"+
        "              num_atom(C1,NC1), num_atom(C2,NC2),                                          \n"+
        "              atom_chars(N1, [ '[ ',Name,' ] eating ',NC1,' ',NC2,' ']),                   \n"+
        "              write(N1),nl.                                                                \n"+
        "philo(TCName,TCAddress,Name,C1,C2):-                                                       \n"+
        "              think(Name),                                                                 \n"+
        "              TCName @ TCAddress ? in(chops(C1,C2)),                                       \n"+
        "              eat(Name,C1,C2),                                                             \n"+
        "              TCName @ TCAddress ? out(chops(C1,C2)),                                      \n"+
        "              philo(TCName,TCAddress,Name,C1,C2).                                          \n"+
        "go:- '"+tid.getName()+"' @ '"+tid.getNode()+"' ? rd(philo("+name+",C1,C2)),                         \n"+
        "     philo('"+tid.getName()+"','"+tid.getNode()+"',"+name+",C1,C2).                                 \n";
    }
}


/**
 * install proper reactions to avoid deadlock and
 * make chops resources avaiable to philosophers
 */
class MainResource implements Runnable {

    TupleCentreId tid;
    TucsonContext context;

    MainResource(TupleCentreId tid_) throws TucsonException {
        tid=tid_;
        context = Tucson.enterDefaultContext();
        new Thread(this).start();
    }

    public void run(){
        System.out.println("[ main ] run");

        try {
            context.setSpec(tid,"reaction(in(chops(C1,C2)), (pre, out_r(required(C1,C2)))).\n"+
                "reaction(in(chops(C1,C2)), (post,in_r(required(C1,C2)))).\n"+
                "reaction(out_r(required(C1,C2)),(in_r(chop(C1)),in_r(chop(C2)),out_r(chops(C1,C2)))).\n"+
                "reaction(out(chops(C1,C2)), ( in_r(chops(C1,C2)),out_r(chop(C1)),out_r(chop(C2)))).\n"+
                "reaction(out_r(chop(C1)), (rd_r(required(C1,C)), in_r(chop(C1)),in_r(chop(C)),out_r(chops(C1,C)))).\n"+
                "reaction(out_r(chop(C2)), (rd_r(required(C,C2)), in_r(chop(C)),in_r(chop(C2)),out_r(chops(C,C2)))).\n");

            context.out(tid,new LogicTuple("chop",new Value(0)));
            context.out(tid,new LogicTuple("chop",new Value(1)));
            context.out(tid,new LogicTuple("chop",new Value(2)));
            context.out(tid,new LogicTuple("chop",new Value(3)));
            context.out(tid,new LogicTuple("chop",new Value(4)));
            // we make philosophers known what chops they must use
            context.out(tid,new LogicTuple("philo",new Value("arnaldo"),new Value(0),new Value(1)));
            context.out(tid,new LogicTuple("philo",new Value("etles"),new Value(1),new Value(2)));
            context.out(tid,new LogicTuple("philo",new Value("beppo"),new Value(2),new Value(3)));
            context.out(tid,new LogicTuple("philo",new Value("cledes"),new Value(3),new Value(4)));
            context.out(tid,new LogicTuple("philo",new Value("rosa"),new Value(4),new Value(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class PhiloRun {
    public static void main (String args[]) {
        try {
            TupleCentreId tid;
            if (args.length==0){
                tid=new TupleCentreId("default");
            } else if (args.length==1) {
                tid=new TupleCentreId(args[0]);
            } else {
                System.err.println("args: {tuple centre identifier}");
                System.exit(-1);
                return;
            }
            new MainResource(tid);
            new JavaPhilo("arnaldo",tid);
            new JavaPhilo("etles",tid);
            new PrologPhilo("beppo",tid);
            new PrologPhilo("cledes",tid);
            new PrologPhilo("rosa",tid);
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}