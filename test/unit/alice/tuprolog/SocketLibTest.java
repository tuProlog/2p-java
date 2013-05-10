package alice.tuprolog;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import alice.tuprolog.lib.SocketLibrary;


public class SocketLibTest {

	private static Prolog engine = null;
	private static  Prolog engine2=null;

	@BeforeClass
	public static void before() throws InterruptedException, InvalidTheoryException, MalformedGoalException {
		engine = new Prolog();
		engine2=new Prolog();
		
		System.out.println("Server configuration"); 
		Thread myThread = new Thread() { 
			public void run() { 
				System.out.println("Running myThread"); 
				SocketLibrary lib;

				Struct Address=new Struct("127.0.0.1:4444");
				Term Socket= new Var();
				Struct Options=new Struct("[]");
				Term ClientSocketSalve=new Var();
				Term Msg=new Var();
				
					try {			
				
					engine.loadLibrary("alice.tuprolog.lib.SocketLibrary");
					engine2.loadLibrary("alice.tuprolog.lib.SocketLibrary");
						
					lib = (SocketLibrary) engine.getLibrary("alice.tuprolog.lib.SocketLibrary");
									
					boolean ts;
					ts=lib.tcp_socket_server_open_3(Address, Socket, Options);
					if (ts)	
					{
						System.out.println("Server connection established"); 
						
					}
					boolean ta;
					ta=lib.tcp_socket_server_accept_3(Socket, Address, ClientSocketSalve);
					if(ta)
					{
						System.out.println("Connection accepted");
					}				
					
					boolean rd;
					rd=lib.read_from_socket_3(ClientSocketSalve, Msg, Options);
					if(rd)
					{
						System.out.println("I read  " + Msg);
					}
				
					
				} catch (InvalidLibraryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				 catch (PrologError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			
		}; 
		myThread.start(); 
		Thread.sleep(1000);
	} 

	@Test
	public void testTcp_socket_client_open_2() throws PrologException, PrologError {

		String theory="client(X):-tcp_socket_client_open('127.0.0.1:4444',Sock).";
		engine2.setTheory(new Theory(theory));
		SolveInfo goal=engine2.solve("client(X).");
		assertTrue(goal.isSuccess());
		
	}
	

	@Test
	public void testTcp_socket_server_close_1() throws PrologError, InvalidTheoryException, MalformedGoalException {
		Struct Address=new Struct("127.0.0.1:4441");
        Term Socket= new Var();
        Struct Options=new Struct("[]");
                
        SocketLibrary lib= (SocketLibrary) engine.getLibrary("alice.tuprolog.lib.SocketLibrary");
        lib.tcp_socket_server_open_3(Address, Socket, Options);

        boolean close=lib.tcp_socket_server_close_1(Socket);
        
        assertTrue(close);
	}

	@Test
	public void testWrite_to_socket_2() throws InvalidTheoryException, MalformedGoalException, PrologError {
		String theory="client(X,Y,Z):-tcp_socket_client_open('127.0.0.1:4444',Sock),write_to_socket(Sock,test1).";
		engine2.setTheory(new Theory(theory));
		SolveInfo goal=engine2.solve("client(X,Y,Z).");
		assertTrue(goal.isSuccess());	
	
	}

//	@Test
//	public void testRead_from_socket_3() throws InvalidTheoryException, MalformedGoalException, PrologError, NoSolutionException, UnknownVarException {
//		
//			
//		String theory="client(X):-tcp_socket_client_open('127.0.0.1:4444',Sock),read_from_socket(Sock,X,[]).";
//		
//		engine2.setTheory(new Theory(theory));
//		SolveInfo goal=engine2.solve("client(X).");
//
//		assertTrue(goal.isSuccess());
//		System.out.println("I read"+ goal);
//		
//		
//		
//	}

	


}
