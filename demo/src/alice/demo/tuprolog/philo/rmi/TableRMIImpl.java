/*
 * Table.java
 *
 * Created on 31 ottobre 2000, 22.31
 */

package alice.demo.tuprolog.philo.rmi;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author  Administrator
 * @version
 */
public class TableRMIImpl extends UnicastRemoteObject implements TableRMI {

    boolean[] chops;
    boolean ticket;

    /** Creates new Table */
    public TableRMIImpl(int n) throws RemoteException {
        chops=new boolean[n];
        for (int i=0; i<chops.length; i++)
        chops[i]=true;
        ticket=true;
    }

    public synchronized void acquireChop(int i)  throws RemoteException {
        while (!chops[i])
        try {
            wait();
        } catch (InterruptedException ex){
        }
        chops[i]=false;
    }

    public synchronized void releaseChop(int i)  throws RemoteException {
        chops[i]=true;
        notifyAll();
    }

    public synchronized void acquireTicket()  throws RemoteException {
        while (!ticket)
        try {
            wait();
        } catch (InterruptedException ex){
        }
        ticket=false;
    }

    public synchronized void releaseTicket()  throws RemoteException {
        ticket=true;
        notifyAll();
    }
}
