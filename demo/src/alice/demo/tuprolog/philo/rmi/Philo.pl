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
    java_object('java.rmi.RMISecurityManager',[],X),                                   
    'java.lang.System' <- setSecurityManager((X as 'java.lang.SecurityManager')),      
    write('Security manager ok.'),nl,
    'java.rmi.Naming' <- lookup(TableName) returns Table,    
    write('found table '),write(Table),nl,
    philo(Name,Table,LeftFork,RightFork).                                                    
    

    

