think(Name):-                                                                             
      atom_chars(N1, [ '[ ',Name,' ] thinking']), 
      write(N1),nl.

eat(Name,C1,C2):-                                             
      num_atom(C1,NC1), num_atom(C2,NC2),                      
      atom_chars(N1, [ '[ ',Name,' ] eating ',NC1,' ',NC2,' ']),    
      write(N1),nl.     

philo(Name,Table,C1,C2):-
      think(Name),                                               
      Table <- acquireTicket,                    
      Table <- acquireChop(C1),                    
      Table <- acquireChop(C2),                   
      eat(Name,C1,C2),               
      Table <- releaseChop(C1),        
      Table <- releaseChop(C2),                       
      Table <- releaseTicket,        
      philo(Name,Table,C1,C2).             

go(Name, TableName, LeftFork, RightFork):-
    'org.omg.CORBA.ORB' <-  init((null as 'java.lang.String[]'), (null as 'java.util.Properties')) returns ORB,
    ORB <- resolve_initial_references('NameService') returns ObjRef,
    'org.omg.CosNaming.NamingContextHelper' <- narrow((ObjRef as 'org.omg.CORBA.Object')) returns NcRef,
    java_object('org.omg.CosNaming.NameComponent',[ 'myTable',' '],NameComp),
    NameComp <- getClass returns NameCompClass,
    'java.lang.reflect.Array' <- newInstance( (NameCompClass as 'java.lang.Class'),1) returns Path,
    'java.lang.reflect.Array' <- set( (Path as 'java.lang.Object'), 0, ( NameComp as 'java.lang.Object')),
    NcRef <- resolve(Path) returns NcRefResolved,
    'demo.philo.corba.TableCORBAHelper' <- narrow((NcRefResolved as 'org.omg.CORBA.Object')) returns Table,
    nl,write('found the table '),
    philo(Name,Table,LeftFork,RightFork).                                                    

    

    
