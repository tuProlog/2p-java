/*
 * Created on 15-set-2004
 *
 */
package alice.tuprologx.ide;

/**
 * @author <a href="mailto:simone.pellegrini@studio.unibo.it">Simone Pellegrini</a>
 *
 */
public class EngineRunningException extends Exception{
	public EngineRunningException(){
		super("Engine is running!");
	}
}
