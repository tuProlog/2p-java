:-load_library('tuprolog.lib.TucsonLibrary').

think(Name):-
     atom_chars(N1, [ '[ ',Name,' ] thinking']),
     write(N1),nl.

eat(Name,C1,C2):-
     num_atom(C1,NC1), num_atom(C2,NC2),
     atom_chars(N1, [ '[ ',Name,' ] eating ',NC1,' ',NC2,' ']),
     write(N1),nl.

philo(TCName,TCAddress,Name,C1,C2):-
     think(Name),
     TCName @ TCAddress ? in(chops(C1,C2)),
     eat(Name,C1,C2),
     TCName @ TCAddress ? out(chops(C1,C2)),
     philo(TCName,TCAddress,Name,C1,C2).

