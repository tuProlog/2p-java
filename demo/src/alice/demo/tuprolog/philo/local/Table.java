/*
 * Table.java
 *
 * Created on 31 ottobre 2000, 22.31
 */

package alice.demo.tuprolog.philo.local;

/**
 *
 * @author  Administrator
 * @version
 */
public class Table extends Object {

    boolean[] chops;
    boolean ticket;

    /** Creates new Table */
    public Table(int n) {
      chops=new boolean[n];
      for (int i=0; i<chops.length; i++)
        chops[i]=true;
      ticket=true;
    }

    public synchronized void acquireChop(int i){
      while (!chops[i])
        try { wait(); } catch (InterruptedException ex){}
      chops[i]=false;
    }

    public synchronized void releaseChop(int i){
      chops[i]=true;
      notifyAll();
    }

    public synchronized void acquireTicket(){
      while (!ticket)
        try { wait(); } catch (InterruptedException ex){}
      ticket=false;
    }

    public synchronized void releaseTicket(){
      ticket=true;
      notifyAll();
    }

}
