package alice.demo.tuprolog.philo.local;
import alice.tuprolog.*;
import java.io.*;
import java.util.*;

class JavaPhiloTable implements Runnable {
    String name;
    int c1,c2;
    Table table;

    public JavaPhiloTable(String n, Table table_, int c1_,int c2_){
        name=n;
        c1=c1_;
        table=table_;
        c2=c2_;
        new Thread(this).start();
    }

    public void run(){
        System.out.println("[ "+name+" ] run");
        while (true){
            think();
            table.acquireTicket();
            table.acquireChop(c1);
            table.acquireChop(c2);
            eat();
            table.releaseChop(c1);
            table.releaseChop(c2);
            table.releaseTicket();
        }
    }

    void think(){
        System.out.println("[ "+name+" ] thinking");
        /*
        try {
            Thread.currentThread().sleep(1);
        } catch (Exception ex){
        }*/
    }

    void eat(){
        System.out.println("[ "+name+" ] eating "+c1+" "+c2);
        /*
        try {
            Thread.currentThread().sleep(2);
        } catch (Exception ex){
        }*/
    }
}

/**
 * the prolog philosopher running on the tuprolog virtual machine
 */
class PrologPhiloTable extends Thread {
    int c1,c2;
    Table table;
    Prolog core=new Prolog();

    PrologPhiloTable(String name, Table table_, int c1_,int c2_){
        try {
            table=table_;
            c1=c1_;
            c2=c2_;
            String theory=makeTheory(name);
            OutputListener l=new OutputListener() {
                public void onOutput(OutputEvent ev) {
                    System.out.print(ev.getMsg());
            }};
            core.addOutputListener(l);
            core.loadLibrary("alice.tuprolog.lib.JavaLibrary");
            alice.tuprolog.lib.JavaLibrary lib=(alice.tuprolog.lib.JavaLibrary)core.getLibrary("alice.tuprolog.lib.JavaLibrary");
            lib.register(new Struct("table"),table);
            core.addTheory(new Theory(new ByteArrayInputStream(theory.getBytes())));
            start();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void run(){
        try {
            core.solve("go("+c1+","+c2+").");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    String makeTheory(String name){
        return
        "think(Name):-                                                                              \n"+
        "              atom_chars(N1, [ '[ ',Name,' ] thinking']),                                  \n"+
        "              write(N1),nl.                                                                \n"+
        "eat(Name,C1,C2):-                                                                          \n"+
        "              num_atom(C1,NC1), num_atom(C2,NC2),                                          \n"+
        "              atom_chars(N1, [ '[ ',Name,' ] eating ',NC1,' ',NC2,' ']),                   \n"+
        "              write(N1),nl.                                                                \n"+
        "philo(Name,C1,C2):-                                                                        \n"+
        "              think(Name),                                                                 \n"+
        "              table <- acquireTicket,                                                      \n"+
        "              table <- acquireChop(C1),                                                    \n"+
        "              table <- acquireChop(C2),                                                    \n"+
        "              eat(Name,C1,C2),                                                             \n"+
        "              table <- releaseChop(C1),                                                    \n"+
        "              table <- releaseChop(C2),                                                    \n"+
        "              table <- releaseTicket,                                                      \n"+
        "              philo(Name,C1,C2).                                                           \n"+
        "go(C1,C2):-philo("+name+",C1,C2).                                                          \n";
    }
}


public class PhiloRun {
    public static void main (String args[]) {
        try {
            Table table=new Table(5);
            new JavaPhiloTable("arnaldo",table,0,1);
            new JavaPhiloTable("etles",table,1,2);
            new PrologPhiloTable("beppo",table,2,3);
            new PrologPhiloTable("piero",table,3,4);
            new PrologPhiloTable("furio",table,4,0);
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}
