package alice.demo.tuprolog.philo.corba;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

import java.net.InetAddress;
import java.net.*;

public class PhiloRun implements Runnable {
    String name;
    int c1,c2;
    TableCORBA table;

    public PhiloRun(String n,TableCORBA table_,int c1_,int c2_){
        name=n;
        c1=c1_;
        table=table_;
        c2=c2_;
        new Thread(this).start();
    }

    public void run(){
        System.out.println("[ "+name+" ] run");
        try {
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
        } catch (Exception ex){
        }
    }

    void think(){
        System.out.println("[ "+name+" ] thinking");
        //try { Thread.currentThread().sleep(1); } catch (Exception ex){}
    }

    void eat(){
        System.out.println("[ "+name+" ] eating "+c1+" "+c2);
        //try { Thread.currentThread().sleep(2); } catch (Exception ex){}
    }


    public static void main (String args[]) {
        String tableName="table";
        int port=1099;
        String portString="1099";
        String hostName="127.0.0.1";
        int iLeftFork=0;
        int iRightFork=1;
        TableCORBA table=null;
        String name="Socrate";

        try {
            hostName=InetAddress.getLocalHost().toString();
            int index=hostName.indexOf('/');
            if (index>=0)
                hostName=hostName.substring(0,index);
        } catch (Exception ex){
        }
        if (args.length <3){
            System.err.println("\nargs: PhiloName LeftFork RightFork {-N<table name>} {-H<host address>} {-P<rmi server port>} \nex: socrate 0 1 -Ntable -P1099 -Hlocalhost\n");
            System.exit(-1);
        }
        try {
            name=args[0];
            iLeftFork=Integer.parseInt(args[1]);
            iRightFork=Integer.parseInt(args[2]);
        } catch (Exception ex){
            System.err.println("\nInvalid forks");
            System.exit(-1);
        }
        if (args.length >0){
            if (args[0].equals("-?")){
                System.out.println("\nargs: PhiloName LeftFork RightFork {-N<table name>} {-H<host address>} {-P<rmi server port>} \nex: socrate 0 1 -Ntable -P1099 -Hlocalhost\n");
                System.exit(-1);
            }
            String tname=getOpt(args,"-N");
            if (tname!=null)
                tableName=tname;
            String portSt=getOpt(args,"-P");
            if (portSt!=null){
                try {
                    port=Integer.parseInt(portSt);
                    portString=portSt;
                } catch (Exception ex){
                    System.err.println("Invalid port specification - "+portSt+" - setting default.");
                }
            }
            String hostSt=getOpt(args,"-H");
            if (hostSt!=null)
                hostName=hostSt;
        }
        try {
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(tableName, " ");
            NameComponent path[] = {nc};
            table = TableCORBAHelper.narrow(ncRef.resolve(path));
            new PhiloRun(name,table,iLeftFork,iRightFork);
        } catch (Exception ex){
            //ex.printStackTrace();
            System.err.println("table "+tableName+" not reachable.");
            System.exit(-1);
        }
    }

    static String getOpt(String[] args,String prefix){
        for (int i=0; i<args.length; i++)
            if (args[i].startsWith(prefix))
                return args[i].substring(prefix.length());
        return null;
    }
}
