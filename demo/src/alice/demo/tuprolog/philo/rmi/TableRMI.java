/*
 * TableRMI.java
 *
 * Created on 31 ottobre 2000, 22.31
 */

package alice.demo.tuprolog.philo.rmi;

/**
 *
 * @author  Administrator
 * @version
 */

public interface TableRMI extends java.rmi.Remote {

    void acquireChop(int i)  throws java.rmi.RemoteException;

    void releaseChop(int i)  throws java.rmi.RemoteException;

    void acquireTicket()  throws java.rmi.RemoteException;

    void releaseTicket()  throws java.rmi.RemoteException;

}
