package alice.demo.tuprolog.philo.corba;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.io.*;
import java.net.InetAddress;

public class TableRun {
    public static void main(String args[]){
        String tableName="table";
        int port=1099;
        //String portString="1099";
        int nForks=5;
        if (args.length >0){
            if (args[0].equals("-?")){
                System.out.println("\nargs: {-N<table name>} { -F<n forks> } \nex: -NmyTable -F5 \n");
                System.exit(-1);
            }
            String name=getOpt(args,"-N");
            if (name!=null)
                tableName=name;
            String nforks=getOpt(args,"-F");
            if (nforks!=null){
                try {
                    nForks=Integer.parseInt(nforks);
                } catch (Exception ex){
                    System.err.println("Invalid forks numeber: setting default - "+nForks);
                }
            }
            /*
            String portSt=getOpt(args,"-P");
            if (portSt!=null){
            try {
            port=Integer.parseInt(portSt);
            portString=portSt;
            } catch (Exception ex){
            System.err.println("Invalid port specification - "+portSt+" - setting default.");
            }
            }
             */
        }
        try {
            ORB orb = ORB.init(args, null);
            TableCORBAImpl prologRef = new TableCORBAImpl(nForks);
            orb.connect(prologRef);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(tableName, " ");
            NameComponent path[] = {nc};
            ncRef.rebind(path, prologRef);
            System.out.println("CORBA Table "+tableName+" waiting "+nForks+" philosophers.");
            // Wait for invocations from clients
            java.lang.Object sync = new java.lang.Object();
            synchronized(sync){
                sync.wait();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    static String getOpt(String[] args,String prefix){
        for (int i=0; i<args.length; i++)
            if (args[i].startsWith(prefix))
                return args[i].substring(prefix.length());
        return null;
    }
}

