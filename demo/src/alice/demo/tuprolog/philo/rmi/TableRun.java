package alice.demo.tuprolog.philo.rmi;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.io.*;
import java.net.InetAddress;

public class TableRun {

    public static void main(String args[]){
        String tableName="table";
        int port=1099;
        String portString="1099";
        int nForks=5;
        if (args.length >0){
            if (args[0].equals("-?")){
                System.out.println("\nargs: {-N<table name>} { -F<n forks> }{-P<rmi server port>} \nex: -NmyTable -P1099\n");
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
            String portSt=getOpt(args,"-P");
            if (portSt!=null){
                try {
                    port=Integer.parseInt(portSt);
                    portString=portSt;
                } catch (Exception ex){
                    System.err.println("Invalid port specification - "+portSt+" - setting default.");
                }
            }
        }
        System.setSecurityManager(new RMISecurityManager());
        try {
            LocateRegistry.createRegistry(port);
        } catch (Exception ex){
        }
        try {
            TableRMIImpl table=new TableRMIImpl(nForks);
            String hostName=InetAddress.getLocalHost().toString();
            int index=hostName.indexOf('/');
            if (index>=0)
                hostName=hostName.substring(0,index);
            Naming.rebind("//"+hostName+":"+portString+"/"+tableName,table);
            System.out.println("RMI Table "+tableName+" waiting "+nForks+" philosophers at "+portString+".");
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

